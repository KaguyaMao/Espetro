// obb-broken-scan.mjs — 在解出的模组数据里找"OBB 数组未闭合"的损坏 JSON
import fs from 'node:fs';
import path from 'node:path';

const ROOTS = ['modjar-veh/dr', 'modjar-veh/fcp', 'sbwsrc/data'];
let checked = 0, broken = [];
function walk(dir) {
  let entries = [];
  try { entries = fs.readdirSync(dir, { withFileTypes: true }); } catch { return; }
  for (const e of entries) {
    const p = path.join(dir, e.name);
    if (e.isDirectory()) { walk(p); continue; }
    if (!e.name.endsWith('.json')) continue;
    const text = fs.readFileSync(p, 'utf8');
    if (!text.includes('"OBB"')) continue;
    checked++;
    try { JSON.parse(text); } catch (err) {
      if (/Unterminated|Unexpected end/i.test(err.message)) broken.push({ p, err: err.message, len: text.length, lines: text.split('\n').length });
    }
  }
}
for (const r of ROOTS) walk(r);
console.log(`含 OBB 的 JSON 检查数: ${checked}`);
console.log(`疑似截断/未闭合: ${broken.length}`);
for (const b of broken.slice(0, 20)) console.log(`  !! ${b.p}  行数=${b.lines} 字节=${b.len}\n     ${b.err}`);

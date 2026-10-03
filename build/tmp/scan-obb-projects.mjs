// scan-obb-projects.mjs — 在 GScode / superb 源码树里找"含 OBB 的可疑 JSON"
import fs from 'node:fs';
import path from 'node:path';

const ROOTS = ['D:/minecraft/modp/GScode/src', 'D:/minecraft/modp/superb/src'];
let n = 0;
const bad = [];

function walk(dir) {
  let entries = [];
  try { entries = fs.readdirSync(dir, { withFileTypes: true }); } catch { return; }
  for (const e of entries) {
    const p = path.join(dir, e.name);
    if (e.isDirectory()) { walk(p); continue; }
    if (!e.name.endsWith('.json')) continue;
    let t;
    try { t = fs.readFileSync(p, 'utf8'); } catch { continue; }
    if (!t.includes('"OBB"')) continue;
    n++;
    const s = t.trimEnd();
    if (!s.endsWith('}')) {
      bad.push(`${p}  (不以 } 结尾，尾部=${JSON.stringify(s.slice(-30))}，字节=${t.length})`);
      continue;
    }
    try { JSON.parse(s.replace(/,(\s*[}\]])/g, '$1')); }
    catch (err) { bad.push(`${p}  (宽松解析失败: ${err.message.slice(0, 70)})`); }
  }
}

for (const r of ROOTS) walk(r);
console.log(`含 OBB 的 JSON: ${n}`);
console.log(`可疑文件: ${bad.length}`);
bad.slice(0, 15).forEach(x => console.log('  !! ' + x));

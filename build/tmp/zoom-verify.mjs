// zoom-verify.mjs — 校验 DefaultZoom 改写：严格 JSON、目标值、除 DefaultZoom 外无差异
import fs from 'node:fs';
import path from 'node:path';

const OUT = process.argv[2] ?? 'zoom';
const man = JSON.parse(fs.readFileSync(path.join(OUT, 'manifest.json'), 'utf8'));
const TARGET = man.target;

function matchBrace(text, open) {
  let depth = 0, inStr = false, esc = false;
  for (let j = open; j < text.length; j++) {
    const ch = text[j];
    if (inStr) { if (esc) esc = false; else if (ch === '\\') esc = true; else if (ch === '"') inStr = false; continue; }
    if (ch === '"') { inStr = true; continue; }
    if (ch === '{') depth++;
    else if (ch === '}') { depth--; if (depth === 0) return j; }
  }
  return -1;
}
function findWeaponTables(text) {
  const out = []; const re = /"Weapons"\s*:\s*\{/g; let m;
  while ((m = re.exec(text)) !== null) {
    const open = text.indexOf('{', m.index + m[0].length - 1);
    const close = matchBrace(text, open);
    if (close > 0) out.push({ open, close });
  }
  return out;
}
/** 递归删除 DefaultZoom 字段后比较（判断“除 DefaultZoom 外无差异”） */
function stripZooms(obj) {
  if (Array.isArray(obj)) return obj.map(stripZooms);
  if (obj && typeof obj === 'object') {
    const o = {};
    for (const [k, v] of Object.entries(obj)) { if (k === 'DefaultZoom') continue; o[k] = stripZooms(v); }
    return o;
  }
  return obj;
}

let ok = 0, bad = 0;
for (const f of man.files) {
  if (f.status !== 'ok') { console.log(`SKIP ${f.id} (${f.status})`); continue; }
  const before = fs.readFileSync(path.join(OUT, 'before', `${f.id.split(':')[0]}__${f.id.split(':')[1]}.json`), 'utf8');
  const after = fs.readFileSync(path.join(OUT, 'after', `${f.id.split(':')[0]}__${f.id.split(':')[1]}.json`), 'utf8');
  const problems = [];
  let j = null;
  try { j = JSON.parse(after); } catch (e) { problems.push('after 非法 JSON: ' + e.message); }
  if (j) {
    const tables = findWeaponTables(after);
    let weapons = 0;
    for (const [, v] of Object.entries(j.Weapons ?? {})) {
      if (!v || typeof v !== 'object' || Array.isArray(v)) continue;
      if (Object.keys(v).length === 0) continue; // 空占位
      weapons++;
      if (v.DefaultZoom !== TARGET) problems.push(`武器 ${JSON.stringify(Object.keys(v).slice(0, 2))} 的 DefaultZoom=${v.DefaultZoom}（应为 ${TARGET}）`);
    }
    if (!tables.length) problems.push('没找到对象形式武器表');
    // 除 DefaultZoom 外无差异
    const sa = JSON.stringify(stripZooms(JSON.parse(after)));
    const sb = JSON.stringify(stripZooms(JSON.parse(before)));
    if (sa !== sb) problems.push('除 DefaultZoom 外内容有差异');
    if (problems.length) { bad++; console.log(`[BAD] ${f.id}\n   - ` + problems.join('\n   - ')); }
    else { ok++; console.log(`[OK]  ${f.id.padEnd(32)} 武器 ${weapons} 个全部 = ${TARGET}`); }
  } else { bad++; console.log(`[BAD] ${f.id}\n   - ` + problems.join('\n   - ')); }
}
console.log(`\n校验通过 ${ok}，异常 ${bad}`);

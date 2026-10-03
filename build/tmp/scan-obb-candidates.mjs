// scan-obb-candidates.mjs — 用宽松解析（去注释+尾逗号）重扫从所有模组 jar 抽出的 OBB 候选
import fs from 'node:fs';
import path from 'node:path';

const DIR = 'obb-candidates';
function lenient(text) {
  let out = '', inStr = false, esc = false;
  for (let i = 0; i < text.length; i++) {
    const c = text[i];
    if (inStr) { out += c; if (esc) esc = false; else if (c === '\\') esc = true; else if (c === '"') inStr = false; continue; }
    if (c === '"') { inStr = true; out += c; continue; }
    if (c === '/' && text[i + 1] === '/') { while (i < text.length && text[i] !== '\n') i++; continue; }
    if (c === '/' && text[i + 1] === '*') { const e = text.indexOf('*/', i + 2); i = e < 0 ? text.length : e + 1; continue; }
    out += c;
  }
  return JSON.parse(out.replace(/,(\s*[}\]])/g, '$1'));
}
const files = fs.readdirSync(DIR);
const bad = [];
for (const f of files) {
  const t = fs.readFileSync(path.join(DIR, f), 'utf8');
  try { lenient(t); } catch (err) { bad.push(`${f}  → ${err.message.slice(0, 90)}`); }
}
console.log(`候选文件: ${files.length}`);
console.log(`宽松解析仍失败: ${bad.length}`);
bad.slice(0, 20).forEach(x => console.log('  !! ' + x));

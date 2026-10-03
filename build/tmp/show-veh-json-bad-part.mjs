// show-veh-json-bad-part.mjs — 定位坏 JSON 的具体行
import fs from 'node:fs';

const f = process.argv[2];
const t = fs.readFileSync(f, 'utf8');
const lines = t.split('\n');
try { JSON.parse(t); console.log('JSON OK'); }
catch (e) { console.log('FAIL:', e.message); }

// 用逐行累加方式找第一个非法位置（简单括号/引号状态机）
let inStr = false, esc = false, depth = 0, bad = -1;
for (let i = 0; i < t.length; i++) {
  const ch = t[i];
  if (inStr) { if (esc) esc = false; else if (ch === '\\') esc = true; else if (ch === '"') inStr = false; continue; }
  if (ch === '"') { inStr = true; continue; }
  if (ch === '[' || ch === '{') depth++;
  else if (ch === ']' || ch === '}') { depth--; if (depth < 0) { bad = i; break; } }
}
console.log(`最终 depth=${depth} inStr=${inStr}${bad >= 0 ? ' 首个多余闭合在 ' + bad : ''}`);
if (bad >= 0) {
  const line = t.slice(0, bad).split('\n').length;
  console.log(`位置 ${bad} ≈ 第 ${line} 行`);
  console.log('---- 附近 12 行 ----');
  for (let i = Math.max(0, line - 8); i < Math.min(lines.length, line + 4); i++) {
    console.log(String(i + 1).padStart(4) + ': ' + lines[i]);
  }
}

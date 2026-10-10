// find-trailing-commas.mjs — 找出 JSON 文本里的尾随逗号（, 后紧跟 ] 或 }）
import fs from 'node:fs';
const f = process.argv[2];
const t = fs.readFileSync(f, 'utf8');
const re = /,(\s*[\]\}])/g;
let m, n = 0;
while ((m = re.exec(t)) !== null) {
  n++;
  const upto = t.slice(0, m.index);
  const line = upto.split('\n').length;
  const col = m.index - upto.lastIndexOf('\n');
  const lines = t.split('\n');
  console.log(`--- 第 ${n} 处: 行 ${line} 列 ${col} ---`);
  for (let i = Math.max(0, line - 4); i < Math.min(lines.length, line + 2); i++) {
    console.log(String(i + 1).padStart(4) + ': ' + lines[i]);
  }
}
console.log(`共 ${n} 处尾随逗号；文件 ${t.length} 字符`);

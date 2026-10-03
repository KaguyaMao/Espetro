// grep-short.mjs — 在大文件里搜关键词并打印每条命中（截断到 180 字符，避免超长行刷屏）
// 用法: node grep-short.mjs <文件> <正则> [显示条数=40]
import fs from 'node:fs';
const [file, pat, limit] = process.argv.slice(2);
const re = new RegExp(pat);
const lines = fs.readFileSync(file, 'utf8').split('\n');
let n = 0;
for (let i = 0; i < lines.length && n < Number(limit ?? 40); i++) {
  const l = lines[i];
  if (!re.test(l)) continue;
  n++;
  const s = l.replace(/\s+/g, ' ').trim();
  console.log(`${i + 1}: ${s.length > 180 ? s.slice(0, 180) + ' …' : s}`);
}
console.log(`--- ${n} 条命中（${file}）`);

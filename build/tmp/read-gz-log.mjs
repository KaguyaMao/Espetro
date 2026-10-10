// read-gz-log.mjs — 解压 .log.gz 并输出尾部 / 关键词命中
// 用法: node read-gz-log.mjs <文件.gz> [正则] [tailN=0]
import fs from 'node:fs';
import zlib from 'node:zlib';

const [file, pat, tailN] = process.argv.slice(2);
const buf = zlib.gunzipSync(fs.readFileSync(file));
const text = buf.toString('utf8');
const lines = text.split('\n');
console.log(`=== ${file}：${buf.length} B，${lines.length} 行 ===`);
if (pat) {
  const re = new RegExp(pat);
  let n = 0;
  for (const l of lines) {
    if (!re.test(l)) continue;
    n++;
    const s = l.replace(/\s+/g, ' ').trim();
    console.log(s.length > 260 ? s.slice(0, 260) + ' …' : s);
  }
  console.log(`--- 命中 ${n} 行（/${pat}/）---`);
}
const n = Number(tailN ?? 0);
if (n > 0) {
  console.log(`--- 末尾 ${n} 行 ---`);
  for (const l of lines.slice(-n)) {
    const s = l.replace(/\s+/g, ' ').trim();
    if (s) console.log(s.length > 220 ? s.slice(0, 220) + ' …' : s);
  }
}

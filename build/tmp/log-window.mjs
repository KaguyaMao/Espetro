// log-window.mjs — 按时间窗口过滤日志，输出与关键词匹配的行
// 用法: node log-window.mjs <logFile> <起始HH:MM> <结束HH:MM> [正则]
import fs from 'node:fs';
const [file, from, to, pat] = process.argv.slice(2);
const re = pat ? new RegExp(pat) : /ERROR|WARN|Exception|squad|Squad|小队|Espetro|voicechat/;
const lines = fs.readFileSync(file, 'utf8').split('\n');
let n = 0;
for (const l of lines) {
  const m = /^\[(\d{1,2})\S{0,4}(\d{4}) (\d{2}:\d{2}:\d{2})\./.exec(l);
  if (!m) continue;
  const t = m[3];
  if (t < from || t > to) continue;
  if (!re.test(l)) continue;
  n++;
  const s = l.replace(/\s+/g, ' ').trim();
  console.log(s.length > 300 ? s.slice(0, 300) + ' …' : s);
}
console.log(`--- ${n} 行（${from}~${to}，${file}）`);

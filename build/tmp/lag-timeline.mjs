// lag-timeline.mjs — 列出全部卡顿时刻/间隔，并与玩家在线时间轴对照
import fs from 'node:fs';
const l = fs.readFileSync(process.argv[2] ?? 'server-check.log', 'utf8').split('\n');

console.log('=== 全部 "Can not keep up" 卡顿时刻 ===');
let prev = null, n = 0;
for (const x of l) {
  const m = /^\[\d{1,2}\S{0,4}\d{4} (\d{2}):(\d{2}):(\d{2})\.(\d{3}).*Is the server overloaded\? Running (\d+)ms or (\d+) ticks behind/.exec(x);
  if (!m) continue;
  const t = (+m[1]) * 3600 + (+m[2]) * 60 + (+m[3]) + (+m[4]) / 1000;
  const gap = prev === null ? '-' : (t - prev).toFixed(1);
  prev = t; n++;
  console.log(`  ${m[1]}:${m[2]}:${m[3]}  ${m[5]}ms / ${m[6]} ticks behind   距上次=${gap}s`);
}
console.log(`共 ${n} 次`);

console.log('\n=== 玩家进出时间轴 ===');
for (const x of l) {
  if (!/joined the game|left the game|lost connection/.test(x)) continue;
  const m = /^\[(\d{1,2}\S{0,4}\d{4} \d{2}:\d{2}:\d{2})/.exec(x);
  const tail = x.replace(/\s+/g, ' ').trim().split('] ').slice(-1)[0];
  console.log(`  ${m ? m[1] : '?'}  ${tail}`);
}

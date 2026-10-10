// read-radio-policy.mjs — 打印两个方法的实现
import fs from 'node:fs';
const l = fs.readFileSync('src/main/java/org/espetro/bastion/BastionManager.java', 'utf8').split('\n');
for (const name of ['wouldRadioCoverageOverlap', 'getMinimumRadioCenterDistance']) {
  let s = -1;
  for (let i = 0; i < l.length; i++) {
    if (l[i].includes(name + '(') && (l[i].includes('public') || l[i].includes('private'))) { s = i; break; }
  }
  console.log('===== ' + name + ' (行 ' + (s + 1) + ') =====');
  if (s < 0) continue;
  for (let i = s - 3; i < s + 26 && i < l.length; i++) {
    const t = (l[i] ?? '').replace(/\s+$/, '');
    if (t.trim()) console.log((i + 1) + ': ' + t.slice(0, 168));
  }
}

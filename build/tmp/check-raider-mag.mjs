// check-raider-mag.mjs — 核对奇袭兵各变体「枪内存弹匣」与「备用弹匣」的族/容量是否一致
// 用法: node check-raider-mag.mjs <faction.json> [classKey]
import fs from 'node:fs';

const file = process.argv[2];
const cls = process.argv[3] ?? 'PLA_112_RAIDER';
const j = JSON.parse(fs.readFileSync(file, 'utf8').replace(/,(\s*[}\]])/g, '$1'));
const c = j.classes?.[cls];
if (!c) { console.log('no class', cls); process.exit(1); }

function fams(s) {
  return [...String(s).matchAll(/MagazineFamily:"([^"]+)",MaxCapacity:(\d+)/g)].map((m) => m[1] + '/' + m[2]);
}
function tailCount(s) {
  const m = String(s).match(/\s(\d+)\s*$/);
  return m ? m[1] : '?';
}

console.log(`# ${file}  职业 ${cls} 「${c.name}」`);
for (const [vk, va] of Object.entries(c.variants || {})) {
  const cmds = va.commands || [];
  const gunFam = fams(cmds[0] ?? '');
  console.log(`== 变体 ${vk}`);
  console.log(`   #0 枪内存弹匣 family=${JSON.stringify(gunFam)}`);
  for (let i = 1; i < cmds.length; i++) {
    const s = String(cmds[i]);
    if (!/taczmagazines:magazine/.test(s)) continue;
    console.log(`   #${i} 备用弹匣 family=${JSON.stringify(fams(s))} 数量=${tailCount(s)}`);
  }
}

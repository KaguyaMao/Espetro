// fix-class-icons.mjs — 按“看枪不看职业性质”修正配置里的 icon slug
// 用法: node fix-class-icons.mjs [--write]
// 输入: build/tmp/fxall-new/（当前服务器状态，已含弹匣改造）
// 输出: build/tmp/fxall-icons/
import fs from 'fs';

const SRC = 'D:/minecraft/modp/Espetro/build/tmp/fxall-new/';
const DST = 'D:/minecraft/modp/Espetro/build/tmp/fxall-icons/';
const WRITE = process.argv.includes('--write');

// 规则：职业名 + 当前 icon -> 目标 icon（同一条规则只作用于匹配项，避免误伤）
const RULES = [
  { name: '侦察兵', from: null, to: 'scout', why: '侦察兵独立图标（原先误用精确射手/轻反坦克图）' },
  { name: '战斗工兵', from: null, to: 'engineer', why: '战斗工兵改用 engineer 图（原 sapper）' },
  { name: '班组机枪', from: 'machine_gunner', to: 'automatic_rifleman', why: '班组机枪=M249/RPK-74M/QJB-201（班用机枪）' },
  { name: '自动步枪', from: 'automatic_rifleman', to: 'rifleman', why: '自动步枪=M4A1/QBZ-191（突击步枪）与其它编制一致' }
];

fs.mkdirSync(DST, { recursive: true });
const report = [];
let changed = 0;

for (const f of fs.readdirSync(SRC).filter((x) => x.endsWith('.json')).sort()) {
  const j = JSON.parse(fs.readFileSync(SRC + f, 'utf8'));
  const hits = [];
  for (const [cid, cls] of Object.entries(j.classes || {})) {
    for (const rule of RULES) {
      if (cls.name !== rule.name) continue;
      const cur = cls.icon;
      if (rule.from !== null && cur !== rule.from) continue;
      if (cur === rule.to) continue;
      cls.icon = rule.to;
      hits.push(`${cid}: ${cur} → ${rule.to}`);
      changed++;
    }
  }
  if (hits.length) report.push(`${f}\n  ` + hits.join('\n  '));
  if (WRITE) fs.writeFileSync(DST + f, JSON.stringify(j, null, 2) + '\n', 'utf8');
}

console.log(report.join('\n'));
console.log(`\n共修改 ${changed} 处 → ${WRITE ? '已写出 ' + DST : '（演练，未写盘）'}`);
if (WRITE) {
  // 校验：职业数不变、resupply 条目不变
  let clsA = 0, clsB = 0, itemsA = 0, itemsB = 0;
  for (const f of fs.readdirSync(SRC).filter((x) => x.endsWith('.json'))) {
    const a = JSON.parse(fs.readFileSync(SRC + f, 'utf8'));
    const b = JSON.parse(fs.readFileSync(DST + f, 'utf8'));
    clsA += Object.keys(a.classes || {}).length;
    clsB += Object.keys(b.classes || {}).length;
    for (const j of [a, b]) for (const c of Object.values(j.classes || {})) for (const v of Object.values(c.variants || {}))
      { if (j === a) itemsA += ((v.resupply || {}).items || []).length; else itemsB += ((v.resupply || {}).items || []).length; }
  }
  console.log(`校验: 职业 ${clsA}/${clsB}  补给条目 ${itemsA}/${itemsB}`);
}

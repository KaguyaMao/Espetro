// lint-factions.mjs — 复刻 FactionDataLoader 的拒绝规则，离线体检
import fs from 'fs';
const FILES = [
  'pla_112th_brigade', 'pla_112th_brigade_mesh', 'pla_118th_brigade', 'pla_195th',
  'ru_205th', 'ru_3th', 'ru_49th', 'ru_6th_tank',
  'us_1th_ar', 'us_1th_ri', 'us_2nd_stryker', 'us_redone'
];
const dir = process.argv[2] || '.';
let bad = 0;
for (const f of FILES) {
  const p = dir === '.' ? `fix-${f}.json` : `${dir}${f}.json`;
  if (!fs.existsSync(p)) { console.log(`-- ${f.padEnd(24)} 无 ${p}（跳过）`); continue; }
  const j = JSON.parse(fs.readFileSync(p, 'utf8'));
  const issues = [];
  let classes = 0, variants = 0;
  for (const [cid, cls] of Object.entries(j.classes || {})) {
    classes++;
    let sum = 0;
    const vk = Object.keys(cls.variants || {});
    if (vk.length === 0) issues.push(`${cid}: 没有变体`);
    for (const [k, v] of Object.entries(cls.variants || {})) {
      variants++;
      if (!(v.maxPlayers >= 1)) issues.push(`${cid}[${k}]: maxPlayers=${v.maxPlayers} 必须≥1`);
      sum += v.maxPlayers || 0;
      const items = (v.resupply && v.resupply.items) || [];
      if (items.length === 0) issues.push(`${cid}[${k}]: resupply.items 为空（会被拒载）`);
      if (items.length > 64) issues.push(`${cid}[${k}]: resupply.items=${items.length} 超过 64`);
      // 每条补给项必须有 id
      for (const it of items) if (!it || typeof it.id !== 'string' || !it.id) issues.push(`${cid}[${k}]: 补给项缺 id`);
    }
    if (cls.strict_count && sum !== cls.maxPlayers) {
      issues.push(`${cid}: strict_count=true 时变体上限总和 ${sum} != 职业上限 ${cls.maxPlayers}  ← 会被拒载`);
    }
  }
  const mark = issues.length ? 'XX' : 'OK';
  if (issues.length) bad++;
  console.log(`${mark} ${f.padEnd(24)} 职业=${String(classes).padStart(3)} 变体=${String(variants).padStart(3)}${issues.length ? '  问题' + issues.length : ''}`);
  issues.forEach(x => console.log('     - ' + x));
}
console.log(bad === 0 ? '\n=== 全部通过加载器规则 ===' : `\n=== ${bad} 个文件存在会被拒载的问题 ===`);

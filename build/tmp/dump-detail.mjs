// dump-detail.mjs — 轻筒/重筒/工兵 的完整给予与补给明细（含 SNBT）
import fs from 'fs';

const FILES = [
  'pla_112th_brigade', 'pla_112th_brigade_mesh', 'pla_118th_brigade', 'pla_195th',
  'ru_205th', 'ru_3th', 'ru_49th', 'ru_6th_tank',
  'us_1th_ar', 'us_1th_ri', 'us_2nd_stryker', 'us_redone'
];

for (const f of FILES) {
  const j = JSON.parse(fs.readFileSync(`out-${f}.json`, 'utf8'));
  for (const [cid, cls] of Object.entries(j.classes)) {
    if (!/(ANTITANK|ENGINEER)/.test(cid)) continue;
    console.log(`===== ${f} / ${cid} (${cls.name})`);
    for (const [vk, v] of Object.entries(cls.variants || {})) {
      console.log(`  [${vk}] ${v.name}  ammo_cost=${v.resupply ? v.resupply.ammo_cost : '-'}`);
      console.log('    给予:');
      for (const s of (v.commands || [])) {
        if (/^(tacz:|taczmagazines:|cib:|superbwarfare:)/.test(s)) {
          console.log('      ' + (s.length > 150 ? s.slice(0, 150) + '…' : s));
        }
      }
      const items = (v.resupply && v.resupply.items) || [];
      console.log(`    补给(${items.length}):`);
      for (const it of items) {
        console.log(`      id=${it.id}  count=${it.count}  max=${it.max}`);
      }
    }
  }
}

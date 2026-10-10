// check-engineer.mjs — 各编制工兵：C4 / 反坦克地雷 是否在给予与补给里
import fs from 'fs';
const FILES = [
  'pla_112th_brigade', 'pla_112th_brigade_mesh', 'pla_118th_brigade', 'pla_195th',
  'ru_205th', 'ru_3th', 'ru_49th', 'ru_6th_tank',
  'us_1th_ar', 'us_1th_ri', 'us_2nd_stryker', 'us_redone'
];
const rows = [];
for (const f of FILES) {
  const j = JSON.parse(fs.readFileSync(`out-${f}.json`, 'utf8'));
  for (const [cid, cls] of Object.entries(j.classes)) {
    if (!/ENGINEER/.test(cid)) continue;
    for (const [vk, v] of Object.entries(cls.variants || {})) {
      const cmds = v.commands || [];
      const res = (v.resupply && v.resupply.items) || [];
      const c4Give = cmds.filter(s => s.startsWith('superbwarfare:c4_bomb'));
      const mineGive = cmds.filter(s => s.startsWith('superbwarfare:tm_62') || s.startsWith('superbwarfare:m15'));
      const c4Res = res.filter(it => it.id.startsWith('superbwarfare:c4_bomb'));
      const mineRes = res.filter(it => it.id.startsWith('superbwarfare:tm_62') || it.id.startsWith('superbwarfare:m15'));
      console.log(`${f} / ${cid} [${vk}]`);
      console.log(`   给予 C4: ${c4Give.length ? c4Give.join(' | ') : '(无)'}`);
      console.log(`   给予 地雷: ${mineGive.length ? mineGive.join(' | ') : '(无)'}`);
      console.log(`   补给 C4: ${c4Res.length ? JSON.stringify(c4Res) : '(无)'}   补给 地雷: ${mineRes.length ? JSON.stringify(mineRes) : '(无)'}   ammo_cost=${v.resupply ? v.resupply.ammo_cost : '-'}`);
      console.log(`   现有补给项: ${res.map(it => it.id.split('{')[0] + ' x' + it.count + '/max' + it.max).join(' , ')}`);
    }
  }
}

// cmp-at-resupply.mjs — 轻筒/重筒 补给表里的"筒/弹"明细
import fs from 'fs';
const FILES = ['pla_112th_brigade', 'ru_205th', 'us_1th_ar', 'ru_49th', 'us_redone'];
for (const f of FILES) {
  const j = JSON.parse(fs.readFileSync(`out-${f}.json`, 'utf8'));
  for (const [cid, cls] of Object.entries(j.classes)) {
    if (!/ANTITANK/.test(cid)) continue;
    for (const [vk, v] of Object.entries(cls.variants || {})) {
      const given = (v.commands || []).filter(s => s.startsWith('tacz:modern_kinetic_gun') || s.startsWith('tacz:ammo'))
        .map(s => {
          const g = (s.match(/GunId:"([^"]+)"/) || [])[1];
          const a = (s.match(/AmmoId:"([^"]+)"/) || [])[1];
          return g ? 'gun:' + g : a ? 'ammo:' + a : s;
        });
      const res = ((v.resupply && v.resupply.items) || []).map(it => {
        const g = (it.id.match(/GunId:"([^"]+)"/) || [])[1];
        const a = (it.id.match(/AmmoId:"([^"]+)"/) || [])[1];
        return (g ? 'gun:' + g : a ? 'ammo:' + a : it.id.split('{')[0]) + ' x' + it.count + '/max' + it.max;
      });
      console.log(`${f} / ${cid} [${vk}] ${v.name}`);
      console.log('   给予: ' + given.join(' , '));
      console.log('   补给: ' + res.join(' , '));
    }
  }
}

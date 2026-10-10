// dump-us-mg.mjs
import fs from 'fs';
const j = JSON.parse(fs.readFileSync('out-us_1th_ar.json', 'utf8'));
for (const cid of ['US_1th_arma_MMG', 'US_1th_arma_SQUADMG']) {
  const cls = j.classes[cid];
  console.log('===== ' + cid + ' (' + cls.name + ') row=' + cls.row + ' max=' + cls.maxPlayers + '/squad=' + cls.max_per_squad);
  for (const [vk, v] of Object.entries(cls.variants)) {
    console.log('  [' + vk + '] ' + v.name);
    for (const s of (v.commands || []).filter(x => /tacz:modern_kinetic_gun|taczmagazines/.test(x))) {
      console.log('     ' + s);
    }
    const res = ((v.resupply && v.resupply.items) || []).map(it => {
      const a = (it.id.match(/AmmoId:"([^"]+)"/) || [])[1];
      const g = (it.id.match(/GunId:"([^"]+)"/) || [])[1];
      return (g ? 'gun:' + g : a ? 'ammo:' + a : it.id.split('{')[0]) + ' x' + it.count + '/max' + it.max;
    });
    console.log('     补给: ' + res.join(' , '));
  }
}

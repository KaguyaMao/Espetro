// cmp-antitank.mjs — 轻筒/重筒：给予的筒与火箭弹 vs 补给表覆盖情况
import fs from 'fs';

const FILES = [
  'pla_112th_brigade', 'pla_112th_brigade_mesh', 'pla_118th_brigade', 'pla_195th',
  'ru_205th', 'ru_3th', 'ru_49th', 'ru_6th_tank',
  'us_1th_ar', 'us_1th_ri', 'us_2nd_stryker', 'us_redone'
];

function guns(cmds) {
  return (cmds || []).filter(s => s.startsWith('tacz:modern_kinetic_gun'))
    .map(s => {
      const id = (s.match(/GunId:"([^"]+)"/) || [])[1];
      const ammo = (s.match(/GunCurrentAmmoCount:(\d+)/) || [])[1];
      return id + '(弹' + ammo + ')';
    });
}
function rockets(cmds) {
  return (cmds || []).filter(s => s.startsWith('tacz:ammo'))
    .map(s => (s.match(/AmmoId:"([^"]+)"/) || [])[1] + '×' + (s.split(/\s+/)[1] || 1));
}
function resupplyNames(v) {
  return ((v.resupply && v.resupply.items) || []).map(it => {
    const id = it.id;
    const ammoId = (id.match(/AmmoId:"([^"]+)"/) || [])[1];
    const gunId = (id.match(/GunId:"([^"]+)"/) || [])[1];
    const base = id.split('{')[0];
    return (gunId ? 'gun:' + gunId : ammoId ? 'ammo:' + ammoId : base)
      + '×' + it.count + '/max' + it.max;
  });
}

for (const f of FILES) {
  const j = JSON.parse(fs.readFileSync(`out-${f}.json`, 'utf8'));
  for (const [cid, cls] of Object.entries(j.classes)) {
    if (!/ANTITANK/.test(cid)) continue;
    console.log(`===== ${f} / ${cid} (${cls.name})`);
    for (const [vk, v] of Object.entries(cls.variants || {})) {
      const givenGuns = guns(v.commands);
      const givenRockets = rockets(v.commands);
      const res = resupplyNames(v);
      const resGuns = res.filter(x => x.startsWith('gun:')).map(x => x.slice(4));
      const resAmmo = res.filter(x => x.startsWith('ammo:')).map(x => x.slice(5));
      // 给予的火箭弹里，哪些没进补给表
      const missing = givenRockets.filter(r => !resAmmo.some(a => a.startsWith(r.split('×')[0])));
      // 给予的筒里，哪些没进补给表（一次性筒通常可补）
      const missingGuns = givenGuns.filter(g => !resGuns.some(a => a.startsWith(g.split('(')[0])));
      console.log(`  [${vk}] ${v.name}`);
      console.log(`     给予筒: ${givenGuns.join(', ') || '(无)'}`);
      console.log(`     给予火箭弹: ${givenRockets.join(', ') || '(无)'}`);
      console.log(`     补给: ${res.join(' , ')}`);
      if (missing.length) console.log(`     !! 补给表缺火箭弹: ${missing.join(', ')}`);
      if (missingGuns.length) console.log(`     ~~ 补给表未覆盖筒: ${missingGuns.join(', ')}`);
    }
  }
}

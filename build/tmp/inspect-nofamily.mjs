// inspect-nofamily.mjs — 查看某弹药在该变体无弹匣族的具体职业/装备构成
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const DIR = 'D:/minecraft/modp/Espetro/build/tmp/fxall/';
const TARGET_AMMO = process.argv[2] || 'tacz:58x42';
const FILES = fs.readdirSync(DIR).filter((f) => f.endsWith('.json'));

const MAG = /taczmagazines:magazine\s*\{([^}]*)\}/g;
const FAM = /MagazineFamily\s*:\s*"([^"]+)"/;
const AMMO_IN = /AmmoId\s*:\s*"([^"]+)"/;
const CAP = /MaxCapacity\s*:\s*(\d+)/;

function mags(text) {
  const out = [];
  let m; MAG.lastIndex = 0;
  while ((m = MAG.exec(text)) !== null) {
    const b = m[1];
    out.push(`${(b.match(AMMO_IN) || [])[1]}#${(b.match(FAM) || [])[1]}|${(b.match(CAP) || [])[1]}`);
  }
  return out;
}

let shown = 0;
for (const f of FILES) {
  const j = JSON.parse(fs.readFileSync(DIR + f, 'utf8'));
  for (const [cid, cls] of Object.entries(j.classes || {})) {
    for (const [vid, v] of Object.entries(cls.variants || {})) {
      const cmds = v.commands || [];
      const pairs = mags(cmds.join('\n'));
      const hasFamily = pairs.some((p) => p.startsWith(TARGET_AMMO + '#'));
      const resupply = ((v.resupply && v.resupply.items) || [])
        .filter((it) => String(it.id || '').includes(TARGET_AMMO));
      if (hasFamily || resupply.length === 0) continue;

      console.log('--------------------------------------------------');
      console.log(`${f} / ${cid} / 变体=${vid}`);
      console.log('  补给条目: ' + resupply.map((it) => `${it.id} count=${it.count} max=${it.max}`).join(' ; '));
      const guns = cmds.filter((c) => /modern_kinetic_gun/.test(c))
        .map((c) => `${(c.match(/GunId:"([^"]+)"/) || [])[1]}`);
      console.log('  枪械: ' + guns.join(', '));
      console.log('  该变体所有弹匣: ' + [...new Set(pairs)].join(' | '));
      if (++shown >= 8) { console.log('\n...(仅显示前 8 例)'); process.exit(0); }
    }
  }
}
console.log('shown=' + shown);

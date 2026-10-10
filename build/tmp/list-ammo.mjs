// Full inventory: every weapon + ammo id across the 47 server vehicles
import fs from 'node:fs';
import path from 'node:path';

const VDIR = 'D:/minecraft/modp/Espetro/build/tmp/srv-vehicles-fresh';
const files = fs.readdirSync(VDIR).filter(f => f.endsWith('.json') && f !== 'manifest.json');
const ammoIds = new Map(); // ammoId -> Set of "vehicle/weapon(dmg)"
const projIds = new Map();

for (const f of files) {
  let j; try { j = JSON.parse(fs.readFileSync(path.join(VDIR, f), 'utf8')); } catch { continue; }
  const W = j.Weapons;
  if (!W || typeof W !== 'object') continue;
  for (const [wn, w] of Object.entries(W)) {
    if (!w || typeof w !== 'object') continue;
    const proj = w.Projectile ?? '(none)';
    if (!projIds.has(proj)) projIds.set(proj, []);
    projIds.get(proj).push(`${j.ID}/${wn}`);
    const ammos = Array.isArray(w.AmmoType) ? w.AmmoType : (w.AmmoType ? [w.AmmoType] : []);
    for (const a of ammos) {
      if (typeof a === 'string') {
        if (!ammoIds.has(a)) ammoIds.set(a, []);
        ammoIds.get(a).push(`${j.ID}/${wn}[${w.Damage ?? '-'}]`);
      } else {
        const o = a.Override ?? {};
        if (!ammoIds.has(a.Ammo)) ammoIds.set(a.Ammo, []);
        ammoIds.get(a.Ammo).push(`${j.ID}/${wn}[${o.Damage ?? w.Damage ?? '-'}]${o.IsHighExplosiveProjectile === true ? '(HE)' : ''}`);
      }
    }
  }
}
console.log('=== 弹药 id 清单 ===');
for (const [k, v] of [...ammoIds.entries()].sort()) console.log(`${k}  ×${v.length}\n    ${v.join('\n    ')}`);
console.log('\n=== 弹丸（Projectile）清单 ===');
for (const [k, v] of [...projIds.entries()].sort()) console.log(`${k}  ×${v.length}`);

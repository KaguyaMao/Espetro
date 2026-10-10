import fs from 'node:fs';
const lp = (t) => JSON.parse(fs.readFileSync(t, 'utf8').replace(/,(\s*[}\]])/g, '$1'));
for (const f of ['dragonrise__zbl08.json', 'dragonrise__ztz99a.json', 'dragonrise__m1126.json', 'dragonrise__zbd04a.json']) {
  const j = lp('./veh-now/' + f);
  for (const [k, w] of Object.entries(j.Weapons || {})) {
    if (!/Cannon|Missile/.test(k)) continue;
    console.log(`${j.ID} | ${k} | proj=${w.Projectile} | dmg=${w.Damage} | rpm=${w.RPM} | mag=${w.Magazine} | reloadTicks=${w.EmptyReloadTime} | AP=${w.IsArmorPiercingProjectile} | HE=${w.IsHighExplosiveProjectile}`);
  }
}

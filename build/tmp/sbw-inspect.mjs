// sbw-inspect.mjs — 打印 SBW 载具文件的关键战斗字段
import fs from 'fs';

const FILES = [
  ['fcp', 'D:/minecraft/modp/Espetro/build/tmp/sbw-veh/fcp/stryker_m2.json'],
  ['fcp', 'D:/minecraft/modp/Espetro/build/tmp/sbw-veh/fcp/stryker_mgs.json'],
  ['fcp', 'D:/minecraft/modp/Espetro/build/tmp/sbw-veh/fcp/stryker_dragoon.json'],
  ['fcp', 'D:/minecraft/modp/Espetro/build/tmp/sbw-veh/fcp/stryker_mortar.json'],
  ['dr ', 'D:/minecraft/modp/Espetro/build/tmp/sbw-veh/dr/m1126.json'],
  ['dr ', 'D:/minecraft/modp/Espetro/build/tmp/sbw-veh/dr/m1128.json'],
  ['dr ', 'D:/minecraft/modp/Espetro/build/tmp/sbw-veh/dr/m1296.json']
];

const WEAPON_KEYS = ['RPM', 'Damage', 'BypassesArmor', 'Velocity', 'Magazine', 'EmptyReloadTime',
  'HeatPerShoot', 'NaturalCooldown', 'Spread', 'RecoilTime', 'RecoilForce', 'DefaultFireMode',
  'AvailableFireModes', 'Projectile', 'AmmoType', 'Name', 'Icon', 'Crosshair', 'SoundRadius',
  'ExplosionRadius', 'ExplosionDamage', 'ExplosionDestroyBlocks', 'Penetration', 'Headshot',
  'DefaultZoom', 'ShootAnimationTime', 'ReloadTime', 'ChargeTime', 'BurstAmount', 'BurstCooldown'];

for (const [tag, p] of FILES) {
  const j = JSON.parse(fs.readFileSync(p, 'utf8'));
  console.log(`\n══════ ${tag} ${p.split('/').pop()}  ID=${j.ID}`);
  console.log(`  MaxHealth=${j.MaxHealth}  MaxEnergy=${j.MaxEnergy}  Type=${j.Type}  Mass=${j.Mass}  HudType=${j.HudType}`);
  console.log(`  DestroyInfo=${JSON.stringify(j.DestroyInfo)}`);
  console.log(`  CollisionLevel=${JSON.stringify(j.CollisionLevel)}`);
  const dm = j.DamageModifiers ?? [];
  console.log(`  DamageModifiers(${dm.length}): ${dm.join(' | ')}`);
  const weapons = j.Weapons ?? {};
  console.log(`  武器键: ${JSON.stringify(Object.keys(weapons))}`);
  for (const [wn, w] of Object.entries(weapons)) {
    const stats = [];
    for (const k of WEAPON_KEYS) if (w[k] !== undefined) stats.push(`${k}=${typeof w[k] === 'object' ? JSON.stringify(w[k]) : w[k]}`);
    console.log(`    [${wn}] ${stats.join(' ')}`);
    const extra = Object.keys(w).filter((k) => !WEAPON_KEYS.includes(k));
    console.log(`        其他字段: ${extra.join(', ')}`);
  }
  // 座位引用的武器
  const refs = (j.Seats ?? []).map((s, i) => s.Weapons ? `${i}:${JSON.stringify(s.Weapons)}` : null).filter(Boolean);
  console.log(`  座位武器引用: ${refs.join(' ')}`);
  // 弹种/娱乐字段
  for (const k of ['AmmoType', 'Projectile', 'FirePos', 'VehicleType', 'EngineType']) if (j[k] !== undefined) console.log(`  ${k}=${JSON.stringify(j[k])}`);
}

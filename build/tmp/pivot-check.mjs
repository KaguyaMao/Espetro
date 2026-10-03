import fs from 'node:fs';
import path from 'node:path';
// 检查：使用 WeaponStation/WeaponStationBarrel 的武器，其枢轴字段是否齐全；以及 TurretPos/BarrelPos 是否缺失
const dir = process.argv[2];
const geoDir = process.argv[3];
function boneNames(id) {
  for (const c of [path.join(geoDir, id + '.geo.json'), path.join(geoDir, id + '.json')])
    if (fs.existsSync(c)) {
      const g = JSON.parse(fs.readFileSync(c, 'utf8'));
      const out = [];
      for (const gg of g['minecraft:geometry'] || []) for (const b of (gg.bones || [])) out.push(b.name);
      return out;
    }
  return null;
}
for (const f of fs.readdirSync(dir).filter(x => x.endsWith('.json')).sort()) {
  const id = f.replace('.json', '');
  const j = JSON.parse(fs.readFileSync(path.join(dir, f), 'utf8'));
  const bn = boneNames(id);
  const ws = Object.entries(j.Weapons || {}).filter(([, w]) => /^WeaponStation/.test(w.ShootPos?.Transform || ''));
  const hasTurret = j.TurretPos !== undefined, hasBarrel = j.BarrelPos !== undefined;
  const hasPws = j.PassengerWeaponStationPos !== undefined, hasPwsB = j.PassengerWeaponStationBarrelPos !== undefined;
  const shotWeapons = Object.entries(j.Weapons || {}).filter(([, w]) => w.ShootPos?.Transform === 'Barrel' || w.ShootPos?.Transform === 'Turret');
  const issues = [];
  if (shotWeapons.length && !(hasTurret && hasBarrel)) issues.push(`用 Barrel/Turret 但缺 TurretPos/BarrelPos`);
  if (ws.length && !(hasPws && hasPwsB)) issues.push(`用 ${ws.map(([n]) => n).join('/')} 但缺 PassengerWeaponStationPos/BarrelPos`);
  const modelHasPws = bn ? bn.some(n => n === 'passengerWeaponStationPitch') : null;
  const modelHasTurret = bn ? bn.includes('turret') : null;
  console.log(`${id.padEnd(20)} turretPos=${hasTurret ? 'Y' : 'N'} barrelPos=${hasBarrel ? 'Y' : 'N'} pws=${hasPws ? 'Y' : 'N'} pwsB=${hasPwsB ? 'Y' : 'N'}`
    + ` | 模型 turret=${modelHasTurret === null ? '?' : (modelHasTurret ? 'Y' : 'N')} pwsPitch=${modelHasPws === null ? '?' : (modelHasPws ? 'Y' : 'N')}`
    + (issues.length ? `   ⚠ ${issues.join('；')}` : ''));
}

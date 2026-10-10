import fs from 'node:fs';
import path from 'node:path';

// 用法: node cannon-abs.mjs <geoDir> <dataJson> [<dataJson2> ...]
// 打印：Transform / Positions / TurretPos / BarrelPos / turretCustomPitch，
// 并把 Positions 换算成"载具空间绝对坐标"（按 superb 的枢轴链），同时给出模型里 CannonPos* 骨骼的直接坐标。
const geoDir = process.argv[2];
const jsons = process.argv.slice(3);

function loadGeo(name) {
  const cands = [
    path.join(geoDir, name + '.geo.json'),
    path.join(geoDir, name + '.json'),
  ];
  for (const c of cands) if (fs.existsSync(c)) return JSON.parse(fs.readFileSync(c, 'utf8'));
  return null;
}

function bonePivots(geo) {
  const out = [];
  if (!geo?.['minecraft:geometry']) return out;
  for (const g of geo['minecraft:geometry']) {
    for (const b of (g.bones || [])) {
      if (b.pivot) out.push({ name: b.name, pivot: b.pivot });
    }
  }
  return out;
}

const fmt = (a) => '[' + a.map(v => Number(v).toFixed(4)).join(', ') + ']';
const toBlocks = (p) => [p[0] / 16, p[1] / 16, -p[2] / 16];

for (const jf of jsons) {
  const j = JSON.parse(fs.readFileSync(jf, 'utf8'));
  const id = (j.ID || '').split(':').pop() || path.basename(jf, '.json');
  const geo = loadGeo(id);
  console.log('════════════════════════════════════════════');
  console.log(`文件: ${jf}`);
  console.log(`  TurretPos=${JSON.stringify(j.TurretPos)}  BarrelPos=${JSON.stringify(j.BarrelPos)}  turretCustomPitch=${JSON.stringify(j.TurretCustomPitch)}  PassengerWeaponStationPos=${JSON.stringify(j.PassengerWeaponStationPos)}`);
  const tp = j.TurretPos || null, bp = j.BarrelPos || null;
  const weapons = j.Weapons || {};
  for (const wname of ['Cannon', 'MachineGun', 'MainMachineGun', '100MM_Cannon', 'Missile', 'PassengerMachineGun']) {
    const w = weapons[wname];
    if (!w) continue;
    const sp = w.ShootPos;
    if (!sp) { console.log(`  ${wname}: <无 ShootPos>`); continue; }
    const T = sp.Transform ?? '(缺失)';
    for (const [i, p] of (sp.Positions || []).entries()) {
      let abs = p.slice();
      if (T === 'Barrel' && tp && bp) abs = abs.map((v, k) => v + tp[k] + bp[k]);
      else if (T === 'Turret' && tp) abs = abs.map((v, k) => v + tp[k]);
      else if (T === 'WeaponStationBarrel' && tp && j.PassengerWeaponStationPos && j.PassengerWeaponStationBarrelPos)
        abs = abs.map((v, k) => v + tp[k] + j.PassengerWeaponStationPos[k] + j.PassengerWeaponStationBarrelPos[k]);
      console.log(`  ${wname}: Transform=${T}  填写=${fmt(p)}  →  绝对坐标=${fmt(abs)}`);
    }
  }
  if (geo) {
    const piv = bonePivots(geo).filter(b => /^(CannonPos|MachineGunPos|MissilePos|BombPos|RocketPos|PassengerMachineGunPos|turret|barrel)/i.test(b.name));
    console.log('  —— 模型骨骼（换算为块坐标）:');
    for (const b of piv) console.log(`     ${b.name.padEnd(28)} ${fmt(toBlocks(b.pivot))}`);
  } else {
    console.log('  —— 未找到模型 geo');
  }
}

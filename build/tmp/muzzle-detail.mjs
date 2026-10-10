import fs from 'node:fs';
import path from 'node:path';

// 详细打印：kb/jar 填入值、枢轴、模型同名骨骼绝对坐标，以及两种解释的残差
const geoDir = process.argv[2], jarDir = process.argv[3], kbDir = process.argv[4];
const only = process.argv.slice(5);
const f3 = (a) => '[' + a.map(v => (+v).toFixed(3).padStart(8)).join(',') + ']';

function loadGeo(id) {
  for (const c of [path.join(geoDir, id + '.geo.json'), path.join(geoDir, id + '.json')])
    if (fs.existsSync(c)) return JSON.parse(fs.readFileSync(c, 'utf8'));
  return null;
}
function bones(geo) {
  const m = {};
  for (const g of geo?.['minecraft:geometry'] || []) for (const b of (g.bones || []))
    if (b.pivot) m[b.name] = [b.pivot[0] / 16, b.pivot[1] / 16, -b.pivot[2] / 16];
  return m;
}

for (const f of fs.readdirSync(kbDir).filter(x => x.endsWith('.json')).sort()) {
  const id = f.replace('.json', '');
  if (only.length && !only.includes(id)) continue;
  const kb = JSON.parse(fs.readFileSync(path.join(kbDir, f), 'utf8'));
  const jar = JSON.parse(fs.readFileSync(path.join(jarDir, f), 'utf8'));
  const geo = loadGeo(id);
  if (!geo) continue;
  const bm = bones(geo);
  const tp = kb.TurretPos || [0, 0, 0], bp = kb.BarrelPos || [0, 0, 0];
  const piv = tp.map((v, i) => v + bp[i]);
  console.log('════════════════════════════════════════════════════');
  console.log(`${id}  TurretPos=${f3(tp)}  BarrelPos=${f3(bp)}  枢轴和(Turret+Barrel)=${f3(piv)}`);
  for (const wn of ['Cannon', 'MachineGun', 'MainMachineGun', '100MM_Cannon', 'Missile', 'PassengerMachineGun']) {
    const kbw = kb.Weapons?.[wn], jarw = jar.Weapons?.[wn];
    if (!kbw?.ShootPos) continue;
    const T = kbw.ShootPos.Transform;
    const p = T === 'Barrel' ? piv : (T === 'Turret' ? tp : [0, 0, 0]);
    console.log(`  ${wn}: Transform=${T}`);
    for (const [i, v] of kbw.ShootPos.Positions.entries())
      console.log(`     kubejs Positions[${i}] = ${f3(v)}   → 游戏实际出膛点(绝对) = ${f3(v.map((x, k) => x + p[k]))}`);
    for (const [i, v] of (jarw?.ShootPos?.Positions || []).entries())
      console.log(`     包内   Positions[${i}] = ${f3(v)}   → 游戏实际出膛点(绝对) = ${f3(v.map((x, k) => x + p[k]))}`);
  }
  const cand = Object.keys(bm).filter(n => /^(CannonPos|MachineGunPos|MissilePos|BombPos|RocketPos|PassengerMachineGunPos)/.test(n));
  for (const n of cand) console.log(`     模型骨骼 ${n.padEnd(24)} 绝对=${f3(bm[n])}   相对枢轴=${f3(bm[n].map((v, k) => v - piv[k]))}`);
}

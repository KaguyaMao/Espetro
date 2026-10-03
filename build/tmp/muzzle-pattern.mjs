import fs from 'node:fs';
import path from 'node:path';

// 校验：kubejs 是否"只把 z 换成了模型骨骼绝对 z，x/y 仍是相对值"
const geoDir = process.argv[2], jarDir = process.argv[3], kbDir = process.argv[4];
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
const near = (a, b, e = 0.005) => Math.abs(a - b) <= e;

for (const f of fs.readdirSync(kbDir).filter(x => x.endsWith('.json')).sort()) {
  const id = f.replace('.json', '');
  if (!fs.existsSync(path.join(jarDir, f))) continue;
  const kb = JSON.parse(fs.readFileSync(path.join(kbDir, f), 'utf8'));
  const jar = JSON.parse(fs.readFileSync(path.join(jarDir, f), 'utf8'));
  const geo = loadGeo(id);
  const bm = geo ? bones(geo) : {};
  const tp = kb.TurretPos || [0, 0, 0], bp = kb.BarrelPos || [0, 0, 0];
  for (const [wn, wd] of Object.entries(kb.Weapons || {})) {
    const ksp = wd.ShootPos, jsp = jar.Weapons?.[wn]?.ShootPos;
    if (!ksp?.Positions?.length || !jsp?.Positions?.length) continue;
    const kp = ksp.Positions[0], jp = jsp.Positions[0];
    if (kp.every((v, i) => near(v, jp[i])) && ksp.Transform === jsp.Transform) continue; // 未改动
    const piv = ksp.Transform === 'Barrel' ? tp.map((v, i) => v + bp[i]) : (ksp.Transform === 'Turret' ? tp : [0, 0, 0]);
    const absOld = jp.map((v, i) => v + piv[i]);   // 包内值 → 绝对
    const absNew = kp.map((v, i) => v + piv[i]);   // 你填的值 → 绝对
    // 找模型骨骼里最接近"你填的绝对 z"的
    const boneHit = Object.entries(bm).filter(([, b]) => near(b[2], kp[2], 0.01)).map(([n]) => n);
    console.log(`${(id + '/' + wn).padEnd(26)} T=${(ksp.Transform || '').padEnd(18)}`
      + ` kubejs=${JSON.stringify(kp).padEnd(28)} 包内=${JSON.stringify(jp).padEnd(28)}`
      + ` xy相同=${near(kp[0], jp[0]) && near(kp[1], jp[1]) ? '是' : '否'}`
      + ` 你填的z=模型骨骼[${boneHit.join(',') || '无同名骨骼'}]`
      + ` → 出膛绝对z: 包内=${absOld[2].toFixed(3)} 你的=${absNew[2].toFixed(3)} 差${(absNew[2] - absOld[2] >= 0 ? '+' : '')}${(absNew[2] - absOld[2]).toFixed(3)}`);
  }
}

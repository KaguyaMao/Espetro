import fs from 'node:fs';
import path from 'node:path';

// 判定：kubejs 里填的值到底是"绝对坐标"还是"相对枢轴偏移"
// 用法: node muzzle-verdict.mjs <geoDir> <jarDataDir> <kubejsDataDir>
const geoDir = process.argv[2], jarDir = process.argv[3], kbDir = process.argv[4];

const BONE_PREFIX = {
  Cannon: 'CannonPos', MachineGun: 'MachineGunPos', MainMachineGun: 'MachineGunPos',
  '100MM_Cannon': 'CannonPos', Missile: 'MissilePos', Bomb: 'BombPos', Rocket: 'RocketPos',
  PassengerMachineGun: 'PassengerMachineGunPos',
};
const r3 = (v) => Math.round(v * 1000) / 1000;

function bones(geo) {
  const m = {};
  if (!geo?.['minecraft:geometry']) return m;
  for (const g of geo['minecraft:geometry']) for (const b of (g.bones || [])) {
    if (b.pivot) m[b.name] = [b.pivot[0] / 16, b.pivot[1] / 16, -b.pivot[2] / 16];
  }
  return m;
}
function loadGeo(id) {
  for (const c of [path.join(geoDir, id + '.geo.json'), path.join(geoDir, id + '.json')])
    if (fs.existsSync(c)) return JSON.parse(fs.readFileSync(c, 'utf8'));
  return null;
}
const sub = (a, b) => a.map((v, i) => v - b[i]);

console.log('载具 / 武器          骨骼绝对z   jar填写   kubejs填写   jar填+枢轴   kubejs填+枢轴');
for (const f of fs.readdirSync(kbDir).filter(x => x.endsWith('.json')).sort()) {
  const id = f.replace('.json', '');
  const kb = JSON.parse(fs.readFileSync(path.join(kbDir, f), 'utf8'));
  const jarPath = path.join(jarDir, f);
  if (!fs.existsSync(jarPath)) continue;
  const jar = JSON.parse(fs.readFileSync(jarPath, 'utf8'));
  const geo = loadGeo(id);
  if (!geo) continue;
  const bm = bones(geo);
  for (const [wn, prefix] of Object.entries(BONE_PREFIX)) {
    const kbw = kb.Weapons?.[wn], jarw = jar.Weapons?.[wn];
    if (!kbw?.ShootPos || !jarw?.ShootPos) continue;
    const T = kbw.ShootPos.Transform;
    if (T !== 'Barrel' && T !== 'Turret') continue;
    const boneName = Object.keys(bm).find(n => n.startsWith(prefix));
    if (!boneName) continue;
    const tp = kb.TurretPos || [0, 0, 0];
    const bp = T === 'Barrel' ? (kb.BarrelPos || [0, 0, 0]) : [0, 0, 0];
    const pivotSum = tp.map((v, i) => v + bp[i]);

    const kbP = kbw.ShootPos.Positions?.[0], jarP = jarw.ShootPos.Positions?.[0];
    if (!kbP || !jarP) continue;
    const abs = (p) => p.map((v, i) => v + pivotSum[i]);
    const boneAbs = bm[boneName];
    const d = (a, b) => Math.abs(a[2] - b[2]).toFixed(3);
    const tag = Math.abs(abs(kbP)[2] - jarP[2]) < 0.002 ? '' : '  ← 值不同';
    console.log(`${(id + '/' + wn).padEnd(30)} z=${boneAbs[2].toFixed(3).padStart(8)}  jar=${String(jarP[2]).padStart(9)}  kb=${String(kbP[2]).padStart(9)}`
      + `   |jar填−骨|=${d(jarP, sub(boneAbs, pivotSum))}  |kb填−骨|=${d(kbP, boneAbs)}  |kb绝对−骨|=${d(abs(kbP), boneAbs)}${tag}`);
  }
}

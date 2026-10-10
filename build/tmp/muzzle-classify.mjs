import fs from 'node:fs';
import path from 'node:path';

// 分类：kubejs 里 ShootPos.Positions 填的是"绝对坐标"还是"相对枢轴偏移"？
// 对每个武器找出同名骨骼（CannonPos* / MachineGunPos* / MissilePos* ...），比较两种解释谁更接近。
// 用法: node muzzle-classify.mjs <geoDir> <jarDataDir> <kubejsDataDir>
const geoDir = process.argv[2], jarDir = process.argv[3], kbDir = process.argv[4];

const PREFIX = {
  Cannon: ['CannonPos'], MachineGun: ['MachineGunPos'], MainMachineGun: ['MachineGunPos', 'CannonPos'],
  '100MM_Cannon': ['CannonPos'], Missile: ['MissilePos'], Bomb: ['BombPos'], Rocket: ['RocketPos'],
  PassengerMachineGun: ['PassengerMachineGunPos'],
};
const r3 = (v) => Math.round(v * 1000) / 1000;

function bones(geo) {
  const m = {};
  for (const g of geo?.['minecraft:geometry'] || []) for (const b of (g.bones || []))
    if (b.pivot) m[b.name] = [b.pivot[0] / 16, b.pivot[1] / 16, -b.pivot[2] / 16];
  return m;
}
function loadGeo(id) {
  for (const c of [path.join(geoDir, id + '.geo.json'), path.join(geoDir, id + '.json')])
    if (fs.existsSync(c)) return JSON.parse(fs.readFileSync(c, 'utf8'));
  return null;
}

console.log('载具/武器                      | 值(填入) | 骨骼绝对 | 相对值(骨−枢轴) | 判定            | 包内是否相对');
for (const f of fs.readdirSync(kbDir).filter(x => x.endsWith('.json')).sort()) {
  const id = f.replace('.json', '');
  const kb = JSON.parse(fs.readFileSync(path.join(kbDir, f), 'utf8'));
  const jp = path.join(jarDir, f);
  const jar = fs.existsSync(jp) ? JSON.parse(fs.readFileSync(jp, 'utf8')) : null;
  const geo = loadGeo(id);
  if (!geo) continue;
  const bm = bones(geo);
  const tp = kb.TurretPos || [0, 0, 0], bp = kb.BarrelPos || [0, 0, 0];

  for (const [wn, prefixes] of Object.entries(PREFIX)) {
    const kbw = kb.Weapons?.[wn];
    if (!kbw?.ShootPos?.Positions?.length) continue;
    const T = kbw.ShootPos.Transform;
    if (T !== 'Barrel' && T !== 'Turret') continue;
    const piv = T === 'Barrel' ? [tp[0] + bp[0], tp[1] + bp[1], tp[2] + bp[2]] : tp;
    const cands = Object.keys(bm).filter(n => prefixes.some(p => n.startsWith(p)));
    if (!cands.length) continue;

    const kbP = kbw.ShootPos.Positions[0];
    const jarP = jar?.Weapons?.[wn]?.ShootPos?.Positions?.[0];

    // 找最匹配的骨骼：|值 − 绝对| 或 |值 − 相对|
    let best = null;
    for (const bn of cands) {
      const abs = bm[bn];
      const rel = abs.map((v, i) => v - piv[i]);
      const dAbs = Math.hypot(...kbP.map((v, i) => v - abs[i]));
      const dRel = Math.hypot(...kbP.map((v, i) => v - rel[i]));
      const cand = { bn, abs, rel, dAbs, dRel };
      if (!best || Math.min(dAbs, dRel) < Math.min(best.dAbs, best.dRel)) best = cand;
    }

    const verdict = best.dRel < 0.02 && best.dAbs > 0.05 ? '相对枢轴 ✔'
      : best.dAbs < 0.02 && best.dRel > 0.05 ? '绝对坐标 ✘(会被再加枢轴)'
        : best.dAbs < 0.02 && best.dRel < 0.02 ? '枢轴≈0，两种等价'
          : `都不匹配(Δ绝对=${best.dAbs.toFixed(3)}, Δ相对=${best.dRel.toFixed(3)})`;

    let jarRel = '—';
    if (jarP) {
      const rel = best.abs.map((v, i) => v - piv[i]);
      const dRel = Math.hypot(...jarP.map((v, i) => v - rel[i]));
      const dAbs = Math.hypot(...jarP.map((v, i) => v - best.abs[i]));
      jarRel = dRel < 0.02 ? '是' : (dAbs < 0.02 ? '否(绝对!)' : `? Δ=${dRel.toFixed(3)}`);
    }
    console.log(`${(id + '/' + wn).padEnd(30)} | ${JSON.stringify(kbP).padEnd(30)} | ${best.bn.padEnd(10)} 枢轴z=${piv[2].toFixed(3).padStart(7)} | ${verdict.padEnd(24)} | ${jarRel}`);
  }
}

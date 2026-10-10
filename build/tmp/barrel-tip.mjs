import fs from 'node:fs';
import path from 'node:path';

// 计算：模型里"炮管末端"的绝对坐标（块），与当前生效数据算出的实际出膛点比较。
// 用法: node barrel-tip.mjs <geoDir> <dataDir> [载具id ...]
const geoDir = process.argv[2], dataDir = process.argv[3];
const only = process.argv.slice(4);

function loadGeo(id) {
  for (const c of [path.join(geoDir, id + '.geo.json'), path.join(geoDir, id + '.json')])
    if (fs.existsSync(c)) return JSON.parse(fs.readFileSync(c, 'utf8'));
  return null;
}
// Blockbench -> MC 块坐标：x/16, y/16, -z/16
const bb2mc = (p) => [p[0] / 16, p[1] / 16, -p[2] / 16];

function collect(geo) {
  const bones = [], byName = {};
  for (const g of geo['minecraft:geometry'] || []) for (const b of (g.bones || [])) { bones.push(b); byName[b.name] = b; }
  return { bones, byName };
}

// 某个骨骼及其全部后代的方块在 Blockbench 中的 z 范围（取最前端 = 最小 bb_z）
function subtreeCubes(bones, rootName) {
  const parentOf = {};
  for (const b of bones) for (const c of (b.children || [])) parentOf[c] = b.name;
  const isDesc = (name, root) => { let n = name; while (n) { if (n === root) return true; n = parentOf[n]; } return false; };
  const cubes = [];
  for (const b of bones) if (isDesc(b.name, rootName)) for (const c of (b.cubes || [])) cubes.push({ bone: b.name, origin: c.origin, size: c.size });
  return cubes;
}

for (const f of fs.readdirSync(dataDir).filter(x => x.endsWith('.json')).sort()) {
  const id = f.replace('.json', '');
  if (only.length && !only.includes(id)) continue;
  const j = JSON.parse(fs.readFileSync(path.join(dataDir, f), 'utf8'));
  const geo = loadGeo(id);
  if (!geo) { console.log(`═══ ${id}: 无模型`); continue; }
  const { bones } = collect(geo);
  const tp = j.TurretPos || [0, 0, 0], bp = j.BarrelPos || [0, 0, 0];
  const pivotZ = tp[2] + bp[2], pivotY = tp[1] + bp[1];

  // 炮管末端：barrel 骨骼（及其后代，排除 turret 子树里的其他东西）的方块最前端
  const barrelBone = bones.find(b => b.name === 'barrel');
  let tipZ = null;
  if (barrelBone) {
    const cubes = subtreeCubes(bones, 'barrel');
    let minBbZ = Infinity, maxY = -Infinity;
    for (const c of cubes) { minBbZ = Math.min(minBbZ, c.origin[2]); }
    if (minBbZ !== Infinity) tipZ = -minBbZ / 16;
    // 末端立方体的 y 中心
    const tipCubes = cubes.filter(c => Math.abs(c.origin[2] - minBbZ) < 0.01);
    for (const c of tipCubes) maxY = Math.max(maxY, (c.origin[1] + (c.size?.[1] || 0) / 2) / 16);
    console.log(`═══ ${id}  (枢轴链 turretZ=${tp[2]} barrelZ=${bp[2]} → 炮管枢轴绝对z=${pivotZ.toFixed(3)}, y=${pivotY.toFixed(3)})`);
    console.log(`   barrel 子树方块数=${cubes.length}  模型炮管末端绝对坐标 ≈ [z=${tipZ.toFixed(3)}, y=${maxY === -Infinity ? '?' : maxY.toFixed(3)}]`);
  } else {
    console.log(`═══ ${id}  (无双枢轴? turretZ=${tp[2]} barrelZ=${bp[2]})  模型无名为 barrel 的骨骼`);
  }

  for (const [wn, wd] of Object.entries(j.Weapons || {})) {
    const sp = wd.ShootPos;
    if (!sp?.Positions?.length) continue;
    const T = sp.Transform;
    for (const [i, p] of sp.Positions.entries()) {
      const add = T === 'Barrel' ? [tp[0] + bp[0], tp[1] + bp[1], tp[2] + bp[2]]
        : T === 'Turret' ? tp : [0, 0, 0];
      const abs = p.map((v, k) => v + add[k]);
      const delta = tipZ !== null ? (abs[2] - tipZ) : null;
      console.log(`   ${wn}[${i}] T=${T} 填=${JSON.stringify(p)} → 出膛点绝对z=${abs[2].toFixed(3)}`
        + (delta !== null ? `   与模型炮管末端相差 ${delta >= 0 ? '+' : ''}${delta.toFixed(3)} 格` : ''));
    }
  }
}

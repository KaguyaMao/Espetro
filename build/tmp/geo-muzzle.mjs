import fs from 'node:fs';
import path from 'node:path';
// 用几何估算"炮管末端"：取名字像炮管的骨骼（含其子骨骼）所有方块中，Blockbench z 最小（= 最前端）
const geoDir = process.argv[2];
const ids = process.argv.slice(3);
const GUN_RE = /barrel|cannon|gun|tube|muzzle|炮/i;
for (const id of ids) {
  let geo = null;
  for (const c of [path.join(geoDir, id + '.geo.json'), path.join(geoDir, id + '.json')])
    if (fs.existsSync(c)) { geo = JSON.parse(fs.readFileSync(c, 'utf8')); break; }
  if (!geo) { console.log(`${id}: 无模型`); continue; }
  const bones = [];
  for (const g of geo['minecraft:geometry'] || []) for (const b of (g.bones || [])) bones.push(b);
  const parentOf = {};
  for (const b of bones) for (const c of (b.children || [])) parentOf[c] = b.name;
  const roots = bones.filter(b => GUN_RE.test(b.name));
  const isUnder = (name, root) => { let n = name; while (n) { if (n === root) return true; n = parentOf[n]; } return false; };
  const rows = [];
  for (const r of roots) {
    let minZ = Infinity, count = 0;
    for (const b of bones) {
      if (!isUnder(b.name, r.name)) continue;
      for (const c of (b.cubes || [])) { if (c.origin) { minZ = Math.min(minZ, c.origin[2]); count++; } }
    }
    if (minZ !== Infinity) rows.push({ name: r.name, tip: -minZ / 16, cubes: count });
  }
  rows.sort((a, b) => b.tip - a.tip);
  console.log(`═══ ${id}`);
  for (const r of rows.slice(0, 6)) console.log(`   ${r.name.padEnd(22)} 末端绝对 z ≈ ${r.tip.toFixed(3)}  (方块 ${r.cubes})`);
}

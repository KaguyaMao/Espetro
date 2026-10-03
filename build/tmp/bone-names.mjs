import fs from 'node:fs';
import path from 'node:path';
// 列出模型里所有骨骼名（用于找炮口骨骼的命名习惯）
const geoDir = process.argv[2];
const ids = process.argv.slice(3);
for (const id of ids) {
  let geo = null;
  for (const c of [path.join(geoDir, id + '.geo.json'), path.join(geoDir, id + '.json')])
    if (fs.existsSync(c)) { geo = JSON.parse(fs.readFileSync(c, 'utf8')); break; }
  const names = [];
  for (const g of geo?.['minecraft:geometry'] || []) for (const b of (g.bones || [])) names.push(b.name);
  const interesting = names.filter(n => /pos|muzzle|gun|cannon|barrel|fire|tube|shot|炮|dummy|nacelle/i.test(n));
  console.log(`═══ ${id}  骨骼总数=${names.length}`);
  console.log('   炮口相关: ' + (interesting.join(', ') || '（无）'));
}

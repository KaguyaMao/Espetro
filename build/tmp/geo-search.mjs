// geo-search.mjs — 在 geo 模型里按正则搜索骨骼名
import fs from 'fs';
const file = process.argv[2];
const re = new RegExp(process.argv[3], 'i');
const j = JSON.parse(fs.readFileSync(file, 'utf8'));
const geo = j['minecraft:geometry']?.[0] ?? j.geometry?.[0] ?? j;
const bones = geo.bones ?? [];
const hits = bones.filter((b) => re.test(b.name));
console.log(`${file}: 命中 ${hits.length} 个骨骼`);
for (const b of hits) console.log(`  · ${b.name}  parent=${b.parent ?? '(根)'}  cubes=${(b.cubes ?? []).length}`);

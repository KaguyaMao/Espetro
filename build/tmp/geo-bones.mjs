// geo-bones.mjs — 打印 bedrock geo 模型的骨骼树（只显示含 tusk 的分支及其祖先/子节点）
import fs from 'fs';

const file = process.argv[2];
const j = JSON.parse(fs.readFileSync(file, 'utf8'));
const geo = j['minecraft:geometry']?.[0] ?? j.geometry?.[0] ?? j;
const bones = geo.bones ?? [];
const byName = new Map(bones.map((b) => [b.name, b]));
console.log(`模型: ${file}`);
console.log(`骨骼总数: ${bones.length}`);
console.log(`identifier: ${geo.description?.identifier ?? '(无)'}`);

const tusk = bones.filter((b) => /tusk/i.test(b.name));
console.log(`\n含 tusk 的骨骼 ${tusk.length} 个:`);
for (const b of tusk) {
  console.log(`  · ${b.name}  parent=${b.parent ?? '(根)'}  pivot=${JSON.stringify(b.pivot)}  cubes=${(b.cubes ?? []).length}`);
  // 祖先链
  const chain = [];
  let p = b.parent;
  while (p && byName.has(p)) { chain.push(p); p = byName.get(p).parent; }
  if (chain.length) console.log(`      祖先: ${chain.join(' → ')}`);
  // 直接子节点
  const kids = bones.filter((x) => x.parent === b.name).map((x) => x.name);
  if (kids.length) console.log(`      子节点(${kids.length}): ${kids.slice(0, 12).join(', ')}${kids.length > 12 ? ' …' : ''}`);
}

// 是否有 move_ 前缀骨骼被别处引用（例如动画）
const moveBones = bones.filter((b) => /^move_/.test(b.name)).map((b) => b.name);
console.log(`\nmove_ 前缀骨骼 ${moveBones.length} 个: ${moveBones.join(', ')}`);
const anims = Object.keys(geo.animations ?? j.animations ?? {});
if (anims.length) console.log(`动画: ${anims.join(', ')}`);

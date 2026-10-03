// verify-icons.mjs — 校验配置 icon slug 与 roles 目录文件一一对应
import fs from 'fs';

const CFG = process.argv[2] || 'D:/minecraft/modp/Espetro/build/tmp/fxall-icons/';
const DIR = 'D:/minecraft/modp/Espetro/src/main/resources/assets/espetro/textures/gui/roles/';

const files = new Set(fs.readdirSync(DIR).filter((f) => f.endsWith('.png')).map((f) => f.replace(/\.png$/, '')));
const used = new Map();
let missing = 0;

for (const f of fs.readdirSync(CFG).filter((x) => x.endsWith('.json')).sort()) {
  const j = JSON.parse(fs.readFileSync(CFG + f, 'utf8'));
  for (const [cid, cls] of Object.entries(j.classes || {})) {
    const slug = cls.icon;
    if (!used.has(slug)) used.set(slug, []);
    used.get(slug).push(cid);
    if (!files.has(slug)) { console.log(`缺图: ${f} ${cid} icon=${slug}`); missing++; }
  }
}

console.log('=== 配置使用的 slug → 文件');
for (const [slug, ids] of [...used.entries()].sort()) {
  console.log(`${slug.padEnd(22)} ×${String(ids.length).padStart(3)}  png=${files.has(slug) ? 'OK' : '缺失'}`);
}
const unused = [...files].filter((f) => !used.has(f)).sort();
console.log('\n目录中未被任何职业引用（备用素材）: ' + unused.join(', '));
console.log(`\n职业引用缺失数: ${missing}   使用中 slug ${used.size} 个   目录文件 ${files.size} 个`);

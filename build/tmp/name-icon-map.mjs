// name-icon-map.mjs — 汇总 名称/role → icon 的映射（跨编制去重）
import fs from 'fs';

const CFG = 'D:/minecraft/modp/Espetro/build/tmp/fxall/';
const map = new Map();
for (const f of fs.readdirSync(CFG).filter((x) => x.endsWith('.json'))) {
  const j = JSON.parse(fs.readFileSync(CFG + f, 'utf8'));
  for (const [cid, cls] of Object.entries(j.classes || {})) {
    const key = `${cls.name} | ${cls.role} | icon=${cls.icon} | img=${(cls.IconImage || cls.iconImage || '').split('/').pop()}`;
    if (!map.has(key)) map.set(key, []);
    map.get(key).push(cid);
  }
}
for (const [k, ids] of [...map.entries()].sort()) {
  console.log(`${k}    ×${ids.length}`);
}
console.log('\n共 ' + map.size + ' 种组合');

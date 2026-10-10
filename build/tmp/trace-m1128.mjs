// trace-m1128.mjs — 逐阶段追踪 m1128 的各版本差异
import fs from 'fs';

const VERSIONS = {
  '①原始(服务器)': 'D:/minecraft/modp/Espetro/build/tmp/sbw-veh-backup/m1128.json',
  '②移植后(fcp数值)': 'D:/minecraft/modp/Espetro/build/tmp/sbw-veh-out/m1128.json',
  '③去弹夹前(重下)': 'D:/minecraft/modp/Espetro/build/tmp/sbw-all/dr/m1128.json',
  '④去弹夹后(当前)': 'D:/minecraft/modp/Espetro/build/tmp/sbw-strip-out/dr/m1128.json',
  '⑤本地GScode': 'D:/minecraft/modp/GScode/src/main/resources/data/dragonrise_reforge/sbw/vehicles/m1128.json'
};

const flat = (o, p = '', out = {}) => {
  if (o === null || typeof o !== 'object') { out[p] = o; return out; }
  if (Array.isArray(o)) { o.forEach((v, i) => flat(v, `${p}[${i}]`, out)); return out; }
  for (const [k, v] of Object.entries(o)) flat(v, p ? `${p}.${k}` : k, out);
  return out;
};

const data = {};
for (const [tag, p] of Object.entries(VERSIONS)) {
  const j = JSON.parse(fs.readFileSync(p, 'utf8'));
  data[tag] = flat(j);
  const at = j.Weapons?.Cannon?.AmmoType;
  console.log(`\n=== ${tag}`);
  console.log(`  AmmoType 条目数: ${Array.isArray(at) ? at.length : '(非数组:' + typeof at + ')'}`);
  if (Array.isArray(at)) at.forEach((e, i) => console.log(`    [${i}] ${typeof e === 'string' ? e : (e.Ammo ?? '?') + (e.Override ? ' keys=' + Object.keys(e.Override).join(',') : '')}`));
  console.log(`  叶节点数: ${Object.keys(data[tag]).length}`);
}

const tags = Object.keys(VERSIONS);
const base = tags[0];
for (let i = 1; i < tags.length; i++) {
  const A = data[base], B = data[tags[i]];
  const onlyA = Object.keys(A).filter((k) => !(k in B));
  const onlyB = Object.keys(B).filter((k) => !(k in A));
  const diff = Object.keys(A).filter((k) => k in B && JSON.stringify(A[k]) !== JSON.stringify(B[k]));
  console.log(`\n──── ${base} vs ${tags[i]}: 仅前者 ${onlyA.length}, 仅后者 ${onlyB.length}, 值不同 ${diff.length}`);
  if (onlyA.length) console.log('   仅前者: ' + onlyA.slice(0, 12).join(', ') + (onlyA.length > 12 ? ` …+${onlyA.length - 12}` : ''));
  if (onlyB.length) console.log('   仅后者: ' + onlyB.slice(0, 12).join(', ') + (onlyB.length > 12 ? ` …+${onlyB.length - 12}` : ''));
}

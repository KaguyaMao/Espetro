// diff-struct.mjs — 两个 JSON 的结构化差异
import fs from 'fs';

const flat = (o, p = '', out = {}) => {
  if (o === null || typeof o !== 'object') { out[p] = o; return out; }
  if (Array.isArray(o)) { o.forEach((v, i) => flat(v, `${p}[${i}]`, out)); return out; }
  for (const [k, v] of Object.entries(o)) flat(v, p ? `${p}.${k}` : k, out);
  return out;
};

const [aPath, bPath, ...rest] = process.argv.slice(2);
const A = flat(JSON.parse(fs.readFileSync(aPath, 'utf8')));
const B = flat(JSON.parse(fs.readFileSync(bPath, 'utf8')));
console.log(`A = ${aPath}  (${Object.keys(A).length} 叶)`);
console.log(`B = ${bPath}  (${Object.keys(B).length} 叶)`);
const onlyA = Object.keys(A).filter((k) => !(k in B));
const onlyB = Object.keys(B).filter((k) => !(k in A));
const diff = Object.keys(A).filter((k) => k in B && JSON.stringify(A[k]) !== JSON.stringify(B[k]));
console.log(`\n仅 A 有 (${onlyA.length}):`);
for (const k of onlyA.slice(0, Number(rest[0] ?? 40))) console.log(`   - ${k} = ${JSON.stringify(A[k])}`);
if (onlyA.length > 40) console.log(`   …+${onlyA.length - 40}`);
console.log(`仅 B 有 (${onlyB.length}):`);
for (const k of onlyB.slice(0, 40)) console.log(`   + ${k} = ${JSON.stringify(B[k])}`);
if (onlyB.length > 40) console.log(`   …+${onlyB.length - 40}`);
console.log(`值不同 (${diff.length}):`);
for (const k of diff.slice(0, 40)) console.log(`   ~ ${k}: ${JSON.stringify(A[k])} → ${JSON.stringify(B[k])}`);
if (diff.length > 40) console.log(`   …+${diff.length - 40}`);

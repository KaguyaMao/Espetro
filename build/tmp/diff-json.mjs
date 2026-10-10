// diff-json.mjs — 语义比较两个 JSON 文件（顶层 + classes + vehicles）
import fs from 'fs';

const [aPath, bPath] = process.argv.slice(2);
const a = JSON.parse(fs.readFileSync(aPath, 'utf8'));
const b = JSON.parse(fs.readFileSync(bPath, 'utf8'));

const flat = (o, prefix = '') => {
  const out = {};
  if (o === null || typeof o !== 'object') { out[prefix] = o; return out; }
  if (Array.isArray(o)) { out[prefix] = JSON.stringify(o); return out; }
  for (const [k, v] of Object.entries(o)) Object.assign(out, flat(v, prefix ? prefix + '.' + k : k));
  return out;
};

const fa = flat(a), fb = flat(b);
const keys = new Set([...Object.keys(fa), ...Object.keys(fb)]);
const onlyA = [], onlyB = [], diff = [];
for (const k of [...keys].sort()) {
  const inA = k in fa, inB = k in fb;
  if (inA && !inB) onlyA.push(`${k} = ${String(fa[k]).slice(0, 60)}`);
  else if (!inA && inB) onlyB.push(`${k} = ${String(fb[k]).slice(0, 60)}`);
  else if (JSON.stringify(fa[k]) !== JSON.stringify(fb[k]))
    diff.push(`${k}: ${String(fa[k]).slice(0, 50)} → ${String(fb[k]).slice(0, 50)}`);
}

console.log(`A(本地) 叶节点 ${Object.keys(fa).length}  B(服务器) ${Object.keys(fb).length}`);
console.log(`\n仅 A 有 (${onlyA.length}):`);
for (const l of onlyA.slice(0, 40)) console.log('  - ' + l);
console.log(`\n仅 B 有 (${onlyB.length}):`);
for (const l of onlyB.slice(0, 40)) console.log('  + ' + l);
console.log(`\n值不同 (${diff.length}):`);
for (const l of diff.slice(0, 60)) console.log('  ~ ' + l);

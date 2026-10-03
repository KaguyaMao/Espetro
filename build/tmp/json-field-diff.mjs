import fs from 'node:fs';
import path from 'node:path';

const A = process.argv[2]; // dir A (基线, 例如 jar 内)
const B = process.argv[3]; // dir B (用户 kubejs)
const onlyDiff = process.argv.includes('--diff');

function flat(o, prefix = '', out = {}) {
  if (o === null || typeof o !== 'object') { out[prefix] = o; return out; }
  if (Array.isArray(o)) {
    if (o.length === 0) { out[prefix] = '[]'; return out; }
    o.forEach((v, i) => flat(v, `${prefix}[${i}]`, out));
    return out;
  }
  for (const k of Object.keys(o)) flat(o[k], prefix ? `${prefix}.${k}` : k, out);
  return out;
}

for (const name of fs.readdirSync(B).filter(f => f.endsWith('.json')).sort()) {
  const fa = path.join(A, name), fb = path.join(B, name);
  if (!fs.existsSync(fa)) { console.log(`═══ ${name}: 基线缺失`); continue; }
  const ja = JSON.parse(fs.readFileSync(fa, 'utf8'));
  const jb = JSON.parse(fs.readFileSync(fb, 'utf8'));
  const a = flat(ja), b = flat(jb);
  const keys = new Set([...Object.keys(a), ...Object.keys(b)]);
  const diffs = [];
  for (const k of keys) {
    const va = JSON.stringify(a[k]), vb = JSON.stringify(b[k]);
    if (va !== vb) diffs.push(`      ${k}:  基线=${va}  →  kubejs=${vb}`);
  }
  if (diffs.length) {
    console.log(`═══ ${name}  (${diffs.length} 处差异)`);
    for (const d of diffs) console.log(d);
  } else if (!onlyDiff) {
    console.log(`═══ ${name}: 完全一致`);
  }
}

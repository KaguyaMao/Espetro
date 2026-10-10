import fs from 'node:fs';
import path from 'node:path';

// 只对比 ShootPos/TurretPos/BarrelPos 相关字段，输出带文件名
const A = process.argv[2];
const B = process.argv[3];
const RE = /ShootPos|TurretPos|BarrelPos|WeaponStationPos|ViewPosition/;

function flat(o, prefix = '', out = {}) {
  if (o === null || typeof o !== 'object') { out[prefix] = o; return out; }
  if (Array.isArray(o)) { o.forEach((v, i) => flat(v, `${prefix}[${i}]`, out)); return out; }
  for (const k of Object.keys(o)) flat(o[k], prefix ? `${prefix}.${k}` : k, out);
  return out;
}

for (const name of fs.readdirSync(B).filter(f => f.endsWith('.json')).sort()) {
  const fa = path.join(A, name);
  if (!fs.existsSync(fa)) continue;
  const a = flat(JSON.parse(fs.readFileSync(fa, 'utf8')));
  const b = flat(JSON.parse(fs.readFileSync(path.join(B, name), 'utf8')));
  const rows = [];
  for (const k of new Set([...Object.keys(a), ...Object.keys(b)])) {
    if (!RE.test(k)) continue;
    const va = JSON.stringify(a[k]), vb = JSON.stringify(b[k]);
    if (va !== vb) rows.push(`   ${k.replace(/^Weapons\./, '')}:  包内=${va}  →  kubejs=${vb}`);
  }
  if (rows.length) { console.log(`═══ ${name}`); rows.forEach(r => console.log(r)); }
}

import fs from 'node:fs';
import path from 'node:path';

// 比较两个目录里同名 json：列出"基线有、新文件没有"的字段路径（用于发现覆盖导致的字段丢失）
const A = process.argv[2], B = process.argv[3];
function flat(o, prefix = '', out = {}) {
  if (o === null || typeof o !== 'object') { out[prefix] = o; return out; }
  if (Array.isArray(o)) { o.forEach((v, i) => flat(v, `${prefix}[${i}]`, out)); return out; }
  for (const k of Object.keys(o)) flat(o[k], prefix ? `${prefix}.${k}` : k, out);
  return out;
}
for (const f of fs.readdirSync(B).filter(x => x.endsWith('.json')).sort()) {
  const fa = path.join(A, f);
  if (!fs.existsSync(fa)) { console.log(`═══ ${f}: 基线没有这个文件`); continue; }
  const a = flat(JSON.parse(fs.readFileSync(fa, 'utf8')));
  const b = flat(JSON.parse(fs.readFileSync(path.join(B, f), 'utf8')));
  const missing = Object.keys(a).filter(k => !(k in b));
  const added = Object.keys(b).filter(k => !(k in a));
  const changed = Object.keys(a).filter(k => k in b && JSON.stringify(a[k]) !== JSON.stringify(b[k]));
  console.log(`═══ ${f}   丢失字段=${missing.length}  新增字段=${added.length}  值变化=${changed.length}`);
  const interesting = (arr) => arr.filter(k => !/^OBB|^Seats\[\d+\]\.CameraPos|^DamageModifiers|^Models/.test(k));
  const m = interesting(missing), ad = interesting(added);
  if (m.length) console.log(`   丢失: ${m.slice(0, 20).join(', ')}${m.length > 20 ? ` …(共 ${m.length})` : ''}`);
  if (ad.length) console.log(`   新增: ${ad.slice(0, 20).join(', ')}${ad.length > 20 ? ` …(共 ${ad.length})` : ''}`);
}

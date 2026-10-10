// compare-dirs.mjs — 比较服务器原配置(fxall) 与本地打包副本
import fs from 'fs';
import crypto from 'crypto';

const A = process.argv[2];
const B = process.argv[3];
const hash = (p) => crypto.createHash('sha1').update(fs.readFileSync(p)).digest('hex').slice(0, 10);

const names = fs.readdirSync(A).filter((f) => f.endsWith('.json')).sort();
for (const f of names) {
  const bp = B + (B.endsWith('/') || B.endsWith('\\') ? '' : '/') + f;
  const a = fs.readFileSync(A + (A.endsWith('/') ? '' : '/') + f, 'utf8');
  if (!fs.existsSync(bp)) { console.log(`缺失 ${f}`); continue; }
  const b = fs.readFileSync(bp, 'utf8');
  const ja = JSON.parse(a), jb = JSON.parse(b);
  const ca = Object.keys(ja.classes || {}).length, cb = Object.keys(jb.classes || {}).length;
  const same = JSON.stringify(ja) === JSON.stringify(jb);
  console.log(`${f}: 完全一致=${same ? 'Y' : 'N'} 字节=${a.length}/${b.length} 职业=${ca}/${cb}`);
}

// cmp-jars.mjs — 比较两个 jar 的条目差异（判断工作区源码是否与线上 jar 同源）
// 用法: node cmp-jars.mjs <jarA> <jarB>
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');
const os = require('os');

const [a, b] = process.argv.slice(2);
const tmp = fs.mkdtempSync(path.join(os.tmpdir(), 'cmpjar-'));
function entries(jar, out) {
  // 用 tar 解 zip（Windows 10+ 自带 bsdtar 可读 zip）
  execSync(`tar -xf "${jar}" -C "${out}"`, { stdio: 'ignore' });
  const map = new Map();
  (function walk(d, rel) {
    for (const e of fs.readdirSync(d, { withFileTypes: true })) {
      const p = path.join(d, e.name);
      const r = rel ? rel + '/' + e.name : e.name;
      if (e.isDirectory()) walk(p, r);
      else map.set(r, fs.statSync(p).size);
    }
  })(out, '');
  return map;
}
const da = path.join(tmp, 'a'), db = path.join(tmp, 'b');
fs.mkdirSync(da, { recursive: true }); fs.mkdirSync(db, { recursive: true });
const ma = entries(a, da), mb = entries(b, db);

const onlyA = [...ma.keys()].filter((k) => !mb.has(k));
const onlyB = [...mb.keys()].filter((k) => !ma.has(k));
const diffSize = [...ma.keys()].filter((k) => mb.has(k) && ma.get(k) !== mb.get(k));

const cls = (k) => k.endsWith('.class');
console.log(`A=${path.basename(a)}  条目 ${ma.size}`);
console.log(`B=${path.basename(b)}  条目 ${mb.size}`);
console.log(`\n只在 A（线上）有的条目: ${onlyA.length}（class ${onlyA.filter(cls).length}）`);
for (const k of onlyA.filter(cls).slice(0, 40)) console.log('   A-only class: ' + k);
for (const k of onlyA.filter((x) => !cls(x)).slice(0, 20)) console.log('   A-only other: ' + k);
console.log(`\n只在 B（新构建）有的条目: ${onlyB.length}（class ${onlyB.filter(cls).length}）`);
for (const k of onlyB.filter(cls).slice(0, 40)) console.log('   B-only class: ' + k);
for (const k of onlyB.filter((x) => !cls(x)).slice(0, 20)) console.log('   B-only other: ' + k);
console.log(`\n同名但大小不同: ${diffSize.length}`);
for (const k of diffSize.slice(0, 60)) console.log(`   ${k}  A=${ma.get(k)}  B=${mb.get(k)}`);
fs.rmSync(tmp, { recursive: true, force: true });

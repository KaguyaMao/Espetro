// compare-server.mjs — 比较服务器拉回的文件与本地新配置是否一致
import fs from 'fs';

const A = (process.argv[2] ?? 'D:/minecraft/modp/Espetro/build/tmp/fxall-new/').replace(/\/?$/, '/');
const B = (process.argv[3] ?? 'D:/minecraft/modp/Espetro/build/tmp/server-verify/').replace(/\/?$/, '/');

const files = fs.readdirSync(B).filter((f) => f.endsWith('.json')).sort();
if (!files.length) { console.log('server-verify/ 为空'); process.exit(1); }

let same = 0, diff = 0;
for (const f of files) {
  const a = JSON.parse(fs.readFileSync(A + f, 'utf8'));
  const b = JSON.parse(fs.readFileSync(B + f, 'utf8'));
  // 比较每个职业的 resupply.items 与职业数量
  const diffs = [];
  const aClasses = Object.keys(a.classes || {}), bClasses = Object.keys(b.classes || {});
  if (aClasses.length !== bClasses.length) diffs.push(`职业数 ${aClasses.length} vs ${bClasses.length}`);
  for (const cid of aClasses) {
    const av = a.classes[cid].variants || {}, bv = (b.classes[cid] || {}).variants || {};
    for (const vid of Object.keys(av)) {
      const ai = JSON.stringify((av[vid].resupply || {}).items ?? null);
      const bi = JSON.stringify(((bv[vid] || {}).resupply || {}).items ?? null);
      if (ai !== bi) diffs.push(`${cid}/${vid}`);
    }
  }
  if (diffs.length) { diff++; console.log(`差异 ${f}: ${diffs.slice(0, 5).join(', ')}${diffs.length > 5 ? ` +${diffs.length - 5}` : ''}`); }
  else { same++; console.log(`一致 ${f}`); }
}
console.log(`\n一致=${same} 有差异=${diff} / 共 ${files.length}`);

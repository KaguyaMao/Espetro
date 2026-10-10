// compare-strict.mjs — 严格比较两个目录的 JSON（完整对象 + icon 字段明细）
import fs from 'fs';

const A = (process.argv[2]).replace(/\/?$/, '/');
const B = (process.argv[3]).replace(/\/?$/, '/');
const files = fs.readdirSync(B).filter((f) => f.endsWith('.json')).sort();

let same = 0, diff = 0, iconDiff = 0;
for (const f of files) {
  const a = JSON.parse(fs.readFileSync(A + f, 'utf8'));
  const b = JSON.parse(fs.readFileSync(B + f, 'utf8'));
  const eq = JSON.stringify(a) === JSON.stringify(b);
  if (eq) { same++; continue; }
  diff++;
  const ac = a.classes || {}, bc = b.classes || {};
  const diffs = [];
  for (const cid of new Set([...Object.keys(ac), ...Object.keys(bc)])) {
    const ai = ac[cid]?.icon, bi = bc[cid]?.icon;
    if (ai !== bi) { diffs.push(`${cid}: icon ${ai} vs ${bi}`); iconDiff++; }
  }
  console.log(`差异 ${f}: ${diffs.length ? diffs.slice(0, 4).join(' | ') : '非 icon 字段差异'}`);
}
console.log(`\n完全一致=${same} 有差异=${diff}  其中 icon 差异=${iconDiff}  共 ${files.length} 个文件`);

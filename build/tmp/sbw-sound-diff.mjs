// sbw-sound-diff.mjs — 比较服务器 kubejs 的 dr 载具文件 与 本地 GScode 的音效引用差异
import fs from 'fs';

const LOCAL = 'D:/minecraft/modp/GScode/src/main/resources/data/dragonrise_reforge/sbw/vehicles/';
const SRV = 'D:/minecraft/modp/Espetro/build/tmp/sbw-strip-out/dr/';   // 服务器当前状态（已去弹夹字段）

const isSoundKey = (k) => /sound/i.test(k);

function collect(o, p = '', inSound = false) {
  const out = {};
  if (o === null || typeof o !== 'object') { if (inSound && p) out[p] = o; return out; }
  if (Array.isArray(o)) {
    o.forEach((v, i) => Object.assign(out, collect(v, `${p}[${i}]`, inSound)));
    return out;
  }
  for (const [k, v] of Object.entries(o)) {
    const path = p ? `${p}.${k}` : k;
    const sound = inSound || isSoundKey(k);
    Object.assign(out, collect(v, path, sound));
  }
  return out;
}

const files = fs.readdirSync(SRV).filter((f) => f.endsWith('.json')).sort();
const missingLocal = [];
let sameCount = 0;
const diffs = [];

for (const f of files) {
  if (!fs.existsSync(LOCAL + f)) { missingLocal.push(f); continue; }
  const s = JSON.parse(fs.readFileSync(SRV + f, 'utf8'));
  const l = JSON.parse(fs.readFileSync(LOCAL + f, 'utf8'));
  const S = collect(s), L = collect(l);
  const keys = new Set([...Object.keys(S), ...Object.keys(L)]);
  const changed = [];
  for (const k of [...keys].sort()) {
    if (JSON.stringify(S[k]) !== JSON.stringify(L[k])) changed.push({ k, srv: S[k], loc: L[k] });
  }
  if (changed.length) diffs.push({ file: f, id: s.ID, server: S, local: L, changed });
  else sameCount++;
}

console.log(`服务器 dr 载具文件 ${files.length} 个；本地缺失 ${missingLocal.length} 个${missingLocal.length ? ': ' + missingLocal.join(', ') : ''}`);
console.log(`音效引用完全一致: ${sameCount} 个；存在差异: ${diffs.length} 个\n`);

let nReplace = 0, nAdd = 0, nDel = 0;
for (const d of diffs) {
  for (const c of d.changed) {
    if (c.srv === undefined) nAdd++;
    else if (c.loc === undefined) nDel++;
    else nReplace++;
  }
}
console.log('=== 汇总');
for (const d of diffs) {
  const add = d.changed.filter((c) => c.srv === undefined).length;
  const del = d.changed.filter((c) => c.loc === undefined).length;
  const rep = d.changed.length - add - del;
  console.log(`  ${d.file.padEnd(24)} 共 ${String(d.changed.length).padStart(2)} 处 (改值 ${rep} / 本地新增 ${add} / 服务器多余 ${del})`);
}
console.log(`\n总计: 改值 ${nReplace} 处, 本地新增 ${nAdd} 处, 服务器多余 ${nDel} 处`);
const sameFiles = files.filter((f) => fs.existsSync(LOCAL + f) && !diffs.some((d) => d.file === f));
console.log(`已一致的文件: ${sameFiles.join(', ') || '无'}`);
const delList = [];
for (const d of diffs) for (const c of d.changed) if (c.loc === undefined) delList.push(`${d.file}:${c.k}=${JSON.stringify(c.srv)}`);
if (delList.length) { console.log(`\n仅服务器有（本地没有）的音效键 ${delList.length} 处:`); for (const x of delList.slice(0, 20)) console.log('   - ' + x); }

console.log('\n=== 全文差异明细');

for (const d of diffs) {
  console.log(`▸ ${d.file}  (${d.id})  差异 ${d.changed.length} 处`);
  for (const c of d.changed.slice(0, 12)) {
    console.log(`   ${c.k}\n      服务器: ${JSON.stringify(c.srv)}\n      本地  : ${JSON.stringify(c.loc)}`);
  }
  if (d.changed.length > 12) console.log(`   … 其余 ${d.changed.length - 12} 处`);
}
// 本地存在但服务器没有的文件数量
const allLocal = fs.readdirSync(LOCAL).filter((f) => f.endsWith('.json'));
console.log(`\n本地 vehicles 共 ${allLocal.length} 个；服务器覆盖 ${files.length} 个；本地独有（服务器未覆盖）${allLocal.filter((f) => !files.includes(f)).length} 个`);

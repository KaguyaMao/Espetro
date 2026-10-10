// veh-json-audit.mjs — 载具 DamageModifiers 全面审计：格式 / 重复 / 顺序 / 归零 / 脚本条目
// 用法: node veh-json-audit.mjs <目录>
import fs from 'node:fs';
import path from 'node:path';

const DIR = process.argv[2] ?? 'veh-angle/before';
// SBW 实际使用的正则（DamageModify.MODIFY_PATTERN）
const RE = /^(?<prefix>(@#|#|@)?)(?<id>\w+(:\w+)?)\s*(?<operator>[-*+]?)\s*(?<value>([+-]?\d+(\.\d*)?)?)$/;

function lenient(text) {
  let out = '', inStr = false, esc = false;
  for (let i = 0; i < text.length; i++) {
    const c = text[i];
    if (inStr) { out += c; if (esc) esc = false; else if (c === '\\') esc = true; else if (c === '"') inStr = false; continue; }
    if (c === '"') { inStr = true; out += c; continue; }
    out += c;
  }
  return JSON.parse(out.replace(/,(\s*[}\]])/g, '$1'));
}

const files = fs.readdirSync(DIR).filter(f => f.endsWith('.json') && f !== 'manifest.json').sort();
const problems = [];
const scriptFiles = [];
let totalEntries = 0;

for (const f of files) {
  const j = lenient(fs.readFileSync(path.join(DIR, f), 'utf8'));
  const mods = j.DamageModifiers;
  if (!Array.isArray(mods)) continue;
  totalEntries += mods.length;

  const seen = new Map();
  const invalid = [], dup = [], zero = [], scripts = [];
  let firstMul = -1, reduceAfterMul = [];

  mods.forEach((raw, i) => {
    const s = String(raw);
    if (s.trim().startsWith('$')) { scripts.push(i); return; }         // 脚本条目（实测整层失效）
    const m = s.match(RE);
    if (!m) { invalid.push({ i, s }); return; }
    const key = `${m.groups.prefix ?? ''}${m.groups.id} ${m.groups.operator ?? ''}`;
    if (seen.has(key)) dup.push({ i, s, first: seen.get(key) });
    else seen.set(key, i);
    if (m.groups.operator === '*') {
      const v = Number(m.groups.value);
      if (v === 0) zero.push({ i, s });
      if (firstMul < 0) firstMul = i;
    } else if ((m.groups.operator === '-' || m.groups.operator === '+') && firstMul >= 0) {
      reduceAfterMul.push({ i, s, firstMul });
    }
  });

  if (scripts.length) scriptFiles.push(`${f}(${scripts.length} 条)`);
  if (invalid.length || dup.length || zero.length || reduceAfterMul.length) {
    problems.push({ f, id: j.ID, invalid, dup, zero, reduceAfterMul });
  }
}

console.log(`审计文件: ${files.length}，DamageModifiers 条目合计: ${totalEntries}`);
console.log(`含 $ 脚本条目的文件: ${scriptFiles.length} → ${scriptFiles.join(', ')}`);
console.log(`\n===== 有问题的文件 =====`);
if (!problems.length) console.log('（无格式/重复/归零/顺序问题）');
for (const p of problems) {
  console.log(`\n${p.f}  (${p.id})`);
  for (const x of p.invalid) console.log(`  [格式不合法] #${x.i} "${x.s}"  ← SBW 会忽略并告警`);
  for (const x of p.dup) console.log(`  [重复] #${x.i} "${x.s}" 与 #${x.first} 同源同操作符`);
  for (const x of p.zero) console.log(`  [归零风险] #${x.i} "${x.s}" 会把伤害乘成 0`);
  for (const x of p.reduceAfterMul) console.log(`  [顺序] #${x.i} "${x.s}" 是加减，但前面 #${x.firstMul} 已有乘法 → 加减作用在已缩放的值上`);
}

console.log(`\n===== 顺序快照（前 12 条，看 ± 与 × 的相对位置）=====`);
for (const f of ['fcp__bmp2.json', 'dragonrise__zbd04a.json', 'dragonrise__ztz99a.json', 'dragonrise__zbl08.json']) {
  if (!files.includes(f)) continue;
  const j = lenient(fs.readFileSync(path.join(DIR, f), 'utf8'));
  const mods = j.DamageModifiers || [];
  console.log(`\n${j.ID}（共 ${mods.length} 条）`);
  mods.forEach((s, i) => {
    const t = String(s);
    const kind = t.trim().startsWith('$') ? '脚本' : (t.match(/\*\s*[\d.]+$/) ? '×' : '±');
    console.log(`  #${String(i).padStart(2)} [${kind}] ${t}`);
  });
}

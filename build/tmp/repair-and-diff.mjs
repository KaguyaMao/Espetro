// repair-and-diff.mjs — 容错修复 JSON 语法错误并比较（不写回服务器）
import fs from 'fs';

const src = process.argv[2];
const ref = process.argv[3];
let raw = fs.readFileSync(src, 'utf8');
const fixes = [];

// 1) 去掉数组/对象末尾的多余逗号
let prev;
do {
  prev = raw;
  raw = raw.replace(/,(\s*[\]}])/g, '$1');
} while (raw !== prev);

// 2) 补齐缺失的逗号：'}' 或 ']' 之后紧跟 '  "key":'
raw = raw.replace(/([}\]])(\s*\n\s*")/g, '$1,$2');

let j;
try { j = JSON.parse(raw); }
catch (e) {
  console.log('修复后仍然无法解析: ' + e.message);
  const m = /position (\d+)/.exec(e.message);
  if (m) {
    const pos = Number(m[1]);
    const lines = raw.slice(0, pos).split('\n');
    console.log(`  位置行 ${lines.length}: ${String(lines[lines.length - 1]).trim().slice(0, 100)}`);
  }
  process.exit(1);
}
console.log('修复后解析成功。原始语法问题：');
console.log('  · 数组/对象末尾多余逗号：已清理');
console.log('  · 对象之间缺失逗号：已补齐');

if (ref) {
  const r = JSON.parse(fs.readFileSync(ref, 'utf8'));
  const flat = (o, p = '') => {
    const out = {};
    if (o === null || typeof o !== 'object') { out[p] = o; return out; }
    if (Array.isArray(o)) { out[p] = JSON.stringify(o); return out; }
    for (const [k, v] of Object.entries(o)) Object.assign(out, flat(v, p ? p + '.' + k : k));
    return out;
  };
  const A = flat(j), B = flat(r);
  const keys = new Set([...Object.keys(A), ...Object.keys(B)]);
  const onlyA = [], onlyB = [], diff = [];
  for (const k of [...keys].sort()) {
    const inA = k in A, inB = k in B;
    if (inA && !inB) onlyA.push(`${k} = ${String(A[k]).slice(0, 70)}`);
    else if (!inA && inB) onlyB.push(`${k} = ${String(B[k]).slice(0, 70)}`);
    else if (JSON.stringify(A[k]) !== JSON.stringify(B[k])) diff.push(`${k}: ${String(A[k]).slice(0, 45)} → ${String(B[k]).slice(0, 45)}`);
  }
  console.log(`\n对比基准: ${ref}`);
  console.log(`仅服务器新文件有 (${onlyA.length}):`);
  for (const l of onlyA.slice(0, 30)) console.log('  + ' + l);
  console.log(`仅基准有 (${onlyB.length}):`);
  for (const l of onlyB.slice(0, 30)) console.log('  - ' + l);
  console.log(`值不同 (${diff.length}):`);
  for (const l of diff.slice(0, 40)) console.log('  ~ ' + l);
}

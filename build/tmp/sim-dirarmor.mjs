// sim-dirarmor.mjs — 用 DirectionArmorRule 的同款正则离线推演倍率
import fs from 'node:fs';

const ANGLE = /getSourceAngle\s*\(\s*source\s*,\s*(-?\d+(?:\.\d*)?)\s*\)/;
const GATE = /getHealth\s*\(\s*\)\s*([<>]=?)\s*(-?\d+(?:\.\d*)?)\s*\?\s*(-?\d+(?:\.\d*)?)\s*:\s*(-?\d+(?:\.\d*)?)/;

function parseRule(script) {
  const text = script.trim().replace(/^\$/, '');
  const a = ANGLE.exec(text);
  if (!a) return null;
  const gate = GATE.exec(text);
  return { m: Number(a[1]), gate: gate ? { op: gate[1], thr: Number(gate[2]), t: Number(gate[3]), f: Number(gate[4]) } : null };
}
function apply(rule, dot, health) {
  let r = Math.max(1 - rule.m * dot, 0.5);
  if (rule.gate) {
    const g = rule.gate;
    const cmp = { '>': health > g.thr, '>=': health >= g.thr, '<': health < g.thr, '<=': health <= g.thr }[g.op];
    r *= cmp ? g.t : g.f;
  }
  return r;
}

const files = process.argv.slice(2);
const rows = [];
for (const f of files) {
  const t = fs.readFileSync(f, 'utf8');
  const j = JSON.parse(t);
  const dollars = (j.DamageModifiers ?? []).filter((x) => typeof x === 'string' && x.trim().startsWith('$') && /getSourceAngle/.test(x));
  for (const s of dollars) {
    const rule = parseRule(s);
    rows.push({ file: f.replace(/^.*[\\/]/, ''), script: s, rule, front: apply(rule, 1, 100), side: apply(rule, 0, 100), back: apply(rule, -1, 100) });
  }
}
console.log('文件 | 正面(×) | 侧面(×) | 背面(×) | 规则');
const seen = new Set();
for (const r of rows) {
  const key = r.script;
  const tag = seen.has(key) ? '(同上)' : '';
  seen.add(key);
  console.log(`${r.file.padEnd(34)} ${r.front.toFixed(4)} ${r.side.toFixed(4)} ${r.back.toFixed(4)}  ${tag ? tag : r.script}`);
}

// 载具层面的总倍率（同车所有规则连乘 —— 游戏里真正生效的值）
console.log('\n=== 载具层面总倍率（同车规则连乘）===');
for (const f of files) {
  const t = fs.readFileSync(f, 'utf8');
  const j = JSON.parse(t);
  const rs = (j.DamageModifiers ?? []).filter((x) => typeof x === 'string' && x.trim().startsWith('$') && /getSourceAngle/.test(x)).map(parseRule);
  const prod = (dot) => rs.reduce((p, r) => p * apply(r, dot, 100), 1);
  console.log(`${f.replace(/^.*[\\/]/, '').padEnd(34)} 正面 ${prod(1).toFixed(5)} | 侧面 ${prod(0).toFixed(5)} | 背面 ${prod(-1).toFixed(5)}  (${rs.length} 条规则)`);
}

console.log(`\n共 ${rows.length} 条方向规则（${new Set(rows.map((r) => r.script)).size} 种写法）`);
const uniq = [...new Set(rows.map((r) => r.script))];
for (const s of uniq) {
  const rule = parseRule(s);
  console.log(`\n规则: ${s}`);
  console.log(`  解析: m=${rule.m}${rule.gate ? `, gate=health ${rule.gate.op} ${rule.gate.thr} ? ${rule.gate.t} : ${rule.gate.f}` : ''}`);
  console.log(`  正面 dot=+1 → ${apply(rule, 1, 100).toFixed(6)}`);
  console.log(`  侧面 dot= 0 → ${apply(rule, 0, 100).toFixed(6)}`);
  console.log(`  背面 dot=-1 → ${apply(rule, -1, 100).toFixed(6)}`);
}

// veh-explosion-table.mjs — 迫击炮弹(custom_explosion)对各类载具的爆心伤害与所需发数
import fs from 'node:fs';
import path from 'node:path';

const DIR = 'veh-angle/after';
const SBW = 'sbwsrc/data/superbwarfare';
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
function loadTags(kind) {
  const dir = path.join(SBW, 'tags', kind); const tags = new Map();
  if (!fs.existsSync(dir)) return tags;
  for (const f of fs.readdirSync(dir).filter(x => x.endsWith('.json'))) {
    tags.set('superbwarfare:' + f.replace(/\.json$/, ''), (lenient(fs.readFileSync(path.join(dir, f), 'utf8')).values || []).map(v => typeof v === 'string' ? v : v.id).filter(Boolean));
  }
  return tags;
}
const DT = loadTags('damage_type'), ET = loadTags('entity_types');
const inTag = (t, tag, id) => (t.get(tag.replace(/^#/, '')) || []).includes(id);

function parseEntry(raw) {
  const s = String(raw).trim();
  if (s.startsWith('$')) return { script: true };
  const m = s.match(/^(\S+)\s*([*+\-/])\s*(-?[\d.]+)$/);
  if (!m) return null;
  const [, source, op, num] = m; const v = Number(num);
  return { source, op, value: op === '*' ? v : (op === '+' ? v : -v) };
}
function matches(source, ctx) {
  if (source === 'All') return true;
  if (source.startsWith('@#')) return inTag(ET, source.slice(1), ctx.entityType);
  if (source.startsWith('@')) return source.slice(1) === ctx.entityType;
  if (source.startsWith('#')) return inTag(DT, source, ctx.damageType);
  return source === ctx.damageType;
}
function chain(mods, ctx, amount, verbose) {
  let a = amount; const used = [];
  for (const raw of (mods || [])) {
    const e = parseEntry(raw);
    if (!e || e.script || !matches(e.source, ctx)) continue;
    a = e.op === '*' ? a * e.value : Math.max(a + e.value, 0);
    used.push(`${raw} → ${a.toFixed(2)}`);
  }
  return verbose ? { value: a, used } : a;
}
const ctx = { entityType: 'superbwarfare:mortar_shell', damageType: 'superbwarfare:custom_explosion' };
const HP = { 'fcp:bmp2': 'BMP-2 履带', 'dragonrise_reforge:zbd04a': 'ZBD-04A 履带', 'dragonrise_reforge:zbl08': 'ZBL-08 轮式',
  'fcp:btr82': 'BTR-82A 轮式', 'dragonrise_reforge:m3a3': 'M3A3 履带', 'dragonrise_reforge:ztz99a': 'ZTZ-99A 主战',
  'dragonrise_reforge:m1a2sepv2': 'M1A2 SEPv2 主战' };

console.log('| 载具 | 血量 | 爆炸抗性链倍率 | 默认100 爆心伤害 | 默认发数 | 强化450 爆心伤害 | 强化发数 |');
console.log('|---|---|---|---|---|---|---|');
for (const f of fs.readdirSync(DIR).filter(x => x.endsWith('.json'))) {
  const j = lenient(fs.readFileSync(path.join(DIR, f), 'utf8'));
  if (!HP[j.ID]) continue;
  const mul = chain(j.DamageModifiers, ctx, 100) / 100;
  const d100 = chain(j.DamageModifiers, ctx, 100);
  const d450 = chain(j.DamageModifiers, ctx, 450);
  const n100 = Math.ceil(j.MaxHealth / d100), n450 = Math.ceil(j.MaxHealth / d450);
  console.log(`| ${HP[j.ID]} | ${j.MaxHealth} | ${mul.toFixed(4)}× | ${d100.toFixed(1)} | ${n100} 发 | ${d450.toFixed(1)} | ${n450} 发 |`);
}
console.log('\n--- 明细（ZBD-04A / ZTZ-99A）---');
for (const f of ['dragonrise__zbd04a.json', 'dragonrise__ztz99a.json']) {
  const j = lenient(fs.readFileSync(path.join(DIR, f), 'utf8'));
  const r = chain(j.DamageModifiers, ctx, 100, true);
  console.log(`${j.ID}: ${r.used.join(' ; ')}`);
}
console.log('\n注：爆炸有距离衰减，上表是爆心（0 距离）值；垂直落体角度系数≈1.0，未计入。');

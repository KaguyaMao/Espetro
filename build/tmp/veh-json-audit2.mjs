// veh-json-audit2.mjs — 修正版：真重复（同源同操作符同数值）/ 顺序 / 实际抗性倍率
import fs from 'node:fs';
import path from 'node:path';

const DIR = process.argv[2] ?? 'veh-angle/before';
const RE = /^(?<prefix>(@#|#|@)?)(?<id>\w+(:\w+)?)\s*(?<operator>[-*+]?)\s*(?<value>([+-]?\d+(\.\d*)?)?)$/;
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

function parse(raw) {
  const s = String(raw).trim();
  if (s.startsWith('$')) return { script: true, raw: s };
  const m = s.match(RE);
  if (!m) return { invalid: true, raw: s };
  const g = m.groups;
  const src = `${g.prefix ?? ''}${g.id}`;
  const op = g.operator ?? '';
  const val = g.value === undefined || g.value === '' ? 0 : Number(g.value);
  return { src, op, val, raw: s };
}
function match(src, ctx) {
  if (src === 'All') return true;
  if (src.startsWith('@#')) return inTag(ET, src.slice(1), ctx.entityType);
  if (src.startsWith('@')) return src.slice(1) === ctx.entityType;
  if (src.startsWith('#')) return inTag(DT, src, ctx.damageType);
  return src === ctx.damageType;
}
function chain(mods, ctx, base = 100, trace) {
  let a = base;
  for (const raw of mods) {
    const e = parse(raw);
    if (!e || e.script || e.invalid || !match(e.src, ctx)) continue;
    if (e.op === '*') a *= e.val;
    else if (e.op === '-') a = Math.max(a - e.val, 0);
    else if (e.op === '+') a = Math.max(a + e.val, 0);
    if (trace) trace.push(`${e.raw} → ${a.toFixed(3)}`);
  }
  return a;
}

const KEY = [
  ['主炮炮弹(命中)', { entityType: 'superbwarfare:cannon_shell', damageType: 'superbwarfare:projectile_hit' }],
  ['机炮炮弹(命中)', { entityType: 'superbwarfare:small_cannon_shell', damageType: 'superbwarfare:projectile_hit' }],
  ['迫击炮弹(爆炸)', { entityType: 'superbwarfare:mortar_shell', damageType: 'superbwarfare:custom_explosion' }],
  ['原版TNT', { entityType: 'minecraft:tnt', damageType: 'minecraft:explosion' }],
];

const files = fs.readdirSync(DIR).filter(f => f.endsWith('.json') && f !== 'manifest.json').sort();
const dupFiles = [], orderFiles = [], invalidFiles = [], scriptFiles = [];
console.log('| 载具 | 血量 | 主炮炮弹 | 机炮炮弹 | 迫击炮弹 | TNT | 真重复 | 顺序异常 | 非法条目 |');
console.log('|---|---|---|---|---|---|---|---|---|');
for (const f of files) {
  const j = lenient(fs.readFileSync(path.join(DIR, f), 'utf8'));
  const mods = j.DamageModifiers || [];
  const seen = new Map(), dup = [], invalid = [], scripts = [];
  let firstMul = -1, badOrder = [];
  mods.forEach((raw, i) => {
    const e = parse(raw);
    if (!e || e.script) { if (e?.script) scripts.push(i); return; }
    if (e.invalid) { invalid.push(i); return; }
    const k = `${e.src} ${e.op} ${e.val}`;
    if (seen.has(k)) dup.push(`#${i}=#${seen.get(k)}`); else seen.set(k, i);
    if (e.op === '*') { if (firstMul < 0) firstMul = i; }
    else if (firstMul >= 0 && (e.src === 'All' || e.src.startsWith('@superbwarfare:cannon') || e.src.startsWith('@superbwarfare:small'))) {
      badOrder.push(`#${i}(${e.raw}) 在 #${firstMul} 乘法之后`);
    }
  });
  if (dup.length) dupFiles.push(`${j.ID}: ${dup.join(', ')}`);
  if (badOrder.length) orderFiles.push(`${j.ID}: ${badOrder.join('; ')}`);
  if (invalid.length) invalidFiles.push(`${j.ID}: ${invalid.join(',')}`);
  if (scripts.length) scriptFiles.push(`${j.ID}(${scripts.length})`);
  const vals = KEY.map(([, ctx]) => chain(mods, ctx).toFixed(1)).join(' | ');
  console.log(`| ${j.ID} | ${j.MaxHealth} | ${vals} | ${dup.length} | ${badOrder.length} | ${invalid.length} |`);
}
console.log(`\n真重复: ${dupFiles.length} 个文件`);
dupFiles.forEach(x => console.log('  ' + x));
console.log(`\n顺序异常(炮弹类加减在乘法之后): ${orderFiles.length} 个文件`);
orderFiles.slice(0, 10).forEach(x => console.log('  ' + x));
console.log(`\n非法格式条目: ${invalidFiles.length} 个文件`); invalidFiles.forEach(x => console.log('  ' + x));
console.log(`\n脚本条目(实测失效): ${scriptFiles.length} 个文件`);

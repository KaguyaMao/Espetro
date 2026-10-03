// veh-angle-ttk.mjs — 加装 $getSourceAngle 后的互毁矩阵（正面/侧面/背面三分支）
// 用法: node veh-angle-ttk.mjs <afterDir> <sbwDataDir> [multiplier=0.35]
import fs from 'node:fs';
import path from 'node:path';

const VEH_DIR = process.argv[2] ?? 'veh-angle/after';
const SBW_DATA = process.argv[3] ?? 'sbwsrc/data/superbwarfare';
const M = Number(process.argv[4] ?? 0.35);
const ANGLE = { 正面: 1 - M, 侧面: 1.0, 背面: 1 + M };

function lenientParse(text) {
  let out = '', inStr = false, esc = false;
  for (let i = 0; i < text.length; i++) {
    const c = text[i];
    if (inStr) { out += c; if (esc) esc = false; else if (c === '\\') esc = true; else if (c === '"') inStr = false; continue; }
    if (c === '"') { inStr = true; out += c; continue; }
    out += c;
  }
  return JSON.parse(out.replace(/,(\s*[}\]])/g, '$1'));
}
const load = (p) => lenientParse(fs.readFileSync(p, 'utf8'));
function loadTags(kind) {
  const dir = path.join(SBW_DATA, 'tags', kind);
  const tags = new Map();
  if (!fs.existsSync(dir)) return tags;
  for (const f of fs.readdirSync(dir).filter(x => x.endsWith('.json'))) {
    tags.set('superbwarfare:' + f.replace(/\.json$/, ''),
      (load(path.join(dir, f)).values || []).map(v => (typeof v === 'string' ? v : v.id)).filter(Boolean));
  }
  return tags;
}
const DT_TAGS = loadTags('damage_type'), ET_TAGS = loadTags('entity_types');
const inTag = (tags, tag, id) => (tags.get(tag.replace(/^#/, '')) || []).includes(id);

function parseEntry(raw) {
  const s = String(raw).trim();
  const script = s.match(/^\$entity\.getSourceAngle\(source,\s*([\d.]+)\)\s*\*\s*damage$/);
  if (script) return { script: Number(script[1]) };
  const m = s.match(/^(\S+)\s*([*+\-/])\s*(-?[\d.]+)$/);
  if (!m) return null;
  const [, source, op, num] = m;
  const v = Number(num);
  return { source, op, value: op === '*' ? v : (op === '+' ? v : -v) };
}
function matches(source, ctx) {
  if (source === 'All') return true;
  if (source.startsWith('@#')) return inTag(ET_TAGS, source.slice(1), ctx.entityType);
  if (source.startsWith('@')) return source.slice(1) === ctx.entityType;
  if (source.startsWith('#')) return inTag(DT_TAGS, source, ctx.damageType);
  return source === ctx.damageType;
}
/** 按抗性链结算；脚本条目按给定 factor（正面 0.65 / 侧面 1.0 / 背面 1.35）参与 */
function damageVs(targetMods, ctx, amount, factor) {
  let a = amount;
  for (const raw of (targetMods || [])) {
    const e = parseEntry(raw);
    if (!e) continue;
    if (e.script !== undefined) { a = a * factor; continue; }
    if (!matches(e.source, ctx)) continue;
    a = e.op === '*' ? a * e.value : Math.max(a + e.value, 0);
  }
  return a;
}

const VIABLE = ['superbwarfare:cannon_shell', 'superbwarfare:small_cannon_shell', 'superbwarfare:wire_guide_missile'];
const vehicles = [];
for (const f of fs.readdirSync(VEH_DIR).filter(x => x.endsWith('.json') && x !== 'manifest.json').sort()) {
  const j = load(path.join(VEH_DIR, f));
  const weapons = [];
  for (const [k, w] of Object.entries(j.Weapons || {})) {
    if (VIABLE.includes(w.Projectile)) weapons.push({ label: k, ...w });
    for (const a of (Array.isArray(w.AmmoType) ? w.AmmoType : [])) {
      const o = a?.Override;
      if (o?.Projectile && VIABLE.includes(o.Projectile)) weapons.push({ ...w, ...o, label: `${k}·${String(a.Ammo).split(':')[1]}` });
    }
  }
  vehicles.push({ ...j, weapons });
}
const CN = {
  'dragonrise_reforge:zbl08': 'ZBL-08 轮式', 'fcp:btr82': 'BTR-82A 轮式',
  'dragonrise_reforge:zbd04a': 'ZBD-04A 履带', 'dragonrise_reforge:m3a3': 'M3A3 履带',
  'dragonrise_reforge:ztz99a': 'ZTZ-99A 主战', 'dragonrise_reforge:m1a2sepv2': 'M1A2 主战',
};
const nm = (id) => CN[id] ?? id;
const byId = new Map(vehicles.map(v => [v.ID, v]));
const timeFor = (w, n) => (n - 1) * (w.RPM ? 60 / w.RPM : 0) + Math.floor((n - 1) / (w.Magazine ?? Infinity)) * (w.EmptyReloadTime ? w.EmptyReloadTime / 20 : 0);

const PICK = {
  'dragonrise_reforge:zbl08': 'Cannon',
  'dragonrise_reforge:zbd04a': '100MM_Cannon·medium_anti_ground_missile',
  'dragonrise_reforge:ztz99a': 'Cannon',
};
const reps = Object.keys(PICK);
const targets = ['dragonrise_reforge:zbl08', 'dragonrise_reforge:zbd04a', 'dragonrise_reforge:ztz99a'];

console.log(`# 互毁矩阵（含角度减伤：正面 ×${(1 - M).toFixed(2)} / 侧面 ×1.00 / 背面 ×${(1 + M).toFixed(2)}）\n`);
console.log('| 攻 \\ 守 | ' + targets.map(id => nm(id)).join(' | ') + ' |');
console.log('|' + '---|'.repeat(targets.length + 1));
for (const aid of reps) {
  const a = byId.get(aid);
  const w = a.weapons.find(x => x.label === PICK[aid]);
  const cells = targets.map(tid => {
    const t = byId.get(tid);
    const parts = Object.entries(ANGLE).map(([k, factor]) => {
      const d = damageVs(t.DamageModifiers, { entityType: w.Projectile, damageType: 'superbwarfare:projectile_hit' }, w.Damage, factor);
      const n = Math.ceil(t.MaxHealth / d);
      return `${k} **${n}发/${timeFor(w, n).toFixed(1)}s**`;
    });
    return parts.join(' · ');
  });
  console.log(`| **${nm(aid)}** ${w.label} | ${cells.join(' | ')} |`);
}

console.log('\n# 与改动前对比（侧面=未变，正面/背面为新增）\n');
console.log('| 攻击方 | 目标 | 单发(侧) | 正面 | 背面 | 侧 | 正面 | 背面 |');
console.log('|---|---|---|---|---|---|---|---|');
for (const aid of reps) {
  const a = byId.get(aid);
  const w = a.weapons.find(x => x.label === PICK[aid]);
  for (const tid of targets) {
    const t = byId.get(tid);
    const d = (f) => damageVs(t.DamageModifiers, { entityType: w.Projectile, damageType: 'superbwarfare:projectile_hit' }, w.Damage, f);
    const n = (f) => Math.ceil(t.MaxHealth / d(f));
    console.log(`| ${nm(aid)} ${w.label} | ${nm(tid)} | ${d(1).toFixed(1)} | ${d(1 - M).toFixed(1)} | ${d(1 + M).toFixed(1)} | ${n(1)}发 | ${n(1 - M)}发 | ${n(1 + M)}发 |`);
  }
}

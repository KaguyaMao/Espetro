// veh-ttk.mjs — 载具互毁计算：按 SBW 实际伤害管线算"多少炮 / 多久"
// 模型（来自字节码反编译）：
//   VehicleEntity.hurt:  amount -> DamageModifier.compute(...) -> 部件血量 & 车体血量
//   DamageModify.compute: IMMUNITY -> 0 ; REDUCE -> max(amount - value, 0) ; MULTIPLY -> amount * value
//   列表按顺序逐条应用；"+N" 表示 +N，"-N" 表示 -N
//   DamageSource 命中类型 = superbwarfare:projectile_hit（causeProjectileHitDamage），
//   directEntity = 炮弹实体（cannon_shell / small_cannon_shell / wire_guide_missile ...）
import fs from 'node:fs';
import path from 'node:path';

const VEH_DIR = process.argv[2] ?? 'veh-now';
const SBW_DATA = process.argv[3] ?? 'sbwsrc/data/superbwarfare';

// ---------- 宽松 JSON ----------
function lenientParse(text) {
  let out = '', inStr = false, esc = false;
  for (let i = 0; i < text.length; i++) {
    const c = text[i];
    if (inStr) { out += c; if (esc) esc = false; else if (c === '\\') esc = true; else if (c === '"') inStr = false; continue; }
    if (c === '"') { inStr = true; out += c; continue; }
    if (c === '/' && text[i + 1] === '/') { while (i < text.length && text[i] !== '\n') i++; continue; }
    if (c === '/' && text[i + 1] === '*') { const e = text.indexOf('*/', i + 2); i = e < 0 ? text.length : e + 1; continue; }
    out += c;
  }
  return JSON.parse(out.replace(/,(\s*[}\]])/g, '$1'));
}
const load = (p) => lenientParse(fs.readFileSync(p, 'utf8'));

// ---------- 标签 ----------
function loadTags(kind) {
  const dir = path.join(SBW_DATA, 'tags', kind);
  const tags = new Map();
  if (!fs.existsSync(dir)) return tags;
  for (const f of fs.readdirSync(dir)) {
    if (!f.endsWith('.json')) continue;
    const name = 'superbwarfare:' + f.replace(/\.json$/, '');
    const j = load(path.join(dir, f));
    tags.set(name, (j.values || []).map(v => (typeof v === 'string' ? v : v.id)).filter(Boolean));
  }
  return tags;
}
const DT_TAGS = loadTags('damage_type');
const ET_TAGS = loadTags('entity_types');

function inTag(tags, tag, id) {
  const clean = tag.replace(/^#/, '');
  const list = tags.get(clean) || tags.get(clean.replace(/^superbwarfare:/, 'superbwarfare:')) || [];
  return list.includes(id);
}

// ---------- 单条抗性 ----------
function parseEntry(raw) {
  const m = String(raw).trim().match(/^(\S+)\s*([*+\-/])\s*(-?[\d.]+)$/);
  if (!m) return null;
  const op = m[2];
  const v = Number(m[3]);
  // "+N" => 伤害 +N（内部 value=-N）；"-N" => 伤害 -N
  const delta = op === '*' ? null : (op === '+' ? v : (op === '-' ? -v : null));
  return { source: m[1], op, value: op === '*' ? v : delta, raw: String(raw).trim() };
}

function matches(source, ctx) {
  if (source === 'All') return true;
  if (source.startsWith('@#')) return inTag(ET_TAGS, source.slice(1), ctx.entityType); // 实体类型标签
  if (source.startsWith('@')) return source.slice(1) === ctx.entityType;              // 直接实体 id
  if (source.startsWith('#')) return inTag(DT_TAGS, source, ctx.damageType);          // 伤害类型标签
  return source === ctx.damageType;                                                   // 伤害类型 id
}

function applyMods(targetMods, ctx, amount, verbose) {
  let a = amount;
  const used = [];
  for (const raw of (targetMods || [])) {
    const e = parseEntry(raw);
    if (!e || !matches(e.source, ctx)) continue;
    if (e.op === '*') { a = a * e.value; used.push(`${e.raw} → ${a.toFixed(2)}`); }
    else { a = Math.max(a + e.value, 0); used.push(`${e.raw} → ${a.toFixed(2)}`); }
  }
  if (verbose) return { value: a, used };
  return a;
}

// ---------- 载具 ----------
const vehicles = [];
for (const f of fs.readdirSync(VEH_DIR).filter(x => x.endsWith('.json')).sort()) {
  const j = load(path.join(VEH_DIR, f));
  vehicles.push({ file: f, ...j });
}

const CN = {
  'dragonrise_reforge:zbl08': 'ZBL-08 轮式步战', 'fcp:btr82': 'BTR-82A 轮式步战',
  'dragonrise_reforge:m1126': 'M1126 斯崔克(仅机枪)', 'dragonrise_reforge:zbd04a': 'ZBD-04A 履带步战',
  'dragonrise_reforge:m3a3': 'M3A3 布莱德利', 'dragonrise_reforge:ztz99a': 'ZTZ-99A 主战坦克',
  'dragonrise_reforge:m1a2sepv2': 'M1A2 SEPv2 主战坦克',
};
const name = (id) => CN[id] ?? id;

// 主武器筛选：可打载具的火炮/导弹（含 AmmoType 里的炮射导弹等弹种变体）
const VIABLE = ['superbwarfare:cannon_shell', 'superbwarfare:small_cannon_shell', 'superbwarfare:wire_guide_missile'];
function mainWeapons(v) {
  const out = [];
  for (const [k, w] of Object.entries(v.Weapons || {})) {
    if (VIABLE.includes(w.Projectile)) out.push({ key: k, ...w });
    for (const a of (Array.isArray(w.AmmoType) ? w.AmmoType : [])) {
      const o = a?.Override;
      if (!o || !o.Projectile || !VIABLE.includes(o.Projectile)) continue;
      out.push({ ...w, ...o, key: `${k}[${String(a.Ammo).split(':')[1]}]` });
    }
  }
  return out;
}

const ctxFor = (proj, damageType = 'superbwarfare:projectile_hit') => ({ entityType: proj, damageType });

console.log('# 载具互毁计算（服务端当前数据）\n');
console.log('| 载具 | 血量 | 主武器 | 单发伤害 | 射速/装填 | 对各类目标的实际单发伤害 |');
console.log('|---|---|---|---|---|---|');
for (const v of vehicles) {
  for (const w of mainWeapons(v)) {
    const rpm = w.RPM && w.Projectile !== 'superbwarfare:cannon_shell' && w.RPM > 60 ? ` ${w.RPM} RPM` : '';
    const reload = w.EmptyReloadTime ? ` 装填 ${(w.EmptyReloadTime / 20).toFixed(1)}s` : '';
    const mag = w.Magazine ? ` 弹匣 ${w.Magazine}` : '';
    const per = vehicles.map(t => {
      const d = applyMods(t.DamageModifiers, ctxFor(w.Projectile), w.Damage);
      return `${name(t.ID).split(' ')[0]}:${d.toFixed(1)}`;
    }).join(' / ');
    console.log(`| ${name(v.ID)} | ${v.MaxHealth} | ${w.key} | ${w.Damage} |${rpm || ''}${reload}${mag} | ${per} |`);
  }
}

// ---------- 3x3 代表互毁 ----------
const REPS = ['dragonrise_reforge:zbl08', 'dragonrise_reforge:zbd04a', 'dragonrise_reforge:ztz99a',
              'fcp:btr82', 'dragonrise_reforge:m3a3', 'dragonrise_reforge:m1a2sepv2'];

function cycleSeconds(w) {
  if (w.Projectile === 'superbwarfare:small_cannon_shell') return 60 / (w.RPM || 330);
  if (w.EmptyReloadTime) return w.EmptyReloadTime / 20;
  return null;
}

console.log('\n# 代表载具互毁矩阵（主炮/机炮，全部命中车体）\n');
for (const aid of REPS) {
  const a = vehicles.find(v => v.ID === aid);
  if (!a) { console.log(`(缺少 ${aid})`); continue; }
  const w = mainWeapons(a).sort((x, y) => (y.Projectile === 'superbwarfare:cannon_shell' ? 1 : 0) - (x.Projectile === 'superbwarfare:cannon_shell' ? 1 : 0))[0];
  if (!w) continue;
  const cyc = cycleSeconds(w);
  console.log(`## ${name(aid)} — ${w.key}（标称 ${w.Damage}，${w.Projectile.split(':')[1]}，${w.Projectile === 'superbwarfare:small_cannon_shell' ? `${w.RPM} RPM ≈ ${cyc.toFixed(2)}s/发` : `装填 ${cyc.toFixed(1)}s`}）`);
  console.log('| 目标 | 血量 | 实际单发伤害 | 需要炮数 | 用时(首发命中后) |');
  console.log('|---|---|---|---|---|');
  for (const tid of REPS) {
    const t = vehicles.find(v => v.ID === tid);
    if (!t) continue;
    const d = applyMods(t.DamageModifiers, ctxFor(w.Projectile), w.Damage);
    const shots = Math.ceil(t.MaxHealth / d);
    const time = (shots - 1) * cyc;
    console.log(`| ${name(tid)} | ${t.MaxHealth} | ${d.toFixed(1)} | ${shots} | ${time.toFixed(1)}s |`);
  }
  console.log('');
}

// ---------- 抗性明细（炮弹相关）----------
console.log('\n# 各载具对两类炮弹的抗性链（含命中类型）\n');
for (const v of vehicles) {
  console.log(`## ${name(v.ID)}（HP ${v.MaxHealth}）`);
  for (const proj of ['superbwarfare:cannon_shell', 'superbwarfare:small_cannon_shell']) {
    const r = applyMods(v.DamageModifiers, ctxFor(proj), 100, true);
    console.log(`- 100 基础 × ${proj.split(':')[1]} → **${r.value.toFixed(1)}**  (${(r.value / 100).toFixed(3)}×)`);
    console.log(`  ${r.used.join(' ; ') || '无匹配条目'}`);
  }
  console.log('');
}

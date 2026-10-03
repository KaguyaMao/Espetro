// veh-ttk-matrix.mjs — 载具互毁矩阵（每台载具的每种反装甲武器一行）
// 伤害模型同 veh-ttk.mjs：REDUCE 绝对加减 + MULTIPLY 乘算，按 DamageModifiers 列表顺序
// 时间模型：time(n) = (n-1)*每发间隔 + floor((n-1)/弹匣)*装填
//   机炮（无弹匣字段）：间隔 = 60/RPM
//   单发主炮：弹匣=1、间隔=0、装填=EmptyReloadTime/20
//   双联导弹：间隔 = 60/RPM、装填 = EmptyReloadTime/20
import fs from 'node:fs';
import path from 'node:path';

const VEH_DIR = process.argv[2] ?? 'veh-now';
const SBW_DATA = process.argv[3] ?? 'sbwsrc/data/superbwarfare';

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
const DT_TAGS = loadTags('damage_type');
const ET_TAGS = loadTags('entity_types');
const inTag = (tags, tag, id) => (tags.get(tag.replace(/^#/, '')) || []).includes(id);

function parseEntry(raw) {
  const m = String(raw).trim().match(/^(\S+)\s*([*+\-/])\s*(-?[\d.]+)$/);
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
function applyMods(mods, ctx, amount) {
  let a = amount;
  for (const raw of (mods || [])) {
    const e = parseEntry(raw);
    if (!e || !matches(e.source, ctx)) continue;
    a = e.op === '*' ? a * e.value : Math.max(a + e.value, 0);
  }
  return a;
}

const VIABLE = ['superbwarfare:cannon_shell', 'superbwarfare:small_cannon_shell', 'superbwarfare:wire_guide_missile'];
const vehicles = [];
for (const f of fs.readdirSync(VEH_DIR).filter(x => x.endsWith('.json')).sort()) {
  const j = load(path.join(VEH_DIR, f));
  const weapons = [];
  for (const [k, w] of Object.entries(j.Weapons || {})) {
    if (VIABLE.includes(w.Projectile)) weapons.push({ label: k, ...w });
    for (const a of (Array.isArray(w.AmmoType) ? w.AmmoType : [])) {
      const o = a?.Override;
      if (o?.Projectile && VIABLE.includes(o.Projectile)) {
        weapons.push({ ...w, ...o, label: `${k}·${String(a.Ammo).split(':')[1]}` });
      }
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

function cycle(w) {
  return { rpm: w.RPM ? 60 / w.RPM : 0, mag: w.Magazine ?? Infinity, reload: w.EmptyReloadTime ? w.EmptyReloadTime / 20 : 0 };
}
function timeFor(w, n) {
  const { rpm, mag, reload } = cycle(w);
  return (n - 1) * rpm + Math.floor((n - 1) / mag) * reload;
}
const wdesc = (w) => `${w.label}（${w.Projectile.split(':')[1]} ${w.Damage}${w.IsArmorPiercingProjectile ? ' AP' : ''}${w.RPM ? ' ' + w.RPM + 'RPM' : ''}${w.EmptyReloadTime ? ' 装填' + (w.EmptyReloadTime / 20) + 's' : ''}${w.Magazine ? ' 弹匣' + w.Magazine : ''}）`;

const ORDER = ['dragonrise_reforge:zbl08', 'fcp:btr82', 'dragonrise_reforge:zbd04a', 'dragonrise_reforge:m3a3',
               'dragonrise_reforge:ztz99a', 'dragonrise_reforge:m1a2sepv2'];
const byId = new Map(vehicles.map(v => [v.ID, v]));

console.log('# 互毁矩阵（行=攻击方武器，列=目标；单元格 = 单发实伤 → 需要炮数 / 用时）\n');
console.log('目标血量：' + ORDER.map(id => `${nm(id)} ${byId.get(id).MaxHealth}`).join('，') + '\n');
console.log('| 攻击方 / 武器 | ' + ORDER.map(id => nm(id)).join(' | ') + ' |');
console.log('|' + '---|'.repeat(ORDER.length + 1));
for (const id of ORDER) {
  const a = byId.get(id);
  if (!a) continue;
  for (const w of a.weapons) {
    const cells = ORDER.map(tid => {
      const t = byId.get(tid);
      const d = applyMods(t.DamageModifiers, { entityType: w.Projectile, damageType: 'superbwarfare:projectile_hit' }, w.Damage);
      const n = Math.ceil(t.MaxHealth / d);
      return `${d.toFixed(1)} → **${n}发 / ${timeFor(w, n).toFixed(1)}s**`;
    });
    console.log(`| **${nm(id)}** ${wdesc(w)} | ${cells.join(' | ')} |`);
  }
}

console.log('\n# 3×3 代表矩阵（04A 按炮射导弹）\n');
const PICK = {
  'dragonrise_reforge:zbl08': 'Cannon',
  'dragonrise_reforge:zbd04a': '100MM_Cannon·medium_anti_ground_missile',
  'dragonrise_reforge:ztz99a': 'Cannon',
};
const reps = ['dragonrise_reforge:zbl08', 'dragonrise_reforge:zbd04a', 'dragonrise_reforge:ztz99a'];
console.log('| 攻 \\ 守 | ' + reps.map(id => nm(id)).join(' | ') + ' |');
console.log('|' + '---|'.repeat(reps.length + 1));
for (const aid of reps) {
  const a = byId.get(aid);
  const w = a.weapons.find(x => x.label === PICK[aid]);
  const cells = reps.map(tid => {
    const t = byId.get(tid);
    const d = applyMods(t.DamageModifiers, { entityType: w.Projectile, damageType: 'superbwarfare:projectile_hit' }, w.Damage);
    const n = Math.ceil(t.MaxHealth / d);
    return `**${n}发 / ${timeFor(w, n).toFixed(1)}s**（${d.toFixed(0)}/发）`;
  });
  console.log(`| **${nm(aid)}** ${w.label} | ${cells.join(' | ')} |`);
}

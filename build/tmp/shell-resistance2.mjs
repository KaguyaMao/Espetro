// Corrected shell-resistance analysis for SBW vehicles (server data).
// Semantics verified from decompiled SBW 0.8.9.1:
//   CannonShellEntity (superbwarfare:cannon_shell) / SmallCannonShellEntity
//   (superbwarfare:small_cannon_shell) deal damage type
//   "superbwarfare:projectile_hit" on direct hit (direct entity = the shell itself),
//   and their splash uses CustomExplosion -> "superbwarfare:custom_explosion".
//   "superbwarfare:projectile_explosion" belongs to mines (Ptkm etc.).
//   Tag #superbwarfare:projectile / #projectile_absolute contain gunfire* / arrow /
//   trident / tacz:bullet ... -> BULLETS ONLY, they never match shells.
//   Modifier string regex (SBW DamageModify):
//     ^(?<prefix>(@#|#|@)?)(?<id>\w+(:\w+)?)\s*(?<operator>[-*+]?)\s*(?<value>([+-]?\d+(\.\d*)?)?)$
//   applied in list order: * -> dmg*value ; - -> max(0, dmg-value) ; + -> max(0, dmg+value)
//   ( '+' is REDUCE with negated value ); no operator + value 0 -> immunity (0);
//   no operator + other value -> INVALID (unchanged).

import fs from 'node:fs';
import path from 'node:path';

const ROOT = 'D:/minecraft/modp/Espetro/build/tmp';
const VDIR = path.join(ROOT, process.argv[2] ?? 'srv-vehicles-fresh');
const TDIR = path.join(ROOT, 'sbw-tags');

// ---------- damage type tags ----------
const tagMap = new Map(); // "ns:tag" -> [entries]
for (const file of fs.readdirSync(TDIR)) {
  const ns = file.split('__')[1];
  const name = file.replace(/^data__[^_]+__tags__damage_type__/, '').replace(/\.json$/, '');
  const raw = fs.readFileSync(path.join(TDIR, file), 'utf8');
  let json;
  try { json = JSON.parse(raw); } catch { continue; }
  const vals = [];
  for (const v of json.values ?? []) {
    if (typeof v === 'string') vals.push(v.replace(/^#/, '#'));
    else if (v && typeof v.id === 'string') vals.push(v.id.replace(/^#/, '#'));
  }
  tagMap.set(`${ns}:${name}`, vals);
}
function tagHas(tagId, damageType, depth = 0) {
  if (depth > 8) return false;
  const vals = tagMap.get(tagId);
  if (!vals) return false;
  for (const v of vals) {
    if (v.startsWith('#')) { if (tagHas(v.slice(1), damageType, depth + 1)) return true; }
    else if (v === damageType) return true;
  }
  return false;
}

// ---------- modifier parsing ----------
const RE = /^(?<prefix>(@#|#|@)?)(?<id>\w+(:\w+)?)\s*(?<operator>[-*+]?)\s*(?<value>([+-]?\d+(\.\d*)?)?)$/;
function parseEntry(s) {
  const m = RE.exec(String(s).trim());
  if (!m) return { raw: s, invalid: true };
  const { prefix = '', id = '', operator = '', value = '' } = m.groups;
  return { raw: s, prefix, id, operator, value };
}
function matches(e, sc) {
  if (e.invalid) return false;
  if (e.prefix === '' && e.id === 'All') return true;
  if (e.prefix === '') return e.id === sc.damageType;
  if (e.prefix === '#') return tagHas(e.id, sc.damageType);
  if (e.prefix === '@') return e.id === sc.directEntity || (!sc.directEntity && e.id === sc.sourceEntity);
  if (e.prefix === '@#') return false; // entity-type tags: none used by these files
  return false;
}
function apply(dmg, e) {
  if (e.operator === '*') return dmg * parseFloat(e.value || '0');
  if (e.operator === '-') return Math.max(0, dmg - parseFloat(e.value || '0'));
  if (e.operator === '+') return Math.max(0, dmg + parseFloat(e.value || '0'));
  // no operator
  if (e.value === '0') return 0;   // IMMUNITY
  return dmg;                       // INVALID -> unchanged
}
function scenario(dmg, entries, sc) {
  const used = [];
  let d = dmg;
  for (const e of entries) {
    if (!matches(e, sc)) continue;
    const before = d;
    d = apply(d, e);
    used.push(`${e.raw}  [${before.toFixed(2)} -> ${d.toFixed(2)}]`);
  }
  return { dmg: d, used };
}

// ---------- vehicles ----------
const files = fs.readdirSync(VDIR).filter(f => f.endsWith('.json') && f !== 'manifest.json');
const SC = {
  ac:   { name: '机炮直击',   damageType: 'superbwarfare:projectile_hit',     directEntity: 'superbwarfare:small_cannon_shell',  sourceEntity: null, base: 65 },
  mg:   { name: '主炮直击',   damageType: 'superbwarfare:projectile_hit',     directEntity: 'superbwarfare:cannon_shell',        sourceEntity: null, base: 200 },
  acHE: { name: '机炮HE直击', damageType: 'superbwarfare:projectile_hit',     directEntity: 'superbwarfare:small_cannon_shell',  sourceEntity: null, base: 30 },
  ex:   { name: '炮弹爆炸',   damageType: 'superbwarfare:custom_explosion',   directEntity: 'superbwarfare:small_cannon_shell',  sourceEntity: null, base: 30 },
  exM:  { name: '主炮爆炸',   damageType: 'superbwarfare:custom_explosion',   directEntity: 'superbwarfare:cannon_shell',        sourceEntity: null, base: 30 },
  blt:  { name: '参考·子弹',  damageType: 'superbwarfare:gunfire',            directEntity: null,                                sourceEntity: null, base: 10 },
};

const rows = [];
for (const f of files) {
  const raw = fs.readFileSync(path.join(VDIR, f), 'utf8');
  let j;
  try { j = JSON.parse(raw); } catch { rows.push({ file: f, err: 'parse error' }); continue; }
  const entries = (j.DamageModifiers ?? []).map(parseEntry);
  const sc = {};
  for (const [k, def] of Object.entries(SC)) sc[k] = scenario(def.base, entries, def);
  // weapon damage summary (shell weapons only)
  const shells = [];
  const weapons = j.Weapons && typeof j.Weapons === 'object' ? j.Weapons : {};
  for (const [wn, w] of Object.entries(weapons)) {
    if (!w || typeof w !== 'object') continue;
    const proj = w.Projectile;
    if (proj === 'superbwarfare:small_cannon_shell' || proj === 'superbwarfare:cannon_shell') {
      shells.push({ weapon: wn, proj, damage: w.Damage, rpm: w.RPM });
      for (const a of (Array.isArray(w.AmmoType) ? w.AmmoType : [])) {
        if (a && typeof a === 'object' && a.Override?.Damage != null) shells.push({ weapon: wn, proj: a.Override.Projectile ?? proj, damage: a.Override.Damage, ammo: a.Ammo });
      }
    }
  }
  rows.push({
    file: f, id: j.ID, hp: j.MaxHealth, part: j.PartHealth, entries: entries.length, sc, shells,
    rawEntries: (j.DamageModifiers ?? []),
  });
}

// ---------- report ----------
const out = [];
out.push('# 服务端载具对「机炮炮弹 / 主炮炮弹」的抗性（修正版）');
out.push('');
out.push('判定依据（反编译 SBW 0.8.9.1 确认）：');
out.push('- `superbwarfare:cannon_shell`（主炮）与 `superbwarfare:small_cannon_shell`（机炮）**直接命中**时造成的伤害类型是 `superbwarfare:projectile_hit`，直接实体就是炮弹本身。');
out.push('- 炮弹**爆炸溅射**走 `CustomExplosion` → 伤害类型 `superbwarfare:custom_explosion`。');
out.push('- `#superbwarfare:projectile`（含 gunfire/arrow/trident/tacz:bullet…）与 `#superbwarfare:projectile_absolute`（gunfire_absolute…）**只对子弹生效，对炮弹无效**；`projectile_explosion` 是反坦克地雷（Ptkm 等）的伤害类型。');
out.push('- 条目按列表顺序结算：`*` 乘法；`-` = max(0, 伤害−值)；`+` = max(0, 伤害+值)。');
out.push('');
out.push('下表为「基础伤害 → 实收伤害」：机炮直击基准 65（30mm APDS）、机炮 HE 直击 30、主炮直击 200（100/125mm）、爆炸 30、子弹 10（参考）。');
out.push('');

const hdr = '| 载具 | 血量/炮塔 | 机炮直击 65 | 机炮HE 30 | 主炮直击 200 | 炮弹爆炸 30 | 参考·子弹 10 |';
const sep = '|---|---|---|---|---|---|---|';
out.push(hdr); out.push(sep);
for (const r of rows.filter(r => !r.err).sort((a, b) => String(a.id).localeCompare(String(b.id)))) {
  const g = k => {
    const s = r.sc[k];
    const mult = s.dmg / SC[k].base;
    return `${s.dmg.toFixed(1)} (×${mult.toFixed(3)})`;
  };
  const pt = r.part ? `炮塔${r.part.Turret ?? '-'} 轮${r.part.LeftWheel ?? '-'} 引擎${r.part.MainEngine ?? '-'}` : '无';
  out.push(`| ${r.id} | ${r.hp}/${pt} | ${g('ac')} | ${g('acHE')} | ${g('mg')} | ${g('ex')} | ${g('blt')} |`);
}
out.push('');
out.push('## 生效条目明细（只列对炮弹真正生效的条目）');
out.push('');
for (const r of rows.filter(r => !r.err).sort((a, b) => String(a.id).localeCompare(String(b.id)))) {
  out.push(`### ${r.id}  (HP ${r.hp})`);
  for (const k of ['ac', 'mg', 'ex']) {
    out.push(`  - ${SC[k].name}: ${r.sc[k].used.length ? r.sc[k].used.join('  |  ') : '（无任何条目生效 → 100%）'}`);
  }
  out.push('');
}
out.push('## 原始 DamageModifiers（全部条目，便于核对）');
out.push('');
for (const r of rows.filter(r => !r.err).sort((a, b) => String(a.id).localeCompare(String(b.id)))) {
  out.push(`### ${r.id}`);
  out.push('```');
  out.push(r.rawEntries.join('\n'));
  out.push('```');
  out.push('');
}
const bad = rows.filter(r => r.err);
if (bad.length) { out.push('## 解析失败'); bad.forEach(b => out.push(`- ${b.file}: ${b.err}`)); }

fs.writeFileSync(path.join(ROOT, process.argv[3] ?? 'shell-resistance2.md'), out.join('\n'), 'utf8');

// console summary
const byKey = new Map();
for (const r of rows.filter(r => !r.err)) {
  const key = `机炮×${(r.sc.ac.dmg / 65).toFixed(3)} 主炮×${(r.sc.mg.dmg / 200).toFixed(3)} 爆炸×${(r.sc.ex.dmg / 30).toFixed(3)}`;
  byKey.set(key, [...(byKey.get(key) ?? []), r.id]);
}
console.log('分组统计：');
for (const [k, v] of [...byKey.entries()].sort((a, b) => b[1].length - a[1].length)) {
  console.log(`\n[${v.length} 台] ${k}`);
  console.log('  ' + v.join(', '));
}

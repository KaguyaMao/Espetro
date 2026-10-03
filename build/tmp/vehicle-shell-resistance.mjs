import fs from 'node:fs';
import path from 'node:path';

// vehicle-shell-resistance.mjs — 统计服务端载具对"机炮炮弹/主炮炮弹"的抗性
// 用法: node vehicle-shell-resistance.mjs <已抓取载具目录>
const dir = process.argv[2];
const manifest = JSON.parse(fs.readFileSync(path.join(dir, 'manifest.json'), 'utf8'));

// 与炮弹相关的判定
const AUTOCANNON_ENTITY = 'superbwarfare:small_cannon_shell'; // 机炮炮弹（小口径）
const CANNON_ENTITY = 'superbwarfare:cannon_shell';           // 主炮炮弹（中/大口径）
const SHARED_DAMAGE_TYPES = new Set([
  'superbwarfare:projectile_hit',
  'superbwarfare:projectile_hit_headshot',
  'superbwarfare:projectile_explosion',
]);
const SHARED_TAGS = new Set(['#superbwarfare:projectile', '#superbwarfare:projectile_absolute']);

function parseEntry(raw) {
  const s = String(raw).trim();
  // 例: "@superbwarfare:cannon_shell * 1.5" / "All * 0.77" / "minecraft:lava + 10"
  const m = s.match(/^(\S+)\s*([*+\-/])\s*(-?[\d.]+)$/);
  if (!m) return { source: s, op: null, value: null, raw: s };
  return { source: m[1], op: m[2], value: Number(m[3]), raw: s };
}

function classify(e) {
  const src = e.source;
  const isAuto = src === `@${AUTOCANNON_ENTITY}`;
  const isCannon = src === `@${CANNON_ENTITY}`;
  const isShared = src === 'All' || (!src.startsWith('@') && !src.startsWith('#') && SHARED_DAMAGE_TYPES.has(src)) || SHARED_TAGS.has(src);
  return { isAuto, isCannon, isShared };
}

const rows = [];
for (const m of manifest.sort((a, b) => a.name.localeCompare(b.name))) {
  const j = JSON.parse(fs.readFileSync(path.join(dir, m.file), 'utf8'));
  const id = j.ID ?? `${m.ns}:${m.name.replace('.json', '')}`;
  const list = Array.isArray(j.DamageModifiers) ? j.DamageModifiers.map(parseEntry) : [];
  const entries = list.map(e => ({ ...e, ...classify(e) }));

  const auto = entries.filter(e => e.isAuto);
  const cannon = entries.filter(e => e.isCannon);
  const shared = entries.filter(e => e.isShared);

  const mul = (arr) => arr.filter(e => e.op === '*').reduce((s, e) => s * e.value, 1);
  const add = (arr) => arr.filter(e => e.op === '+' || e.op === '-').reduce((s, e) => s + (e.op === '-' ? -e.value : e.value), 0);
  const addText = (arr) => { const a = add(arr); return a === 0 ? '' : `${a > 0 ? '+' : ''}${a}`; };

  const sharedMul = mul(shared), sharedAdd = add(shared);
  const autoMul = sharedMul * mul(auto);
  const cannonMul = sharedMul * mul(cannon);
  const autoAdd = sharedAdd + add(auto);
  const cannonAdd = sharedAdd + add(cannon);

  rows.push({
    id, ns: m.ns, name: m.name,
    hasMods: list.length > 0,
    autoMul, cannonMul, autoAdd, cannonAdd,
    autoEntries: auto.map(e => e.raw), cannonEntries: cannon.map(e => e.raw),
    sharedEntries: shared.map(e => e.raw),
    applyDefault: j.ApplyDefaultDamageModifiers !== false,
  });
}

const pct = (m, a) => {
  const hurt = m !== 1 || a !== 0;
  return `${(m * 100).toFixed(1)}%${a !== 0 ? (a > 0 ? ` +${a}` : ` ${a}`) : ''}${hurt ? `（减伤 ${((1 - m) * 100).toFixed(1)}%）` : '（无减伤）'}`;
};

console.log('| 载具 | 机炮炮弹（small_cannon_shell） | 主炮炮弹（cannon_shell） |');
console.log('|---|---|---|');
for (const r of rows) {
  console.log(`| ${r.id} | ${pct(r.autoMul, r.autoAdd)} | ${pct(r.cannonMul, r.cannonAdd)} |`);
}

console.log('\n\n===== 明细（只列与炮弹有关的条目）=====');
for (const r of rows) {
  if (!r.autoEntries.length && !r.cannonEntries.length && !r.sharedEntries.length) {
    console.log(`\n### ${r.id}  —  没有与炮弹相关的 DamageModifiers${r.hasMods ? `（共有 ${'其他'} 条其它类型条目）` : '（完全没有 DamageModifiers）'}`);
    continue;
  }
  console.log(`\n### ${r.id}`);
  if (r.sharedEntries.length) console.log(`   通用（机炮/主炮都吃）: ${r.sharedEntries.join('  |  ')}`);
  if (r.autoEntries.length) console.log(`   机炮专属: ${r.autoEntries.join('  |  ')}`);
  if (r.cannonEntries.length) console.log(`   主炮专属: ${r.cannonEntries.join('  |  ')}`);
  console.log(`   合计倍率: 机炮 ×${r.autoMul}${r.autoAdd ? ` ${r.autoAdd > 0 ? '+' : ''}${r.autoAdd}` : ''}   主炮 ×${r.cannonMul}${r.cannonAdd ? ` ${r.cannonAdd > 0 ? '+' : ''}${r.cannonAdd}` : ''}`);
}

// supply-table-md.mjs — 完整表：每台载具部署自带弹药（与补给站 getAmmoRule 语义一致）
import fs from 'node:fs';
import path from 'node:path';

const TMP = 'D:/minecraft/modp/Espetro/build/tmp';
const VDIR = path.join(TMP, 'srv-vehicles-new');
const cfg = JSON.parse(fs.readFileSync(path.join(TMP, 'supply-default-clean.json'), 'utf8'));
const pick = (o, a, b) => (o && o[a] !== undefined ? o[a] : (o ? o[b] : undefined));
const fix = o => Number(pick(o, 'FixedAmount', 'fixedAmount') ?? 0);
const modeOf = o => String(pick(o, 'Mode', 'mode') ?? 'MAGAZINE');
const PSEUDO = {
  '@RifleAmmo': 'superbwarfare:rifle_ammo', '@HeavyAmmo': 'superbwarfare:heavy_ammo',
  '@ShotgunAmmo': 'superbwarfare:shotgun_ammo', '@SniperAmmo': 'superbwarfare:sniper_ammo',
  '@HandgunAmmo': 'superbwarfare:handgun_ammo',
};
const short = s => String(s).replace('superbwarfare:', '');

// 载具武器 → [{ammo, magazine}]
function weaponAmmo(j) {
  const out = [];
  for (const w of Object.values(j.Weapons ?? {})) {
    if (!w || typeof w !== 'object') continue;
    const magazine = Number(w.Magazine ?? 0) || 0;
    const ammos = Array.isArray(w.AmmoType) ? w.AmmoType : (w.AmmoType ? [w.AmmoType] : []);
    for (const a of ammos) {
      const id = typeof a === 'string' ? a : a && a.Ammo;
      if (typeof id !== 'string' || !id) continue;
      const real = id.startsWith('@') ? (PSEUDO[id] ?? id) : id;
      if (real.startsWith('@')) continue;
      out.push({ ammo: real, magazine });
    }
  }
  return out;
}

const rows = [];
for (const f of fs.readdirSync(VDIR).filter(x => x.endsWith('.json') && x !== 'manifest.json')) {
  let j; try { j = JSON.parse(fs.readFileSync(path.join(VDIR, f), 'utf8')); } catch { continue; }
  if (!j.ID) continue;
  const own = cfg.VehicleOverrides?.[j.ID] ?? null;
  const rule = own ?? cfg.Default;
  const ruleOverrides = pick(rule, 'AmmoOverrides', 'ammoOverrides') ?? {};
  const ruleMode = modeOf(rule), ruleFixed = fix(rule);

  const given = new Map();
  for (const { ammo, magazine } of weaponAmmo(j)) {
    const ar = ruleOverrides[ammo];
    const mode = ar ? modeOf(ar) : ruleMode;
    const fixed = ar ? fix(ar) : ruleFixed;
    const item = ar ? (pick(ar, 'CustomItem', 'customItem') || ammo) : ammo;
    let count;
    if (mode.toUpperCase() === 'PACKAGE') count = Math.max(1, pick(ar, 'CustomItemCount', 'customItemCount') ?? 1);
    else if (mode.toUpperCase() === 'MAGAZINE' && magazine > 0) count = magazine;
    else count = fixed > 0 ? fixed : 100;
    given.set(item, Math.max(given.get(item) ?? 0, count));
  }
  const rb = pick(rule, 'BonusItem', 'bonusItem') || (rule !== cfg.Default ? (pick(cfg.Default, 'BonusItem', 'bonusItem') || '') : '');
  if (rb) given.set(rb, pick(rule, 'BonusItemCount', 'bonusItemCount') || pick(cfg.Default, 'BonusItemCount', 'bonusItemCount') || 1);

  rows.push({ id: j.ID, own: !!own, given: [...given.entries()].map(([k, v]) => `${short(k)}×${v}`) });
}
rows.sort((a, b) => a.id.localeCompare(b.id));

const L = ['# 编制载具部署自带弹药全表（47 台，与补给站同规则）', '',
  '规则：有 `VehicleOverrides` 用专属；未列出的弹药走规则回退（FIXED→`FixedAmount`/100，MAGAZINE→一匣）。', '',
  '| 载具 | 配置 | 出生自带 |', '|---|---|---|'];
for (const r of rows) L.push(`| ${r.id} | ${r.own ? '专属' : 'Default'} | ${r.given.join(' , ') || '（无）'} |`);
const def = rows.filter(r => !r.own);
L.push('', `## 没有专属配置的车型（${def.length} 台）`, '',
  def.map(r => `- **${r.id}**：${r.given.join(' , ') || '（无弹药）'}`).join('\n'));
fs.writeFileSync(path.join(TMP, 'supply-loadout-table.md'), L.join('\n'), 'utf8');
console.log('rows=' + rows.length + ' default=' + def.length);

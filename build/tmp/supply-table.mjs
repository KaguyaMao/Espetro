// supply-table.mjs — 汇总每台载具部署时会自带的弹药（用服务器载具数据 + 补给站配置推算）
import fs from 'node:fs';
import path from 'node:path';

const TMP = 'D:/minecraft/modp/Espetro/build/tmp';
const VDIR = path.join(TMP, 'srv-vehicles-new');
const cfg = JSON.parse(fs.readFileSync(path.join(TMP, 'supply-default-clean.json'), 'utf8'));
const pick = (o, a, b) => (o && o[a] !== undefined ? o[a] : (o ? o[b] : undefined));
const fix = (o) => Number(pick(o, 'FixedAmount', 'fixedAmount') ?? 0);
const modeOf = (o) => String(pick(o, 'Mode', 'mode') ?? '');

// SBW 弹药伪 id → 实际物品 id
const PSEUDO = {
  '@RifleAmmo': 'superbwarfare:rifle_ammo',
  '@HeavyAmmo': 'superbwarfare:heavy_ammo',
  '@ShotgunAmmo': 'superbwarfare:shotgun_ammo',
  '@SniperAmmo': 'superbwarfare:sniper_ammo',
  '@HandgunAmmo': 'superbwarfare:handgun_ammo',
};

function ammoOf(j) {
  const set = new Set();
  const W = j.Weapons && typeof j.Weapons === 'object' ? j.Weapons : {};
  for (const w of Object.values(W)) {
    if (!w || typeof w !== 'object') continue;
    const ammos = Array.isArray(w.AmmoType) ? w.AmmoType : (w.AmmoType ? [w.AmmoType] : []);
    for (const a of ammos) {
      const id = typeof a === 'string' ? a : a && a.Ammo;
      if (typeof id !== 'string' || !id) continue;
      const real = id.startsWith('@') ? (PSEUDO[id] ?? id) : id;
      if (real.startsWith('@')) continue; // 未知伪 id
      set.add(real);
    }
  }
  return set;
}

const vehicles = [];
for (const f of fs.readdirSync(VDIR).filter(x => x.endsWith('.json') && x !== 'manifest.json')) {
  let j; try { j = JSON.parse(fs.readFileSync(path.join(VDIR, f), 'utf8')); } catch { continue; }
  if (!j.ID) continue;
  const id = j.ID;
  const own = cfg.VehicleOverrides ? cfg.VehicleOverrides[id] : null;
  const rule = own ?? cfg.Default;
  const ammo = ammoOf(j);
  const entries = Object.entries(rule.AmmoOverrides ?? {});
  const given = [];
  const skipped = [];
  for (const [key, ar] of entries) {
    if (!ammo.has(key)) { skipped.push(key); continue; }
    const item = pick(ar, 'CustomItem', 'customItem') || key;
    const count = modeOf(ar).toUpperCase() === 'PACKAGE'
      ? Math.max(1, pick(ar, 'CustomItemCount', 'customItemCount') ?? 1)
      : (fix(ar) > 0 ? fix(ar) : (fix(rule) > 0 ? fix(rule) : 100));
    given.push(`${item.replace('superbwarfare:', '')}×${count}`);
  }
  const ruleBonus = pick(rule, 'BonusItem', 'bonusItem') || ''
  const bonusId = ruleBonus !== '' ? ruleBonus
    : (rule !== cfg.Default && (pick(cfg.Default, 'BonusItem', 'bonusItem') || '') ? pick(cfg.Default, 'BonusItem', 'bonusItem') : '');
  const bonusCount = ruleBonus !== '' ? (pick(rule, 'BonusItemCount', 'bonusItemCount') ?? 1)
    : (rule !== cfg.Default && (pick(cfg.Default, 'BonusItem', 'bonusItem') || '') ? (pick(cfg.Default, 'BonusItemCount', 'bonusItemCount') ?? 1) : 0);
  if (bonusId) given.push(`${bonusId.replace('superbwarfare:', '')}×${bonusCount}`);
  vehicles.push({ id, own: !!own, given, skipped, vehicleAmmo: [...ammo] });
}

vehicles.sort((a, b) => (a.own === b.own ? a.id.localeCompare(b.id) : (a.own ? 1 : -1)));
console.log('== 有专属覆盖（VehicleOverrides）==');
for (const v of vehicles.filter(v => v.own)) {
  console.log(`${v.id}\n    ${v.given.join(' , ')}`);
}
console.log('\n== 无专属覆盖 → 走 Default（总数 ' + vehicles.filter(v => !v.own).length + '）==');
for (const v of vehicles.filter(v => !v.own)) {
  console.log(`${v.id}\n    自带: ${v.given.join(' , ')}\n    该车弹药: ${v.vehicleAmmo.join(', ') || '(无)'}\n    被过滤(用不到): ${v.skipped.join(', ') || '(无)'}`);
}

// List all weapon/ammo entries across server vehicles (HE detection + damage sources)
import fs from 'node:fs';
import path from 'node:path';

const VDIR = 'D:/minecraft/modp/Espetro/build/tmp/srv-vehicles-fresh';
const files = fs.readdirSync(VDIR).filter(f => f.endsWith('.json') && f !== 'manifest.json');

const rows = [];
for (const f of files) {
  let j;
  try { j = JSON.parse(fs.readFileSync(path.join(VDIR, f), 'utf8')); } catch { continue; }
  const id = j.ID ?? f;
  const W = j.Weapons;
  if (!W || typeof W !== 'object') continue;
  const entries = Array.isArray(W) ? W.map((n, i) => [n, j[`Weapon_${n}`]]) : Object.entries(W);
  for (const [wn, w] of Object.entries(W)) {
    if (!w || typeof w !== 'object') continue;
    const proj = w.Projectile ?? '';
    const ammos = Array.isArray(w.AmmoType) ? w.AmmoType : (w.AmmoType ? [w.AmmoType] : []);
    const ammoStr = ammos.map(a => {
      if (typeof a === 'string') return `${a}[${w.Damage ?? '-'}]`;
      const o = a.Override ?? {};
      return `${a.Ammo}->${o.Projectile ?? proj}[${o.Damage ?? w.Damage ?? '-'}]${o.IsHighExplosiveProjectile === true ? '(HE)' : ''}`;
    }).join(' , ');
    rows.push({
      id, wn, proj, dmg: w.Damage, exd: w.ExplosionDamage, exr: w.ExplosionRadius,
      he: w.IsHighExplosiveProjectile === true, ap: w.IsArmorPiercingProjectile === true,
      rpm: w.RPM, ammoStr, nAmmo: ammos.length,
    });
  }
}

const shellRows = rows.filter(r => /cannon_shell|mortar_shell|medium_rocket|gun_grenade|_shell/.test(r.proj) || /_he/.test(r.ammoStr));
console.log(`总武器条目 ${rows.length}，炮弹/HE 相关 ${shellRows.length}\n`);
for (const r of shellRows) {
  console.log(`${r.id} :: ${r.wn} | proj=${r.proj} | Dmg=${r.dmg} ExDmg=${r.exd} r=${r.exr} | HE=${r.he} AP=${r.ap} RPM=${r.rpm ?? '-'}`);
  console.log(`     ammo: ${r.ammoStr}`);
}

// distinct ammo ids containing _he
const heAmmo = new Set();
for (const r of shellRows) for (const m of r.ammoStr.matchAll(/([a-z_]+:[a-z0-9_]+)/g)) if (/_he\b/.test(m[1])) heAmmo.add(m[1]);
console.log('\nHE ammo ids: ' + [...heAmmo].sort().join(', '));
// weapons flagged HE at weapon level
console.log('\nweapon-level HE weapons: ' + [...new Set(rows.filter(r => r.he).map(r => `${r.id}/${r.wn}`))].join(', '));

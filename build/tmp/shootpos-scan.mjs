import fs from 'node:fs';
import path from 'node:path';

const KNOWN = new Set(['VehicleFlat','Turret','Barrel','WeaponStation','WeaponStationBarrel','Default']);
const roots = process.argv.slice(2);
if (!roots.length) { console.error('usage: node shootpos-scan.mjs <dir> [...]'); process.exit(1); }

function walk(dir, out = []) {
  for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
    const p = path.join(dir, e.name);
    if (e.isDirectory()) walk(p, out);
    else if (e.name.endsWith('.json')) out.push(p);
  }
  return out;
}

for (const root of roots) {
  console.log('══════ ' + root);
  const files = walk(root);
  for (const f of files) {
    let j;
    try { j = JSON.parse(fs.readFileSync(f, 'utf8')); } catch (e) { console.log(`  [解析失败] ${path.basename(f)}: ${e.message}`); continue; }
    const weapons = j?.Weapons || j?.weapons;
    const flags = [];
    if (weapons && typeof weapons === 'object') {
      for (const [wn, wd] of Object.entries(weapons)) {
        const sp = wd?.ShootPos ?? wd?.shootPos;
        if (!sp) { flags.push(`${wn}: <无 ShootPos>`); continue; }
        const t = sp.Transform ?? sp.transform ?? '<缺失→Default>';
        const n = Array.isArray(sp.Positions) ? sp.Positions.length : 0;
        const nd = Array.isArray(sp.Directions) ? sp.Directions.length : 0;
        const bits = [`T=${t}`, `P=${n}`, `D=${nd}`];
        if (!KNOWN.has(String(t))) bits.push('!!未知Transform(回退Default)');
        if (sp.BoundUpWithAmmoAmount === true) bits.push('byAmmo');
        if (sp.ShootPositionForHud) bits.push(`HUD=${JSON.stringify(sp.ShootPositionForHud)}`);
        if (sp.ShootDirectionForHud) bits.push(`HUDdir=${JSON.stringify(sp.ShootDirectionForHud)}`);
        if (sp.ViewPosition) bits.push(`View=${JSON.stringify(sp.ViewPosition)}`);
        if (sp.DefaultTransform && sp.DefaultTransform !== 'Default') bits.push(`DefT=${sp.DefaultTransform}`);
        if (n > 1) bits.push(`P0=${JSON.stringify(sp.Positions[0])}`);
        flags.push(`${wn}: ${bits.join('  ')}`);
      }
    } else flags.push('<无 Weapons 段>');
    console.log(`  —— ${path.basename(f)}`);
    for (const l of flags) console.log('       ' + l);
  }
}

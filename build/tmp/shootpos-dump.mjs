import fs from 'node:fs';
import path from 'node:path';

// 从 zip/jar 中导出指定 json 条目并打印 ShootPos 摘要（用 PowerShell 预先解包则不必要；此处直接读已解包目录）
const dir = process.argv[2];
const names = process.argv.slice(3);
const KNOWN = new Set(['VehicleFlat','Turret','Barrel','WeaponStation','WeaponStationBarrel','Default']);
for (const n of names) {
  const f = path.join(dir, n);
  if (!fs.existsSync(f)) { console.log(`[缺失] ${n}`); continue; }
  const j = JSON.parse(fs.readFileSync(f, 'utf8'));
  console.log(`═══ ${n}`);
  const weapons = j.Weapons || j.weapons || {};
  for (const [wn, wd] of Object.entries(weapons)) {
    const sp = wd?.ShootPos;
    if (!sp) { console.log(`   ${wn}: <无>`); continue; }
    const t = sp.Transform ?? '<缺失→Default>';
    console.log(`   ${wn}: T=${t}${KNOWN.has(String(t)) ? '' : ' !!未知'} P=${(sp.Positions||[]).length} D=${(sp.Directions||[]).length}`
      + (sp.BoundUpWithAmmoAmount ? ' byAmmo' : '')
      + (sp.ShootPositionForHud ? ` HUD=${JSON.stringify(sp.ShootPositionForHud)}` : '')
      + (sp.Positions?.length ? ` P0=${JSON.stringify(sp.Positions[0])}` : ''));
  }
  // 变换相关字段
  for (const k of ['TurretPos','BarrelPos','PassengerWeaponStationPos','PassengerWeaponStationBarrelPos','RotateOffsetHeight']) {
    if (j[k] !== undefined) console.log(`   [数据] ${k} = ${JSON.stringify(j[k])}`);
  }
}

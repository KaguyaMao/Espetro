// veh-weapons-dump.mjs — 打印载具当前武器/血量/抗性关键字段
// 用法: node veh-weapons-dump.mjs <载具JSON目录>
import fs from 'node:fs';
import path from 'node:path';

const dir = process.argv[2];
const files = fs.readdirSync(dir).filter(f => f.endsWith('.json'));

for (const f of files.sort()) {
  const j = JSON.parse(fs.readFileSync(path.join(dir, f), 'utf8'));
  console.log(`\n===== ${j.ID} =====`);
  console.log(`MaxHealth=${j.MaxHealth} Type=${j.Type} EngineType=${j.EngineType} PartHealth=${JSON.stringify(j.PartHealth)}`);
  const w = j.Weapons || {};
  for (const [k, v] of Object.entries(w)) {
    const ammo = Array.isArray(v.AmmoType)
      ? v.AmmoType.map(a => `${a.Ammo}${a.Override ? '->' + JSON.stringify(a.Override) : ''}`).join(' | ')
      : String(v.AmmoType ?? '');
    const keys = Object.keys(v).filter(x => /Rate|RPM|Reload|Magazine|Burst|Cooldown|Charge|Fire|Damage|Velocity|Projectile/.test(x));
    console.log(`  [${k}]`);
    for (const kk of keys) {
      const val = v[kk];
      if (typeof val === 'number' || typeof val === 'string' || typeof val === 'boolean') console.log(`      ${kk} = ${val}`);
    }
    if (ammo) console.log(`      AmmoType = ${ammo}`);
  }
}

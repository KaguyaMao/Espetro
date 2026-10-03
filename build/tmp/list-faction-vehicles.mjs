// list-faction-vehicles.mjs — 汇总各编制 vehicles{} 里的 载具槽 → 实体 id + 显示名
import fs from 'node:fs';
import path from 'node:path';

const DIR = process.argv[2] ?? 'fxall';
const map = new Map(); // id -> { roles:Set, names:Set, factions:Set }
for (const f of fs.readdirSync(DIR)) {
  if (!f.endsWith('.json')) continue;
  const j = JSON.parse(fs.readFileSync(path.join(DIR, f), 'utf8'));
  for (const [role, v] of Object.entries(j.vehicles ?? {})) {
    for (const id of v.entity ?? []) {
      if (!map.has(id)) map.set(id, { roles: new Set(), names: new Set(), factions: new Set() });
      const e = map.get(id);
      e.roles.add(role);
      e.names.add(String(v.display_name ?? ''));
      e.factions.add(f.replace(/\.json$/, ''));
    }
  }
}
const TRUCKJEEP = /^(truck|supply_truck|car)$/;
for (const [id, e] of [...map].sort()) {
  const roles = [...e.roles].join('/');
  const flag = [...e.roles].every((r) => TRUCKJEEP.test(r)) ? '【卡车/吉普】' : '';
  console.log(`${id.padEnd(34)} ${roles.padEnd(20)} ${[...e.names].join(' | ')} ${flag}`);
}
console.log(`\n共 ${map.size} 种载具`);
const tj = [...map].filter(([, e]) => [...e.roles].every((r) => TRUCKJEEP.test(r))).map(([id]) => id);
console.log(`卡车/吉普（role ∈ truck/supply_truck/car）共 ${tj.length}: ${tj.join(', ')}`);

import fs from 'node:fs';
import path from 'node:path';

// list-faction-vehicle-types.mjs — 汇总编制里出现的载具类型/名称/实体 id
const dir = process.argv[2];
const byType = new Map(); // type -> Map(entityId -> {names:Set, factions:Set})
for (const f of fs.readdirSync(dir).filter(x => x.endsWith('.json'))) {
  const j = JSON.parse(fs.readFileSync(path.join(dir, f), 'utf8'));
  const fac = String(j.faction?.name || f).replace(/§./g, '').trim();
  for (const [type, v] of Object.entries(j.vehicles || {})) {
    if (!byType.has(type)) byType.set(type, new Map());
    const m = byType.get(type);
    const entities = Array.isArray(v.entity) ? v.entity : (v.entity ? [v.entity] : []);
    for (const e of entities) {
      if (!m.has(e)) m.set(e, { names: new Set(), factions: new Set() });
      m.get(e).names.add(String(v.display_name || '').trim());
      m.get(e).factions.add(fac);
    }
  }
}
for (const [type, m] of [...byType.entries()].sort()) {
  console.log(`═══ VehTypes: ${type}`);
  for (const [e, info] of [...m.entries()].sort()) {
    console.log(`   ${e.padEnd(40)} ${[...info.names].join(' / ')}   [${info.factions.size} 个编制]`);
  }
}

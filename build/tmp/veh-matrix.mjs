// veh-matrix.mjs — 汇总 15 个编制的载具与 vehicle_crew_seats
import fs from 'fs';

const DIR = process.argv[2] || 'D:/minecraft/modp/Espetro/build/tmp/fxall-icons/';
const rows = [];
const types = new Set();

for (const f of fs.readdirSync(DIR).filter((x) => x.endsWith('.json')).sort()) {
  const j = JSON.parse(fs.readFileSync(DIR + f, 'utf8'));
  const veh = j.vehicles || {};
  const declared = j.VehTypes || [];
  const missing = Object.keys(veh).filter((k) => !declared.includes(k));
  const extra = declared.filter((k) => !veh[k]);
  const row = { f, missing, extra, cells: {} };
  for (const [vid, v] of Object.entries(veh)) {
    types.add(vid);
    row.cells[vid] = {
      seats: v.vehicle_crew_seats,
      cap: v.capacity,
      per: v.per_max_count,
      ent: (v.entity || []).length
    };
  }
  rows.push(row);
}

const typeList = [...types].sort();
console.log('编制'.padEnd(26) + typeList.map((t) => t.padEnd(14)).join('') + '  VehTypes缺失/多余');
for (const r of rows) {
  const cells = typeList.map((t) => {
    const c = r.cells[t];
    if (!c) return '—'.padEnd(14);
    return `crew=${c.seats ?? 'null'}`.padEnd(14);
  });
  console.log(r.f.replace('.json', '').padEnd(26) + cells.join('') +
    `  ${r.missing.length ? '缺:' + r.missing.join(',') : ''}${r.extra.length ? ' 多:' + r.extra.join(',') : ''}`);
}

console.log('\n容量/实体数明细（仅 truck/car 等载人载具）:');
for (const r of rows) {
  const parts = [];
  for (const t of ['truck', 'car', 'supply_truck', 'transport_helicopter']) {
    const c = r.cells[t];
    if (c) parts.push(`${t}(crew=${c.seats ?? 'null'},cap=${c.cap ?? '-'},per=${c.per ?? '-'},ent=${c.ent})`);
  }
  console.log(r.f.replace('.json', '').padEnd(26) + parts.join('  '));
}

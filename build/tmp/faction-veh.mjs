// faction-veh.mjs — 打印编制的载具/载具组员相关配置
import fs from 'fs';

const DIR = process.argv[2] || 'D:/minecraft/modp/Espetro/build/tmp/fxall-icons/';
const files = process.argv.slice(3).length ? process.argv.slice(3) : ['pla_118th_brigade.json', 'pla_112th_brigade.json'];

for (const f of files) {
  const j = JSON.parse(fs.readFileSync(DIR + f, 'utf8'));
  console.log(`\n===== ${f}`);
  const top = { ...j };
  delete top.classes;
  console.log('顶层字段: ' + Object.keys(top).join(', '));
  if (top.vehicles) {
    console.log(`vehicles: ${Object.keys(top.vehicles).length} 项`);
    for (const [vid, v] of Object.entries(top.vehicles)) {
      const flat = JSON.stringify(v);
      console.log(`  ${vid.padEnd(28)} ${flat.slice(0, 260)}`);
    }
  }
  for (const [k, v] of Object.entries(top)) {
    if (k === 'vehicles') continue;
    if (typeof v === 'object' && v !== null) {
      const s = JSON.stringify(v);
      console.log(`${k}: ${s.length > 400 ? s.slice(0, 400) + '…' : s}`);
    } else {
      console.log(`${k}: ${v}`);
    }
  }
  console.log('--- 职业 vehicle_crew / icon:');
  for (const [cid, cls] of Object.entries(j.classes || {})) {
    console.log(`  ${cid.padEnd(26)} icon=${String(cls.icon).padEnd(16)} vehicle_crew=${JSON.stringify(cls.vehicle_crew ?? cls.vehicleCrew ?? null)}`);
  }
}

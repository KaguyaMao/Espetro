import fs from 'node:fs';
import path from 'node:path';

// summarize-factions.mjs — 列出每个编制里的职业（含载具相关标记）以及是否已包含维修工具
const dir = process.argv[2];
for (const f of fs.readdirSync(dir).filter(x => x.endsWith('.json')).sort()) {
  const j = JSON.parse(fs.readFileSync(path.join(dir, f), 'utf8'));
  const classes = Object.entries(j.classes || {});
  const vehicleish = classes.filter(([, c]) =>
    c.vehicle_crew === true || /载具|车长|驾驶员|炮手|crew/i.test(String(c.name || '') + String(c.role || '') + String(c.description || '')));
  console.log(`═══ ${f}   职业总数=${classes.length}  载具相关=${vehicleish.length}`);
  for (const [k, c] of vehicleish) {
    const variantKeys = Object.keys(c.variants || {});
    let repair = 0, total = 0;
    for (const v of Object.values(c.variants || {})) {
      for (const cmd of (v.commands || [])) { total++; if (/repair_tool|repairtool/i.test(cmd)) repair++; }
    }
    console.log(`   ${k.padEnd(28)} name=${String(c.name).padEnd(10)} vehicle_crew=${c.vehicle_crew === true} role=${String(c.role || '-').padEnd(10)} variants=${variantKeys.join(',')} 指令=${total} 含维修工具=${repair}`);
  }
}

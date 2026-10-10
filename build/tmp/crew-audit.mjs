// crew-audit.mjs — 各编制载具组员职业名额 vs 载具所需组员座位
import fs from 'fs';

const DIR = process.argv[2] || 'D:/minecraft/modp/Espetro/build/tmp/fxall-icons/';
for (const f of fs.readdirSync(DIR).filter((x) => x.endsWith('.json')).sort()) {
  const j = JSON.parse(fs.readFileSync(DIR + f, 'utf8'));
  const crewClasses = [];
  for (const [cid, cls] of Object.entries(j.classes || {})) {
    const vc = cls.vehicle_crew ?? cls.vehicleCrew ?? null;
    if (vc === true || cls.icon === 'crewman' || String(cid).includes('CREW')) {
      crewClasses.push(`${cid}(icon=${cls.icon},vehicle_crew=${vc},max=${cls.maxPlayers},team_count=${cls.team_count ?? false},max_per_squad=${cls.max_per_squad ?? 0})`);
    }
  }
  const seats = Object.entries(j.vehicles || {})
    .filter(([, v]) => (v.vehicle_crew_seats ?? 0) > 0)
    .map(([t, v]) => `${t}=${v.vehicle_crew_seats}`);
  console.log(`\n${f.replace('.json', '')}`);
  console.log(`  组员座位: ${seats.join(' ') || '无'}`);
  for (const c of crewClasses) console.log(`  ${c}`);
}

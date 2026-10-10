// dump-pla-mg.mjs — reference dump
import fs from 'fs';
const j = JSON.parse(fs.readFileSync('out-pla_112th_brigade.json', 'utf8'));
for (const cid of ['PLA_112_MG', 'PLA_112_SQUADMG', 'PLA_112_BIG_SQUADMG', 'PLA_112_ASSAULT']) {
  const cls = j.classes[cid];
  if (!cls) continue;
  console.log(`===== ${cid} (${cls.name}) row=${cls.row} max=${cls.maxPlayers} /squad=${cls.max_per_squad} unlockPerN=${cls.unlock_per_n} unlockMinSquad=${cls.unlock_min_squad} teamCount=${cls.team_count}`);
  for (const [vk, v] of Object.entries(cls.variants || {})) {
    console.log(`  [${vk}] ${v.name}  (ammo_cost=${v.resupply ? v.resupply.ammo_cost : '-'})`);
    for (const s of (v.commands || [])) {
      if (/^tacz:|^taczmagazines:|^superbwarfare:|^cib:/.test(s)) console.log('     ' + s);
    }
    const res = ((v.resupply && v.resupply.items) || []).map(it => it.id + ' x' + it.count + '/max' + it.max);
    console.log('     补给: ' + res.join(' , '));
  }
}

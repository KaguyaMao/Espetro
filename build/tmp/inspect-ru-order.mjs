// inspect-ru-order.mjs — 俄军编制：职业 JSON 顺序 / row / 精确射手瞄准镜
import fs from 'fs';
for (const f of ['ru_205th', 'ru_3th', 'ru_49th', 'ru_6th_tank']) {
  const j = JSON.parse(fs.readFileSync(`fix-${f}.json`, 'utf8'));
  console.log(`===== ${f} 职业 JSON 顺序`);
  Object.entries(j.classes).forEach(([cid, cls], i) => {
    console.log(`  ${String(i).padStart(2)}. row=${String(cls.row).padEnd(2)} ${cid.padEnd(30)} ${cls.name}  变体=[${Object.keys(cls.variants).join(',')}]`);
  });
  console.log('  --- 精确射手 (MARKSMAN) 的武器/瞄具 ---');
  for (const [cid, cls] of Object.entries(j.classes)) {
    if (!/MARKSMAN/.test(cid)) continue;
    for (const [vk, v] of Object.entries(cls.variants || {})) {
      const guns = (v.commands || []).filter(s => s.startsWith('tacz:modern_kinetic_gun'));
      console.log(`  [${cid}/${vk}] ${v.name}`);
      guns.forEach(g => console.log('      ' + g));
      console.log('      补给: ' + ((v.resupply && v.resupply.items) || []).map(it => it.id.split('{')[0]).join(' , '));
    }
  }
}

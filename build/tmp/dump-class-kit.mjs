// dump-class-kit.mjs — 打印指定编制里"名称含某关键词"的职业：变体、枪、弹药/弹匣
// 用法: node dump-class-kit.mjs <faction.json> [关键词]
import fs from 'node:fs';

const file = process.argv[2];
const keyword = process.argv[3] ?? '奇袭';
const j = JSON.parse(fs.readFileSync(file, 'utf8').replace(/,(\s*[}\]])/g, '$1'));

console.log(`# ${file}  faction=${j.faction?.name ?? '?'}`);
for (const [ck, cls] of Object.entries(j.classes || {})) {
  const name = String(cls.name ?? '');
  if (!name.includes(keyword) && !ck.includes(keyword)) continue;
  console.log(`\n=== 职业 ${ck} 「${name}」 maxPlayers=${cls.maxPlayers} row=${cls.row} ===`);
  for (const [vk, va] of Object.entries(cls.variants || {})) {
    console.log(`  --- 变体 ${vk} (maxPlayers=${va.maxPlayers}${va.unlock_min_squad !== undefined ? ', unlock_min_squad=' + va.unlock_min_squad : ''}) ---`);
    const cmds = va.commands || [];
    cmds.forEach((c, i) => {
      const s = String(c);
      // 只打印与枪/弹药/弹匣相关的片段，长 NBT 折叠
      const bits = [];
      const gm = s.match(/GunId:"([^"]+)"/); if (gm) bits.push('GunId=' + gm[1]);
      for (const m of s.matchAll(/Attachment([A-Z_]+)\]?:\{[^}]*AttachmentId:"([^"]+)"/g)) bits.push(`附件${m[1]}=${m[2]}`);
      const am = s.match(/AmmoId:"([^"]+)"/); if (am) bits.push('AmmoId=' + am[1]);
      const it = s.match(/id:"([^"]+)"/); if (it) bits.push('item=' + it[1]);
      const cnt = s.match(/Count:(\d+)b/); if (cnt) bits.push('Count=' + cnt[1]);
      if (bits.length) console.log(`    #${i} ${bits.join('  ')}`);
      else console.log(`    #${i} ${s.slice(0, 140)}`);
    });
  }
}

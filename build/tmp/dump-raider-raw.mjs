// dump-raider-raw.mjs — 打印各 PLA 编制里 RAIDER(奇袭兵) 的全部原始 commands
import fs from 'node:fs';
import path from 'node:path';

const DIR = process.argv[2] ?? 'srv-factions';
const files = fs.readdirSync(DIR).filter(f => f.endsWith('.json')).sort();
for (const f of files) {
  let j;
  try { j = JSON.parse(fs.readFileSync(path.join(DIR, f), 'utf8').replace(/,(\s*[}\]])/g, '$1')); } catch { continue; }
  for (const [ck, cls] of Object.entries(j.classes || {})) {
    const name = String(cls.name ?? '');
    if (!name.includes('奇袭') && !ck.includes('RAIDER')) continue;
    console.log(`\n########## ${f}  ${ck} 「${name}」 ##########`);
    for (const [vk, va] of Object.entries(cls.variants || {})) {
      console.log(`  --- ${vk} ---`);
      (va.commands || []).forEach((c, i) => console.log(`    #${i} ${String(c)}`));
    }
  }
}

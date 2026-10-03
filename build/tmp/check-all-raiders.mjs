// check-all-raiders.mjs — 巡检 fix-factions 下所有「奇袭兵」职业的枪内弹匣 vs 备用弹匣
import fs from 'node:fs';
import path from 'node:path';

const dir = process.argv[2] ?? 'fix-factions';
function fams(s) {
  return [...String(s).matchAll(/MagazineFamily:"([^"]+)",MaxCapacity:(\d+)/g)].map((m) => m[1] + '/' + m[2]);
}
function tailCount(s) { const m = String(s).match(/\s(\d+)\s*$/); return m ? m[1] : '?'; }

let bad = 0, total = 0;
for (const f of fs.readdirSync(dir).filter((x) => x.endsWith('.json')).sort()) {
  const j = JSON.parse(fs.readFileSync(path.join(dir, f), 'utf8').replace(/,(\s*[}\]])/g, '$1'));
  for (const [ck, c] of Object.entries(j.classes || {})) {
    if (!String(c.name ?? '').includes('奇袭')) continue;
    total++;
    console.log(`\n## ${f} :: ${ck} 「${c.name}」`);
    for (const [vk, va] of Object.entries(c.variants || {})) {
      const cmds = va.commands || [];
      const gunFam = fams(cmds[0] ?? '');
      const mags = [];
      for (let i = 1; i < cmds.length; i++) {
        const s = String(cmds[i]);
        if (/taczmagazines:magazine/.test(s)) mags.push(`#${i}=${fams(s).join(',')}x${tailCount(s)}`);
      }
      const gunF = gunFam[0] ?? '?';
      const ok = mags.every((m) => m.includes(gunF));
      if (!ok) bad++;
      console.log(`   ${vk}: 枪=${gunF} 备用[${mags.join(' | ')}] ${ok ? 'OK' : '❌不一致'}`);
    }
  }
}
console.log(`\n检查奇袭兵职业 ${total} 个，不一致变体 ${bad} 个`);

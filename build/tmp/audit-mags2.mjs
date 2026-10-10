// audit-mags2.mjs — 只比"弹药/弹匣族/容量"，忽略匣数；找出真正错配的职业
import fs from 'node:fs';
import path from 'node:path';

const DIR = process.argv[2] ?? 'fxall';
const files = fs.readdirSync(DIR).filter(f => f.endsWith('.json')).sort();

function kits(cmds) {
  const guns = [], mags = [];
  for (const c of cmds) {
    const s = String(c);
    const gm = s.match(/GunId:"([^"]+)"/);
    if (gm && /modern_kinetic_gun/.test(s)) guns.push(gm[1]);
    const mm = s.match(/taczmagazines:magazine\{([^}]*)\}\s*(\d+)/);
    if (mm) {
      const body = mm[1];
      const ammo = (body.match(/AmmoId:"([^"]+)"/) || [])[1] ?? '?';
      const fam = (body.match(/MagazineFamily:"([^"]+)"/) || [])[1] ?? '?';
      const cap = (body.match(/MaxCapacity:(\d+)/) || [])[1] ?? '?';
      mags.push({ sig: `${ammo} / ${fam} / ${cap}发`, count: Number(mm[2]) });
    }
  }
  return { guns, mags };
}

let flagged = 0, total = 0;
for (const f of files) {
  let j;
  try { j = JSON.parse(fs.readFileSync(path.join(DIR, f), 'utf8').replace(/,(\s*[}\]])/g, '$1')); } catch { continue; }
  for (const [ck, cls] of Object.entries(j.classes || {})) {
    total++;
    const perVariant = [];
    for (const [vk, va] of Object.entries(cls.variants || {})) {
      const k = kits(va.commands || []);
      perVariant.push({ vk, ...k });
    }
    if (perVariant.length < 2) continue;
    const sigSets = perVariant.map(v => [...new Set(v.mags.map(m => m.sig))].sort().join(' | '));
    if (new Set(sigSets).size > 1) {
      flagged++;
      console.log(`\n### ${cls.name} (${ck})  ${f}`);
      console.log(`    枪: ${[...new Set(perVariant.flatMap(v => v.guns))].join(', ')}`);
      perVariant.forEach(v => console.log(`    ${v.vk.padEnd(10)} → ${sigSets[perVariant.indexOf(v)]}   [匣数 ${v.mags.map(m => m.count).join(',')}]`));
    }
  }
}
console.log(`\n检查职业数: ${total}，弹匣"弹药/族/容量"在变体间不一致的职业: ${flagged}`);

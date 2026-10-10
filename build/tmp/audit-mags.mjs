// audit-mags.mjs — 复核"同一职业不同变体的枪/弹匣"是否一致，找出错配
// 用法: node audit-mags.mjs <factionDir>
import fs from 'node:fs';
import path from 'node:path';

const DIR = process.argv[2] ?? 'fxall';
const files = fs.readdirSync(DIR).filter(f => f.endsWith('.json')).sort();

function parseKit(cmds) {
  let gun = null, mags = [];
  for (const c of cmds) {
    const s = String(c);
    const gm = s.match(/GunId:"([^"]+)"/);
    if (gm && /modern_kinetic_gun/.test(s)) gun = gm[1];
    const mm = s.match(/taczmagazines:magazine\{([^}]*)\}\s*(\d+)/);
    if (mm) {
      const body = mm[1];
      const ammo = (body.match(/AmmoId:"([^"]+)"/) || [])[1] ?? '?';
      const fam = (body.match(/MagazineFamily:"([^"]+)"/) || [])[1] ?? '?';
      const cap = (body.match(/MaxCapacity:(\d+)/) || [])[1] ?? '?';
      mags.push({ ammo, fam, cap, count: Number(mm[2]), raw: s });
    }
  }
  return { gun, mags };
}

let mismatchClasses = 0, totalClasses = 0;
const report = [];
for (const f of files) {
  let j;
  try { j = JSON.parse(fs.readFileSync(path.join(DIR, f), 'utf8').replace(/,(\s*[}\]])/g, '$1')); } catch { continue; }
  for (const [ck, cls] of Object.entries(j.classes || {})) {
    totalClasses++;
    const byGun = new Map();
    for (const [vk, va] of Object.entries(cls.variants || {})) {
      const kit = parseKit(va.commands || []);
      if (!kit.gun) continue;
      if (!byGun.has(kit.gun)) byGun.set(kit.gun, []);
      byGun.get(kit.gun).push({ vk, mags: kit.mags });
    }
    for (const [gun, variants] of byGun) {
      const sigs = new Set(variants.map(v => v.mags.map(m => `${m.ammo}/${m.fam}/${m.cap}x${m.count}`).join('|')));
      if (sigs.size > 1) {
        mismatchClasses++;
        report.push({ f, ck, name: cls.name, gun, variants: variants.map(v => ({ vk: v.vk, mags: v.mags })) });
      }
    }
  }
}
console.log(`检查职业数: ${totalClasses}，同枪弹匣不一致的职业: ${mismatchClasses}\n`);
for (const r of report) {
  console.log(`### ${r.name} (${r.ck})  ${r.f}  枪=${r.gun}`);
  for (const v of r.variants) {
    console.log(`   ${v.vk.padEnd(10)} → ${v.mags.map(m => `${m.ammo} / ${m.fam} / ${m.cap}发 × ${m.count}匣`).join('  ;  ') || '(无弹匣)'}`);
  }
}

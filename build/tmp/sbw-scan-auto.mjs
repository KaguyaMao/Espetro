// sbw-scan-auto.mjs — 扫描所有载具文件里的"连发"武器及其 Magazine / EmptyReloadTime
import fs from 'fs';

const ROOTS = process.argv[2]
  ? [['out', process.argv[2]]]
  : [
    ['fcp', 'D:/minecraft/modp/Espetro/build/tmp/sbw-all/fcp/'],
    ['dr ', 'D:/minecraft/modp/Espetro/build/tmp/sbw-all/dr/']
  ];

const isAuto = (w) => {
  const modes = [w.DefaultFireMode, w.AvailableFireModes].filter((x) => typeof x === 'string').join(',');
  return /auto/i.test(modes);
};

let totalWeapons = 0, autoWeapons = 0, autoWithMag = 0, affected = 0;
const rows = [];
const badFiles = [];

for (const [tag, dir] of ROOTS) {
  for (const f of fs.readdirSync(dir).filter((x) => x.endsWith('.json')).sort()) {
    let j;
    try { j = JSON.parse(fs.readFileSync(dir + f, 'utf8')); }
    catch (e) { badFiles.push(`${tag} ${f}: ${e.message}`); continue; }
    const weapons = j.Weapons ?? {};
    const hits = [];
    for (const [wn, w] of Object.entries(weapons)) {
      if (w === null || typeof w !== 'object') continue;
      totalWeapons++;
      const auto = isAuto(w);
      if (auto) autoWeapons++;
      const hasMag = w.Magazine !== undefined, hasReload = w.EmptyReloadTime !== undefined;
      if (auto && hasMag) autoWithMag++;
      if (auto && (hasMag || hasReload)) {
        hits.push(`${wn}(modes=${w.DefaultFireMode ?? '-'}/${w.AvailableFireModes ?? '-'} RPM=${w.RPM ?? '-'} Magazine=${w.Magazine ?? '(无)'} EmptyReloadTime=${w.EmptyReloadTime ?? '(无)'})`);
        affected++;
      }
      // 弹种 override 里也可能带这两个字段
      for (const [i, e] of (Array.isArray(w.AmmoType) ? w.AmmoType : []).entries()) {
        if (e?.Override && (e.Override.Magazine !== undefined || e.Override.EmptyReloadTime !== undefined)) {
          hits.push(`  ↳ 弹种[${i}] ${e.Ammo}: Magazine=${e.Override.Magazine ?? '(无)'} EmptyReloadTime=${e.Override.EmptyReloadTime ?? '(无)'}`);
        }
      }
    }
    if (hits.length) rows.push({ file: `${tag} ${f} (${j.ID})`, hits });
  }
}

for (const r of rows) {
  console.log(`\n▸ ${r.file}`);
  for (const h of r.hits) console.log('   - ' + h);
}
const fileCount = ROOTS.reduce((n, [, d]) => n + fs.readdirSync(d).filter((f) => f.endsWith('.json')).length, 0);
console.log(`\n扫描统计: 文件 ${fileCount} 个, 武器槽 ${totalWeapons} 个, 其中连发 ${autoWeapons} 个`);
console.log(`连发且带 Magazine 的武器: ${autoWithMag} 个；需要处理（带 Magazine 或 EmptyReloadTime）: ${affected} 个，分布在 ${rows.length} 个文件`);
if (badFiles.length) { console.log('\n无法解析（跳过）:'); for (const b of badFiles) console.log('   ! ' + b); }

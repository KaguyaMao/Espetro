// sbw-strip-report.mjs — 汇总：原有"无 Magazine 的连发武器"数量、未改动文件清单
import fs from 'fs';

const ROOTS = [['fcp', 'D:/minecraft/modp/Espetro/build/tmp/sbw-all/fcp/'], ['dr', 'D:/minecraft/modp/Espetro/build/tmp/sbw-all/dr/']];
const OUT = 'D:/minecraft/modp/Espetro/build/tmp/sbw-strip-out/';
const isAuto = (w) => /auto/i.test([w.DefaultFireMode, w.AvailableFireModes].filter((x) => typeof x === 'string').join(','));

let alreadyBelt = 0;
const alreadyList = [];
const untouched = [];
let changed = 0;

for (const [ns, dir] of ROOTS) {
  for (const f of fs.readdirSync(dir).filter((x) => x.endsWith('.json')).sort()) {
    const j = JSON.parse(fs.readFileSync(dir + f, 'utf8'));
    for (const [wn, w] of Object.entries(j.Weapons ?? {})) {
      if (!w || typeof w !== 'object' || !isAuto(w)) continue;
      if (w.Magazine === undefined) { alreadyBelt++; alreadyList.push(`${ns}/${f}:${wn}`); }
    }
    if (fs.existsSync(OUT + ns + '/' + f)) changed++; else untouched.push(`${ns}/${f}`);
  }
}
console.log(`原有"连发但本来就没有 Magazine"的武器槽: ${alreadyBelt} 个（说明这种写法在载具包里本来就存在）`);
console.log(alreadyList.slice(0, 14).join('  |  '));
console.log(`\n本次改动文件 ${changed} 个；未改动（连发武器已无弹夹字段）${untouched.length} 个:`);
console.log('  ' + untouched.join(', '));

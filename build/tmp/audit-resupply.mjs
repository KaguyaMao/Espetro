// audit-resupply.mjs — 全量审计：补给段位置、弹药-枪械错配、发射器弹药归属
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const DIR = 'D:/minecraft/modp/Espetro/build/tmp/fxall/';
const FILES = fs.readdirSync(DIR).filter((f) => f.endsWith('.json'));
const MAG = /taczmagazines:magazine\s*\{([^}]*)\}/g;
const FAM = /MagazineFamily\s*:\s*"([^"]+)"/;
const AMMO_IN = /AmmoId\s*:\s*"([^"]+)"/;
const CAP = /MaxCapacity\s*:\s*(\d+)/;

// 1) resupply 段出现在哪些 JSON 路径
const resupplyPaths = {};
function walk(obj, path, file) {
  if (obj && typeof obj === 'object') {
    for (const [k, v] of Object.entries(obj)) {
      const p = path ? path + '.' + k : k;
      if (k === 'resupply') (resupplyPaths[p.replace(/\.(default|[^.]*)$/, '.<variant>')] = resupplyPaths[p.replace(/\.(default|[^.]*)$/, '.<variant>')] || new Set()).add(file);
      walk(v, p, file);
    }
  }
}
// 2) 发射器弹药归属
const launcherOwners = {};
// 3) 错配：补给弹药不在本变体枪械/弹匣弹药集合内
const mismatches = [];
let variantCount = 0, resupplyItemCount = 0, ammoItemCount = 0;

for (const f of FILES) {
  const j = JSON.parse(fs.readFileSync(DIR + f, 'utf8'));
  walk(j, '', f);
  const allText = JSON.stringify(j);
  for (const am of ['murasamet:og7he', 'murasamet:pg7heat', 'murasamet:pg7vr_tandem_heat', 'suffuse:120mm', 'tacz:40mm', 'ts:40mm_vog25', 'ts:84mm_ffv751']) {
    if (allText.includes(am)) (launcherOwners[am] = launcherOwners[am] || new Set()).add(f);
  }
  for (const [cid, cls] of Object.entries(j.classes || {})) {
    for (const [vid, v] of Object.entries(cls.variants || {})) {
      variantCount++;
      const cmds = v.commands || [];
      const gunAmmo = new Set();
      for (const c of cmds) {
        if (!/modern_kinetic_gun/.test(c)) continue;
        const sm = c.match(/TaCZMag_StoredMagazine:\{Count:[^}]*AmmoId:"([^"]+)"/);
        if (sm) gunAmmo.add(sm[1]);
      }
      const magAmmo = new Set();
      let m; MAG.lastIndex = 0;
      while ((m = MAG.exec(cmds.join('\n'))) !== null) {
        const am = (m[1].match(AMMO_IN) || [])[1];
        if (am) magAmmo.add(am);
      }
      const items = (v.resupply && v.resupply.items) || [];
      resupplyItemCount += items.length;
      for (const it of items) {
        const id = String(it.id || '');
        if (!/^tacz:ammo/i.test(id)) continue;
        ammoItemCount++;
        const am = (id.match(/AmmoId\s*:\s*"([^"]+)"/) || [])[1];
        if (!am) continue;
        const known = gunAmmo.has(am) || magAmmo.has(am);
        if (!known) mismatches.push({ file: f, cid, vid, am, count: it.count, max: it.max, guns: [...gunAmmo].join(','), mags: [...magAmmo].join(',') });
      }
    }
  }
}

console.log('编制文件:', FILES.length, '| 变体数:', variantCount, '| 补给条目总数:', resupplyItemCount, '| 其中散装弹药条目:', ammoItemCount);
console.log('\n=== resupply 段出现位置（路径模板 → 文件数）===');
for (const [p, set] of Object.entries(resupplyPaths)) console.log(`${p}  x${set.size}`);
console.log('\n=== 发射器/火箭筒类弹药出现的文件 ===');
for (const [am, set] of Object.entries(launcherOwners)) console.log(`${am}  x${set.size}  ${[...set].slice(0, 3).join(', ')}...`);
console.log('\n=== 错配：补给弹药不在本变体枪械/弹匣弹药集合内 ===');
const byKey = {};
for (const mm of mismatches) {
  const k = `${mm.am} <- 枪=[${mm.guns}] 弹匣=[${mm.mags}]`;
  (byKey[k] = byKey[k] || []).push(`${mm.file}/${mm.cid}/${mm.vid}(count=${mm.count},max=${mm.max})`);
}
for (const [k, list] of Object.entries(byKey).sort((a, b) => b[1].length - a[1].length)) {
  console.log(`\nx${list.length}  ${k}`);
  list.slice(0, 3).forEach((s) => console.log('    ' + s));
}
console.log('\n错配条目总数:', mismatches.length);

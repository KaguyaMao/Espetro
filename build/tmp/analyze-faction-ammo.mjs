// analyze-faction-ammo.mjs — 统计编制补给中的弹药条目、弹匣 family/容量映射、枪械→弹药
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const DIR = 'D:/minecraft/modp/Espetro/build/tmp/fxall/';
const FILES = fs.readdirSync(DIR).filter((f) => f.endsWith('.json'));

const MAG = /taczmagazines:magazine\s*\{([^}]*)\}\s*(\d+)?/g;
const FAM = /MagazineFamily\s*:\s*"([^"]+)"/;
const AMMO_IN = /AmmoId\s*:\s*"([^"]+)"/;
const CAP = /MaxCapacity\s*:\s*(\d+)/;

const ammoToMags = {};      // ammoId -> Set("family|cap")
const resupplyAmmo = {};    // ammoId -> 出现处
const gunAmmo = {};         // gunId -> Set(ammoId)
const magEntriesInResupply = {}; // 已是弹匣的补给条目
let classVariants = 0;

for (const f of FILES) {
  const j = JSON.parse(fs.readFileSync(DIR + f, 'utf8'));
  for (const [cid, cls] of Object.entries(j.classes || {})) {
    for (const [vid, v] of Object.entries(cls.variants || {})) {
      classVariants++;
      for (const c of (v.commands || [])) {
        let m; MAG.lastIndex = 0;
        while ((m = MAG.exec(c)) !== null) {
          const body = m[1];
          const fam = (body.match(FAM) || [])[1];
          const am = (body.match(AMMO_IN) || [])[1];
          const cap = (body.match(CAP) || [])[1];
          if (fam && am && cap) (ammoToMags[am] = ammoToMags[am] || new Set()).add(fam + '|' + cap);
        }
        if (/modern_kinetic_gun/.test(c)) {
          const gid = (c.match(/GunId:"([^"]+)"/) || [])[1];
          const sm = c.match(/TaCZMag_StoredMagazine:\{Count:[^}]*AmmoId:"([^"]+)"/);
          if (gid && sm) (gunAmmo[gid] = gunAmmo[gid] || new Set()).add(sm[1]);
        }
      }
      for (const it of ((v.resupply && v.resupply.items) || [])) {
        const id = String(it.id || '').trim();
        const am = (id.match(/AmmoId\s*:\s*"([^"]+)"/) || [])[1];
        const where = `${f}/${cid}/${vid}`;
        if (/^tacz:ammo/i.test(id)) {
          (resupplyAmmo[am || '(空AmmoId)'] = resupplyAmmo[am || '(空AmmoId)'] || [])
            .push(`${where} count=${it.count} max=${it.max} cost=${it.ammo_cost}`);
        } else if (/^taczmagazines:/i.test(id)) {
          const key = (id.match(FAM) || [])[1] + '|' + (id.match(AMMO_IN) || [])[1] + '|' + (id.match(CAP) || [])[1];
          (magEntriesInResupply[key] = magEntriesInResupply[key] || []).push(`${where} count=${it.count} max=${it.max}`);
        }
      }
    }
  }
}

console.log('编制文件数:', FILES.length, ' 职业变体数:', classVariants);
console.log('\n=== 补给中的【散装弹药】条目（按 AmmoId 汇总）===');
for (const [am, list] of Object.entries(resupplyAmmo).sort()) {
  console.log(`${am}  x${list.length}  例: ${list[0]}`);
}
console.log('\n=== 补给中【已是弹匣】的条目 ===');
for (const [k, list] of Object.entries(magEntriesInResupply).sort()) {
  console.log(`${k}  x${list.length}  例: ${list[0]}`);
}
console.log('\n=== 装备中出现过的弹匣 family|容量（按弹药 ID）===');
for (const [am, set] of Object.entries(ammoToMags).sort()) {
  console.log(`${am} -> ${[...set].sort().join(', ')}`);
}
console.log('\n=== 枪械 -> 弹药 ===');
for (const [g, set] of Object.entries(gunAmmo).sort()) {
  console.log(`${g} -> ${[...set].sort().join(', ')}`);
}
console.log('\n=== 有散装弹药补给但【没有对应弹匣定义】的弹药（疑似火箭筒等）===');
for (const am of Object.keys(resupplyAmmo).sort()) {
  if (!ammoToMags[am]) console.log('  ' + am);
}

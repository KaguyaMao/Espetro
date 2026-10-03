// analyze-resupply-mapping.mjs — 为每个"散装弹药补给条目"找出该职业变体内对应弹匣 family/容量
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const DIR = 'D:/minecraft/modp/Espetro/build/tmp/fxall/';
const FILES = fs.readdirSync(DIR).filter((f) => f.endsWith('.json'));

const MAG = /taczmagazines:magazine\s*\{([^}]*)\}/g;
const FAM = /MagazineFamily\s*:\s*"([^"]+)"/;
const AMMO_IN = /AmmoId\s*:\s*"([^"]+)"/;
const CAP = /MaxCapacity\s*:\s*(\d+)/;

const agg = {};        // key -> count
const multiEx = {};    // ammo -> 例子
const noFamily = {};   // ammo -> count（无弹匣定义）
const gunsNoResupply = {}; // ammo -> {guns:Set, variants:Set}

function magPairs(text) {
  const out = new Set();
  let m; MAG.lastIndex = 0;
  while ((m = MAG.exec(text)) !== null) {
    const body = m[1];
    const fam = (body.match(FAM) || [])[1];
    const am = (body.match(AMMO_IN) || [])[1];
    const cap = (body.match(CAP) || [])[1];
    if (fam && am && cap) out.add(`${am}#${fam}|${cap}`);
  }
  return out;
}

for (const f of FILES) {
  const j = JSON.parse(fs.readFileSync(DIR + f, 'utf8'));
  for (const [cid, cls] of Object.entries(j.classes || {})) {
    for (const [vid, v] of Object.entries(cls.variants || {})) {
      const where = `${f}/${cid}/${vid}`;
      // 变体内所有弹匣 pair（来自 commands 中枪械内弹匣 + 独立弹匣）
      const pairs = magPairs((v.commands || []).join('\n'));
      const byAmmo = {};
      for (const p of pairs) {
        const [am, famCap] = p.split('#');
        (byAmmo[am] = byAmmo[am] || new Set()).add(famCap);
      }
      const resupplyAmmos = [];
      for (const it of ((v.resupply && v.resupply.items) || [])) {
        const id = String(it.id || '');
        if (!/^tacz:ammo/i.test(id)) continue;
        const am = (id.match(/AmmoId\s*:\s*"([^"]+)"/) || [])[1] || '(空)';
        resupplyAmmos.push({ am, count: it.count, max: it.max, cost: it.ammo_cost });
      }
      for (const rs of resupplyAmmos) {
        const fams = byAmmo[rs.am] ? [...byAmmo[rs.am]].sort() : [];
        let cls2;
        if (fams.length === 0) {
          cls2 = 'NO_FAMILY';
          noFamily[rs.am] = (noFamily[rs.am] || 0) + 1;
        } else if (fams.length === 1) {
          cls2 = fams[0];
        } else {
          cls2 = 'MULTI:' + fams.join('+');
          if (!multiEx[rs.am]) multiEx[rs.am] = where + ' -> ' + fams.join(', ');
        }
        const key = `${rs.am} | ${cls2} | count=${rs.count} max=${rs.max} cost=${rs.cost}`;
        agg[key] = (agg[key] || 0) + 1;
      }
      // 覆盖缺口：变体内有枪，但该弹药没有补给条目
      const gunAmmos = new Set();
      for (const c of (v.commands || [])) {
        if (!/modern_kinetic_gun/.test(c)) continue;
        const sm = c.match(/TaCZMag_StoredMagazine:\{Count:[^}]*AmmoId:"([^"]+)"/);
        const gid = (c.match(/GunId:"([^"]+)"/) || [])[1];
        if (sm) {
          gunAmmos.add(sm[1]);
          (gunsNoResupply[sm[1]] = gunsNoResupply[sm[1]] || { guns: new Set(), variants: new Set() });
          if (gid) gunsNoResupply[sm[1]].guns.add(gid);
          gunsNoResupply[sm[1]].variants.add(where);
        }
      }
      for (const am of gunAmmos) {
        if (!resupplyAmmos.some((r) => r.am === am)) {
          // 记录为覆盖缺口（该弹药在本变体无补给条目）
          (gunsNoResupply[am].missing = gunsNoResupply[am].missing || new Set()).add(where);
        }
      }
    }
  }
}

console.log('=== 散装弹药补给条目 → 弹匣映射（聚合，按出现次数）===');
for (const [k, n] of Object.entries(agg).sort((a, b) => b[1] - a[1])) {
  console.log(`x${n}  ${k}`);
}
console.log('\n=== 同一弹药出现多个弹匣族的例子 ===');
for (const [am, ex] of Object.entries(multiEx)) console.log(am + ' : ' + ex);
console.log('\n=== 无任何弹匣定义的弹药（次数）===');
for (const [am, n] of Object.entries(noFamily).sort((a, b) => b[1] - a[1])) console.log(`x${n}  ${am}`);
console.log('\n=== 枪械弹药 vs 补给覆盖（可能缺口）===');
for (const [am, info] of Object.entries(gunsNoResupply).sort()) {
  const missing = info.missing ? info.missing.size : 0;
  console.log(`${am}: 枪=[${[...info.guns].join(',')}] 变体数=${info.variants.size} 无补给条目的变体数=${missing}`);
}

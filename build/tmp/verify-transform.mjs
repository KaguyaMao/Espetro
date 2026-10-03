// verify-transform.mjs — 校验转换结果：职业数一致、散装弹药仅剩发射器、弹匣条目统计
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const A = 'D:/minecraft/modp/Espetro/build/tmp/fxall/';
const B = 'D:/minecraft/modp/Espetro/build/tmp/fxall-new/';
const LAUNCH = new Set(['murasamet:og7he', 'murasamet:pg7heat', 'murasamet:pg7vr_tandem_heat',
  'suffuse:120mm', 'tacz:40mm', 'ts:40mm_vog25', 'ts:84mm_ffv751']);
const files = fs.readdirSync(B).filter((f) => f.endsWith('.json'));

let oldAmmo = 0, newAmmo = 0, newLaunch = 0, newMag = 0, clsOld = 0, clsNew = 0;
let itemsOld = 0, itemsNew = 0;
const badAmmo = new Map();

for (const f of files) {
  const a = JSON.parse(fs.readFileSync(A + f, 'utf8'));
  const b = JSON.parse(fs.readFileSync(B + f, 'utf8'));
  clsOld += Object.keys(a.classes || {}).length;
  clsNew += Object.keys(b.classes || {}).length;
  for (const cls of Object.values(a.classes || {})) {
    for (const v of Object.values(cls.variants || {})) {
      for (const it of ((v.resupply && v.resupply.items) || [])) {
        itemsOld++;
        if (/^tacz:ammo/i.test(String(it.id || ''))) oldAmmo++;
      }
    }
  }
  for (const cls of Object.values(b.classes || {})) {
    for (const v of Object.values(cls.variants || {})) {
      for (const it of ((v.resupply && v.resupply.items) || [])) {
        itemsNew++;
        const id = String(it.id || '');
        if (/^tacz:ammo/i.test(id)) {
          newAmmo++;
          const am = (id.match(/AmmoId\s*:\s*"([^"]+)"/) || [])[1];
          if (LAUNCH.has(am)) newLaunch++; else badAmmo.set(am, (badAmmo.get(am) || 0) + 1);
        } else if (/^taczmagazines:/i.test(id)) newMag++;
      }
    }
  }
}
console.log('职业数 旧/新:', clsOld, '/', clsNew);
console.log('补给条目总数 旧/新:', itemsOld, '/', itemsNew);
console.log('散装弹药条目 旧/新:', oldAmmo, '/', newAmmo, '（其中发射器保留）', newLaunch);
console.log('弹匣条目（新）:', newMag);
if (badAmmo.size) {
  console.log('!! 仍未转换的非发射器弹药:', [...badAmmo.entries()].map(([k, v]) => k + 'x' + v).join(', '));
} else {
  console.log('OK: 所有非发射器弹药条目均已改为弹匣条目');
}

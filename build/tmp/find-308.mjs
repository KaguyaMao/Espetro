// find-308.mjs — 找出编制里已使用的 tacz:308 弹匣家族与对应枪
import fs from 'fs';
const files = fs.readdirSync('.').filter(f => /^out-.*\.json$/.test(f));
const seen = new Map();
const RE = /TaCZMag_StoredMagazine:\{Count:(\d+)b,id:"([^"]+)",tag:\{AmmoCount:(\d+),AmmoId:"([^"]+)",MagazineFamily:"([^"]+)",MaxCapacity:(\d+)\}\}/;
for (const f of files) {
  const j = JSON.parse(fs.readFileSync(f, 'utf8'));
  for (const [cid, cls] of Object.entries(j.classes || {})) {
    for (const [vk, v] of Object.entries(cls.variants || {})) {
      for (const s of (v.commands || [])) {
        if (!s.includes('tacz:308')) continue;
        const gun = (s.match(/GunId:"([^"]+)"/) || [])[1];
        const m = s.match(RE);
        const key = m ? `family=${m[5]} cap=${m[6]} ammo=${m[4]}` : '(无弹匣段)';
        if (!seen.has(key)) seen.set(key, new Set());
        seen.get(key).add(`${f.replace('out-', '').replace('.json', '')}/${cid}/${vk} gun=${gun}`);
      }
    }
  }
}
for (const [k, v] of seen) {
  console.log(k + '  <- ' + [...v].slice(0, 4).join(' | ') + (v.size > 4 ? ` (+${v.size - 4})` : ''));
}
console.log('\n含有 tacz:308 的补给项：');
const seen2 = new Set();
for (const f of files) {
  const j = JSON.parse(fs.readFileSync(f, 'utf8'));
  for (const cls of Object.values(j.classes || {})) {
    for (const v of Object.values(cls.variants || {})) {
      for (const it of ((v.resupply && v.resupply.items) || [])) {
        if (it.id.includes('tacz:308')) seen2.add(it.id + ' x' + it.count + '/max' + it.max);
      }
    }
  }
}
[...seen2].forEach(x => console.log('  ' + x));

import fs from 'fs';

const SRC = process.argv[2] || 'D:/minecraft/modp/Espetro/build/tmp/fxall-new/';
const counts = new Map();
for (const f of fs.readdirSync(SRC).filter((x) => x.endsWith('.json'))) {
  const j = JSON.parse(fs.readFileSync(SRC + f, 'utf8'));
  for (const c of Object.values(j.classes || {})) {
    for (const v of Object.values(c.variants || {})) {
      for (const it of (v.resupply && v.resupply.items) || []) {
        const id = String(it.id || '');
        if (!/^tacz:ammo/i.test(id)) continue;
        const m = id.match(/AmmoId\s*:\s*"([^"]+)"/);
        if (!m) continue;
        const key = `${m[1]} count=${it.count || 1} max=${it.max || 1}`;
        counts.set(key, (counts.get(key) || 0) + 1);
      }
    }
  }
}
let total = 0;
for (const [k, n] of [...counts.entries()].sort()) {
  console.log(`${k}  ×${n}`);
  total += n;
}
console.log(`\n散装弹药条目合计: ${total}`);

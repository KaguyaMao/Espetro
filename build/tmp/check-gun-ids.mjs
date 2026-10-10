import fs from 'node:fs';
import path from 'node:path';

// 检查编制里用到的枪 id 在生成的枪包索引里是否都有对应数据文件
const taczDir = process.argv[2];
const factionDir = process.argv[3];

const used = new Map(); // gunId -> [职业...]
for (const f of fs.readdirSync(factionDir).filter(x => x.endsWith('.json'))) {
  const j = JSON.parse(fs.readFileSync(path.join(factionDir, f), 'utf8'));
  for (const [ck, cls] of Object.entries(j.classes || {})) {
    for (const [vk, va] of Object.entries(cls.variants || {})) {
      for (const cmd of (va.commands || [])) {
        const m = String(cmd).match(/GunId:"([^"]+)"/);
        if (m) {
          if (!used.has(m[1])) used.set(m[1], new Set());
          used.get(m[1]).add(`${ck}/${vk}`);
        }
      }
    }
  }
}

// 收集所有枪包里可用的枪 id（index + data）
const packs = fs.readdirSync(taczDir).map(p => path.join(taczDir, p)).filter(p => { try { return fs.statSync(p).isDirectory(); } catch { return false; } });
const haveIndex = new Set(), haveData = new Set();
for (const p of packs) {
  const root = path.join(p, 'data');
  if (!fs.existsSync(root)) continue;
  for (const ns of fs.readdirSync(root)) {
    const idx = path.join(root, ns, 'index', 'guns');
    if (fs.existsSync(idx)) for (const f of fs.readdirSync(idx)) if (f.endsWith('.json')) haveIndex.add(`${ns}:${f.replace(/\.json$/, '')}`);
    const gd = path.join(root, ns, 'data', 'guns');
    if (fs.existsSync(gd)) for (const f of fs.readdirSync(gd)) if (f.endsWith('.json')) haveData.add(`${ns}:${f.replace(/_data\.json$/, '')}`);
  }
}

console.log(`编制里用到的枪 id = ${used.size}\n`);
for (const [id, users] of [...used.entries()].sort()) {
  const idx = haveIndex.has(id) ? 'index✔' : 'index✘';
  const dat = haveData.has(id) ? 'data✔' : 'data✘';
  console.log(`  ${id.padEnd(34)} ${idx} ${dat}   用于 ${users.size} 个变体`);
}

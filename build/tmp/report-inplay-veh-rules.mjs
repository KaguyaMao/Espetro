// report-inplay-veh-rules.mjs — 交叉核对：编制里实际出场的载具 vs 有方向抗性规则的载具
import fs from 'node:fs';
import path from 'node:path';

const FACTION_DIR = process.argv[2] ?? 'fxall';
const eff = JSON.parse(fs.readFileSync('veh-rules-effective.json', 'utf8'));
const allKeys = new Set(Object.keys(eff.effective));
const withRule = new Set(Object.entries(eff.effective).filter(([, v]) => (v.angle ?? []).length > 0).map(([k]) => k));

const ids = new Map(); // id -> Set(编制文件)
for (const f of fs.readdirSync(FACTION_DIR)) {
  if (!f.endsWith('.json')) continue;
  const t = fs.readFileSync(path.join(FACTION_DIR, f), 'utf8');
  for (const m of t.matchAll(/\b((?:dragonrise_reforge|fcp|vvp|superbwarfare):[a-z0-9_]+)\b/g)) {
    const id = m[1];
    if (!allKeys.has(id)) continue; // 只要「确实是载具 JSON」的 id
    (ids.get(id) ?? ids.set(id, new Set()).get(id)).add(f.replace(/\.json$/, ''));
  }
}

const inplay = [...ids.keys()].sort();
const ok = inplay.filter((k) => withRule.has(k));
const no = inplay.filter((k) => !withRule.has(k));
console.log(`编制中出场的载具（能在载具数据里找到的）: ${inplay.length}`);
console.log(`  有方向规则: ${ok.length}`);
ok.forEach((k) => console.log(`    ✓ ${k}  (${[...ids.get(k)].join(',')})`));
console.log(`\n  无方向规则: ${no.length}`);
no.forEach((k) => console.log(`    ✗ ${k}  (${[...ids.get(k)].join(',')})`));

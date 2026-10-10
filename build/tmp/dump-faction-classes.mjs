// dump-faction-classes.mjs — 打印某编制所有职业 key + 该职业 NBT 里出现的载具 id
import fs from 'node:fs';
const f = process.argv[2];
const eff = JSON.parse(fs.readFileSync('veh-rules-effective.json', 'utf8'));
const known = new Set(Object.keys(eff.effective));
const raw = fs.readFileSync(f, 'utf8');
const j = JSON.parse(raw);
for (const [ck, cls] of Object.entries(j.classes ?? {})) {
  const text = JSON.stringify(cls);
  const ids = [...new Set([...text.matchAll(/\b((?:dragonrise_reforge|fcp|vvp|superbwarfare):[a-z0-9_]+)\b/g)].map((m) => m[1]))];
  const veh = ids.filter((k) => known.has(k));
  console.log(`${ck.padEnd(26)} 「${cls.name ?? ''}」 载具=[${veh.join(', ')}] 其它id数=${ids.length - veh.length}`);
}

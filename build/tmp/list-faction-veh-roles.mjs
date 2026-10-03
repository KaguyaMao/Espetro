// list-faction-veh-roles.mjs — 列出编制里每个载具槽位（class key）对应的载具实体 id
import fs from 'node:fs';
import path from 'node:path';

const DIR = process.argv[2] ?? 'fxall';
const eff = JSON.parse(fs.readFileSync('veh-rules-effective.json', 'utf8'));
const known = new Set(Object.keys(eff.effective));

const roleIds = new Map(); // role -> Map(id -> Set(faction))
for (const f of fs.readdirSync(DIR)) {
  if (!f.endsWith('.json')) continue;
  const j = JSON.parse(fs.readFileSync(path.join(DIR, f), 'utf8'));
  for (const [ck, cls] of Object.entries(j.classes ?? {})) {
    const text = JSON.stringify(cls.variants ?? {});
    for (const m of text.matchAll(/\b((?:dragonrise_reforge|fcp|vvp|superbwarfare):[a-z0-9_]+)\b/g)) {
      if (!known.has(m[1])) continue;
      if (!roleIds.has(ck)) roleIds.set(ck, new Map());
      const mm = roleIds.get(ck);
      if (!mm.has(m[1])) mm.set(m[1], new Set());
      mm.get(m[1]).add(f.replace(/\.json$/, ''));
    }
  }
}
for (const role of [...roleIds.keys()].sort()) {
  const ids = [...roleIds.get(role).keys()].sort();
  console.log(`${role.padEnd(22)} ${ids.join(', ')}`);
}

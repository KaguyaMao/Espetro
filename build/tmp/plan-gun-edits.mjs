import fs from 'node:fs';
import path from 'node:path';

// plan-gun-edits.mjs — 先看清每把在用枪的类别/口径/当前值，便于按规则改动
const dataDir = process.argv[2];
function lenientParse(text) {
  let out = '', inStr = false, esc = false;
  for (let i = 0; i < text.length; i++) {
    const c = text[i];
    if (inStr) { out += c; if (esc) esc = false; else if (c === '\\') esc = true; else if (c === '"') inStr = false; continue; }
    if (c === '"') { inStr = true; out += c; continue; }
    if (c === '/' && text[i + 1] === '/') { while (i < text.length && text[i] !== '\n') i++; out += '\n'; continue; }
    if (c === '/' && text[i + 1] === '*') { const e = text.indexOf('*/', i + 2); i = e < 0 ? text.length : e + 1; out += '\n'; continue; }
    out += c;
  }
  return JSON.parse(out.replace(/,(\s*[}\]])/g, '$1').replace(/:\s*\+(\d)/g, ': $1'));
}
const manifest = JSON.parse(fs.readFileSync(path.join(dataDir, 'manifest.json'), 'utf8'));
const byId = new Map();
for (const m of manifest) if (m.kind === 'gun') byId.set(m.id, m);
const idxById = new Map();
for (const m of manifest) if (m.kind === 'index') idxById.set(m.id, m);

for (const id of [...byId.keys()].sort()) {
  const m = byId.get(id);
  const j = lenientParse(fs.readFileSync(path.join(dataDir, m.file), 'utf8'));
  const idx = idxById.get(id) ? lenientParse(fs.readFileSync(path.join(dataDir, idxById.get(id).file), 'utf8')) : {};
  const ex = j.bullet?.extra_damage || {};
  console.log(`${id.padEnd(32)} type=${String(idx.type).padEnd(8)} ammo=${String(j.ammo).padEnd(22)} 伤害=${j.bullet?.damage} 瞄准=${j.aim_time} 弹速=${j.bullet?.speed} 爆头=${ex.head_shot_multiplier} 扩散=${j.inaccuracy?.stand}/${j.inaccuracy?.move}/${j.inaccuracy?.sneak}/${j.inaccuracy?.lie}/镜${j.inaccuracy?.aim}`);
  console.log(`     文件: ${m.serverRel}`);
}

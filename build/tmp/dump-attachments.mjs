import fs from 'node:fs';
import path from 'node:path';

// dump-attachments.mjs — 打印编制里用到的全部附件数据（含所有 modifier 字段）
const taczDir = process.argv[2];
const factionDir = process.argv[3];

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

const files = new Map();
for (const pack of fs.readdirSync(taczDir)) {
  const root = path.join(taczDir, pack, 'data');
  if (!fs.existsSync(root)) continue;
  for (const ns of fs.readdirSync(root)) {
    const d = path.join(root, ns, 'data', 'attachments');
    if (!fs.existsSync(d)) continue;
    for (const f of fs.readdirSync(d)) if (f.endsWith('.json')) files.set(`${ns}:${f.replace(/_data\.json$/, '').replace(/\.json$/, '')}`, path.join(d, f));
  }
}

const used = new Set();
for (const f of fs.readdirSync(factionDir).filter(x => x.endsWith('.json'))) {
  const j = JSON.parse(fs.readFileSync(path.join(factionDir, f), 'utf8'));
  for (const cls of Object.values(j.classes || {})) for (const va of Object.values(cls.variants || {}))
    for (const cmd of (va.commands || []))
      for (const m of String(cmd).matchAll(/AttachmentId:"([^"]+)"/g)) used.add(m[1]);
}

for (const id of [...used].sort()) {
  const f = files.get(id);
  if (!f) { console.log(`═══ ${id}   ⚠ 未找到数据文件`); continue; }
  let data = {};
  try { data = lenientParse(fs.readFileSync(f, 'utf8')); } catch (e) { console.log(`═══ ${id}   ⚠ 解析失败 ${e.message}`); continue; }
  console.log(`═══ ${id}`);
  console.log('   ' + JSON.stringify(data).slice(0, 300));
}

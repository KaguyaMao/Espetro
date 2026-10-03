import fs from 'node:fs';
import path from 'node:path';

// verify-server-gun-diff.mjs — 对抓下来的每个枪数据，做"值级"对比（服务端 vs 本地包）
const dataDir = process.argv[2];
const localTaczDir = process.argv[3];

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
const flat = (o, p = '', out = {}) => {
  if (o === null || typeof o !== 'object') { out[p] = o; return out; }
  if (Array.isArray(o)) { o.forEach((v, i) => flat(v, `${p}[${i}]`, out)); return out; }
  for (const k of Object.keys(o)) flat(o[k], p ? `${p}.${k}` : k, out);
  return out;
};

const manifest = JSON.parse(fs.readFileSync(path.join(dataDir, 'manifest.json'), 'utf8'));
for (const m of manifest.filter(x => x.kind === 'gun').sort((a, b) => a.id.localeCompare(b.id))) {
  const localPath = path.join(localTaczDir, m.serverRel.replace(/^tacz\//, ''));
  if (!fs.existsSync(localPath)) { console.log(`${m.id}: 本地无对应文件（${m.serverRel}）`); continue; }
  const srv = flat(lenientParse(fs.readFileSync(path.join(dataDir, m.file), 'utf8')));
  const loc = flat(lenientParse(fs.readFileSync(localPath, 'utf8')));
  const keys = new Set([...Object.keys(srv), ...Object.keys(loc)]);
  const diffs = [...keys].filter(k => JSON.stringify(srv[k]) !== JSON.stringify(loc[k]));
  if (!diffs.length) { console.log(`✅ ${m.id}: 与本地包一致`); continue; }
  console.log(`⚠ ${m.id}: ${diffs.length} 处不同`);
  for (const k of diffs.slice(0, 12)) console.log(`     ${k}: 服务端=${JSON.stringify(srv[k])}  本地=${JSON.stringify(loc[k])}`);
  if (diffs.length > 12) console.log(`     …另有 ${diffs.length - 12} 处`);
}

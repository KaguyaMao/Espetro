// lookup-names.mjs — 打印指定载具 id 在模组 lang 里的中文名 + Type
import fs from 'node:fs';
import path from 'node:path';
const LANGDIR = process.argv[2] ?? 'vehlangs';
const JARDIR = process.argv[3] ?? 'vehjars2';
const ids = process.argv.slice(4);
const names = new Map();
for (const jar of fs.readdirSync(LANGDIR, { withFileTypes: true })) {
  if (!jar.isDirectory()) continue;
  for (const f of fs.readdirSync(path.join(LANGDIR, jar.name))) {
    if (!/zh_cn\.json$/.test(f)) continue;
    let j; try { j = JSON.parse(fs.readFileSync(path.join(LANGDIR, jar.name, f), 'utf8')); } catch { continue; }
    for (const [k, v] of Object.entries(j)) {
      const m = /^entity\.([a-z0-9_]+)\.(.+)$/.exec(k);
      if (m) names.set(`${m[1]}:${m[2]}`, String(v));
    }
  }
}
const types = new Map();
for (const jar of fs.readdirSync(JARDIR, { withFileTypes: true })) {
  if (!jar.isDirectory()) continue;
  for (const f of fs.readdirSync(path.join(JARDIR, jar.name))) {
    if (!f.endsWith('.json')) continue;
    const b = f.replace(/\.json$/, ''); const i = b.indexOf('__');
    if (i < 0) continue;
    const id = `${b.slice(0, i)}:${b.slice(i + 2)}`;
    try { const j = JSON.parse(fs.readFileSync(path.join(JARDIR, jar.name, f), 'utf8').replace(/^\uFEFF/, '').replace(/,(\s*[\]\}])/g, '$1')); types.set(id, j.Type); } catch { }
  }
}
for (const id of ids) console.log(`${id.padEnd(34)} Type=${String(types.get(id)).padEnd(10)} 名称=${names.get(id) ?? '(无)'}`);

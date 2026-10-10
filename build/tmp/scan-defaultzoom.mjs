// scan-defaultzoom.mjs — 扫描所有载具 JSON 的 Type 与武器 DefaultZoom
// 用法: node scan-defaultzoom.mjs [dir...]   默认同时扫 vehdata（服务端覆盖）与 vehjars（jar 内置）
import fs from 'node:fs';
import path from 'node:path';

function parseLenient(t) {
  let out = '', i = 0, inStr = false;
  t = t.replace(/^\uFEFF/, '');
  while (i < t.length) {
    const ch = t[i];
    if (inStr) { out += ch; if (ch === '\\') { out += t[i + 1] ?? ''; i += 2; continue; } if (ch === '"') inStr = false; i++; continue; }
    if (ch === '"') { inStr = true; out += ch; i++; continue; }
    if (ch === '/' && t[i + 1] === '*') { const e = t.indexOf('*/', i + 2); i = e === -1 ? t.length : e + 2; continue; }
    if (ch === '/' && t[i + 1] === '/') { const e = t.indexOf('\n', i + 2); i = e === -1 ? t.length : e; continue; }
    out += ch; i++;
  }
  return JSON.parse(out.replace(/,(\s*[\]\}])/g, '$1'));
}

const dirs = process.argv.slice(2);
const roots = dirs.length ? dirs : ['vehdata', 'vehjars'];
const rows = [];
for (const root of roots) {
  if (!fs.existsSync(root)) continue;
  const walk = (dir) => {
    for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
      const p = path.join(dir, e.name);
      if (e.isDirectory()) { walk(p); continue; }
      if (!e.name.endsWith('.json')) continue;
      let j;
      try { j = parseLenient(fs.readFileSync(p, 'utf8')); } catch { continue; }
      const weapons = j.Weapons;
      if (!weapons || Array.isArray(weapons) || typeof weapons !== 'object') continue;
      const zooms = {};
      for (const [wn, wv] of Object.entries(weapons)) {
        if (!wv || typeof wv !== 'object') continue;
        const z = wv.DefaultZoom;
        if (z !== undefined) zooms[wn] = z;
      }
      const key = root === 'vehdata'
        ? e.name.replace(/__/, ':').replace(/\.json$/, '')
        : `${e.name.replace(/__/, ':').replace(/\.json$/, '')} [jar]`;
      rows.push({ key, root, file: p, type: j.Type, hasZoom: Object.keys(zooms).length, zooms });
    }
  };
  walk(root);
}
const byType = {};
for (const r of rows) (byType[String(r.type)] ??= []).push(r);
console.log(`=== 载具 Type 分布（共 ${rows.length} 个有武器表的载具 JSON）===`);
for (const [t, list] of Object.entries(byType)) console.log(`  Type=${t}: ${list.length}`);
console.log(`\n=== 带 DefaultZoom 的载具（${rows.filter((r) => r.hasZoom).length} 个）===`);
for (const r of rows.filter((x) => x.hasZoom).sort((a, b) => String(a.key).localeCompare(String(b.key)))) {
  console.log(`  ${String(r.key).padEnd(46)} Type=${String(r.type).padEnd(12)} ${JSON.stringify(r.zooms)}`);
}
console.log(`\n=== 有武器表但没有 DefaultZoom 的载具（按 Type）===`);
const noZoom = rows.filter((r) => !r.hasZoom);
const noZoomByType = {};
for (const r of noZoom) (noZoomByType[String(r.type)] ??= []).push(r.key);
for (const [t, list] of Object.entries(noZoomByType)) console.log(`  Type=${t}: ${list.length} -> ${list.slice(0, 6).join(', ')}${list.length > 6 ? ' …' : ''}`);

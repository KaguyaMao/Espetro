// classify-tank-ifv.mjs — 用各模组 jar 内 assets/*/lang/zh_cn.json 的实体名给载具分类
// 目标：挑出「坦克」与「步兵战车」，并统计它们的 DefaultZoom 现状与是否已有 kubejs 覆盖
import fs from 'node:fs';
import path from 'node:path';
import { createRequire } from 'node:module';

const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const Mods = 'D:\\minecraft\\squadMC预发布测试\\versions\\Squad预发布测试\\mods';
const LANGDIR = process.argv[2] ?? 'vehlangs';
const JARDIR = process.argv[3] ?? 'vehjars';
const KJSDIR = process.argv[4] ?? 'vehdata';

// 1) 从 jar 里读 zh_cn 名称（用 PowerShell 预处理过的？这里直接扫已解出来的 vehjars 不行——那是 data。
//    改为：从 mods 目录读 jar 的 assets/*/lang/zh_cn.json —— 需要 zip 能力，交给调用方先解到 langs/）
const names = new Map(); // ns:id -> name
for (const jar of fs.readdirSync(LANGDIR, { withFileTypes: true })) {
  if (!jar.isDirectory()) continue;
  for (const f of fs.readdirSync(path.join(LANGDIR, jar.name))) {
    if (!/zh_cn\.json$/.test(f)) continue;
    let j;
    try { j = JSON.parse(fs.readFileSync(path.join(LANGDIR, jar.name, f), 'utf8')); } catch { continue; }
    for (const [k, v] of Object.entries(j)) {
      const m = /^entity\.([a-z0-9_]+)\.(.+)$/.exec(k);
      if (m) names.set(`${m[1]}:${m[2]}`, String(v));
    }
  }
}
console.log(`读到实体中文名 ${names.size} 条`);

// 2) 载具 JSON 清单（jar + kubejs）
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
function splitKey(f) { const b = f.replace(/\.json$/, ''); const i = b.indexOf('__'); return i < 0 ? null : `${b.slice(0, i)}:${b.slice(i + 2)}`; }

const veh = new Map(); // id -> {type, dir, file, zooms}
for (const root of [JARDIR, KJSDIR]) {
  if (!fs.existsSync(root)) continue;
  const walk = (d) => {
    for (const e of fs.readdirSync(d, { withFileTypes: true })) {
      const p = path.join(d, e.name);
      if (e.isDirectory()) { walk(p); continue; }
      if (!e.name.endsWith('.json')) continue;
      const id = splitKey(e.name);
      if (!id) continue;
      let j; try { j = parseLenient(fs.readFileSync(p, 'utf8')); } catch { continue; }
      const isKjs = root === KJSDIR;
      if (isKjs || !veh.has(id)) {
        const zooms = {};
        for (const [wn, wv] of Object.entries(j.Weapons ?? {})) {
          if (wv && typeof wv === 'object' && wv.DefaultZoom !== undefined) zooms[wn] = wv.DefaultZoom;
        }
        veh.set(id, { type: j.Type, source: isKjs ? 'kubejs' : 'jar', file: p, zooms, hasKjs: isKjs || veh.get(id)?.hasKjs || false });
      }
      if (isKjs) { const cur = veh.get(id); if (cur) cur.hasKjs = true; }
    }
  };
  walk(root);
}

const KW_TANK = /坦克/;
const KW_EXCL_TANK = /坦克歼击车|反坦克|自行火炮|自行高炮|装甲输送车/;
const KW_IFV = /步兵战车|步战车|履带式步兵战车|轮式步兵战车/;

const tank = [], ifv = [], other = [];
for (const [id, v] of [...veh].sort()) {
  const name = names.get(id) ?? '';
  let cat = 'other';
  if (KW_IFV.test(name)) cat = 'ifv';
  else if (KW_TANK.test(name) && !KW_EXCL_TANK.test(name)) cat = 'tank';
  (cat === 'tank' ? tank : cat === 'ifv' ? ifv : other).push({ id, name, type: v.type, source: v.source, hasKjs: v.hasKjs, zooms: v.zooms });
}
const show = (label, arr) => {
  console.log(`\n=== ${label}（${arr.length}）===`);
  for (const r of arr) {
    const z = Object.keys(r.zooms).length ? JSON.stringify(r.zooms) : '(无 DefaultZoom)';
    console.log(`  ${r.id.padEnd(34)} ${String(r.type).padEnd(10)} ${r.hasKjs ? 'K' : 'J'}  ${String(r.name).padEnd(30)} ${z}`);
  }
};
show('坦克（名称含“坦克”且非歼击车/自行火炮）', tank);
show('步兵战车（名称含“步兵战车/步战车”）', ifv);
console.log(`\n合计需要改：坦克 ${tank.length} + 步兵战车 ${ifv.length} = ${tank.length + ifv.length}`);
console.log(`其中已有 kubejs 覆盖（可直接改，标 K）：${[...tank, ...ifv].filter((r) => r.hasKjs).length}`);
console.log(`只有 jar 内置（需新建覆盖，标 J）：${[...tank, ...ifv].filter((r) => !r.hasKjs).length}`);
fs.writeFileSync('tank-ifv-classify.json', JSON.stringify({ tank, ifv }, null, 2), 'utf8');
console.log('\n清单写入 tank-ifv-classify.json');

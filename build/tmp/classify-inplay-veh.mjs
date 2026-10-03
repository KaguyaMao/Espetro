// classify-inplay-veh.mjs — 给出场载具逐个定性：jar/覆盖是否含方向规则、JSON 是否可解析
import fs from 'node:fs';
import path from 'node:path';

const FACTION_DIR = 'fxall';
const JARDIR = 'vehjars';
const KJSDIR = 'vehdata';
const eff = JSON.parse(fs.readFileSync('veh-rules-effective.json', 'utf8'));
const allKeys = new Set(Object.keys(eff.effective));

// 编制里出现的载具 id
const inplay = new Set();
for (const f of fs.readdirSync(FACTION_DIR)) {
  if (!f.endsWith('.json')) continue;
  const t = fs.readFileSync(path.join(FACTION_DIR, f), 'utf8');
  for (const m of t.matchAll(/\b((?:dragonrise_reforge|fcp|vvp|superbwarfare):[a-z0-9_]+)\b/g)) if (allKeys.has(m[1])) inplay.add(m[1]);
}

function findJarFile(ns, name) {
  for (const jar of fs.readdirSync(JARDIR, { withFileTypes: true })) {
    if (!jar.isDirectory()) continue;
    const p = path.join(JARDIR, jar.name, `${ns}__${name}.json`);
    if (fs.existsSync(p)) return p;
  }
  return null;
}
function parseLenient(text) {
  let t = text.replace(/^\uFEFF/, '');
  let out = '', i = 0, inStr = false;
  while (i < t.length) {
    const ch = t[i];
    if (inStr) { out += ch; if (ch === '\\') { out += t[i + 1] ?? ''; i += 2; continue; } if (ch === '"') inStr = false; i++; continue; }
    if (ch === '"') { inStr = true; out += ch; i++; continue; }
    if (ch === '/' && t[i + 1] === '*') { const e = t.indexOf('*/', i + 2); i = e === -1 ? t.length : e + 2; continue; }
    if (ch === '/' && t[i + 1] === '/') { const e = t.indexOf('\n', i + 2); i = e === -1 ? t.length : e; continue; }
    out += ch; i++;
  }
  const t2 = out.replace(/,(\s*[\]\}])/g, '$1');
  return JSON.parse(t2);
}
function info(p) {
  if (!p) return { exists: false };
  const t = fs.readFileSync(p, 'utf8');
  const strict = (() => { try { JSON.parse(t); return true; } catch { return false; } })();
  let lenient = false, angle = [];
  try {
    const j = parseLenient(t);
    lenient = true;
    angle = (j.DamageModifiers ?? []).filter((x) => typeof x === 'string' && /getSourceAngle/.test(x));
  } catch { }
  return { exists: true, strict, lenient, angle, trailing: (t.match(/,(\s*[\]\}])/g) ?? []).length };
}

const rows = [];
for (const id of [...inplay].sort()) {
  const [ns, name] = id.split(':');
  const jar = info(findJarFile(ns, name));
  const kjs = info(fs.existsSync(path.join(KJSDIR, `${ns}__${name}.json`)) ? path.join(KJSDIR, `${ns}__${name}.json`) : null);
  const jarAngle = jar.angle?.[0] ?? null;
  const kjsAngle = kjs.angle?.[0] ?? null;
  const mOf = (s) => { const m = /getSourceAngle\s*\(\s*source\s*,\s*(-?\d+(?:\.\d*)?)/.exec(s ?? ''); return m ? Number(m[1]) : null; };
  let kind;
  if (kjs.exists && !kjs.lenient) kind = '覆盖 JSON 损坏→整份被跳过';
  else if (kjsAngle) kind = '生效（覆盖里有）';
  else if (jarAngle) kind = '覆盖把规则遮掉了（jar 有，覆盖无）';
  else kind = '两边都没有规则（需新增）';
  rows.push({ id, kind, jarM: mOf(jarAngle), kjsM: mOf(kjsAngle), jarStrict: jar.strict, kjsStrict: kjs.strict, jarMods: jar.lenient, kjsMods: kjs.lenient });
}

const groups = {};
for (const r of rows) (groups[r.kind] ??= []).push(r);
for (const [kind, list] of Object.entries(groups)) {
  console.log(`\n== ${kind} (${list.length}) ==`);
  for (const r of list) {
    console.log(`   ${r.id}${r.jarM !== null ? '  jar m=' + r.jarM : ''}${r.kjsM !== null ? '  覆盖 m=' + r.kjsM : ''}`);
  }
}
console.log(`\n出场载具合计 ${rows.length} 个：` + Object.entries(groups).map(([k, v]) => `${k}=${v.length}`).join('，'));
fs.writeFileSync('inplay-veh-classify.json', JSON.stringify(rows, null, 2), 'utf8');

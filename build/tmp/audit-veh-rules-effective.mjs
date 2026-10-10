// audit-veh-rules-effective.mjs — 计算「实际生效」的方向抗性规则集
//
// 数据优先级与游戏一致：kubejs 覆盖（vehdata/，来自服务端 kubejs/data/<ns>/sbw/vehicles/）
// 覆盖 jar 内置（vehjars/<jar>/<ns>__<file>.json）。
// 对每个 key（<ns>:<文件名>）统计 $getSourceAngle 规则；输出：
//   - 生效规则总数（应与日志「已加载 N 辆载具共 N 条」一致）
//   - 被 kubejs 覆盖后【丢失】规则的车（jar 有 / 覆盖无）—— 方向抗性会静默失效
//   - 被 kubejs 覆盖后【新增】规则的车
//   - 解析失败的文件（游戏内会被跳过）
// 用法: node audit-veh-rules-effective.mjs [vehjars] [vehdata] [--list]
import fs from 'node:fs';
import path from 'node:path';

const JARDIR = process.argv[2] ?? 'vehjars';
const KJSDIR = process.argv[3] ?? 'vehdata';
const LIST = process.argv.includes('--list');

const ANGLE = /getSourceAngle\s*\(\s*source\s*,\s*(-?\d+(?:\.\d*)?)\s*\)/;

/** 展平文件名 <ns>__<file>.json → 资源键 <ns>:<file>（ns 自身可能含下划线，按第一个 __ 切） */
function splitKey(f) {
  const base = f.replace(/\.json$/, '');
  const i = base.indexOf('__');
  if (i < 0) return null;
  return `${base.slice(0, i)}:${base.slice(i + 2)}`;
}
const GATE = /getHealth\s*\(\s*\)\s*([<>]=?)\s*(-?\d+(?:\.\d*)?)\s*\?\s*(-?\d+(?:\.\d*)?)\s*:\s*(-?\d+(?:\.\d*)?)/;

/** 宽松解析：去注释、去尾随逗号（模拟 Gson lenient 的容错度） */
function parseLenient(text) {
  let t = text;
  // 去 /* */ 与 // 注释（保留字符串内内容）
  let out = '', i = 0, inStr = false;
  while (i < t.length) {
    const ch = t[i];
    if (inStr) { out += ch; if (ch === '\\') { out += t[i + 1] ?? ''; i += 2; continue; } if (ch === '"') inStr = false; i++; continue; }
    if (ch === '"') { inStr = true; out += ch; i++; continue; }
    if (ch === '/' && t[i + 1] === '*') { const e = t.indexOf('*/', i + 2); i = e === -1 ? t.length : e + 2; continue; }
    if (ch === '/' && t[i + 1] === '/') { const e = t.indexOf('\n', i + 2); i = e === -1 ? t.length : e; continue; }
    out += ch; i++;
  }
  const trailing = [];
  const t2 = out.replace(/,(\s*[\]\}])/g, (m, g) => { trailing.push(m); return g; });
  const j = JSON.parse(t2);
  return { json: j, trailingCommas: trailing.length };
}

function analyze(text) {
  const res = { ok: false, err: null, trailing: 0, modCount: null, angle: [], dollar: [], badDollar: [] };
  let p;
  try { p = parseLenient(text); } catch (e) { res.err = e.message; return res; }
  res.ok = true; res.trailing = p.trailingCommas;
  const dm = p.json?.DamageModifiers;
  if (!Array.isArray(dm)) return res;
  res.modCount = dm.length;
  for (const it of dm) {
    if (typeof it !== 'string') continue;
    const s = it.trim();
    if (!s.startsWith('$')) continue;
    res.dollar.push(s);
    const a = ANGLE.exec(s);
    if (!a) { res.badDollar.push(s); continue; }
    const g = GATE.exec(s);
    res.angle.push({ m: Number(a[1]), gate: g ? `${g[1]}${g[2]}?${g[3]}:${g[4]}` : null, raw: s });
  }
  return res;
}

// 1) jar 内置
const jarMap = new Map(); // key -> {jar, file, res}
for (const jar of fs.readdirSync(JARDIR, { withFileTypes: true })) {
  if (!jar.isDirectory()) continue;
  const dir = path.join(JARDIR, jar.name);
  for (const f of fs.readdirSync(dir)) {
    if (!f.endsWith('.json')) continue;
    const key = splitKey(f);
    if (!key) continue;
    const res = analyze(fs.readFileSync(path.join(dir, f), 'utf8'));
    jarMap.set(key, { jar: jar.name, file: f, res });
  }
}

// 2) kubejs 覆盖
const kjsMap = new Map();
if (fs.existsSync(KJSDIR)) {
  for (const f of fs.readdirSync(KJSDIR)) {
    if (!f.endsWith('.json') || f === 'report.json') continue;
    const key = splitKey(f);
    if (!key) continue;
    kjsMap.set(key, { file: f, res: analyze(fs.readFileSync(path.join(KJSDIR, f), 'utf8')) });
  }
}

// 3) 生效集
const keys = new Set([...jarMap.keys(), ...kjsMap.keys()]);
const eff = new Map();
for (const k of keys) {
  const kjs = kjsMap.get(k);
  const src = kjs ? 'kubejs' : 'jar';
  const res = kjs ? kjs.res : jarMap.get(k).res;
  eff.set(k, { source: src, res, jarHad: (jarMap.get(k)?.res.angle.length ?? 0) > 0 });
}

const withRules = [...eff].filter(([, v]) => v.res.angle.length > 0);
const ruleTotal = withRules.reduce((s, [, v]) => s + v.res.angle.length, 0);
const lost = [...eff].filter(([k, v]) => v.source === 'kubejs' && v.jarHad && v.res.angle.length === 0);
const gained = [...eff].filter(([k, v]) => v.source === 'kubejs' && !v.jarHad && v.res.angle.length > 0);
const badParse = [...eff].filter(([, v]) => !v.res.ok);
const badDollar = [...eff].filter(([, v]) => v.res.badDollar.length > 0);
const noRule = [...eff].filter(([, v]) => v.res.ok && v.res.angle.length === 0);

const mDist = {};
for (const [, v] of withRules) for (const a of v.res.angle) mDist[a.m] = (mDist[a.m] ?? 0) + 1;
const withGate = withRules.filter(([, v]) => v.res.angle.some((a) => a.gate));

console.log(`jar 内置载具 JSON: ${jarMap.size} 个 key（${fs.readdirSync(JARDIR).length} 个 jar）`);
console.log(`kubejs 覆盖: ${kjsMap.size} 个 key`);
console.log(`\n===== 生效结果 =====`);
console.log(`有方向规则的载具: ${withRules.length} 辆，共 ${ruleTotal} 条规则`);
console.log(`m 值分布: ${JSON.stringify(mDist)}`);
console.log(`带血量门的载具: ${withGate.length}${withGate.length ? ' -> ' + withGate.map(([k]) => k).join(', ') : ''}`);
console.log(`JSON 解析失败（游戏内被跳过）: ${badParse.length}${badParse.length ? ' -> ' + badParse.map(([k, v]) => k + ' [' + v.res.err + ']').join('; ') : ''}`);
console.log(`$ 条目无法识别: ${badDollar.length}${badDollar.length ? ' -> ' + badDollar.map(([k, v]) => k + ' ' + JSON.stringify(v.res.badDollar)).join('; ') : ''}`);
console.log(`\n被 kubejs 覆盖后【丢失】方向规则: ${lost.length}`);
for (const [k, v] of lost) console.log(`   ${k}  (jar=${jarMap.get(k).jar}, 覆盖后条目数=${v.res.modCount})`);
console.log(`\n被 kubejs 覆盖后【新增】方向规则: ${gained.length}`);
for (const [k] of gained) console.log(`   ${k}`);
console.log(`\n无任何方向规则的载具: ${noRule.length}`);
if (LIST) for (const [k, v] of noRule) console.log(`   ${k} (${v.source})`);
else console.log('   （加 --list 展开）');

fs.writeFileSync('veh-rules-effective.json', JSON.stringify({
  summary: { jarKeys: jarMap.size, kjsKeys: kjsMap.size, withRules: withRules.length, ruleTotal, mDist, lost: lost.map(([k]) => k), gained: gained.map(([k]) => k), badParse: badParse.map(([k, v]) => [k, v.res.err]), noRule: noRule.map(([k]) => k) },
  effective: Object.fromEntries([...eff].map(([k, v]) => [k, { source: v.source, angle: v.res.angle, modCount: v.res.modCount, trailing: v.res.trailing, err: v.res.err }])),
}, null, 2), 'utf8');
console.log('\n明细写入 veh-rules-effective.json');

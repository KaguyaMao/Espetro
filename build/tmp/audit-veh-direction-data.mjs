// audit-veh-direction-data.mjs — 审计服务端 kubejs 载具 JSON 的方向抗性数据
//
// 目的：新的 dragonrise_reforge 方向抗性 mixin（VehicleDamageModifierLoader）会读取
//   data/<ns>/sbw/vehicles/*.json 里的 DamageModifiers 中所有以 "$" 开头的字符串，
//   用正则取 getSourceAngle(source, m) 生成方向规则，键为「JSON 资源路径」= <ns>:<文件基名>。
//   本脚本把服务端 kubejs 覆盖文件全部拉下来，逐个体检：
//     1) JSON 是否可解析
//     2) DamageModifiers 是否存在、条目数
//     3) 是否有 $ 条目、m 值、是否含 getHealth 尾巴
//     4) $ 条目能否被 DirectionArmorRule 的正则识别（不能识别的会被忽略并打警告）
//
// 用法: node audit-veh-direction-data.mjs [输出目录=vehdata]
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const path = require('path');
const https = require('https');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const OUT = process.argv[2] ?? 'vehdata';
fs.mkdirSync(OUT, { recursive: true });

const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

function req(method, urlStr, headers, body, getCookie) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const r = https.request({ method, hostname: url.hostname, port: url.port || 443, path: url.pathname + url.search, headers, rejectUnauthorized: false }, (res) => {
      let d = ''; res.on('data', (c) => (d += c));
      res.on('end', () => resolve({ status: res.statusCode, body: d, cookie: getCookie ? (res.headers['set-cookie'] ?? []).map((l) => l.split(';')[0]).join('; ') : undefined }));
    });
    r.on('error', reject); if (body !== undefined) r.write(body); r.end();
  });
}

const lr = await req('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: USER, password: PASS }), true);
let token = String(JSON.parse(lr.body).data ?? '');
const cookie = lr.cookie ?? '';
const q = () => `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
async function relogin() {
  const r = await req('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: USER, password: PASS }), true);
  token = String(JSON.parse(r.body).data ?? '');
}

async function listDir(rel) {
  for (let a = 0; a < 4; a++) {
    try {
      const r = await req('GET', `${PANEL}/api/files/list?page=0&page_size=500&file_name=&target=${encodeURIComponent(rel)}&${q()}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
      const b = JSON.parse(r.body);
      if (b.status === 200) return b.data?.items ?? [];
    } catch { }
    await sleep(2500);
  }
  return [];
}

async function fetchText(rel) {
  for (let a = 0; a < 5; a++) {
    try {
      const r = await req('PUT', `${PANEL}/api/files?${q()}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target: rel }));
      const body = JSON.parse(r.body);
      if (body.status === 401 || /token/i.test(String(body.data ?? ''))) { await relogin(); continue; }
      const d = String(body.data ?? '');
      if (d.trim().startsWith('{')) return d;
    } catch { }
    await sleep(2500);
  }
  return null;
}

// ---------- DirectionArmorRule 同款正则 ----------
const ANGLE = /getSourceAngle\s*\(\s*source\s*,\s*(-?\d+(?:\.\d*)?)\s*\)/;
const GATE = /getHealth\s*\(\s*\)\s*([<>]=?)\s*(-?\d+(?:\.\d*)?)\s*\?\s*(-?\d+(?:\.\d*)?)\s*:\s*(-?\d+(?:\.\d*)?)/;

// 1) 枚举 kubejs/data 下所有命名空间的 sbw/vehicles
const dataDirs = (await listDir('kubejs/data')).filter((i) => i.type === 0).map((i) => i.name);
console.log(`kubejs/data 下命名空间: ${dataDirs.join(', ')}`);

const files = [];
for (const ns of dataDirs) {
  const rel = `kubejs/data/${ns}/sbw/vehicles`;
  const items = await listDir(rel);
  const jsons = items.filter((i) => i.type !== 0 && String(i.name).endsWith('.json'));
  if (jsons.length) console.log(`  ${rel} → ${jsons.length} 个 json`);
  for (const j of jsons) files.push({ ns, name: j.name, rel: `${rel}/${j.name}`, size: j.size });
  await sleep(400);
}

// 2) 逐个下载 + 体检
const rows = [];
for (const f of files) {
  const local = path.join(OUT, `${f.ns}__${f.name}`);
  let text = null;
  if (fs.existsSync(local) && fs.statSync(local).size > 500) text = fs.readFileSync(local, 'utf8');
  else {
    text = await fetchText(f.rel);
    if (text !== null) fs.writeFileSync(local, text, 'utf8');
    await sleep(500);
  }
  const key = `${f.ns}:${f.name.replace(/\.json$/, '')}`;
  const row = { key, rel: f.rel, size: f.size, ok: false, parseError: null, modCount: null, dollar: [], angle: [], warnings: [] };
  if (text === null) { row.warnings.push('下载失败'); rows.push(row); console.log(`FAIL ${f.rel}`); continue; }
  let j = null;
  try { j = JSON.parse(text); row.ok = true; }
  catch (e) { row.parseError = e.message; row.warnings.push('JSON 解析失败: ' + e.message); }
  if (j) {
    const dm = j.DamageModifiers;
    if (Array.isArray(dm)) {
      row.modCount = dm.length;
      for (const it of dm) {
        if (typeof it !== 'string') continue;
        const s = it.trim();
        if (!s.startsWith('$')) continue;
        row.dollar.push(s);
        const a = ANGLE.exec(s);
        if (!a) { row.warnings.push('$ 条目不含可识别的 getSourceAngle：' + s); continue; }
        const g = GATE.exec(s);
        row.angle.push({ m: Number(a[1]), gate: g ? `${g[1]}${g[2]}?${g[3]}:${g[4]}` : null, raw: s });
      }
    } else row.warnings.push('无 DamageModifiers 数组');
  }
  rows.push(row);
}

// 3) 报告
const withAngle = rows.filter((r) => r.angle.length > 0);
const withDollarNoAngle = rows.filter((r) => r.dollar.length > 0 && r.angle.length === 0);
const noMods = rows.filter((r) => r.modCount === null);
const mDist = {};
for (const r of withAngle) for (const a of r.angle) mDist[a.m] = (mDist[a.m] ?? 0) + 1;

console.log(`\n===== 汇总（服务端 kubejs 覆盖 ${rows.length} 个文件）=====`);
console.log(`有方向规则的文件: ${withAngle.length}`);
console.log(`m 值分布: ${JSON.stringify(mDist)}`);
console.log(`有 $ 但无 getSourceAngle: ${withDollarNoAngle.length}${withDollarNoAngle.length ? ' -> ' + withDollarNoAngle.map((r) => r.key).join(', ') : ''}`);
console.log(`无 DamageModifiers 数组: ${noMods.length}${noMods.length ? ' -> ' + noMods.map((r) => r.key).join(', ') : ''}`);
const badJson = rows.filter((r) => !r.ok);
console.log(`JSON 解析失败: ${badJson.length}${badJson.length ? ' -> ' + badJson.map((r) => r.key + '(' + r.parseError + ')').join(', ') : ''}`);
const warns = rows.filter((r) => r.warnings.length && r.ok);
if (warns.length) {
  console.log(`\n--- 警告 ---`);
  for (const r of warns) for (const w of r.warnings) console.log(`  ${r.key}: ${w}`);
}

console.log(`\n--- 明细（key | 条目数 | m 值 | 是否带血量门）---`);
for (const r of rows.sort((a, b) => a.key.localeCompare(b.key))) {
  const m = r.angle.map((a) => a.m).join(',') || '-';
  const gate = r.angle.some((a) => a.gate) ? '有' : '';
  console.log(`  ${r.key} | ${r.modCount ?? '-'} | ${m} | ${gate}`);
}

fs.writeFileSync(path.join(OUT, 'report.json'), JSON.stringify(rows, null, 2), 'utf8');
console.log(`\n报告写入 ${path.join(OUT, 'report.json')}`);

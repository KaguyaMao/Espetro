import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const https = require('https');
const path = require('path');

// sync-check.mjs — 抓取服务端 kubejs 载具数据并与客户端数据包逐个字段比对（重点是 ShootPos/枢轴/弹药类型）
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const OUT = 'D:/minecraft/modp/Espetro/build/tmp/srv-sync';
const SAVE = 'D:/minecraft/squadMC预发布测试/versions/Squad预发布测试/saves/新的世界/datapacks';
const BASE = SAVE + '/dragonrise_reforge/data/dragonrise_reforge/sbw/vehicles';
const TUNED = SAVE + '/va_shotpos/data/dragonrise_reforge/sbw/vehicles';
const FCP_TUNED = SAVE + '/va_shotpos/data/fcp/sbw/vehicles';

const sleep = (ms) => new Promise(r => setTimeout(r, ms));
function req(method, urlStr, headers, body, getCookie) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const r = https.request({ method, hostname: url.hostname, port: url.port || 443, path: url.pathname + url.search, headers, rejectUnauthorized: false }, (res) => {
      let d = '';
      res.on('data', c => (d += c));
      res.on('end', () => resolve({ status: res.statusCode, body: d, cookie: getCookie ? (res.headers['set-cookie'] ?? []).map(l => l.split(';')[0]).join('; ') : undefined }));
    });
    r.on('error', reject);
    if (body !== undefined) r.write(body);
    r.end();
  });
}
async function login() {
  const r = await req('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: USER, password: PASS }), true);
  return { token: String(JSON.parse(r.body).data ?? ''), cookie: r.cookie ?? '' };
}
let { token, cookie } = await login();
fs.mkdirSync(OUT, { recursive: true });

async function fetchRemote(relPath, tries = 5) {
  const local = path.join(OUT, relPath.replace(/[\\/]/g, '__'));
  if (fs.existsSync(local) && fs.statSync(local).size > 100) return fs.readFileSync(local, 'utf8');
  for (let a = 1; a <= tries; a++) {
    const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
    const res = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target: relPath }));
    let data = '';
    try { data = String(JSON.parse(res.body).data ?? ''); } catch { data = ''; }
    if (data.trim().startsWith('{')) { fs.writeFileSync(local, data, 'utf8'); return data; }
    await sleep(4000);
    ({ token, cookie } = await login());
  }
  return null;
}

const flat = (o, p = '', out = {}) => {
  if (o === null || typeof o !== 'object') { out[p] = o; return out; }
  if (Array.isArray(o)) { o.forEach((v, i) => flat(v, `${p}[${i}]`, out)); return out; }
  for (const k of Object.keys(o)) flat(o[k], p ? `${p}.${k}` : k, out);
  return out;
};
const load = (p) => (fs.existsSync(p) ? JSON.parse(fs.readFileSync(p, 'utf8')) : null);

// 需要比对的文件：客户端调参包里的全部 + 客户端基础包里其余（看服务端有没有）
const tunedNames = fs.readdirSync(TUNED).filter(f => f.endsWith('.json')).sort();
const baseNames = fs.readdirSync(BASE).filter(f => f.endsWith('.json')).sort();
const report = [];
const status = { same: [], differ: [], missingOnServer: [], extraOnServer: [] };

for (const n of [...new Set([...tunedNames, ...baseNames])].sort()) {
  const text = await fetchRemote(`kubejs/data/dragonrise_reforge/sbw/vehicles/${n}`);
  if (text === null) { status.missingOnServer.push(n); report.push(`✘ ${n}：服务端读取失败/不存在`); continue; }
  const srv = JSON.parse(text);
  const base = load(path.join(BASE, n));
  const tuned = load(path.join(TUNED, n));
  const clientEff = tuned || base;   // 客户端生效值（va_shotpos 覆盖 dragonrise_reforge）
  const s = flat(srv), c = flat(clientEff);
  const keys = new Set([...Object.keys(s), ...Object.keys(c)]);
  const diffs = [];
  for (const k of keys) {
    if (JSON.stringify(s[k]) !== JSON.stringify(c[k])) diffs.push(`${k}: 服务端=${JSON.stringify(s[k])} 客户端=${JSON.stringify(c[k])}`);
  }
  // 弹药类型被写成字符串（会刷屏）的检查
  const badAmmo = Object.entries(srv.Weapons || {}).filter(([, w]) => typeof w.AmmoType === 'string' && /^\s*[[{]/.test(w.AmmoType)).map(([k]) => k);
  if (diffs.length === 0 && !badAmmo.length) status.same.push(n);
  else status.differ.push({ n, diffs, badAmmo });
  report.push(`\n═══ ${n}  差异 ${diffs.length} 处${badAmmo.length ? `  ⚠ AmmoType 被字符串化: ${badAmmo.join(',')}` : ''}${tuned ? '  (客户端有调参覆盖)' : ''}`);
  for (const d of diffs.slice(0, 14)) report.push(`     ${d}`);
  if (diffs.length > 14) report.push(`     …另有 ${diffs.length - 14} 处`);
}

// fcp 命名空间
const fcpTuned = fs.readdirSync(FCP_TUNED).filter(f => f.endsWith('.json'));
for (const n of fcpTuned) {
  const text = await fetchRemote(`kubejs/data/fcp/sbw/vehicles/${n}`);
  report.push(`\n═══ fcp/${n}：服务端${text === null ? '缺失 ✘' : '存在 ✔'}`);
  if (text === null) status.missingOnServer.push(`fcp/${n}`);
}

const summary = [
  `\n================ 汇总 ================`,
  `服务端与客户端生效数据【完全一致】的载具 ${status.same.length} 个: ${status.same.join(', ') || '（无）'}`,
  `【有差异】的载具 ${status.differ.length} 个: ${status.differ.map(d => d.n + `(${d.diffs.length}处)`).join(', ') || '（无）'}`,
  `服务端【缺失】的文件: ${status.missingOnServer.join(', ') || '（无）'}`,
  `服务端 AmmoType 被字符串化的武器: ${status.differ.filter(d => d.badAmmo.length).map(d => `${d.n}/${d.badAmmo.join('+')}`).join(', ') || '（无）'}`,
].join('\n');

const all = report.join('\n') + '\n' + summary;
fs.writeFileSync('sync-check-report.txt', all, 'utf8');
console.log(summary);

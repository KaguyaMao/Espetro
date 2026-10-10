// pull-factions.mjs — 抓取服务器 EsFactions 下指定文件到本地目录（带重试、UTF-8 原样落盘）
// 用法: node pull-factions.mjs <本地目录> <文件名...>
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const https = require('https');
const path = require('path');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';

const OUT = process.argv[2];
const names = process.argv.slice(3);
fs.mkdirSync(OUT, { recursive: true });

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

let ok = 0;
for (const n of names) {
  const local = path.join(OUT, n);
  if (fs.existsSync(local) && fs.statSync(local).size > 1000) { console.log(`SKIP ${n}（已存在 ${fs.statSync(local).size} B）`); ok++; continue; }
  let text = null, last = '';
  for (let a = 0; a < 6 && text === null; a++) {
    const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
    const res = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target: `EsFactions/${n}` }));
    try {
      const data = String(JSON.parse(res.body).data ?? '');
      if (data.trim().startsWith('{')) text = data; else { last = res.body.slice(0, 110); await sleep(4000); }
    } catch (e) { last = String(e?.message ?? e); await sleep(4000); }
  }
  if (text === null) { console.log(`FAIL ${n}  (${last})`); continue; }
  fs.writeFileSync(local, text, 'utf8');
  let jsonOk = false;
  try { JSON.parse(text); jsonOk = true; } catch (e) { }
  console.log(`OK   ${n}  ${text.length} 字符  json=${jsonOk}`);
  ok++;
  await sleep(1200);
}
console.log(`\n完成 ${ok}/${names.length}`);

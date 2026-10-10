// refetch-guns.mjs — 按已有 manifest 重新抓取"枪数据"文件（保持文件名一致），得到最新快照
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const path = require('path');
const https = require('https');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';

const SRC = process.argv[2];       // 之前的抓取目录（含 manifest.json）
const OUT = process.argv[3];       // 新目录

const manifest = JSON.parse(fs.readFileSync(path.join(SRC, 'manifest.json'), 'utf8'));
fs.mkdirSync(OUT, { recursive: true });
for (const m of manifest) fs.copyFileSync(path.join(SRC, m.file), path.join(OUT, m.file));
fs.copyFileSync(path.join(SRC, 'manifest.json'), path.join(OUT, 'manifest.json'));

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
const guns = manifest.filter(m => m.kind === 'gun');
for (const m of guns) {
  let text = null;
  for (let a = 0; a < 5 && text === null; a++) {
    const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
    const res = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target: m.serverRel }));
    try { const d = String(JSON.parse(res.body).data ?? ''); if (d.trim().startsWith('{')) text = d; else await sleep(3500); } catch { await sleep(3500); }
  }
  if (text === null) { console.log(`FAIL ${m.id}`); continue; }
  fs.writeFileSync(path.join(OUT, m.file), text, 'utf8');
  ok++;
  await sleep(700);
}
console.log(`重新抓取枪数据 ${ok}/${guns.length} → ${OUT}`);

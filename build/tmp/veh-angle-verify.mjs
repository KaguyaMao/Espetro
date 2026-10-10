// veh-angle-verify.mjs — 逐一比对服务端与本地 after 文件（SHA256）
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const path = require('path');
const https = require('https');
const crypto = require('crypto');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const DIR = process.argv[2] ?? 'veh-angle/after';
const sleep = (ms) => new Promise(r => setTimeout(r, ms));

function req(method, urlStr, headers, body, getCookie, binary) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const r = https.request({ method, hostname: url.hostname, port: url.port || 443, path: url.pathname + url.search, headers, rejectUnauthorized: false }, (res) => {
      const chunks = [];
      res.on('data', c => chunks.push(c));
      res.on('end', () => {
        const buf = Buffer.concat(chunks);
        resolve({ status: res.statusCode, buf, body: binary ? null : buf.toString('utf8'), cookie: getCookie ? (res.headers['set-cookie'] ?? []).map(l => l.split(';')[0]).join('; ') : undefined });
      });
    });
    r.on('error', reject);
    if (body !== undefined) r.write(body);
    r.end();
  });
}
const lr = await req('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: USER, password: PASS }), true);
const token = String(JSON.parse(lr.body).data ?? '');
const cookie = lr.cookie ?? '';
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;

const files = fs.readdirSync(DIR).filter(f => f.endsWith('.json') && f !== 'manifest.json').sort();
let ok = 0; const bad = [];
for (const f of files) {
  const ns = f.startsWith('fcp__') ? 'fcp' : 'dragonrise_reforge';
  const remote = `kubejs/data/${ns}/sbw/vehicles/${f.replace(/^(fcp|dragonrise)__/, '')}`;
  const localHash = crypto.createHash('sha256').update(fs.readFileSync(path.join(DIR, f))).digest('hex');
  let remoteBuf = null;
  for (let a = 0; a < 4 && !remoteBuf; a++) {
    const t = await req('POST', `${PANEL}/api/files/download?${q}&file_name=${encodeURIComponent(remote)}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
    let d = null;
    try { d = JSON.parse(t.body).data; } catch { }
    if (!d?.password) { await sleep(2500); continue; }
    const m = String(d.addr).match(/^wss?:\/\/([^/:]+)(:\d+)?/);
    const daemonUrl = 'https://' + new URL(PANEL).hostname + (m?.[2] ?? '');
    const dl = await req('GET', `${daemonUrl}/download/${d.password}/${encodeURIComponent(remote.split('/').pop())}`, {}, undefined, false, true);
    if (dl.status === 200 && dl.buf.length) remoteBuf = dl.buf;
    else await sleep(2000);
  }
  if (!remoteBuf) { bad.push(`${f}(下载失败)`); continue; }
  const remoteHash = crypto.createHash('sha256').update(remoteBuf).digest('hex');
  if (remoteHash === localHash) ok++;
  else bad.push(`${f}(不一致 服务端${remoteBuf.length}B 本地${fs.statSync(path.join(DIR, f)).size}B)`);
  await sleep(250);
}
console.log(`校验通过 ${ok}/${files.length}`);
if (bad.length) { console.log('问题文件:'); bad.forEach(b => console.log('  ' + b)); process.exit(1); }

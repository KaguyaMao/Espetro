// veh-angle-upload.mjs — 批量上传载具 JSON 到服务端对应目录
// 用法: node veh-angle-upload.mjs <dir> [filter]
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const path = require('path');
const https = require('https');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const DIR = process.argv[2] ?? 'veh-angle/after';
const FILTER = process.argv[3] ?? '';
const sleep = (ms) => new Promise(r => setTimeout(r, ms));

function req(method, urlStr, headers, body, getCookie) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const r = https.request({ method, hostname: url.hostname, port: url.port || 443, path: url.pathname + url.search, headers, rejectUnauthorized: false }, (res) => {
      const chunks = [];
      res.on('data', c => chunks.push(c));
      res.on('end', () => resolve({ status: res.statusCode, body: Buffer.concat(chunks).toString('utf8'), cookie: getCookie ? (res.headers['set-cookie'] ?? []).map(l => l.split(';')[0]).join('; ') : undefined }));
    });
    r.on('error', reject);
    if (body !== undefined) r.write(body);
    r.end();
  });
}
const login = async () => {
  const r = await req('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: USER, password: PASS }), true);
  return { token: String(JSON.parse(r.body).data ?? ''), cookie: r.cookie ?? '' };
};

const ctx = await login();
const q = () => `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${ctx.token}`;
const files = fs.readdirSync(DIR).filter(f => f.endsWith('.json') && f !== 'manifest.json' && f.includes(FILTER)).sort();

let okCount = 0, fail = [];
for (const f of files) {
  const ns = f.startsWith('fcp__') ? 'fcp' : 'dragonrise_reforge';
  const remoteName = f.replace(/^(fcp|dragonrise)__/, '');
  const remoteDir = `kubejs/data/${ns}/sbw/vehicles`;
  const buf = fs.readFileSync(path.join(DIR, f));
  let ok = false;
  for (let a = 1; a <= 4 && !ok; a++) {
    const up = await req('POST', `${PANEL}/api/files/upload?${q()}&upload_dir=${encodeURIComponent(remoteDir)}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': ctx.cookie });
    let data = {};
    try { data = JSON.parse(up.body).data ?? {}; } catch { }
    const m = String(data.addr ?? '').match(/^wss?:\/\/([^/:]+)(:\d+)?/);
    const daemonUrl = 'https://' + new URL(PANEL).hostname + (m?.[2] ?? '');
    if (!data.password) { await sleep(2500); continue; }
    const boundary = '----DSHUp' + Date.now().toString(16);
    const head = Buffer.from(`--${boundary}\r\nContent-Disposition: form-data; name="file"; filename="${remoteName}"\r\nContent-Type: application/octet-stream\r\n\r\n`);
    const tail = Buffer.from(`\r\n--${boundary}--\r\n`);
    const body = Buffer.concat([head, buf, tail]);
    const res = await req('POST', `${daemonUrl}/upload/${data.password}?overwrite=true`, {
      'X-Requested-With': 'XMLHttpRequest', 'Content-Type': `multipart/form-data; boundary=${boundary}`, 'Content-Length': String(body.length)
    }, body);
    ok = res.status === 200 && res.body.trim() === 'OK';
    if (!ok) await sleep(2500);
  }
  if (ok) { okCount++; console.log(`OK   ${remoteDir}/${remoteName} (${buf.length} B)`); }
  else { fail.push(remoteName); console.log(`FAIL ${remoteDir}/${remoteName}`); }
  await sleep(350);
}
console.log(`\n完成 ${okCount}/${files.length}${fail.length ? '，失败: ' + fail.join(', ') : ''}`);
process.exit(fail.length ? 1 : 0);

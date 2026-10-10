// upload-fortifications.mjs — 上传改好的 config/espetro/fortifications.json（先把线上原文件备份到本地）
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const https = require('https');
const PANEL = 'https://www.derpydoge.fun:20000';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
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
const lr = await req('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: 'boy', password: 'boY1145141919810Fuck' }), true);
const token = String(JSON.parse(lr.body).data ?? '');
const cookie = lr.cookie ?? '';
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
const TARGET = 'config/espetro/fortifications.json';
async function read() {
  for (let a = 0; a < 4; a++) {
    const r = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target: TARGET }));
    try { const b = JSON.parse(r.body); if (b.status === 200) return String(b.data ?? ''); } catch { }
    await sleep(2600);
  }
  return null;
}
async function upload(local, dir, name) {
  for (let a = 0; a < 5; a++) {
    const t = await req('POST', `${PANEL}/api/files/upload?${q}&upload_dir=${encodeURIComponent(dir)}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
    let data = {};
    try { data = JSON.parse(t.body).data ?? {}; } catch { }
    if (!data.password) { await sleep(2500); continue; }
    const m = String(data.addr ?? '').match(/^wss?:\/\/([^/:]+)(:\d+)?/);
    const daemonUrl = 'https://' + new URL(PANEL).hostname + (m?.[2] ?? '');
    const buf = fs.readFileSync(local);
    const boundary = '----DSHUpload' + Date.now().toString(16);
    const head = Buffer.from(`--${boundary}\r\nContent-Disposition: form-data; name="file"; filename="${name}"\r\nContent-Type: application/octet-stream\r\n\r\n`);
    const tail = Buffer.from(`\r\n--${boundary}--\r\n`);
    const body = Buffer.concat([head, buf, tail]);
    const res = await req('POST', `${daemonUrl}/upload/${data.password}?overwrite=true`, { 'X-Requested-With': 'XMLHttpRequest', 'Content-Type': `multipart/form-data; boundary=${boundary}`, 'Content-Length': String(body.length) }, body);
    if (res.status === 200 && res.body.trim() === 'OK') return true;
    await sleep(2500);
  }
  return false;
}
const before = await read();
if (before === null) { console.log('❌ 线上配置读取失败'); process.exit(1); }
fs.writeFileSync('build/tmp/fortifications-online-before.json', before, 'utf8');
console.log(`线上原文件已备份 (${before.length} 字符)`);
const after = fs.readFileSync('build/tmp/fortifications-after.json', 'utf8');
console.log(`待上传文件 (${after.length} 字符)`);
const ok = await upload('build/tmp/fortifications-after.json', 'config/espetro', 'fortifications.json');
console.log('上传: ' + (ok ? 'OK' : 'FAIL'));
await sleep(2600);
const check = await read();
console.log('回下载一致: ' + (check !== null && check.trim() === after.trim() ? 'MATCH ✓' : 'DIFF ❌'));

// upload-gun-edits.mjs — 把改好的枪械数据按 manifest 里的服务端路径逐个上传
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const path = require('path');
const https = require('https');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';

const DIR = process.argv[2];
const manifest = JSON.parse(fs.readFileSync(path.join(DIR, 'manifest.json'), 'utf8'));

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
async function newTask(token, cookie, dir) {
  const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&upload_dir=${encodeURIComponent(dir)}&token=${token}`;
  const res = await req('POST', `${PANEL}/api/files/upload?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
  if (res.status !== 200) throw new Error('create task failed: ' + res.body.slice(0, 160));
  const d = JSON.parse(res.body).data ?? {};
  const port = (String(d.addr).match(/^wss?:\/\/[^/:]+(:\d+)?/) || [])[1] ?? '';
  return { password: String(d.password ?? ''), daemonUrl: 'https://' + new URL(PANEL).hostname + port };
}
async function upload(filePath, name, daemonUrl, password) {
  const buf = fs.readFileSync(filePath);
  const boundary = '----DSHUpload' + Date.now().toString(16);
  const head = Buffer.from(`--${boundary}\r\nContent-Disposition: form-data; name="file"; filename="${name}"\r\nContent-Type: application/json\r\n\r\n`);
  const tail = Buffer.from(`\r\n--${boundary}--\r\n`);
  const body = Buffer.concat([head, buf, tail]);
  const res = await req('POST', `${daemonUrl}/upload/${password}?overwrite=true`, {
    'X-Requested-With': 'XMLHttpRequest',
    'Content-Type': `multipart/form-data; boundary=${boundary}`,
    'Content-Length': String(body.length),
  }, body);
  return { ok: res.status === 200 && res.body.trim() === 'OK', detail: res.status + ' ' + res.body.slice(0, 100), size: buf.length };
}

let { token, cookie } = await login();
let ok = 0;
const log = [];
for (const m of manifest) {
  const dir = path.posix.dirname(m.serverRel).replace(/\\/g, '/');
  const name = path.posix.basename(m.serverRel);
  let done = false;
  for (let attempt = 1; attempt <= 3 && !done; attempt++) {
    try {
      const { password, daemonUrl } = await newTask(token, cookie, dir);
      const r = await upload(path.join(DIR, m.file), name, daemonUrl, password);
      if (r.ok) { ok++; log.push(`OK   ${m.gun.padEnd(34)} → ${m.serverRel}`); done = true; }
      else { log.push(`重试${attempt} ${m.gun}: ${r.detail}`); await sleep(2500); }
    } catch (e) {
      log.push(`重试${attempt} ${m.gun} 异常: ${e?.message ?? e}`); await sleep(2500);
      try { ({ token, cookie } = await login()); } catch { }
    }
  }
  if (!done) log.push(`FAIL ${m.gun} (${m.serverRel})`);
  await sleep(600);
}
fs.writeFileSync(path.join(DIR, 'upload-log.txt'), log.join('\n'), 'utf8');
console.log(log.join('\n'));
console.log(`\n完成 ${ok}/${manifest.length}`);

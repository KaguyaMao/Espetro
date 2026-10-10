// delete-two.mjs — 尝试用 MCSM daemon 删除接口（带 target 目录 + targets 文件名）
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const https = require('https');
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const DIR = 'kubejs/server_scripts';
const NAMES = ['esvehhp_ammo.js', 'esvehhp_tacz_caliber.js'];

function req(method, urlStr, headers, body, getCookie) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const r = https.request({ method, hostname: url.hostname, port: url.port || 443, path: url.pathname + url.search, headers, rejectUnauthorized: false }, (res) => {
      let d = ''; res.on('data', c => (d += c));
      res.on('end', () => resolve({ status: res.statusCode, body: d, cookie: getCookie ? (res.headers['set-cookie'] ?? []).map(l => l.split(';')[0]).join('; ') : undefined }));
    });
    r.on('error', reject);
    if (body !== undefined) r.write(body);
    r.end();
  });
}

const lr = await req('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: USER, password: PASS }), true);
const token = String(JSON.parse(lr.body).data ?? '');
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;

for (const body of [
  { target: DIR, targets: NAMES },
  { targets: NAMES.map(n => `${DIR}/${n}`) },
]) {
  const res = await req('DELETE', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': lr.cookie, 'Content-Type': 'application/json' }, JSON.stringify(body));
  console.log(`body=${JSON.stringify(body)}\n  → ${res.status} ${res.body.slice(0, 200)}`);
}

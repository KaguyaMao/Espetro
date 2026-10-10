// del2.mjs — 再试删除接口（/api/files/delete）
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const https = require('https');
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const TARGETS = process.argv.slice(2);

function req(method, urlStr, headers, body) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const r = https.request({ method, hostname: url.hostname, port: url.port || 443, path: url.pathname + url.search, headers, rejectUnauthorized: false }, res => {
      let d = ''; res.on('data', c => (d += c)); res.on('end', () => resolve({ status: res.statusCode, body: d }));
    });
    r.on('error', reject); if (body !== undefined) r.write(body); r.end();
  });
}
const login = await req('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: USER, password: PASS }));
const token = String(JSON.parse(login.body).data ?? '');
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
const H = { 'X-Requested-With': 'XMLHttpRequest', 'Content-Type': 'application/json' };
const tries = [
  ['DELETE /api/files/delete {targets}', 'DELETE', `${PANEL}/api/files/delete?${q}`, JSON.stringify({ targets: TARGETS })],
  ['DELETE /api/files/delete {target}', 'DELETE', `${PANEL}/api/files/delete?${q}`, JSON.stringify({ target: TARGETS })],
  ['DELETE /api/files {targets}', 'DELETE', `${PANEL}/api/files?${q}`, JSON.stringify({ targets: TARGETS })],
];
for (const [name, m, url, body] of tries) {
  const res = await req(m, url, H, body);
  console.log(`${name} -> ${res.status} ${res.body.slice(0, 200).replace(/\n/g, ' ')}`);
  if (res.status === 200 && /"status":200/.test(res.body)) break;
  await new Promise(r => setTimeout(r, 2500));
}

// del-test.mjs — 试出 MCSM 删除接口的正确载荷形状
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
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
const shapes = [
  ['{target:[...]}', JSON.stringify({ target: TARGETS })],
  ['{target:"one"}', JSON.stringify({ target: TARGETS[0] })],
  ['{files:[...]}', JSON.stringify({ files: TARGETS })],
  ['{targets:[...]}+target', JSON.stringify({ targets: TARGETS, target: TARGETS })],
  ['[...]', JSON.stringify(TARGETS)],
];
for (const [name, body] of shapes) {
  const res = await req('DELETE', `${PANEL}/api/files?${q}`, H, body);
  console.log(`${name} -> ${res.status} ${res.body.slice(0, 160)}`);
  if (res.status === 200 && /200/.test(res.body)) break;
}

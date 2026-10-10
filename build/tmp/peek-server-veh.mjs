// peek-server-veh.mjs — 从服务端读一个载具 JSON，打印 DamageModifiers 块
// 用法: node peek-server-veh.mjs <服务端相对路径>
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const https = require('https');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const TARGET = process.argv[2];
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

const lr = await req('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: USER, password: PASS }), true);
const token = String(JSON.parse(lr.body).data ?? '');
const cookie = lr.cookie ?? '';
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;

for (let a = 0; a < 5; a++) {
  const res = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target: TARGET }));
  try {
    const d = String(JSON.parse(res.body).data ?? '');
    if (d.trim().startsWith('{')) {
      const i = d.indexOf('"DamageModifiers"');
      console.log(`文件 ${TARGET} 长度=${d.length}`);
      console.log(i >= 0 ? d.slice(i, i + 600) : '（无 DamageModifiers 字段）');
      process.exit(0);
    }
    console.log('resp:', res.body.slice(0, 200));
  } catch (e) { console.log('parse fail', e.message); }
  await sleep(3000);
}
console.log('FAILED');
process.exit(1);

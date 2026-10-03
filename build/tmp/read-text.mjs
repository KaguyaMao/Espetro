// read-text.mjs — 读取服务器任意文本文件（不做 JSON 校验，自动重试节流）
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const https = require('https');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const TARGET = process.argv[2];
const OUT = process.argv[3];
const TRIES = Number(process.argv[4] ?? 6);
const sleep = ms => new Promise(r => setTimeout(r, ms));

function req(method, urlStr, headers, body, getCookie) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const r = https.request({ method, hostname: url.hostname, port: url.port || 443, path: url.pathname + url.search, headers, rejectUnauthorized: false }, res => {
      let d = ''; res.on('data', c => (d += c));
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
for (let a = 1; a <= TRIES; a++) {
  const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
  const res = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target: TARGET }));
  let data = '';
  try { data = String(JSON.parse(res.body).data ?? ''); } catch { data = ''; }
  if (data && !/冷却中/.test(data)) {
    fs.writeFileSync(OUT, data, 'utf8');
    console.log(`OK 第${a}次  ${TARGET} → ${OUT} (${data.length} 字符)`);
    process.exit(0);
  }
  console.log(`重试${a}: ${data.slice(0, 40) || res.body.slice(0, 40)}`);
  await sleep(3000);
  try { ({ token, cookie } = await login()); } catch { }
}
console.log('全部失败');
process.exit(1);

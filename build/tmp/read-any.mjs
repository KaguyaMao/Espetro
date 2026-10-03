// read-any.mjs — 用 PUT /api/files 读取服务端任意文本文件（带冷却重试）
// 用法: node read-any.mjs <远端相对路径> <本地输出路径>
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const https = require('https');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const REMOTE = process.argv[2];
const LOCAL = process.argv[3];
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

let ctx = await login();
for (let a = 1; a <= 8; a++) {
  const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${ctx.token}`;
  const res = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': ctx.cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target: REMOTE }));
  let text = '';
  try { text = String(JSON.parse(res.body).data ?? ''); } catch { text = ''; }
  if (text.trim() !== '' && !text.includes('此操作冷却中')) {
    fs.writeFileSync(LOCAL, text, 'utf8');
    console.log(`OK 第${a}次  ${REMOTE} → ${LOCAL} (${text.length} 字符)`);
    process.exit(0);
  }
  await sleep(2500);
  try { ctx = await login(); } catch { }
}
console.log('FAILED ' + REMOTE);
process.exit(1);

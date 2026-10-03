// read-debug.mjs — 打印一次文件读取的原始响应，便于诊断失败原因
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const https = require('https');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const TARGET = process.argv[2];

function req(method, urlStr, headers, body, getCookie) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const r = https.request({ method, hostname: url.hostname, port: url.port || 443, path: url.pathname + url.search, headers, rejectUnauthorized: false }, (res) => {
      let data = '';
      res.on('data', (c) => (data += c));
      res.on('end', () => resolve({ status: res.statusCode, body: data, cookie: getCookie ? (res.headers['set-cookie'] ?? []).map((l) => l.split(';')[0]).join('; ') : undefined }));
    });
    r.on('error', reject);
    if (body !== undefined) r.write(body);
    r.end();
  });
}

const login = await req('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: USER, password: PASS }), true);
const token = String(JSON.parse(login.body).data ?? '');
const cookie = login.cookie ?? '';
console.log('login status=' + login.status + ' token=' + token.slice(0, 12) + '...');
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
const res = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target: TARGET }));
console.log('PUT status=' + res.status);
console.log('body(前 400 字符): ' + res.body.slice(0, 400));

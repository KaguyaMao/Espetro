// read-remote.mjs — 读取服务器文件（支持目录列举）
// 用法: node read-remote.mjs --out=<本地目录> --prefix=<服务器相对路径前缀> <文件名...>
//       node read-remote.mjs --list=<服务器相对目录>
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const https = require('https');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';

function httpsReq(method, urlStr, headers, body, getCookie) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const req = https.request({
      method, hostname: url.hostname, port: url.port || 443, path: url.pathname + url.search,
      headers, rejectUnauthorized: false
    }, (res) => {
      let data = '';
      res.on('data', (c) => (data += c));
      res.on('end', () => resolve({ status: res.statusCode, body: data, cookie: getCookie ? (res.headers['set-cookie'] ?? []).map((l) => l.split(';')[0]).join('; ') : undefined }));
    });
    req.on('error', reject);
    if (body !== undefined) req.write(body);
    req.end();
  });
}

const args = process.argv.slice(2);
const outArg = args.find((a) => a.startsWith('--out='));
const prefixArg = args.find((a) => a.startsWith('--prefix='));
const listArg = args.find((a) => a.startsWith('--list='));
const OUT = outArg ? outArg.slice(6) : 'D:/minecraft/modp/Espetro/build/tmp/remote-read/';
const PREFIX = prefixArg ? prefixArg.slice(9) : '';
const LIST = listArg ? listArg.slice(7) : null;
const files = args.filter((a) => !a.startsWith('--'));
const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

const loginRes = await httpsReq('POST', `${PANEL}/api/auth/login`, {
  'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest'
}, JSON.stringify({ username: USER, password: PASS }), true);
const token = String(JSON.parse(loginRes.body).data ?? '');
const cookie = loginRes.cookie ?? '';
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;

if (LIST !== null) {
  for (let a = 0; a < 5; a++) {
    const r = await httpsReq('GET', `${PANEL}/api/files/list?page=0&page_size=500&file_name=&target=${encodeURIComponent(LIST)}&${q}`,
      { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
    const b = JSON.parse(r.body);
    if (b.status === 200) {
      for (const i of (b.data?.items ?? [])) console.log(`${i.type === 0 ? '[DIR] ' : '      '}${i.name}${i.size ? '  ' + i.size : ''}`);
      process.exit(0);
    }
    await sleep(2500);
  }
  console.log('LIST FAILED');
  process.exit(1);
}

fs.mkdirSync(OUT, { recursive: true });
for (const f of files) {
  let text = null;
  for (let a = 0; a < 6 && text === null; a++) {
    const res = await httpsReq('PUT', `${PANEL}/api/files?${q}`, {
      'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json'
    }, JSON.stringify({ target: PREFIX + f }));
    const body = JSON.parse(res.body);
    const data = typeof body.data === 'string' ? body.data : '';
    if (data.trim().startsWith('{') || data.trim().startsWith('[')) text = data;
    else await sleep(2500);
  }
  if (text === null) { console.log(`${f}: FAIL`); continue; }
  const name = f.replace(/[\\/]/g, '__');
  fs.writeFileSync(OUT + name, text, 'utf8');
  console.log(`${f}: OK ${text.length} chars`);
  await sleep(900);
}

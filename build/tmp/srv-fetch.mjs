// srv-fetch.mjs — 逐个（带较长间隔与重试）抓取服务器文件
// 用法: node srv-fetch.mjs --out=<本地目录> --prefix=<服务器相对路径前缀> --from=<本地目录(取其中 *.json 的文件名)> [--filter=正则]
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const https = require('https');
const path = require('path');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';

const args = process.argv.slice(2);
const get = (k, d) => { const a = args.find(x => x.startsWith(`--${k}=`)); return a ? a.slice(k.length + 3) : d; };
const OUT = get('out', 'srv-now');
const PREFIX = get('prefix', '');
const FROM = get('from', null);
const FILTER = get('filter', null);
const GAP = Number(get('gap', 3000));

let names = null;
if (args.filter(a => !a.startsWith('--')).length) names = args.filter(a => !a.startsWith('--'));
else if (FROM) names = fs.readdirSync(FROM).filter(f => f.endsWith('.json'));
if (!names) { console.error('需要文件名或 --from=<目录>'); process.exit(1); }
if (FILTER) names = names.filter(n => new RegExp(FILTER).test(n));
fs.mkdirSync(OUT, { recursive: true });

const sleep = (ms) => new Promise(r => setTimeout(r, ms));
function req(method, urlStr, headers, body, getCookie) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const r = https.request({ method, hostname: url.hostname, port: url.port || 443, path: url.pathname + url.search, headers, rejectUnauthorized: false }, (res) => {
      let data = '';
      res.on('data', c => (data += c));
      res.on('end', () => resolve({ status: res.statusCode, body: data, cookie: getCookie ? (res.headers['set-cookie'] ?? []).map(l => l.split(';')[0]).join('; ') : undefined }));
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
const log = [];
let ok = 0;
for (const n of names) {
  let text = null, last = '';
  for (let a = 0; a < 8 && text === null; a++) {
    const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
    try {
      const res = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target: PREFIX + n }));
      const body = JSON.parse(res.body);
      const data = typeof body.data === 'string' ? body.data : '';
      if (data.trim().startsWith('{')) text = data; else { last = res.body.slice(0, 120); await sleep(GAP); }
    } catch (e) { last = String(e?.message ?? e).slice(0, 120); await sleep(GAP); }
  }
  if (text === null) { log.push(`FAIL ${n}  (${last})`); }
  else { fs.writeFileSync(path.join(OUT, n), text, 'utf8'); log.push(`OK   ${n} (${text.length} 字符)`); ok++; }
  await sleep(1200);
}
fs.writeFileSync('srv-fetch-log.txt', log.join('\n'), 'utf8');
console.log(log.join('\n'));
console.log(`\n完成 ${ok}/${names.length}`);

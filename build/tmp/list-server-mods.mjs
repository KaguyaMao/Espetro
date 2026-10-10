// list-server-mods.mjs — 列出服务端 mods 目录（名称/大小/修改时间）
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const https = require('https');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
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
const dir = process.argv[2] ?? 'mods';
const filter = process.argv[3] ?? '';
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;

let items = [];
for (let a = 0; a < 5; a++) {
  const r = await req('GET', `${PANEL}/api/files/list?page=0&page_size=500&file_name=&target=${encodeURIComponent(dir)}&${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
  try { const b = JSON.parse(r.body); if (b.status === 200) { items = b.data?.items ?? []; break; } } catch { }
  await sleep(2500);
}
const rows = items.filter((i) => i.type !== 0 && String(i.name).includes(filter));
for (const i of rows.sort((a, b) => String(a.name).localeCompare(String(b.name)))) {
  const t = i.mtime ? new Date(i.mtime).toISOString().slice(5, 16).replace('T', ' ') : '';
  console.log(`${String(i.size).padStart(10)}  ${t}  ${i.name}`);
}
console.log(`共 ${rows.length} 个（目录 ${dir}${filter ? ' 过滤 ' + filter : ''}）`);

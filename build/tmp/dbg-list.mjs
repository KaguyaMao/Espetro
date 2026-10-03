import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const https = require('https');
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
function req(method, urlStr, headers, body) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const r = https.request({ method, hostname: url.hostname, port: url.port, path: url.pathname + url.search, headers, rejectUnauthorized: false }, res => { let d=''; res.on('data', c => d += c); res.on('end', () => resolve({status: res.statusCode, body: d})); });
    r.on('error', reject); if (body) r.write(body); r.end();
  });
}
(async () => {
  const login = await req('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: USER, password: PASS }));
  const token = JSON.parse(login.body).data;
  const cookie = (login.headers?.getSetCookie?.() ?? []).map(l => l.split(';')[0]).join('; ');
  const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
  const listRes = await req('GET', `${PANEL}/api/files/list?page=0&page_size=100&file_name=&target=mods&${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie }, null);
  const raw = JSON.parse(listRes.body);
  console.log('keys:', Object.keys(raw));
  console.log('data keys:', raw.data ? Object.keys(raw.data) : 'null');
  const items = raw.data?.items ?? raw.data ?? [];
  console.log('items count:', Array.isArray(items) ? items.length : typeof items);
  if (Array.isArray(items)) {
    items.forEach(i => console.log(JSON.stringify(i)));
  } else {
    console.log(JSON.stringify(raw).slice(0, 800));
  }
})();

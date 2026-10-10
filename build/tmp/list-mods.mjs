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
  const res = await req('PUT', `${PANEL}/api/files?target=mods&token=${token}`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie }, '');
  const data = JSON.parse(res.body);
  const items = data.data?.files ?? data.data ?? [];
  if (Array.isArray(items)) {
    items.filter(f => /pricity|esui|espetro|superbwarfare|dragonrise|fcp|vvp/.test(f.filename ?? '')).forEach(f => console.log(f.filename, f.size));
  } else { console.log(JSON.stringify(data).slice(0, 1500)); }
})();

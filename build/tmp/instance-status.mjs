// instance-status.mjs — 读取实例状态（status/started/进程信息）
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const https = require('https');
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
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
const r = await req('GET', `${PANEL}/api/instance?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
const d = JSON.parse(r.body).data ?? {};
console.log('status=' + d.status + ' (3=运行中)  started=' + d.started + '  endTime=' + d.endTime);
const now = Date.now();
console.log('now=' + now + '  lastDatetime=' + (d.config?.lastDatetime ?? '?') + '  距今 ' + ((now - (d.config?.lastDatetime ?? now)) / 1000).toFixed(0) + ' s');
for (const k of ['status', 'started', 'endTime', 'cpu', 'memory', 'players']) if (d[k] !== undefined) console.log(`  ${k} = ${JSON.stringify(d[k])}`);

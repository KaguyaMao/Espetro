import { createRequire } from 'module';
const require = createRequire('D:/minecraft/modp/Espetro/build/tmp/');
const https = require('https');
const fs = require('fs');
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
function httpGet(url, headers = {}) {
  return new Promise((resolve, reject) => {
    https.get(url, { rejectUnauthorized: false, headers }, (res) => {
      const chunks = [];
      res.on('data', (c) => chunks.push(c));
      res.on('end', () => resolve({ status: res.statusCode, body: Buffer.concat(chunks) }));
    }).on('error', reject);
  });
}
(async () => {
  const loginRes = await fetch(`${PANEL}/api/auth/login`, { method: 'POST', headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, body: JSON.stringify({ username: USER, password: PASS }) });
  const token = String((await loginRes.json()).data ?? '');
  const cookie = (loginRes.headers.getSetCookie?.() ?? []).map((l) => l.split(';')[0]).join('; ');
  const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
  const url = `${PANEL}/api/files/download?file_name=${encodeURIComponent('logs/latest.log')}&${q}`;
  let d = null;
  for (let a = 0; a < 4 && !d; a++) {
    try {
      const r = await fetch(url, { method: 'POST', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie } });
      const b = await r.json();
      d = b.data;
      if (!d?.password) { console.log('retry'); d = null; await new Promise(x => setTimeout(x, 3000)); }
    } catch (e) { console.log('err', e.message); await new Promise(x => setTimeout(x, 3000)); }
  }
  if (!d) { console.log('FAILED'); return; }
  const dl = await httpGet(`https://www.derpydoge.fun:20443/download/${d.password}/${encodeURIComponent('latest.log')}`);
  console.log('dl status', dl.status, 'size', dl.body.length);
  fs.writeFileSync('D:/minecraft/modp/Espetro/build/tmp/server-latest.log', dl.body);
  const lines = dl.body.toString('utf8').split('\n');
  console.log('lines:', lines.length);
  const kw = lines.filter(l => /编制|EsFactions|faction|加载失败|解析失败|无法|失败|兼容|诊断|跳过|error|ERROR|WARN|warning|Exception|Couldn't parse/.test(l));
  console.log('matches:', kw.length);
  kw.slice(-120).forEach(l => console.log(l.substring(0, 220)));
})();

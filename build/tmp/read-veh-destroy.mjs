// read-veh-destroy.mjs — 读 world/serverconfig 里与载具破坏方块相关的配置
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const https = require('https');
const PANEL = 'https://www.derpydoge.fun:20000';
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
const lr = await req('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: 'boy', password: 'boY1145141919810Fuck' }), true);
const token = String(JSON.parse(lr.body).data ?? '');
const cookie = lr.cookie ?? '';
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
async function read(target) {
  for (let a = 0; a < 4; a++) {
    const r = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target }));
    try { const b = JSON.parse(r.body); if (b.status === 200) return String(b.data ?? ''); } catch { }
    await sleep(2600);
  }
  return null;
}
fs.mkdirSync('serverconfig', { recursive: true });
for (const f of ['superbwarfare-server.toml', 'sbw_addition-server.toml', 'tacz-server.toml']) {
  const t = await read('world/serverconfig/' + f);
  if (t === null) { console.log('❌ ' + f + ' 读取失败'); continue; }
  fs.writeFileSync('serverconfig/' + f, t, 'utf8');
  console.log('########## ' + f + ' (' + t.length + ' 字符) ##########');
  const lines = t.split('\n');
  let section = '';
  let n = 0;
  for (let i = 0; i < lines.length; i++) {
    const s = lines[i];
    if (/^\s*\[/.test(s)) section = s.trim();
    if (/destroy|break|Destroy|Break|whitelist|blacklist|WhiteList|BlackList|玻璃|glass|爆炸|方块/i.test(s)) {
      console.log(`  [${section}] ${i + 1}: ${s.trim().slice(0, 190)}`);
      if (++n >= 60) { console.log('  …(截断)'); break; }
    }
  }
  if (!n) console.log('  (无匹配行)');
  console.log('');
  await sleep(2600);
}

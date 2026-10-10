// audit-troops.mjs — 读取 CREATE_PLUS 各配置，列出所有与兵力/增援相关的字段，并导出待改文件
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
  for (let a = 0; a < 5; a++) {
    const r = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target }));
    try { const b = JSON.parse(r.body); if (b.status === 200) return String(b.data ?? ''); } catch { }
    await sleep(2600);
  }
  return null;
}
const BASE = 'EsWorld/CREATE_PLUS';
const targets = [
  'EsConfig/game.json', 'EsConfig/team_pack.json', 'EsConfig/SquadTypes.json', 'EsConfig/logistics.json', 'EsConfig/bastion.json',
  'Points/CapturePointsA.json', 'Points/CapturePointsB.json', 'EsConfig/CapturePoints.json',
];
fs.mkdirSync('points-work/before', { recursive: true });
for (const t of targets) {
  const text = await read(`${BASE}/${t}`);
  const flat = t.replace(/[\\/]/g, '__');
  if (text === null) { console.log(`--- ${t}: 不存在/读取失败`); continue; }
  fs.writeFileSync(`points-work/before/${flat}`, text, 'utf8');
  console.log(`--- ${t} (${text.length} 字符) 已备份`);
  for (const line of text.split('\n')) {
    if (/troop|reinforcement|attack|defend|ATTACK|DEFEND|兵力|amount/i.test(line) && /\d/.test(line)) {
      console.log('     ' + line.trim());
    }
  }
  await sleep(2600);
}

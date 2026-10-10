// find-truck.mjs — 黑山工厂 truck 刷车点（配置）+ 日志里的实际生成坐标
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
// 1) VehSpawn.json
const text = await read('EsWorld/server_battlefield/EsConfig/VehSpawn.json');
if (text) {
  fs.writeFileSync('vehspawn-battlefield.json', text, 'utf8');
  const j = JSON.parse(text);
  console.log('VehTypes = ' + JSON.stringify(j.VehTypes));
  console.log('各类型点位数: ' + Object.entries(j.spawn_points ?? {}).map(([k, v]) => k + '=' + v.length).join(', '));
  console.log('\n=== truck 刷车点（配置原文） ===');
  console.log(JSON.stringify(j.spawn_points?.truck, null, 2));
} else {
  console.log('❌ VehSpawn.json 读取失败');
}
// 2) 日志里的载具生成记录
await sleep(2600);
const log = await read('logs/latest.log');
if (log) {
  fs.writeFileSync('latest-for-truck.log', log, 'utf8');
  console.log('\n=== 日志中与 truck/卡车/载具生成 相关的行 ===');
  const lines = log.split('\n');
  let n = 0;
  for (const l of lines) {
    if (/truck|卡车|载具.*(生成|刷新|spawn)|VehicleManager.*(spawn|生成|放置)/i.test(l)) {
      console.log('  ' + l.replace(/\s+/g, ' ').trim().slice(0, 200));
      if (++n >= 30) break;
    }
  }
  if (!n) console.log('  (无)');
}

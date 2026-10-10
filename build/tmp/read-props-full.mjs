// read-props-full.mjs — 打印服务端 server.properties 中与网络/视距相关的项
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
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
async function read(target) {
  for (let a = 0; a < 6; a++) {
    const r = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target }));
    try {
      const b = JSON.parse(r.body);
      if (b.status === 200) return String(b.data ?? '');
      console.log(`  [${target}] 失败: ${String(b.data).slice(0, 60)}`);
    } catch { }
    await sleep(3000);
  }
  return null;
}
const props = await read('server.properties');
if (props) {
  const keys = /^(view-distance|simulation-distance|network-compression-threshold|max-players|entity-broadcast-range-percentage|player-idle-timeout|sync-chunk-writes|rate-limit|max-tick-time|enable-status|prevent-proxy-connections|online-mode|server-port)=/;
  for (const line of props.split('\n')) if (keys.test(line.trim())) console.log('  ' + line.trim());
}
await sleep(2500);
const espoints = await read('config/espoints-common.toml');
if (espoints) {
  console.log('=== config/espoints-common.toml ===');
  for (const line of espoints.split('\n')) if (/tacticalMap|KiBps|Memory|Disk/i.test(line)) console.log('  ' + line.trim());
}
await sleep(2500);
const vc = await read('config/voicechat/voicechat-server.properties');
if (vc) {
  console.log('=== voicechat-server.properties ===');
  for (const line of vc.split('\n')) if (/bitrate|sample|mtu|keep_alive|codec|distance|udp/i.test(line)) console.log('  ' + line.trim());
}

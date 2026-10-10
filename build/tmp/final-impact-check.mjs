// final-impact-check.mjs — kubejs/data 里的 tag 覆盖 + gamerule 实际值
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
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
async function list(target) {
  const r = await req('GET', `${PANEL}/api/files/list?page=0&page_size=300&file_name=&target=${encodeURIComponent(target)}&${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
  try { const b = JSON.parse(r.body); return (b.data?.items ?? []).map((i) => String(i.name) + (i.type === 0 ? '/' : '')); } catch { return ['(解析失败) ' + String(r.body).slice(0, 60)]; }
}
async function read(target) {
  for (let a = 0; a < 4; a++) {
    const r = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target }));
    try { const b = JSON.parse(r.body); if (b.status === 200) return String(b.data ?? ''); } catch { }
    await sleep(2600);
  }
  return null;
}
const paths = [
  'kubejs', 'kubejs/data', 'kubejs/data/sbw_addition', 'kubejs/data/sbw_addition/tags',
  'kubejs/data/sbw_addition/tags/blocks', 'kubejs/data/superbwarfare',
  'kubejs/data/superbwarfare/tags/blocks',
];
for (const p of paths) {
  const items = await list(p);
  console.log(p + ': ' + (items.length ? items.join(', ') : '(空)'));
  await sleep(2600);
}
// 尝试直接读可能的覆盖文件
for (const f of ['kubejs/data/sbw_addition/tags/blocks/impact_breakable.json',
                 'kubejs/data/sbw_addition/tags/blocks/impact_protected.json',
                 'kubejs/data/sbw_addition/tags/blocks/impact_tree.json',
                 'kubejs/data/superbwarfare/tags/blocks/normal_collision.json',
                 'kubejs/data/superbwarfare/tags/blocks/hard_collision.json',
                 'kubejs/data/superbwarfare/tags/blocks/soft_collision.json']) {
  const t = await read(f);
  console.log('--- ' + f + ': ' + (t === null ? '(不存在)' : '\n' + t.trim()));
  await sleep(2600);
}
// gamerule
await req('POST', `${PANEL}/api/protected_instance/command?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ command: 'gamerule sbw_addition:vehicleImpactBreak' }));
console.log('\n已发送 gamerule 查询，等待日志…');
await sleep(12000);

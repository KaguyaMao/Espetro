// list-kubejs.mjs — 看服务端 kubejs 现有脚本与数据约定
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
  try { return (JSON.parse(r.body).data?.items ?? []).map((i) => String(i.name) + (i.type === 0 ? '/' : '') + (i.type === 0 ? '' : '(' + (i.size ?? 0) + ')')); } catch { return ['(不可读)']; }
}
async function read(target) {
  for (let a = 0; a < 3; a++) {
    const r = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target }));
    try { const b = JSON.parse(r.body); if (b.status === 200) return String(b.data ?? ''); } catch { }
    await sleep(2600);
  }
  return null;
}
for (const d of ['kubejs/server_scripts', 'kubejs/data/superbwarfare', 'kubejs/data/superbwarfare/tags', 'kubejs/data/superbwarfare/sbw', 'kubejs/startup_scripts']) {
  const items = await list(d);
  console.log(d + ': ' + (items.length ? items.join(', ') : '(空)'));
  await sleep(2600);
}
console.log('\n=== 现有 server_scripts 里的 tags 用法（若有）===');
const scripts = (await list('kubejs/server_scripts')).filter((n) => n.endsWith('.js)'));
for (const s of scripts.slice(0, 6)) {
  const name = s.replace(/\(\d+\)$/, '');
  await sleep(2600);
  const t = await read('kubejs/server_scripts/' + name);
  if (t) {
    const hit = /ServerEvents\.tags|event\.remove|event\.add/.test(t);
    console.log(`  ${name}: ${t.length} 字符${hit ? '  ← 含 tags 操作' : ''}`);
    if (hit) for (const line of t.split('\n')) if (/tags|remove\(|add\(/.test(line)) console.log('      ' + line.trim().slice(0, 140));
  }
}

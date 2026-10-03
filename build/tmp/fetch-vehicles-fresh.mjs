// fetch-vehicles-fresh.mjs — 抓取服务器 kubejs 载具 JSON（用于统计抗性）
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const path = require('path');
const https = require('https');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const OUT = process.argv[2];
const DIRS = ['kubejs/data/dragonrise_reforge/sbw/vehicles', 'kubejs/data/fcp/sbw/vehicles'];
fs.mkdirSync(OUT, { recursive: true });

const sleep = (ms) => new Promise(r => setTimeout(r, ms));
function req(method, urlStr, headers, body, getCookie) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const r = https.request({ method, hostname: url.hostname, port: url.port || 443, path: url.pathname + url.search, headers, rejectUnauthorized: false }, (res) => {
      let d = ''; res.on('data', c => (d += c));
      res.on('end', () => resolve({ status: res.statusCode, body: d, cookie: getCookie ? (res.headers['set-cookie'] ?? []).map(l => l.split(';')[0]).join('; ') : undefined }));
    });
    r.on('error', reject); if (body !== undefined) r.write(body); r.end();
  });
}
async function login() {
  const r = await req('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: USER, password: PASS }), true);
  return { token: String(JSON.parse(r.body).data ?? ''), cookie: r.cookie ?? '' };
}
async function listDir(rel, token, cookie) {
  for (let a = 0; a < 5; a++) {
    const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
    const r = await req('GET', `${PANEL}/api/files/list?page=0&page_size=500&file_name=&target=${encodeURIComponent(rel)}&${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
    try { const b = JSON.parse(r.body); if (b.status === 200) return (b.data?.items ?? []).filter(i => i.type !== 0 && String(i.name).endsWith('.json')).map(i => i.name); } catch { }
    await sleep(2500);
  }
  return [];
}
async function fetchJson(rel, token, cookie) {
  for (let a = 0; a < 5; a++) {
    const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
    const res = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target: rel }));
    try { const d = String(JSON.parse(res.body).data ?? ''); if (d.trim().startsWith('{')) return d; } catch { }
    await sleep(3000);
  }
  return null;
}

let { token, cookie } = await login();
let ok = 0;
const manifest = [];
for (const dir of DIRS) {
  const ns = dir.includes('/fcp/') ? 'fcp' : 'dragonrise';
  const names = await listDir(dir, token, cookie);
  console.log(`${dir} → ${names.length} 个文件`);
  for (const name of names) {
    const local = path.join(OUT, `${ns}__${name}`);
    if (fs.existsSync(local) && fs.statSync(local).size > 500) { ok++; manifest.push({ ns, name, file: `${ns}__${name}` }); continue; }
    const text = await fetchJson(`${dir}/${name}`, token, cookie);
    if (text === null) { console.log(`  FAIL ${name}`); continue; }
    fs.writeFileSync(local, text, 'utf8');
    manifest.push({ ns, name, file: `${ns}__${name}` });
    ok++;
    await sleep(600);
  }
}
fs.writeFileSync(path.join(OUT, 'manifest.json'), JSON.stringify(manifest, null, 2), 'utf8');
console.log(`完成 ${ok} 个文件 → ${OUT}`);

// read-impact.mjs — 完整读 [impact] 段 + 找 impact_breakable/protected 标签 + 查 gamerule
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const https = require('https');
const PANEL = 'https://www.derpydoge.fun:20000';
const DAEMON_ID = '647a1b0f9a6'.length === 11 ? '' : '647a21e800714185aec74849f1b0f9a6';
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
async function list(target) {
  const r = await req('GET', `${PANEL}/api/files/list?page=0&page_size=300&file_name=&target=${encodeURIComponent(target)}&${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
  try { return (JSON.parse(r.body).data?.items ?? []).map((i) => String(i.name) + (i.type === 0 ? '/' : '')); } catch { return []; }
}
async function cmd(command) {
  await req('POST', `${PANEL}/api/protected_instance/command?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ command }));
}

// 1) [impact] 段全文（本地已存副本）
const t = fs.readFileSync('serverconfig/sbw_addition-server.toml', 'utf8');
const lines = t.split('\n');
let start = lines.findIndex((l) => /^\[impact\]/.test(l.trim()));
console.log('===== sbw_addition-server.toml [impact] 段 =====');
for (let i = start; i < lines.length; i++) {
  if (i > start && /^\s*\[/.test(lines[i])) break;
  console.log((i + 1) + ': ' + lines[i].replace(/\s+$/, ''));
}
// 2) 查游戏规则
console.log('\n===== 相关 gamerule 查询 =====');
for (const g of ['sbw_addition:vehicleImpactBreak']) await cmd('gamerule ' + g);
console.log('  已发送 gamerule 查询');
// 3) 找 impact 标签（kubejs/data 或 datapacks）
console.log('\n===== kubejs/data 目录 =====');
for (const d of ['kubejs', 'kubejs/data', 'kubejs/data/sbw_addition', 'world/datapacks']) {
  const items = await list(d);
  console.log('  ' + d + ': ' + (items.length ? items.join(', ') : '(空/不可读)'));
  await sleep(2600);
}

// dl-logs.mjs — 下载并解压服务器 logs/ 下指定日志，输出到 logs-dl/
// 用法: node dl-logs.mjs 2026-09-19-1.log.gz 2026-09-19-2.log.gz ...
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const https = require('https');
const fs = require('fs');
const zlib = require('zlib');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const OUT_DIR = 'D:/minecraft/modp/Espetro/build/tmp/logs-dl/';

function httpGet(url, headers = {}) {
  return new Promise((resolve, reject) => {
    https.get(url, { rejectUnauthorized: false, headers }, (res) => {
      const chunks = [];
      res.on('data', (c) => chunks.push(c));
      res.on('end', () => resolve({ status: res.statusCode, headers: res.headers, body: Buffer.concat(chunks) }));
    }).on('error', reject);
  });
}

const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

const loginRes = await fetch(`${PANEL}/api/auth/login`, {
  method: 'POST', headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
  body: JSON.stringify({ username: USER, password: PASS })
});
const token = String((await loginRes.json()).data ?? '');
const cookie = (loginRes.headers.getSetCookie?.() ?? []).map((l) => l.split(';')[0]).join('; ');
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
fs.mkdirSync(OUT_DIR, { recursive: true });

for (const file of process.argv.slice(2)) {
  const url = `${PANEL}/api/files/download?file_name=${encodeURIComponent('logs/' + file)}&${q}`;
  const res = await fetch(url, { method: 'POST', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie } });
  const body = await res.json().catch(() => ({}));
  const d = body.data;
  if (!d || !d.password) { console.log(`${file}: 无下载凭证 ${JSON.stringify(body).slice(0, 120)}`); await sleep(2000); continue; }
  const addr = d.addr.replace('wss://', 'https://').replace('localhost', 'www.derpydoge.fun');
  const dl = await httpGet(`${addr}/download/${d.password}/${file}`, { Cookie: cookie });
  if (dl.status !== 200) { console.log(`${file}: 下载失败 ${dl.status}`); await sleep(2000); continue; }
  const out = OUT_DIR + file.replace(/\.gz$/, '');
  try {
    fs.writeFileSync(out, file.endsWith('.gz') ? zlib.gunzipSync(dl.body) : dl.body);
    console.log(`${file}: OK ${dl.body.length} → ${fs.statSync(out).size} B`);
  } catch (e) {
    fs.writeFileSync(out + '.raw', dl.body);
    console.log(`${file}: 解压失败 ${e.message}（原始已存）`);
  }
  await sleep(800);
}

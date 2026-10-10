// pull-tacz-dirs.mjs — 通过面板下载 API 抓取 tacz 下指定"目录"（返回 zip），解包到本地镜像目录
// 用法: node pull-tacz-dirs.mjs <本地镜像目录> <服务器相对目录...>
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const path = require('path');
const zlib = require('zlib');
const https = require('https');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';

const OUT = process.argv[2];
const dirs = process.argv.slice(3);
fs.mkdirSync(OUT, { recursive: true });

const sleep = (ms) => new Promise(r => setTimeout(r, ms));
function httpsReq(method, urlStr, headers, body, getCookie, binary) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const r = https.request({ method, hostname: url.hostname, port: url.port || 443, path: url.pathname + url.search, headers, rejectUnauthorized: false }, (res) => {
      const chunks = [];
      res.on('data', c => chunks.push(c));
      res.on('end', () => {
        const buf = Buffer.concat(chunks);
        resolve({
          status: res.statusCode,
          buf,
          body: binary ? null : buf.toString('utf8'),
          cookie: getCookie ? (res.headers['set-cookie'] ?? []).map(l => l.split(';')[0]).join('; ') : undefined,
        });
      });
    });
    r.on('error', reject);
    if (body !== undefined) r.write(body);
    r.end();
  });
}

const login = await httpsReq('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: USER, password: PASS }), true);
const token = String(JSON.parse(login.body).data ?? '');
const cookie = login.cookie ?? '';
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;

for (const dir of dirs) {
  const base = path.basename(dir);
  const localZip = path.join(OUT, base.replace(/[^\w.\-]+/g, '_') + '.zip');
  let ok = false;
  for (let attempt = 1; attempt <= 4 && !ok; attempt++) {
    const res = await httpsReq('POST', `${PANEL}/api/files/download?file_name=${encodeURIComponent(dir)}&${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
    let d;
    try { d = JSON.parse(res.body).data; } catch { d = null; }
    if (!d || !d.password) { console.log(`  第${attempt}次创建下载任务失败: ${String(res.body).slice(0, 120)}`); await sleep(3000); continue; }
    const addr = 'https://' + new URL(PANEL).hostname + (String(d.addr).match(/^wss?:\/\/[^/:]+(:\d+)?/) || [])[1];
    const dl = await httpsReq('GET', `${addr}/download/${d.password}/${encodeURIComponent(base)}`, { 'Cookie': cookie }, undefined, false, true);
    if (dl.buf.length > 4 && dl.buf.slice(0, 2).toString() === 'PK') {
      fs.writeFileSync(localZip, dl.buf);
      console.log(`OK  ${dir}  → ${dl.buf.length} B (zip)`);
      ok = true;
    } else if (dl.buf.length > 0 && dl.buf.slice(0, 1).toString() !== '<') {
      // 单文件（目录里只有一个文件时可能直接返回文件）
      const single = path.join(OUT, base);
      fs.writeFileSync(single, dl.buf);
      console.log(`OK  ${dir}  → ${dl.buf.length} B (单文件)`);
      ok = true;
    } else {
      console.log(`  第${attempt}次下载失败: ${dl.status} ${dl.buf.slice(0, 120).toString()}`);
      await sleep(3000);
    }
  }
  if (!ok) console.log(`FAIL ${dir}`);
  await sleep(800);
}

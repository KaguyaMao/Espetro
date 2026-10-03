// dl-file.mjs — 通过 daemon 下载任意服务器文件到本地（原样字节）
// 用法: node dl-file.mjs <远端相对路径> <本地路径>
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const https = require('https');
const fs = require('fs');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const REMOTE = process.argv[2];
const LOCAL = process.argv[3];

function httpGet(url, headers = {}) {
  return new Promise((resolve, reject) => {
    https.get(url, { rejectUnauthorized: false, headers }, (res) => {
      const chunks = [];
      res.on('data', (c) => chunks.push(c));
      res.on('end', () => resolve({ status: res.statusCode, body: Buffer.concat(chunks) }));
    }).on('error', reject);
  });
}

const loginRes = await fetch(`${PANEL}/api/auth/login`, {
  method: 'POST', headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
  body: JSON.stringify({ username: USER, password: PASS })
});
const token = String((await loginRes.json()).data ?? '');
const cookie = (loginRes.headers.getSetCookie?.() ?? []).map((l) => l.split(';')[0]).join('; ');
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
const url = `${PANEL}/api/files/download?file_name=${encodeURIComponent(REMOTE)}&${q}`;
const r = await httpGet(url, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
fs.writeFileSync(LOCAL, r.body);
console.log(`${r.status} ${REMOTE} -> ${LOCAL} (${r.body.length} B)`);

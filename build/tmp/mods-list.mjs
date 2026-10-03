// mods-list.mjs — 列出服务端 mods/ 目录（名称+大小），并对关键 jar 计算 SHA256
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const https = require('https');
const crypto = require('crypto');
const fs = require('fs');

function httpGet(url, headers = {}) {
  return new Promise((resolve, reject) => {
    https.get(url, { rejectUnauthorized: false, headers }, (res) => {
      const chunks = [];
      res.on('data', (c) => chunks.push(c));
      res.on('end', () => resolve({ status: res.statusCode, body: Buffer.concat(chunks) }));
    }).on('error', reject);
  });
}
const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

(async () => {
  const loginRes = await fetch(`${PANEL}/api/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
    body: JSON.stringify({ username: USER, password: PASS })
  });
  const token = String((await loginRes.json()).data ?? '');
  const cookie = (loginRes.headers.getSetCookie?.() ?? []).map((l) => l.split(';')[0]).join('; ');
  const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;

  // 1) 列目录
  try {
    const r = await fetch(`${PANEL}/api/files/list?${q}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest', Cookie: cookie },
      body: JSON.stringify({ target: 'mods', page: 1, page_size: 200, file_name: '' })
    });
    const t = await r.text();
    let j = null; try { j = JSON.parse(t); } catch {}
    const items = j?.data?.items ?? j?.data?.files ?? null;
    if (Array.isArray(items)) {
      console.log(`=== 服务端 mods/ (${items.length}) ===`);
      items
        .filter((i) => i.type === 0 || /\.jar$/i.test(i.name ?? ''))
        .sort((a, b) => String(a.name).localeCompare(String(b.name)))
        .forEach((i) => console.log(`${String(i.size ?? i.sizeBytes ?? '?').padStart(12)}  ${i.name}`));
    } else {
      console.log('files/list 不可用: ' + t.slice(0, 200).replace(/\s+/g, ' '));
    }
  } catch (e) {
    console.log('files/list 失败: ' + e.message);
  }

  // 2) 关键 jar 的 SHA256
  for (const name of ['espetro-1.1.3-x.jar', 'espoints-1.1.1b.jar']) {
    const rel = 'mods/' + name;
    let d = null;
    for (let a = 0; a < 4 && !d; a++) {
      try {
        const r = await fetch(`${PANEL}/api/files/download?file_name=${encodeURIComponent(rel)}&${q}`, {
          method: 'POST',
          headers: { 'X-Requested-With': 'XMLHttpRequest', Cookie: cookie }
        });
        const b = await r.json();
        d = b.data;
        if (!d?.password) { d = null; await sleep(3000); }
      } catch (e) { await sleep(3000); }
    }
    if (!d) { console.log(`!! ${name} 下载任务失败`); continue; }
    const dl = await httpGet(`https://www.derpydoge.fun:20443/download/${d.password}/${encodeURIComponent(name)}`);
    const sha = crypto.createHash('sha256').update(dl.body).digest('hex').toUpperCase();
    fs.writeFileSync(`D:/minecraft/modp/Espetro/build/tmp/srv-${name}`, dl.body);
    console.log(`SRV ${name}  size=${dl.body.length}  sha256=${sha}`);
  }
})();

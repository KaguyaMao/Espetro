// check-done.mjs — 读取服务端 latest.log 并报告是否出现 "Done ("
//
// 用法:
//   node check-done.mjs            单次检查
//   node check-done.mjs 180        轮询最多 180 秒，出现 Done 立即返回
//
// 输出: "DONE: <Done 日志行>" 或 "DONE: not yet"
// 旧版本名为 check-done 但只做单次检查（不轮询），调用方若按 "DONE: yes/no" 解析会误判，故补充轮询参数。
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const https = require('https');
const fs = require('fs');
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const OUT_LOG = 'D:/minecraft/modp/Espetro/build/tmp/server-latest.log';
const WAIT_SECONDS = Number(process.argv[2] ?? 0);

function httpGet(url, headers = {}) {
  return new Promise((resolve, reject) => {
    https.get(url, { rejectUnauthorized: false, headers }, (res) => {
      const chunks = [];
      res.on('data', (c) => chunks.push(c));
      res.on('end', () => resolve({ status: res.statusCode, body: Buffer.concat(chunks) }));
    }).on('error', reject);
  });
}

async function fetchLatestLog(q, cookie) {
  const url = `${PANEL}/api/files/download?file_name=${encodeURIComponent('logs/latest.log')}&${q}`;
  let d = null;
  for (let a = 0; a < 4 && !d; a++) {
    try {
      const r = await fetch(url, { method: 'POST', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie } });
      const b = await r.json();
      d = b.data;
      if (!d?.password) { console.log('retry'); d = null; await new Promise(x => setTimeout(x, 3000)); }
    } catch (e) { console.log('err', e.message); await new Promise(x => setTimeout(x, 3000)); }
  }
  if (!d) return null;
  const dl = await httpGet(`https://www.derpydoge.fun:20443/download/${d.password}/${encodeURIComponent('latest.log')}`);
  fs.writeFileSync(OUT_LOG, dl.body);
  return dl.body.toString('utf8').split('\n');
}

(async () => {
  const loginRes = await fetch(`${PANEL}/api/auth/login`, { method: 'POST', headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, body: JSON.stringify({ username: USER, password: PASS }) });
  const token = String((await loginRes.json()).data ?? '');
  const cookie = (loginRes.headers.getSetCookie?.() ?? []).map((l) => l.split(';')[0]).join('; ');
  const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;

  const deadline = Date.now() + WAIT_SECONDS * 1000;
  let lastLines = null;
  do {
    const lines = await fetchLatestLog(q, cookie);
    if (lines) {
      lastLines = lines;
      const done = lines.filter(l => l.includes('Done ('));
      if (done.length) {
        console.log('DONE:', done[done.length - 1].substring(0, 160));
        console.log('DONE: yes');
        return;
      }
    }
    if (Date.now() < deadline) await new Promise((x) => setTimeout(x, 3000));
  } while (Date.now() < deadline);

  console.log('DONE: not yet');
  if (lastLines) {
    const head = lastLines.slice(0, 40).filter(l => l.includes('espetro') || l.includes('ModLauncher running'));
    head.slice(0, 8).forEach(l => console.log(l.substring(0, 150)));
  }
  process.exit(WAIT_SECONDS > 0 ? 1 : 0);
})();

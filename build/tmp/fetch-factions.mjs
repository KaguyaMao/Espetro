// fetch-factions.mjs — 以正确 UTF-8 抓取 EsFactions JSON (read API data 原样落盘, 不做 latin1 二次还原)
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const OUT_DIR = 'D:/minecraft/modp/Espetro/build/tmp/fx';
const OUT_FILE = 'D:/minecraft/modp/Espetro/build/tmp/mcsm-file-out.txt';
const log = [];

(async () => {
  const loginRes = await fetch(`${PANEL}/api/auth/login`, { method: 'POST', headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, body: JSON.stringify({ username: USER, password: PASS }) });
  const token = String((await loginRes.json()).data ?? '');
  const cookie = (loginRes.headers.getSetCookie?.() ?? []).map((l) => l.split(';')[0]).join('; ');
  const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
  fs.mkdirSync(OUT_DIR, { recursive: true });
  const files = ['pla_112th_brigade_mesh.json', 'plamc_5th.json', 'plamc_5th_at.json', 'ru_205th.json'];
  for (const f of files) {
    const r = await fetch(`${PANEL}/api/files?${q}`, { method: 'PUT', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, body: JSON.stringify({ target: 'EsFactions/' + f }) });
    const b = await r.json();
    if (typeof b.data !== 'string') { log.push(f + ': FAIL ' + JSON.stringify(b).slice(0, 200)); continue; }
    fs.writeFileSync(OUT_DIR + '/' + f, b.data, 'utf8');
    // 校验: 真 UTF-8 可解析, 且含中文而非 FFFD
    let ok = false, cn = false;
    try { const j = JSON.parse(b.data); ok = true; cn = /[\u4e00-\u9fff]/.test(b.data) && !/[\uFFFD]/.test(b.data); } catch (e) { }
    log.push(f + ': ' + b.data.length + ' chars json=' + ok + ' chinese-ok=' + cn);
    await new Promise(x => setTimeout(x, 1200));
  }
  fs.writeFileSync(OUT_FILE, log.join('\n'), 'utf8');
})().catch((e) => {
  fs.writeFileSync(OUT_FILE, 'ERROR: ' + (e?.stack ?? e), 'utf8');
  process.exit(1);
});

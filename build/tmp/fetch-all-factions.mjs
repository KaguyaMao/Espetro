// fetch-all-factions.mjs — 抓取全部编制 JSON（read API data 已是 UTF-8，勿二次转换）
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const OUT_DIR = 'D:/minecraft/modp/Espetro/build/tmp/fxall';
const OUT_FILE = 'D:/minecraft/modp/Espetro/build/tmp/fxall-out.txt';
const log = [];

(async () => {
  const loginRes = await fetch(`${PANEL}/api/auth/login`, { method: 'POST', headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, body: JSON.stringify({ username: USER, password: PASS }) });
  const token = String((await loginRes.json()).data ?? '');
  const cookie = (loginRes.headers.getSetCookie?.() ?? []).map((l) => l.split(';')[0]).join('; ');
  const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
  fs.mkdirSync(OUT_DIR, { recursive: true });

  const listRes = await fetch(`${PANEL}/api/files/list?page=0&page_size=100&file_name=&target=EsFactions&${q}`, { headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie } });
  const listBody = await listRes.json();
  const files = (listBody.data?.items ?? []).filter((i) => i.type !== 0 && i.name.endsWith('.json')).map((i) => i.name);
  log.push('files=' + files.length);

  for (const f of files) {
    let text = null;
    for (let attempt = 0; attempt < 6 && text === null; attempt++) {
      const r = await fetch(`${PANEL}/api/files?${q}`, { method: 'PUT', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, body: JSON.stringify({ target: 'EsFactions/' + f }) });
      const b = await r.json();
      const data = typeof b.data === 'string' ? b.data : '';
      // 面板限流时返回短中文提示（如"此操作冷却中…"），需等待重试
      if (data.trim().startsWith('{')) {
        text = data;
      } else {
        log.push(`${f} attempt${attempt + 1} throttled: ${data.slice(0, 30)}`);
        await new Promise((x) => setTimeout(x, 3000));
      }
    }
    if (text === null) { log.push(f + ' FAIL after retries'); continue; }
    fs.writeFileSync(OUT_DIR + '/' + f, text, 'utf8');
    let ok = false;
    try { JSON.parse(text); ok = true; } catch (e) { }
    log.push(f + ' ' + text.length + ' json=' + ok);
    await new Promise((x) => setTimeout(x, 2500));
  }
  fs.writeFileSync(OUT_FILE, log.join('\n'), 'utf8');
})().catch((e) => { fs.writeFileSync(OUT_FILE, 'ERROR: ' + (e?.stack ?? e), 'utf8'); process.exit(1); });

import { createRequire } from 'module';
const require = createRequire('D:/minecraft/modp/Espetro/build/tmp/');
const fs = require('fs');
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
(async () => {
  const loginRes = await fetch(`${PANEL}/api/auth/login`, { method: 'POST', headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, body: JSON.stringify({ username: USER, password: PASS }) });
  const token = String((await loginRes.json()).data ?? '');
  const cookie = (loginRes.headers.getSetCookie?.() ?? []).map((l) => l.split(';')[0]).join('; ');
  const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
  const list = await fetch(`${PANEL}/api/files/list?page=0&page_size=100&file_name=&target=EsFactions&${q}`, { headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie } });
  const lb = await list.json();
  for (const item of lb.data?.items ?? []) {
    if (item.type !== 0 && item.name.endsWith('.json')) {
      const r = await fetch(`${PANEL}/api/files?${q}`, { method: 'PUT', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, body: JSON.stringify({ target: 'EsFactions/' + item.name }) });
      const b = await r.json();
      const txt = typeof b.data === 'string' ? Buffer.from(b.data, 'latin1').toString('utf8') : '';
      const ids = [...txt.matchAll(/"classId"\s*:\s*"([^"]+)"/g)].map(m => m[1]);
      const vehs = [...txt.matchAll(/"entity"\s*:\s*\[([^\]]*)\]/g)].map(m => m[1]);
      const uniq = [...new Set(ids)];
      if (uniq.length === 0 && vehs.length === 0 && !txt.includes('zsd05')) continue;
      console.log(item.name, '| classes:', uniq.length ? uniq.slice(0,3).join(',') + '...' : '(inline?)', '| vehicleLines:', vehs.length);
      if (txt.includes('zsd05') || txt.includes('bmd4') || txt.includes('truck_supply')) {
        const hit = txt.split('\n').filter(l => /zsd05|bmd4|truck_supply/.test(l));
        console.log('   HIT:', hit.map(h => h.trim()).slice(0, 4).join(' ;; '));
      }
    }
    await new Promise(x => setTimeout(x, 1500));
  }
})();

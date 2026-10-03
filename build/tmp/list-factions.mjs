const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const loginRes = await fetch(`${PANEL}/api/auth/login`, {
  method: 'POST', headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
  body: JSON.stringify({ username: USER, password: PASS })
});
const token = String((await loginRes.json()).data ?? '');
const cookie = (loginRes.headers.getSetCookie?.() ?? []).map((l) => l.split(';')[0]).join('; ');
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
const sleep = ms => new Promise(r => setTimeout(r, ms));
async function list(target) {
  for (let a = 0; a < 6; a++) {
    await sleep(1500);
    const r = await fetch(`${PANEL}/api/files/list?page=0&page_size=100&file_name=&target=${encodeURIComponent(target)}&${q}`, { headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie } });
    const b = await r.json();
    if (b.status === 200) {
      const items = b.data?.items ?? [];
      console.log(items.filter(i => i.name.endsWith('.json')).map(i => i.name).join('\n'));
      return;
    }
  }
  console.log('LIST FAILED');
}
await list('EsFactions');

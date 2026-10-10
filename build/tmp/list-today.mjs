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
  for (let page = 1; page <= 4; page++) {
    const res = await fetch(`${PANEL}/api/files/list?page=${page}&page_size=100&file_name=&target=logs&${q}`, { headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie } });
    const body = await res.json();
    const items = (body.data?.items ?? []).filter(i => i.name.includes('08-30') || i.name === 'latest.log');
    if (items.length) items.forEach(i => console.log(`${i.name} ${i.size} ${i.time}`));
    if ((body.data?.items ?? []).length < 100) break;
    await new Promise(x => setTimeout(x, 1200));
  }
})();

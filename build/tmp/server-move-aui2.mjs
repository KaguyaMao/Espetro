const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
(async () => {
  const loginRes = await fetch(`${PANEL}/api/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
    body: JSON.stringify({ username: USER, password: PASS })
  });
  const loginBody = await loginRes.json();
  const token = String(loginBody.data ?? '');
  const cookie = (loginRes.headers.getSetCookie?.() ?? []).map((l) => l.split(';')[0]).join('; ');
  const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
  const listRes = await fetch(`${PANEL}/api/files/list?page=0&page_size=100&file_name=&target=mods&${q}`, {
    headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie }
  });
  const list = await listRes.json();
  const items = list.data?.items ?? [];
  const oldAui = items.find(i => /pricity/i.test(i.name) && /1\.2\.3\.jar$/.test(i.name) && !/1\.2\.3\.1/.test(i.name));
  console.log('old AUI:', oldAui ? oldAui.name : 'NONE');
  if (oldAui) {
    const mv = await fetch(`${PANEL}/api/files/move?${q}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie },
      body: JSON.stringify({ targets: [[`mods/${oldAui.name}`, `mods/${oldAui.name}.disabled`]] })
    });
    console.log('move:', mv.status, (await mv.text()).slice(0, 200));
  }
})();

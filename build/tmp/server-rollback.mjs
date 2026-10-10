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
  const items = (await listRes.json()).data?.items ?? [];
  const targets = [];
  const esui = items.find(i => /^esui-/.test(i.name));
  if (esui && !esui.name.endsWith('.disabled')) targets.push([`mods/${esui.name}`, `mods/${esui.name}.disabled`]);
  const aui131 = items.find(i => /pricity.*1\.2\.3\.1\.jar$/.test(i.name));
  if (aui131) targets.push([`mods/${aui131.name}`, `mods/${aui131.name}.disabled`]);
  const aui123d = items.find(i => /pricity.*1\.2\.3\.jar\.disabled$/.test(i.name));
  if (aui123d) targets.push([`mods/${aui123d.name}`, `mods/${aui123d.name.replace(/\.disabled$/, '')}`]);
  console.log('targets:', JSON.stringify(targets, null, 1));
  if (targets.length) {
    const mv = await fetch(`${PANEL}/api/files/move?${q}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie },
      body: JSON.stringify({ targets })
    });
    console.log('move:', mv.status, (await mv.text()).slice(0, 200));
  }
})();

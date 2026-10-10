import { createRequire } from 'module';
const require = createRequire('D:/minecraft/modp/Espetro/build/tmp/');
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
  // MCSM10 修改实例配置端点：PUT /api/instance 需要完整 config？先看 GET 是否有编辑用端点
  const res = await fetch(`${PANEL}/api/instance?${q}`, { headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie } });
  const body = await res.json();
  const inst = body.data ?? body;
  const config = JSON.parse(JSON.stringify(inst.config ?? {}));
  config.startCommand = 'sh run.sh';
  // 尝试 MCSM10 的编辑端点变体
  const tries = [
    { name: 'PUT /api/instance full', url: `${PANEL}/api/instance?${q}`, body: config },
    { name: 'POST /api/instance edit', url: `${PANEL}/api/instance/edit?${q}`, body: { config } },
    { name: 'PUT /api/instance edit', url: `${PANEL}/api/instance/edit?${q}`, body: config },
    { name: 'POST /api/protected_instance/edit', url: `${PANEL}/api/protected_instance/edit?${q}`, body: { config } },
  ];
  for (const t of tries) {
    try {
      const r = await fetch(t.url, { method: t.name.startsWith('PUT') ? 'PUT' : 'POST', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, body: JSON.stringify(t.body) });
      console.log(t.name, r.status, (await r.text()).substring(0, 160));
    } catch (e) { console.log(t.name, 'ERR', e.message); }
    await new Promise(x => setTimeout(x, 2500));
  }
})();

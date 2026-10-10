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
const target = 'EsDimensions.json';
const payload = `{
  "_comment": "修改后必须完整重启客户端/服务端；dimension_id 省略时稳定生成 espetro:<map>。",
  "map_vote_seconds": 30,
  "dimensions": [
    {
      "name": "黑山工厂",
      "map": "server_battlefield"
    },
    {
      "name": "越南",
      "map": "越南"
    },
    {
      "name": "CREATE_PLUS",
      "map": "CREATE_PLUS"
    }
  ]
}`;
for (let attempt = 0; attempt < 5; attempt++) {
  const w = await fetch(`${PANEL}/api/files?${q}`, { method: 'PUT', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, body: JSON.stringify({ target, text: payload }) });
  const wt = await w.json();
  console.log('attempt', attempt, 'status:', w.status, JSON.stringify(wt).slice(0, 120));
  if (w.status === 200 && wt.status === 200) break;
  await new Promise(res => setTimeout(res, 4000));
}

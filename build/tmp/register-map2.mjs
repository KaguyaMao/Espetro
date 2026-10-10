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
const r = await fetch(`${PANEL}/api/files?${q}`, { method: 'PUT', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, body: JSON.stringify({ target }) });
const body = await r.json();
const raw = typeof body.data === 'string' ? body.data : JSON.stringify(body);
console.log('RAW:', raw);
// 容错：去掉尾随逗号（",\n ]" -> "\n ]"）再解析
const fixed = raw.replace(/,\s*([\]}])/g, '$1');
let data;
try { data = JSON.parse(fixed); } catch (e) { console.log('parse fail: ' + e.message.slice(0,100)); process.exit(1); }
const dims = data.dimensions ?? [];
if (dims.some(d => d.map === 'CREATE_PLUS')) {
  console.log('CREATE_PLUS already registered');
  process.exit(0);
}
dims.push({ name: 'CREATE_PLUS', map: 'CREATE_PLUS' });
const updated = { _comment: data._comment ?? '修改后必须完整重启客户端/服务端', map_vote_seconds: data.map_vote_seconds ?? 30, dimensions: dims };
const payload = JSON.stringify(updated, null, 2);
const w = await fetch(`${PANEL}/api/files?${q}`, { method: 'PUT', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, body: JSON.stringify({ target, text: payload }) });
const wt = await w.json();
console.log('write status:', w.status, JSON.stringify(wt).slice(0, 150));
// 读回验证
const r2 = await fetch(`${PANEL}/api/files?${q}`, { method: 'PUT', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, body: JSON.stringify({ target }) });
const b2 = await r2.json();
console.log('VERIFY:', typeof b2.data === 'string' ? b2.data : JSON.stringify(b2));

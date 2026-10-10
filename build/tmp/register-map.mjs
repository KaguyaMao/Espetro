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
// 先读当前内容
const r = await fetch(`${PANEL}/api/files?target=${target}&${q}`, { method: 'PUT', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, body: JSON.stringify({ target }) });
const body = await r.text();
console.log('read status:', r.status, 'len:', body.length);
let parsed;
try { parsed = JSON.parse(body); console.log('wrapped json ok'); } catch (e) { console.log('not wrapped json: ' + e.message.slice(0,80)); }
// 尝试解析 data 字段
let dims;
try {
  const data = typeof parsed?.data === 'string' ? JSON.parse(parsed.data) : parsed?.data ?? parsed;
  dims = data.dimensions;
  console.log('existing dims:', JSON.stringify(dims));
} catch (e) { console.log('parse fail: ' + e.message.slice(0,120)); process.exit(1); }
// 检查是否已注册
if (dims.some(d => d.map === 'CREATE_PLUS')) {
  console.log('CREATE_PLUS already registered');
} else {
  dims.push({ name: 'CREATE_PLUS', map: 'CREATE_PLUS' });
  const updated = { _comment: data._comment ?? '修改后必须完整重启客户端/服务端', map_vote_seconds: data.map_vote_seconds ?? 30, dimensions: dims };
  const payload = JSON.stringify(updated, null, 2);
  // 面板写文件接口：POST /api/files 或 PUT with content？
  const w = await fetch(`${PANEL}/api/files?target=${target}&${q}`, { method: 'POST', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, body: JSON.stringify({ target, content: payload }) });
  const wt = await w.text();
  console.log('write status:', w.status, wt.slice(0, 200));
}

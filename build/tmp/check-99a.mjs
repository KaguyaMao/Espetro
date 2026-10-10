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
async function read(target) {
  for (let attempt = 0; attempt < 6; attempt++) {
    await sleep(1500);
    const r = await fetch(`${PANEL}/api/files?target=${encodeURIComponent(target)}&${q}`, { method: 'PUT', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, body: JSON.stringify({ target }) });
    const t = await r.text();
    if (r.status === 200 && !t.includes('冷却')) return t;
  }
  return '<<READ FAILED>>';
}
const raw = await read('EsFactions/pla_195th.json');
let content;
try {
  const wrapped = JSON.parse(raw);
  content = typeof wrapped.data === 'string' ? wrapped.data : raw;
} catch { content = raw; }
console.log('FILE LEN:', content.length);
// 提取 99a 相关片段
const lower = content.toLowerCase();
let idx = lower.indexOf('99a');
let count = 0;
while (idx >= 0 && count < 10) {
  console.log('--- 片段 @' + idx + ' ---');
  console.log(content.slice(Math.max(0, idx - 400), idx + 600));
  idx = lower.indexOf('99a', idx + 3);
  count++;
}
if (count === 0) console.log('未找到 99a 字样');

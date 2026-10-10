import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
async function main() {
  const loginRes = await fetch(`${PANEL}/api/auth/login`, { method: 'POST', headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, body: JSON.stringify({ username: USER, password: PASS }) });
  const loginBody = await loginRes.json();
  const token = String(loginBody.data ?? '');
  const cookie = (loginRes.headers.getSetCookie?.() ?? []).map((l) => l.split(';')[0]).join('; ');
  const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
  const res = await fetch(`${PANEL}/api/files?${q}`, { method: 'PUT', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, body: JSON.stringify({ target: 'logs/latest.log' }) });
  const body = await res.json();
  const data = typeof body.data === 'string' ? body.data : JSON.stringify(body);
  // 打印含中文的样本行，判断是 JSON 转义(\uXXXX)还是原生字符
  const lines = data.split('\n').filter(l => /[\u4e00-\u9fff\\u]/.test(l));
  fs.writeFileSync('D:/minecraft/modp/Espetro/build/tmp/latest-raw.txt', data, 'utf8');
  console.log('total chars:', data.length);
  console.log('sample lines:');
  lines.slice(0, 6).forEach(l => console.log('RAW>>', l.substring(0, 200)));
}
main().catch(e => { console.log('ERR', e.message); process.exit(1); });

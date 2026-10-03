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
// level.dat 是二进制，读文本看能否解析出关键信息（前几千字节可能含可读字符串）
const c = await read('EsWorld/CREATE_PLUS/level.dat');
console.log('LEN', c.length);
// 提取可读 ASCII 片段（世界名、出生点等通常以字符串形式存在）
const printable = (c.match(/[\x20-\x7e]{6,}/g) || []).filter(s => !s.includes('{') && !s.includes('"') && !s.includes('<') && !s.includes('DOCTYPE'));
console.log(printable.slice(0, 40).join('\n'));

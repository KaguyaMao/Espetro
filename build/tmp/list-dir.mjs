// list-dir.mjs — 列出服务器目录文件（默认 mods）
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const TARGET = process.argv[2] ?? 'mods';
const FILTER = process.argv[3] ?? '';

const loginRes = await fetch(`${PANEL}/api/auth/login`, {
  method: 'POST', headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
  body: JSON.stringify({ username: USER, password: PASS })
});
const token = String((await loginRes.json()).data ?? '');
const cookie = (loginRes.headers.getSetCookie?.() ?? []).map((l) => l.split(';')[0]).join('; ');
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

for (let a = 0; a < 6; a++) {
  await sleep(1500);
  const r = await fetch(`${PANEL}/api/files/list?page=0&page_size=500&file_name=${encodeURIComponent(FILTER)}&target=${encodeURIComponent(TARGET)}&${q}`, {
    headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie }
  });
  const b = await r.json();
  if (b.status === 200) {
    const all = b.data?.items ?? [];
    const dirs = all.filter((i) => i.type === 0 || i.type === 'directory');
    const items = all.filter((i) => !(i.type === 0 || i.type === 'directory'));
    for (const i of dirs.sort((x, y) => String(x.name).localeCompare(String(y.name)))) {
      console.log(`[DIR] ${String(i.name).padEnd(52)} ${i.time ?? ''}`);
    }
    for (const i of items.sort((x, y) => String(x.name).localeCompare(String(y.name)))) {
      console.log(`${String(i.name).padEnd(60)} ${String(i.size ?? '').padStart(10)}  ${i.time ?? ''}`);
    }
    console.log(`\n共 ${all.length} 项（目录 ${dirs.length}）(target=${TARGET})`);
    process.exit(0);
  }
}
console.log('LIST FAILED');

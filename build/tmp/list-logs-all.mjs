// 翻页列出 logs 目录全部文件，过滤 08-29
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';

async function main() {
	const loginRes = await fetch(`${PANEL}/api/auth/login`, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
		body: JSON.stringify({ username: USER, password: PASS })
	});
	const loginBody = await loginRes.json();
	const token = String(loginBody.data ?? '');
	const cookie = (loginRes.headers.getSetCookie?.() ?? []).map((l) => l.split(';')[0]).join('; ');
	const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;

	let page = 0;
	const all = [];
	while (true) {
		const res = await fetch(`${PANEL}/api/files/list?page=${page}&page_size=100&file_name=&target=${encodeURIComponent('logs')}&${q}`, {
			headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie }
		});
		const body = await res.json();
		const payload = body.data ?? body;
		const items = payload.items ?? [];
		items.forEach((i) => all.push(`${i.type === 0 ? 'DIR' : 'FILE'} ${i.size} ${i.name}`));
		if (items.length < 100 || page > 20) break;
		page++;
		await new Promise((r) => setTimeout(r, 1200));
	}
	console.log(`total entries: ${all.length}`);
	const recent = all.filter((l) => /2026-08-2[89]|2026-08-29|latest\.log/.test(l));
	console.log('=== 8/28-8/29 与 latest ===');
	recent.forEach((l) => console.log(l));
}
main().catch((e) => { console.error(e); process.exit(1); });

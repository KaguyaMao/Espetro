// 用 file_name 过滤搜索日志
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

	for (const filter of ['2026-08-29', '2026-08-28', 'latest']) {
		const res = await fetch(`${PANEL}/api/files/list?page=0&page_size=100&file_name=${encodeURIComponent(filter)}&target=${encodeURIComponent('logs')}&${q}`, {
			headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie }
		});
		const body = await res.json();
		const payload = body.data ?? body;
		const items = payload.items ?? [];
		console.log(`=== filter=${filter} count=${items.length} ===`);
		items.forEach((i) => console.log(`${i.type === 0 ? 'DIR' : 'FILE'} ${i.size} ${i.name} @ ${i.time}`));
		await new Promise((r) => setTimeout(r, 1500));
	}
}
main().catch((e) => { console.error(e); process.exit(1); });

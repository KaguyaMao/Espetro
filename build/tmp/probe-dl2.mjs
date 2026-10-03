// 检查 /api/files/download 响应内容与可能的二进制参数
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

	// download 端点 GET
	const r = await fetch(`${PANEL}/api/files/download?${q}&file_name=2026-08-29-21.log.gz&target=${encodeURIComponent('logs')}`, {
		headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie }
	});
	console.log('GET download:', r.status, r.headers.get('content-type'));
	console.log(await r.text());

	// 试 POST
	const r2 = await fetch(`${PANEL}/api/files/download?${q}`, {
		method: 'POST',
		headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' },
		body: JSON.stringify({ file_name: '2026-08-29-21.log.gz', target: 'logs' })
	});
	console.log('POST download:', r2.status, r2.headers.get('content-type'));
	const t2 = await r2.text();
	console.log('len:', t2.length, 'head:', t2.substring(0, 120));
}
main().catch((e) => { console.error(e); process.exit(1); });

// 探索 MCSM 文件下载端点
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

	// 1. 检查 read 响应原始 content-type 和 data 类型
	const res1 = await fetch(`${PANEL}/api/files?${q}`, {
		method: 'PUT',
		headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' },
		body: JSON.stringify({ target: 'logs/2026-08-29-21.log.gz' })
	});
	console.log('read content-type:', res1.headers.get('content-type'));
	const body1 = await res1.json();
	const d = body1.data;
	console.log('data type:', typeof d, 'length:', d?.length);
	console.log('data first 80 chars:', JSON.stringify(d?.substring(0, 80)));

	// 2. 尝试常见下载端点
	for (const ep of ['/api/files/download', '/api/download', '/api/files/read']) {
		try {
			const r = await fetch(`${PANEL}${ep}?${q}&file_name=2026-08-29-21.log.gz&target=${encodeURIComponent('logs')}`, {
				headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie }
			});
			console.log(`${ep}: status=${r.status} ct=${r.headers.get('content-type')} len=${(await r.arrayBuffer()).byteLength}`);
			await new Promise((x) => setTimeout(x, 1000));
		} catch (e) {
			console.log(`${ep}: ERR ${e.message}`);
		}
	}
}
main().catch((e) => { console.error(e); process.exit(1); });

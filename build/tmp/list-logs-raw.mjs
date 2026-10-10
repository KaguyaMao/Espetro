// 检查文件列表 API 原始响应结构
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
	const res = await fetch(`${PANEL}/api/files/list?page=1&page_size=100&file_name=&target=${encodeURIComponent('logs')}&${q}`, {
		headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie }
	});
	const body = await res.json();
	console.log(JSON.stringify(body, null, 2).substring(0, 2000));
}
main().catch((e) => { console.error(e); process.exit(1); });

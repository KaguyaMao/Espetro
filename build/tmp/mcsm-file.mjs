// mcsm-file.mjs — 独立文件管理（list/read/write）
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const OUT_FILE = 'D:/minecraft/modp/Espetro/build/tmp/mcsm-file-out.txt';
const fs = require('fs');

async function main() {
	const [action, path, ...rest] = process.argv.slice(2);
	const text = rest.join(' ');
	const loginRes = await fetch(`${PANEL}/api/auth/login`, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
		body: JSON.stringify({ username: USER, password: PASS })
	});
	const loginBody = await loginRes.json();
	const token = String(loginBody.data ?? '');
	const cookie = (loginRes.headers.getSetCookie?.() ?? []).map((l) => l.split(';')[0]).join('; ');
	const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
	let result;
	if (action === 'list') {
		const res = await fetch(`${PANEL}/api/files/list?page=0&page_size=100&file_name=&target=${encodeURIComponent(path)}&${q}`, {
			headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie }
		});
		const body = await res.json();
		const payload = body.data ?? body;
		result = (payload.items ?? []).map((i) => `${i.type === 0 ? 'DIR ' : 'FILE'} ${i.size} ${i.name}`).join('\n');
	} else if (action === 'read') {
		const res = await fetch(`${PANEL}/api/files?${q}`, {
			method: 'PUT',
			headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' },
			body: JSON.stringify({ target: path })
		});
		const body = await res.json();
		result = typeof body.data === 'string' ? body.data : JSON.stringify(body);
	} else if (action === 'write') {
		const res = await fetch(`${PANEL}/api/files?${q}`, {
			method: 'PUT',
			headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' },
			body: JSON.stringify({ target: path, text })
		});
		const body = await res.json();
		result = JSON.stringify(body);
	} else {
		result = 'unknown action';
	}
	fs.writeFileSync(OUT_FILE, result, 'utf8');
}
main().catch((e) => {
	fs.writeFileSync(OUT_FILE, 'ERROR: ' + (e?.stack ?? e), 'utf8');
	process.exit(1);
});

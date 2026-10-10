// 下载 12:26/12:27 新崩溃报告
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const OUT_DIR = 'D:/minecraft/modp/Espetro/build/tmp/';

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

	const names = [
		'crash-2026-08-29_12.26.36-server.txt',
		'crash-2026-08-29_12.27.38-server.txt'
	];
	for (const name of names) {
		let text = null;
		for (let attempt = 0; attempt < 4 && text == null; attempt++) {
			try {
				const res = await fetch(`${PANEL}/api/files?${q}`, {
					method: 'PUT',
					headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' },
					body: JSON.stringify({ target: `crash-reports/${name}` })
				});
				const body = await res.json();
				text = typeof body.data === 'string' ? body.data : JSON.stringify(body);
			} catch (e) {
				console.log(`attempt ${attempt} failed: ${e.message}`);
				await new Promise((r) => setTimeout(r, 3000));
			}
		}
		const out = OUT_DIR + 'crash-' + name.replace('crash-', '').replace('.txt', '.txt');
		fs.writeFileSync(out, text ?? 'FETCH FAILED', 'utf8');
		console.log(`saved ${name} -> ${out} (${(text ?? '').length} chars)`);
		await new Promise((r) => setTimeout(r, 2500));
	}
}
main().catch((e) => { console.error(e); process.exit(1); });

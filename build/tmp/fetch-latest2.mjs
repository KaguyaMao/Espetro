// 下载 13:45/13:46 崩溃报告 + 最新 latest.log
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const path = require('path');
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const OUT_DIR = 'D:/minecraft/modp/Espetro/build/tmp/logs-latest';

async function main() {
	fs.mkdirSync(OUT_DIR, { recursive: true });
	const loginRes = await fetch(`${PANEL}/api/auth/login`, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
		body: JSON.stringify({ username: USER, password: PASS })
	});
	const loginBody = await loginRes.json();
	const token = String(loginBody.data ?? '');
	const cookie = (loginRes.headers.getSetCookie?.() ?? []).map((l) => l.split(';')[0]).join('; ');
	const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;

	const targets = [
		['crash-reports/crash-2026-08-29_13.45.05-server.txt', 'crash-2026-08-29_13.45.05-server.txt'],
		['crash-reports/crash-2026-08-29_13.46.06-server.txt', 'crash-2026-08-29_13.46.06-server.txt'],
		['logs/latest.log', 'latest.log']
	];
	for (const [target, name] of targets) {
		let text = null;
		for (let attempt = 0; attempt < 4 && text == null; attempt++) {
			try {
				const res = await fetch(`${PANEL}/api/files?${q}`, {
					method: 'PUT',
					headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' },
					body: JSON.stringify({ target })
				});
				const body = await res.json();
				const d = body.data;
				if (typeof d === 'string') text = Buffer.from(d, 'latin1').toString('utf8');
				else { console.log(`${name}: retry ${JSON.stringify(body).substring(0, 80)}`); }
			} catch (e) { console.log(`${name}: err ${e.message}`); await new Promise((r) => setTimeout(r, 2500)); }
		}
		if (text != null) {
			fs.writeFileSync(path.join(OUT_DIR, name), text, 'utf8');
			console.log(`OK ${name} (${text.length} chars)`);
		} else console.log(`FAILED ${name}`);
		await new Promise((r) => setTimeout(r, 1500));
	}
	console.log('done');
}
main().catch((e) => { console.error(e); process.exit(1); });

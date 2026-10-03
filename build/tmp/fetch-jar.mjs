// 下载 vehicle_addition jar 和 auratip 旧 jar 到本地检查
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
		'vehicle_addition-1.0-SNAPSHOT-all.jar'
	];
	for (const name of names) {
		let buf = null;
		for (let attempt = 0; attempt < 4 && buf == null; attempt++) {
			try {
				const res = await fetch(`${PANEL}/api/files?${q}`, {
					method: 'PUT',
					headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' },
					body: JSON.stringify({ target: `mods/${name}` })
				});
				// 尝试二进制: 先看响应头
				const ct = res.headers.get('content-type') ?? '';
				if (ct.includes('application/octet-stream') || ct.includes('application/zip')) {
					buf = Buffer.from(await res.arrayBuffer());
				} else {
					const body = await res.json();
					const data = body.data;
					if (typeof data === 'string' && data.startsWith('data:') || typeof data === 'string' && data.length > 1000) {
						// base64 data uri?
						if (data.startsWith('data:')) {
							buf = Buffer.from(data.split(',')[1], 'base64');
						} else {
							buf = Buffer.from(data, 'base64');
						}
					} else {
						console.log(`attempt ${attempt} no binary: ${JSON.stringify(body).substring(0, 200)}`);
					}
				}
			} catch (e) {
				console.log(`attempt ${attempt} failed: ${e.message}`);
				await new Promise((r) => setTimeout(r, 3000));
			}
		}
		if (buf) {
			const out = OUT_DIR + name;
			fs.writeFileSync(out, buf);
			console.log(`saved ${name} (${buf.length} bytes)`);
		} else {
			console.log(`FAILED ${name}`);
		}
		await new Promise((r) => setTimeout(r, 2500));
	}
}
main().catch((e) => { console.error(e); process.exit(1); });

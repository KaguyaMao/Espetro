// 下载 2026-08-29 全部日志（修正版：data 为 JSON 转义字符串，latin1 还原）
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const path = require('path');
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const OUT_DIR = 'D:/minecraft/modp/Espetro/build/tmp/logs-2026-08-29';

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

	const names = ['latest.log'];
	for (let i = 1; i <= 21; i++) names.push(`2026-08-29-${i}.log.gz`);

	for (const name of names) {
		let buf = null;
		let attempts = 0;
		while (buf == null && attempts < 4) {
			attempts++;
			try {
				const res = await fetch(`${PANEL}/api/files?${q}`, {
					method: 'PUT',
					headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' },
					body: JSON.stringify({ target: `logs/${name}` })
				});
				const body = await res.json();
				const d = body.data;
				if (typeof d === 'string') {
					// JSON 解析后每个字符的码位 = 原始字节
					buf = Buffer.from(d, 'latin1');
				} else {
					console.log(`${name}: unexpected data type ${typeof d}`);
				}
			} catch (e) {
				console.log(`${name}: attempt ${attempts} failed: ${e.message}`);
				await new Promise((r) => setTimeout(r, 3000));
			}
		}
		if (buf) {
			const out = path.join(OUT_DIR, name);
			fs.writeFileSync(out, buf);
			console.log(`saved ${name} (${buf.length} bytes)`);
		} else {
			console.log(`FAILED ${name}`);
		}
		await new Promise((r) => setTimeout(r, 1200));
	}
}
main().catch((e) => { console.error(e); process.exit(1); });

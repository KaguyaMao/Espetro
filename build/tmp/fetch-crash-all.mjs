// 下载 crash-reports 目录全部文件
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const path = require('path');
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const OUT_DIR = 'D:/minecraft/modp/Espetro/build/tmp/crash-reports-all';

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

	// 1. 列出目录
	let names = [];
	for (let page = 0; page < 5; page++) {
		const res = await fetch(`${PANEL}/api/files/list?page=${page}&page_size=100&file_name=&target=${encodeURIComponent('crash-reports')}&${q}`, {
			headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie }
		});
		const body = await res.json();
		const payload = body.data ?? body;
		const items = payload.items ?? [];
		names.push(...items.filter((i) => i.type === 1).map((i) => i.name));
		if (items.length < 100) break;
		await new Promise((r) => setTimeout(r, 1200));
	}
	console.log('total files:', names.length);

	// 2. 逐个下载（文本文件，JSON data 字符串直接 latin1 写）
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
				const d = body.data;
				if (typeof d === 'string') {
					text = Buffer.from(d, 'latin1').toString('utf8');
				} else {
					console.log(`${name}: unexpected data ${JSON.stringify(body).substring(0, 100)}`);
				}
			} catch (e) {
				console.log(`${name}: attempt ${attempt} err ${e.message}`);
				await new Promise((r) => setTimeout(r, 3000));
			}
		}
		if (text != null) {
			fs.writeFileSync(path.join(OUT_DIR, name), text, 'utf8');
			console.log(`OK ${name} (${text.length} chars)`);
		} else {
			console.log(`FAILED ${name}`);
		}
		await new Promise((r) => setTimeout(r, 1000));
	}
	console.log('done');
}
main().catch((e) => { console.error(e); process.exit(1); });

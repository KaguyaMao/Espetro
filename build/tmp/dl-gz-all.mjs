// 批量下载 2026-08-29 全部 gz 日志（二进制通道）
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const https = require('https');
const fs = require('fs');
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const OUT_DIR = 'D:/minecraft/modp/Espetro/build/tmp/logs-2026-08-29';

function httpGet(url, headers = {}) {
	return new Promise((resolve, reject) => {
		https.get(url, { rejectUnauthorized: false, headers }, (res) => {
			const chunks = [];
			res.on('data', (c) => chunks.push(c));
			res.on('end', () => resolve({ status: res.statusCode, headers: res.headers, body: Buffer.concat(chunks) }));
		}).on('error', reject);
	});
}

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

	for (let i = 1; i <= 21; i++) {
		const file = `2026-08-29-${i}.log.gz`;
		const url = `${PANEL}/api/files/download?file_name=${encodeURIComponent('logs/' + file)}&${q}`;
		let d = null;
		for (let a = 0; a < 3 && !d; a++) {
			try {
				const res = await fetch(url, { method: 'POST', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie } });
				const body = await res.json();
				d = body.data;
				if (!d?.password) { console.log(file, 'no password:', JSON.stringify(body).substring(0, 120)); d = null; await new Promise((r) => setTimeout(r, 3000)); }
			} catch (e) { console.log(file, 'api err', e.message); await new Promise((r) => setTimeout(r, 3000)); }
		}
		if (!d) { console.log('FAILED', file); continue; }
		const addr = d.addr.replace('wss://', 'https://').replace('localhost', 'www.derpydoge.fun');
		const dl = await httpGet(`${addr}/download/${d.password}/${file}`, { Cookie: cookie });
		if (dl.status === 200 && dl.body.length > 0 && dl.body[0] === 0x1f && dl.body[1] === 0x8b) {
			fs.writeFileSync(`${OUT_DIR}/${file}`, dl.body);
			console.log('OK', file, dl.body.length);
		} else {
			console.log('BAD', file, 'status', dl.status, 'len', dl.body.length, 'head', dl.body.slice(0, 4).toString('hex'));
		}
		await new Promise((r) => setTimeout(r, 600));
	}
	console.log('done');
}
main().catch((e) => { console.error(e); process.exit(1); });

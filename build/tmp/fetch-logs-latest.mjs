// fetch-logs-latest.mjs — 下载 latest.log（文本）与指定 gz 日志（二进制通道）
//
// 用法:
//   node fetch-logs-latest.mjs                      仅下载 latest.log
//   node fetch-logs-latest.mjs 2026-09-04-1.log.gz  额外下载指定 gz（可多个）
//
// 注意: read API(/api/files PUT) 返回的 data 已经是正确 UTF-8 字符串，
//       不要再做 Buffer.from(data,'latin1').toString('utf8') 二次还原，否则中文会变乱码。
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const https = require('https');
const fs = require('fs');
const path = require('path');
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const OUT_DIR = 'D:/minecraft/modp/Espetro/build/tmp/logs-latest';

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
	const gzFiles = process.argv.slice(2);
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

	// 1. latest.log（文本，直接写 UTF-8）
	const res = await fetch(`${PANEL}/api/files?${q}`, {
		method: 'PUT',
		headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' },
		body: JSON.stringify({ target: 'logs/latest.log' })
	});
	const body = await res.json();
	if (typeof body.data === 'string') {
		fs.writeFileSync(path.join(OUT_DIR, 'latest.log'), body.data, 'utf8');
		console.log('OK latest.log', body.data.length, 'chars');
	} else {
		console.log('FAIL latest.log', JSON.stringify(body).slice(0, 120));
	}

	// 2. 指定 gz 走二进制下载通道（文本 API 对压缩文件有损）
	for (const file of gzFiles) {
		const url = `${PANEL}/api/files/download?file_name=${encodeURIComponent('logs/' + file)}&${q}`;
		let d = null;
		for (let a = 0; a < 4 && !d; a++) {
			try {
				const r = await fetch(url, { method: 'POST', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie } });
				const b = await r.json();
				d = b.data;
				if (!d?.password) { console.log(file, 'retry:', JSON.stringify(b).slice(0, 80)); d = null; await new Promise((x) => setTimeout(x, 2500)); }
			} catch (e) { console.log(file, 'err', e.message); await new Promise((x) => setTimeout(x, 2500)); }
		}
		if (!d) { console.log('FAILED', file); continue; }
		const addr = String(d.addr).replace('wss://', 'https://').replace('localhost', 'www.derpydoge.fun');
		const dl = await httpGet(`${addr}/download/${d.password}/${file}`, { Cookie: cookie });
		if (dl.status === 200 && dl.body[0] === 0x1f && dl.body[1] === 0x8b) {
			fs.writeFileSync(path.join(OUT_DIR, file), dl.body);
			console.log('OK', file, dl.body.length);
		} else {
			console.log('BAD', file, dl.status, dl.body.length);
		}
		await new Promise((x) => setTimeout(x, 1200));
	}
	console.log('done ->', OUT_DIR);
}
main().catch((e) => { console.error(e); process.exit(1); });

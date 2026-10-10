// write-file.mjs — 覆盖写入服务端文件（带冷却重试）
// 用法: node write-file.mjs <目标路径> <本地文件>
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const https = require('https');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const OUT_FILE = 'D:/minecraft/modp/Espetro/build/tmp/mcsm-file-out.txt';

function httpsReq(method, urlStr, headers, body, getCookie) {
	return new Promise((resolve, reject) => {
		const url = new URL(urlStr);
		const req = https.request({
			method,
			hostname: url.hostname,
			port: url.port || (url.protocol === 'https:' ? 443 : 80),
			path: url.pathname + url.search,
			headers,
			rejectUnauthorized: false
		}, (res) => {
			let data = '';
			res.on('data', (c) => (data += c));
			res.on('end', () => resolve({ status: res.statusCode, body: data, cookie: getCookie ? (res.headers['set-cookie'] ?? []).map((l) => l.split(';')[0]).join('; ') : undefined }));
		});
		req.on('error', reject);
		if (body !== undefined) req.write(body);
		req.end();
	});
}

const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

async function main() {
	const target = process.argv[2];
	const localFile = process.argv[3];
	const text = fs.readFileSync(localFile, 'utf8');
	const loginRes = await httpsReq('POST', `${PANEL}/api/auth/login`, {
		'Content-Type': 'application/json',
		'X-Requested-With': 'XMLHttpRequest'
	}, JSON.stringify({ username: USER, password: PASS }), true);
	const token = String(JSON.parse(loginRes.body).data ?? '');
	const cookie = loginRes.cookie ?? '';
	const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
	let last = '';
	for (let attempt = 1; attempt <= 6; attempt++) {
		const res = await httpsReq('PUT', `${PANEL}/api/files?${q}`, {
			'X-Requested-With': 'XMLHttpRequest',
			'Cookie': cookie,
			'Content-Type': 'application/json'
		}, JSON.stringify({ target, text }));
		last = `attempt=${attempt} status=${res.status} body=${res.body.slice(0, 200)}`;
		if (res.status === 200) {
			fs.writeFileSync(OUT_FILE, last, 'utf8');
			return;
		}
		await sleep(3500);
	}
	fs.writeFileSync(OUT_FILE, last + '\nFAILED', 'utf8');
	process.exit(1);
}
main().catch((e) => {
	fs.writeFileSync(OUT_FILE, 'ERROR: ' + (e?.stack ?? e), 'utf8');
	process.exit(1);
});

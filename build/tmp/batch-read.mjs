// batch-read.mjs — 批量下载服务端文件到本地
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const https = require('https');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const OUT_DIR = 'D:/minecraft/modp/Espetro/build/tmp/server-vehicles/';
fs.mkdirSync(OUT_DIR, { recursive: true });

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

const args = process.argv.slice(2);
const prefixArg = args.find((a) => a.startsWith('--prefix='));
const prefix = prefixArg ? prefixArg.slice(9) : 'kubejs/data/dragonrise_reforge/sbw/vehicles/';
const files = args.filter((a) => !a.startsWith('--prefix='));

async function main() {
	const loginRes = await httpsReq('POST', `${PANEL}/api/auth/login`, {
		'Content-Type': 'application/json',
		'X-Requested-With': 'XMLHttpRequest'
	}, JSON.stringify({ username: USER, password: PASS }), true);
	const token = String(JSON.parse(loginRes.body).data ?? '');
	const cookie = loginRes.cookie ?? '';
	const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
	for (const f of files) {
		const res = await httpsReq('PUT', `${PANEL}/api/files?${q}`, {
			'X-Requested-With': 'XMLHttpRequest',
			'Cookie': cookie,
			'Content-Type': 'application/json'
		}, JSON.stringify({ target: prefix + f }));
		const body = JSON.parse(res.body);
		const text = typeof body.data === 'string' ? body.data : JSON.stringify(body);
		fs.writeFileSync(OUT_DIR + f, text, 'utf8');
		console.log(`${f}: ${res.status} (${text.length} chars)`);
		await new Promise((r) => setTimeout(r, 1000));
	}
}
main().catch((e) => { console.log('ERROR: ' + (e?.stack ?? e)); process.exit(1); });

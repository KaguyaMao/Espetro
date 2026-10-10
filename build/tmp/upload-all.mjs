// upload-all.mjs — 批量上传桌面编制文件到服务端 EsFactions
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
const DIR = 'C:/Users/Administrator/Desktop/编制文件/';

function httpsReq(method, urlStr, headers, body, getCookie, binary) {
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
			const chunks = [];
			res.on('data', (c) => chunks.push(c));
			res.on('end', () => {
				const buf = Buffer.concat(chunks);
				resolve({ status: res.statusCode, body: binary ? null : buf.toString('utf8'), buf, cookie: getCookie ? (res.headers['set-cookie'] ?? []).map((l) => l.split(';')[0]).join('; ') : undefined });
			});
		});
		req.on('error', reject);
		if (body !== undefined) req.write(body);
		req.end();
	});
}

async function uploadOne(fileName, login) {
	// create task
	const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&upload_dir=EsFactions&token=${login.token}`;
	const taskRes = await httpsReq('POST', `${PANEL}/api/files/upload?${q}`, {
		'X-Requested-With': 'XMLHttpRequest',
		'Cookie': login.cookie
	});
	const taskBody = JSON.parse(taskRes.body);
	if (taskRes.status !== 200 || !taskBody.data) throw new Error('task fail: ' + taskRes.body.slice(0, 150));
	const password = String(taskBody.data.password);
	const addr = String(taskBody.data.addr);
	const host = new URL(PANEL).hostname;
	const m = addr.match(/^wss?:\/\/([^/:]+)(:\d+)?/);
	const daemonUrl = 'https://' + host + (m?.[2] ?? '');
	// upload
	const buf = fs.readFileSync(DIR + fileName);
	const boundary = '----DSHUpload' + Date.now().toString(16) + Math.random().toString(16).slice(2);
	const head = Buffer.from(`--${boundary}\r\nContent-Disposition: form-data; name="file"; filename="${fileName}"\r\nContent-Type: application/json\r\n\r\n`);
	const tail = Buffer.from(`\r\n--${boundary}--\r\n`);
	const body = Buffer.concat([head, buf, tail]);
	const upRes = await httpsReq('POST', `${daemonUrl}/upload/${password}?overwrite=true`, {
		'Content-Type': `multipart/form-data; boundary=${boundary}`,
		'Content-Length': String(body.length)
	}, body);
	if (upRes.status !== 200 || upRes.body.trim() !== 'OK') throw new Error('upload fail: ' + upRes.status + ' ' + upRes.body.slice(0, 150));
	return `${fileName}: OK (${buf.length})`;
}

async function main() {
	const loginRes = await httpsReq('POST', `${PANEL}/api/auth/login`, {
		'Content-Type': 'application/json',
		'X-Requested-With': 'XMLHttpRequest'
	}, JSON.stringify({ username: USER, password: PASS }), true);
	const token = String(JSON.parse(loginRes.body).data ?? '');
	const cookie = loginRes.cookie ?? '';
	const login = { token, cookie };
	const files = fs.readdirSync(DIR).filter((f) => f.endsWith('.json')).sort();
	const results = [];
	for (const f of files) {
		try {
			results.push(await uploadOne(f, login));
		} catch (e) {
			results.push(`${f}: ERROR ${e.message}`);
		}
		await new Promise((r) => setTimeout(r, 1200));
	}
	fs.writeFileSync(OUT_FILE, results.join('\n'), 'utf8');
}
main().catch((e) => {
	fs.writeFileSync(OUT_FILE, 'ERROR: ' + (e?.stack ?? e), 'utf8');
	process.exit(1);
});

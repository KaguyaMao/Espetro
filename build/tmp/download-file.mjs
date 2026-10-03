// download-file.mjs — 从服务端下载文件 (MCSM download flow)
// 用法: node download-file.mjs <目标路径> <本地保存路径>
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
				resolve({
					status: res.statusCode,
					body: binary ? null : buf.toString('utf8'),
					buf,
					cookie: getCookie ? (res.headers['set-cookie'] ?? []).map((l) => l.split(';')[0]).join('; ') : undefined
				});
			});
		});
		req.on('error', reject);
		if (body !== undefined) req.write(body);
		req.end();
	});
}

async function main() {
	const target = process.argv[2];
	const localPath = process.argv[3];
	const loginRes = await httpsReq('POST', `${PANEL}/api/auth/login`, {
		'Content-Type': 'application/json',
		'X-Requested-With': 'XMLHttpRequest'
	}, JSON.stringify({ username: USER, password: PASS }), true);
	const token = String(JSON.parse(loginRes.body).data ?? '');
	const cookie = loginRes.cookie ?? '';
	const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
	// 1. create download task
	const taskRes = await httpsReq('POST', `${PANEL}/api/files/download?${q}&file_name=${encodeURIComponent(target)}`, {
		'X-Requested-With': 'XMLHttpRequest',
		'Cookie': cookie
	});
	const taskBody = JSON.parse(taskRes.body);
	if (taskRes.status !== 200 || !taskBody.data) throw new Error('create download task failed: ' + taskRes.body);
	const password = String(taskBody.data.password);
	let addr = String(taskBody.data.addr);
	const host = new URL(PANEL).hostname;
	const m = addr.match(/^wss?:\/\/([^/:]+)(:\d+)?/);
	const daemonUrl = 'https://' + host + (m?.[2] ?? '');
	const fileName = encodeURIComponent(target.split('/').pop());
	// 2. download from daemon
	const dlRes = await httpsReq('GET', `${daemonUrl}/download/${password}/${fileName}`, {}, undefined, false, true);
	if (dlRes.status !== 200) throw new Error('download failed: ' + dlRes.status + ' ' + (dlRes.body ?? '').slice(0, 200));
	fs.writeFileSync(localPath, dlRes.buf);
	fs.writeFileSync(OUT_FILE, `status=${dlRes.status} size=${dlRes.buf.length}\nSAVED ${localPath}`, 'utf8');
}
main().catch((e) => {
	fs.writeFileSync(OUT_FILE, 'ERROR: ' + (e?.stack ?? e), 'utf8');
	process.exit(1);
});

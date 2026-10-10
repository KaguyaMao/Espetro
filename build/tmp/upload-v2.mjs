// upload-v2.mjs — MCSM10 正确上传流程: 面板创建任务 → 直传 daemon (https.request, 忽略证书)
// 用法: node upload-v2.mjs <本地文件> <目标文件名> [目标目录=mods]
// 日志写入 upload-out.txt（与 mcsm-file.mjs 的 mcsm-file-out.txt 分离，避免互相覆盖）
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const https = require('https');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const OUT_FILE = 'D:/minecraft/modp/Espetro/build/tmp/upload-out.txt';

const LOCAL_FILE = process.argv[2];
const TARGET_NAME = process.argv[3];
const UPLOAD_DIR = process.argv[4] ?? 'mods';
const log = [];

function httpsReq(method, urlStr, headers, body, getCookie) {
	return new Promise((resolve, reject) => {
		const url = new URL(urlStr);
		const opts = {
			method,
			hostname: url.hostname,
			port: url.port || (url.protocol === 'https:' ? 443 : 80),
			path: url.pathname + url.search,
			headers,
			rejectUnauthorized: false
		};
		const req = https.request(opts, (res) => {
			let data = '';
			res.on('data', (c) => (data += c));
			res.on('end', () => resolve({ status: res.statusCode, body: data, cookie: getCookie ? (res.headers['set-cookie'] ?? []).map((l) => l.split(';')[0]).join('; ') : undefined }));
		});
		req.on('error', reject);
		if (body !== undefined) req.write(body);
		req.end();
	});
}

async function main() {
	// 1. login
	const loginRes = await httpsReq('POST', `${PANEL}/api/auth/login`, {
		'Content-Type': 'application/json',
		'X-Requested-With': 'XMLHttpRequest'
	}, JSON.stringify({ username: USER, password: PASS }), true);
	const loginBody = JSON.parse(loginRes.body);
	const token = String(loginBody.data ?? '');
	const cookie = loginRes.cookie ?? '';
	log.push('login status=' + loginRes.status + ' token len=' + token.length + ' cookie len=' + cookie.length);
	if (!token) throw new Error('login failed: ' + loginRes.body);

	// 2. create upload task on panel
	const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&upload_dir=${UPLOAD_DIR}&token=${token}`;
	const taskRes = await httpsReq('POST', `${PANEL}/api/files/upload?${q}`, {
		'X-Requested-With': 'XMLHttpRequest',
		'Cookie': cookie
	});
	log.push('task status=' + taskRes.status + ' body=' + taskRes.body.slice(0, 300));
	if (taskRes.status !== 200) throw new Error('create task failed: ' + taskRes.body);
	const taskBody = JSON.parse(taskRes.body);
	const password = String(taskBody.data?.password ?? '');
	const addr = String(taskBody.data?.addr ?? '');
	if (!password || !addr) throw new Error('no password/addr');

	// rewrite localhost addr to public host, preserving the port
	const host = new URL(PANEL).hostname;
	const m = addr.match(/^wss?:\/\/([^/:]+)(:\d+)?/);
	const daemonPort = m?.[2] ?? '';
	const daemonUrl = 'https://' + host + daemonPort;
	log.push('password=' + password + ' daemon=' + daemonUrl);

	// 3. upload whole file to daemon /upload/{password}
	const buf = fs.readFileSync(LOCAL_FILE);
	log.push('file size=' + buf.length);
	const boundary = '----DSHUpload' + Date.now().toString(16);
	const head = Buffer.from(
		`--${boundary}\r\nContent-Disposition: form-data; name="file"; filename="${TARGET_NAME}"\r\nContent-Type: application/java-archive\r\n\r\n`
	);
	const tail = Buffer.from(`\r\n--${boundary}--\r\n`);
	const body = Buffer.concat([head, buf, tail]);
	const upRes = await httpsReq('POST', `${daemonUrl}/upload/${password}?overwrite=true`, {
		'X-Requested-With': 'XMLHttpRequest',
		'Content-Type': `multipart/form-data; boundary=${boundary}`,
		'Content-Length': String(body.length)
	}, body);
	log.push('upload status=' + upRes.status + ' body=' + upRes.body.slice(0, 300));
	if (upRes.status !== 200 || upRes.body.trim() !== 'OK') throw new Error('upload failed: ' + upRes.body);

	log.push('UPLOAD_OK ' + TARGET_NAME);
	fs.writeFileSync(OUT_FILE, log.join('\n'), 'utf8');
}
main().catch((e) => {
	fs.writeFileSync(OUT_FILE, log.join('\n') + '\nERROR: ' + (e?.stack ?? e), 'utf8');
	process.exit(1);
});

// upload-batch.mjs — 批量上传目录内的 json 到服务器 EsFactions/
// 用法: node upload-batch.mjs <本地目录> [目标目录=EsFactions]
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const https = require('https');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const OUT_FILE = 'D:/minecraft/modp/Espetro/build/tmp/upload-batch-out.txt';

const SRC_DIR = process.argv[2];
const UPLOAD_DIR = process.argv[3] ?? 'EsFactions';
const log = [];

function httpsReq(method, urlStr, headers, body, getCookie) {
	return new Promise((resolve, reject) => {
		const url = new URL(urlStr);
		const req = https.request({
			method,
			hostname: url.hostname,
			port: url.port || 443,
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

async function login() {
	const res = await httpsReq('POST', `${PANEL}/api/auth/login`, {
		'Content-Type': 'application/json',
		'X-Requested-With': 'XMLHttpRequest'
	}, JSON.stringify({ username: USER, password: PASS }), true);
	const body = JSON.parse(res.body);
	const token = String(body.data ?? '');
	log.push('login status=' + res.status + ' token len=' + token.length);
	if (!token) throw new Error('login failed: ' + res.body);
	return { token, cookie: res.cookie ?? '' };
}

async function newTask(token, cookie) {
	const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&upload_dir=${UPLOAD_DIR}&token=${token}`;
	const res = await httpsReq('POST', `${PANEL}/api/files/upload?${q}`, {
		'X-Requested-With': 'XMLHttpRequest',
		'Cookie': cookie
	});
	if (res.status !== 200) throw new Error('create task failed: ' + res.body);
	const data = JSON.parse(res.body).data ?? {};
	const addr = String(data.addr ?? '');
	const m = addr.match(/^wss?:\/\/([^/:]+)(:\d+)?/);
	return { password: String(data.password ?? ''), daemonUrl: 'https://' + new URL(PANEL).hostname + (m?.[2] ?? '') };
}

async function upload(path, name, daemonUrl, password) {
	const buf = fs.readFileSync(path);
	const boundary = '----DSHUpload' + Date.now().toString(16);
	const head = Buffer.from(`--${boundary}\r\nContent-Disposition: form-data; name="file"; filename="${name}"\r\nContent-Type: application/json\r\n\r\n`);
	const tail = Buffer.from(`\r\n--${boundary}--\r\n`);
	const body = Buffer.concat([head, buf, tail]);
	const res = await httpsReq('POST', `${daemonUrl}/upload/${password}?overwrite=true`, {
		'X-Requested-With': 'XMLHttpRequest',
		'Content-Type': `multipart/form-data; boundary=${boundary}`,
		'Content-Length': String(body.length)
	}, body);
	return { ok: res.status === 200 && res.body.trim() === 'OK', size: buf.length, detail: res.status + ' ' + res.body.slice(0, 120) };
}

async function main() {
	let { token, cookie } = await login();
	const files = fs.readdirSync(SRC_DIR).filter((f) => f.endsWith('.json')).sort();
	log.push(`待上传 ${files.length} 个文件 → ${UPLOAD_DIR}/`);
	let okCount = 0;
	for (const f of files) {
		let done = false;
		for (let attempt = 1; attempt <= 3 && !done; attempt++) {
			try {
				const { password, daemonUrl } = await newTask(token, cookie);
				const r = await upload(SRC_DIR.replace(/\/?$/, '/') + f, f, daemonUrl, password);
				if (r.ok) {
					okCount++;
					log.push(`OK  ${f} (${r.size} B)`);
					done = true;
				} else {
					log.push(`重试${attempt} ${f}: ${r.detail}`);
					await sleep(2500);
				}
			} catch (e) {
				log.push(`重试${attempt} ${f} 异常: ${e?.message ?? e}`);
				await sleep(2500);
				try { ({ token, cookie } = await login()); } catch { /* ignore */ }
			}
		}
		if (!done) log.push(`FAIL ${f}`);
		await sleep(700);
	}
	log.push(`完成: ${okCount}/${files.length}`);
	fs.writeFileSync(OUT_FILE, log.join('\n'), 'utf8');
	if (okCount !== files.length) process.exit(1);
}

main().catch((e) => {
	log.push('ERROR: ' + (e?.stack ?? e));
	fs.writeFileSync(OUT_FILE, log.join('\n'), 'utf8');
	process.exit(1);
});

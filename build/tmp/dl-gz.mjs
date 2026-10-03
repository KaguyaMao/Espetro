// 通过面板下载端点获取 gz 日志（二进制）
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

	// 1. 获取下载凭证（POST /api/files/download, params 里 file_name 为完整路径）
	const file = '2026-08-29-21.log.gz';
	const url = `${PANEL}/api/files/download?file_name=${encodeURIComponent('logs/' + file)}&${q}`;
	const res = await fetch(url, {
		method: 'POST',
		headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie }
	});
	const body = await res.json();
	console.log('download api:', JSON.stringify(body).substring(0, 300));
	const d = body.data;
	if (!d || !d.password) { console.log('no password'); return; }

	// 2. 构造下载 URL：addr 的 wss 换成 https（面板同主机 20443 端口可达）
	const addr = d.addr.replace('wss://', 'https://').replace('localhost', 'www.derpydoge.fun');
	const dlUrl = `${addr}/download/${d.password}/${file}`;
	console.log('dl url:', dlUrl);
	const dl = await httpGet(dlUrl, { Cookie: cookie });
	console.log('dl status:', dl.status, 'ct:', dl.headers['content-type'], 'len:', dl.body.length);
	console.log('head bytes:', dl.body.slice(0, 4).toString('hex'));
	if (dl.status === 200) {
		fs.writeFileSync(`${OUT_DIR}/${file}`, dl.body);
		console.log('saved', file, dl.body.length);
	}
}
main().catch((e) => { console.error(e); process.exit(1); });

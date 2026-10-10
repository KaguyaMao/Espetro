// upload-test.mjs — 测试 upload API（小文件）
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const OUT_FILE = 'D:/minecraft/modp/Espetro/build/tmp/mcsm-file-out.txt';

async function jfetch(url, opts) {
	const ctrl = new AbortController();
	const t = setTimeout(() => ctrl.abort(), 60000);
	try { return await fetch(url, { ...opts, signal: ctrl.signal }); } finally { clearTimeout(t); }
}

async function main() {
	const loginRes = await jfetch(`${PANEL}/api/auth/login`, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
		body: JSON.stringify({ username: USER, password: PASS })
	});
	const loginBody = await loginRes.json();
	const token = String(loginBody.data ?? '');
	const cookie = (loginRes.headers.getSetCookie?.() ?? []).map((l) => l.split(';')[0]).join('; ');
	const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;

	const fd = new FormData();
	fd.append('upload_dir', 'mods');
	fd.append('file', new Blob([Buffer.from('upload-test-ok')], { type: 'text/plain' }), 'dsh-upload-test.txt');

	const res = await jfetch(`${PANEL}/api/files/upload?${q}`, {
		method: 'POST',
		headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie },
		body: fd
	});
	const text = await res.text();
	fs.writeFileSync(OUT_FILE, 'STATUS=' + res.status + '\nBODY=' + text, 'utf8');
}
main().catch((e) => {
	fs.writeFileSync(OUT_FILE, 'ERROR: ' + (e?.stack ?? e), 'utf8');
	process.exit(1);
});

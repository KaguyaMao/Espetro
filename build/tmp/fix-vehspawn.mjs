// fix-vehspawn.mjs — 读取越南 VehSpawn.json，删除 truck 数组尾逗号后写回
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const TARGET = 'EsWorld/\u8d8a\u5357/EsConfig/VehSpawn.json';
const OUT_FILE = 'D:/minecraft/modp/Espetro/build/tmp/mcsm-file-out.txt';

async function jfetch(url, opts) {
	const ctrl = new AbortController();
	const t = setTimeout(() => ctrl.abort(), 30000);
	try {
		return await fetch(url, { ...opts, signal: ctrl.signal });
	} finally {
		clearTimeout(t);
	}
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

	const readRes = await jfetch(`${PANEL}/api/files?${q}`, {
		method: 'PUT',
		headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' },
		body: JSON.stringify({ target: TARGET })
	});
	const readBody = await readRes.json();
	const raw = typeof readBody.data === 'string' ? readBody.data : JSON.stringify(readBody);
	const before = raw;
	// 定位 truck_2 对象，用花括号计数找对象结束，删除其后紧跟的尾逗号
	const idx = raw.indexOf('"truck_2"');
	let fixed = before;
	if (idx >= 0) {
		const objStart = raw.lastIndexOf('{', idx);
		let depth = 0, objEnd = -1;
		for (let i = objStart; i < raw.length; i++) {
			if (raw[i] === '{') depth++;
			else if (raw[i] === '}') { depth--; if (depth === 0) { objEnd = i; break; } }
		}
		if (objEnd >= 0) {
			const rest = raw.slice(objEnd + 1);
			const m = rest.match(/^(\s*),(\s*\])/);
			if (m) {
				fixed = raw.slice(0, objEnd + 1) + rest.replace(/^(\s*),(\s*\])/, '$1$2');
			}
		}
	}
	if (fixed === before) {
		fs.writeFileSync(OUT_FILE, 'NO_CHANGE: 未找到 truck_2 尾逗号 (idx=' + idx + ')', 'utf8');
		return;
	}
	try { JSON.parse(fixed); } catch (e) {
		fs.writeFileSync(OUT_FILE, 'FIXED_JSON_INVALID: ' + e.message, 'utf8');
		return;
	}
	const writeRes = await jfetch(`${PANEL}/api/files?${q}`, {
		method: 'PUT',
		headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' },
		body: JSON.stringify({ target: TARGET, text: fixed })
	});
	let writeBody = await writeRes.json();
	// MCSM 写操作有冷却：失败时等待重试
	for (let attempt = 0; attempt < 4 && String(writeBody.status) !== '200'; attempt++) {
		await new Promise((r) => setTimeout(r, 3000));
		const retry = await jfetch(`${PANEL}/api/files?${q}`, {
			method: 'PUT',
			headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' },
			body: JSON.stringify({ target: TARGET, text: fixed })
		});
		writeBody = await retry.json();
	}

	const vRes = await jfetch(`${PANEL}/api/files?${q}`, {
		method: 'PUT',
		headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' },
		body: JSON.stringify({ target: TARGET })
	});
	const vBody = await vRes.json();
	const after = typeof vBody.data === 'string' ? vBody.data : '';
	let jsonOk = true;
	try { JSON.parse(after); } catch (e) { jsonOk = false; }
	fs.writeFileSync(OUT_FILE,
		'WRITE=' + JSON.stringify(writeBody) + '\n'
		+ 'JSON_VALID_AFTER=' + jsonOk + '\n'
		+ 'LEN_BEFORE=' + before.length + ' LEN_AFTER=' + after.length + '\n'
		+ 'HAS_TRAILING_COMMA=' + /"truck_2"[^}]*}\s*,(\s*\])/.test(after),
		'utf8');
}
main().catch((e) => {
	fs.writeFileSync(OUT_FILE, 'ERROR: ' + (e?.stack ?? e), 'utf8');
	process.exit(1);
});

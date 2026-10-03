// mcsm-console.mjs — 直连 MCSM daemon 发送控制台命令（独立于 dsh 插件）
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const WebSocket = require('ws');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const OUT_FILE = 'D:/minecraft/modp/Espetro/build/tmp/mcsm-console-out.txt';

const commands = process.argv.slice(2);
const gapMs = 1000;
const tailLines = 60;

function wsUrl(addr, prefix) {
	const base = addr.replace(/^http:/, 'ws:').replace(/\/+$/, '');
	const path = (prefix ? (prefix.startsWith('/') ? prefix : `/${prefix}`) : '') + '/socket.io/';
	return `${base}${path}?EIO=4&transport=websocket`;
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

	const chRes = await fetch(`${PANEL}/api/protected_instance/stream_channel?daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`, {
		method: 'POST',
		headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie }
	});
	const ch = await chRes.json();
	let addr = String(ch.data?.addr ?? '');
	const password = String(ch.data?.password ?? '');
	const prefix = ch.data?.prefix;
	if (!addr) throw new Error('stream addr empty: ' + JSON.stringify(ch));
	if (/localhost|127\.0\.0\.1/.test(addr)) {
		const port = addr.match(/:(\d+)/)?.[1];
		addr = addr.replace(/^wss?:\/\/[^/]+/, 'wss://www.derpydoge.fun' + (port ? ':' + port : ''));
	}

	const socket = new WebSocket(wsUrl(addr, prefix));
	let buffer = '';
	let settled = false;
	const finish = (ok, msg) => {
		if (settled) return;
		settled = true;
		try { socket.close(); } catch {}
		require('fs').writeFileSync(OUT_FILE, ok ? msg : ('ERROR: ' + msg), 'utf8');
		process.exit(ok ? 0 : 1);
	};
	const timer = setTimeout(() => finish(false, 'timeout'), 120000);

	socket.on('open', () => {});
	socket.on('error', (err) => finish(false, 'ws error: ' + err));
	socket.on('close', (code) => { if (!settled) finish(false, 'ws closed ' + code); });
	socket.on('message', (raw) => {
		const text = raw.toString();
		for (const frame of text.split('\x1e')) {
			if (frame === '') continue;
			const type = frame[0];
			const data = frame.slice(1);
			if (type === '0') {
				socket.send('40');
			} else if (type === '2') {
				socket.send('3');
			} else if (type === '4') {
				const sio = data[0];
				const payload = data.slice(1);
				if (sio === '0') {
					socket.send(`42${JSON.stringify(['stream/auth', { data: { password } }])}`);
				} else if (sio === '2') {
					let event;
					try { event = JSON.parse(payload); } catch { return; }
					const name = event[0];
					const argsV = event.slice(1);
					if (name === 'stream/auth') {
						if (argsV[0]?.data !== true) { finish(false, 'stream/auth failed'); return; }
						socket.send(`42${JSON.stringify(['stream/detail', {}])}`);
						let index = 0;
						const runSeq = () => {
							if (index >= commands.length) {
								setTimeout(() => {
									const lines = buffer.split(/\r?\n/).map((l) => l.trim()).filter((l) => l !== '');
									finish(true, lines.slice(-tailLines).join('\n'));
								}, 1500);
								return;
							}
							socket.send(`42${JSON.stringify(['stream/input', { data: { command: commands[index] } }])}`);
							index++;
							if (index < commands.length) setTimeout(runSeq, Math.max(gapMs, 100));
							else setTimeout(runSeq, Math.max(gapMs, 100));
						};
						runSeq();
					} else if (name === 'instance/stdout') {
						const out = argsV[0]?.data;
						const textOut = typeof out === 'string' ? out : out?.text ?? '';
						if (textOut !== '') { buffer += textOut; if (buffer.length > 8388608) buffer = buffer.slice(-8388608); }
					}
				}
			}
		}
	});
}

main().catch((e) => {
	require('fs').writeFileSync(OUT_FILE, 'ERROR: ' + (e?.stack ?? e), 'utf8');
	process.exit(1);
});

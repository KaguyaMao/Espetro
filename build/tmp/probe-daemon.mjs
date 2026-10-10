// probe-daemon.mjs — 探测 MCSM daemon 的 HTTP API
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const https = require('https');

const DAEMON = 'https://www.derpydoge.fun:20443';
const OUT_FILE = 'D:/minecraft/modp/Espetro/build/tmp/mcsm-file-out.txt';

function jreq(method, path, body, headers = {}) {
	return new Promise((resolve, reject) => {
		const url = new URL(DAEMON + path);
		const opts = {
			method,
			hostname: url.hostname,
			port: url.port,
			path: url.pathname + url.search,
			headers: { 'Content-Type': 'application/json', ...headers },
			rejectUnauthorized: false,
			timeout: 20000
		};
		const req = https.request(opts, (res) => {
			let data = '';
			res.on('data', (c) => (data += c));
			res.on('end', () => resolve({ status: res.statusCode, headers: res.headers, body: data }));
		});
		req.on('error', reject);
		req.on('timeout', () => { req.destroy(new Error('timeout')); });
		if (body !== undefined) req.write(typeof body === 'string' ? body : JSON.stringify(body));
		req.end();
	});
}

async function main() {
	const out = [];
	// 1. root
	try { const r = await jreq('GET', '/'); out.push(`ROOT ${r.status}: ${r.body.slice(0, 300)}`); } catch (e) { out.push(`ROOT ERR ${e.message}`); }
	// 2. login (panel creds)
	try {
		const r = await jreq('POST', '/api/auth/login', { username: 'boy', password: 'boY1145141919810Fuck' });
		out.push(`LOGIN ${r.status}: ${r.body.slice(0, 400)}`);
		// 3. try common file endpoints with token if any
		let token = '';
		try { token = String(JSON.parse(r.body).data ?? ''); } catch {}
		if (token) {
			const h = { Authorization: token, Cookie: `session=${token}` };
			for (const p of ['/api/files', '/api/files/list', '/api/instance/status', '/api/overview']) {
				try { const r2 = await jreq('GET', p, undefined, h); out.push(`GET ${p} ${r2.status}: ${r2.body.slice(0, 200)}`); } catch (e) { out.push(`GET ${p} ERR ${e.message}`); }
			}
		}
	} catch (e) { out.push(`LOGIN ERR ${e.message}`); }
	fs.writeFileSync(OUT_FILE, out.join('\n'), 'utf8');
}
main().catch((e) => { fs.writeFileSync(OUT_FILE, 'ERROR: ' + (e?.stack ?? e), 'utf8'); process.exit(1); });

// upload-vehicles.mjs — 上传改动过的载具 json 到服务器 kubejs 数据包目录
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const path = require('path');
const crypto = require('crypto');
const https = require('https');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const NEW = 'D:/minecraft/modp/Espetro/build/tmp/srv-vehicles-new';
const OLD = 'D:/minecraft/modp/Espetro/build/tmp/srv-vehicles-before-shelledit';
const OUT_FILE = 'D:/minecraft/modp/Espetro/build/tmp/upload-vehicles-out.txt';
const log = [];
const md5 = f => crypto.createHash('md5').update(fs.readFileSync(f)).digest('hex');

function httpsReq(method, urlStr, headers, body, getCookie) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const req = https.request({ method, hostname: url.hostname, port: url.port || 443, path: url.pathname + url.search, headers, rejectUnauthorized: false }, (res) => {
      let data = ''; res.on('data', c => (data += c));
      res.on('end', () => resolve({ status: res.statusCode, body: data, cookie: getCookie ? (res.headers['set-cookie'] ?? []).map(l => l.split(';')[0]).join('; ') : undefined }));
    });
    req.on('error', reject);
    if (body !== undefined) req.write(body);
    req.end();
  });
}
const sleep = ms => new Promise(r => setTimeout(r, ms));

async function login() {
  const res = await httpsReq('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: USER, password: PASS }), true);
  const token = String(JSON.parse(res.body).data ?? '');
  if (!token) throw new Error('login failed: ' + res.body);
  return { token, cookie: res.cookie ?? '' };
}
async function newTask(token, cookie, dir) {
  for (let a = 0; a < 5; a++) {
    const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&upload_dir=${encodeURIComponent(dir)}&token=${token}`;
    const res = await httpsReq('POST', `${PANEL}/api/files/upload?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
    try {
      const data = JSON.parse(res.body).data ?? {};
      const addr = String(data.addr ?? '');
      const m = addr.match(/^wss?:\/\/([^/:]+)(:\d+)?/);
      return { password: String(data.password ?? ''), daemonUrl: 'https://' + new URL(PANEL).hostname + (m?.[2] ?? '') };
    } catch { await sleep(2000); }
  }
  throw new Error('create upload task failed');
}
async function upload(file, name, daemonUrl, password) {
  const buf = fs.readFileSync(file);
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
  const all = fs.readdirSync(NEW).filter(f => f.endsWith('.json') && f !== 'manifest.json');
  const changed = all.filter(f => {
    const o = path.join(OLD, f);
    return !fs.existsSync(o) || md5(path.join(NEW, f)) !== md5(o);
  });
  log.push(`总计 ${all.length}，改动 ${changed.length}，仅上传改动文件`);
  let ok = 0;
  const tasks = {};
  for (const f of changed) {
    const [ns, ...rest] = f.split('__');
    const name = rest.join('__');
    const dir = ns === 'fcp' ? 'kubejs/data/fcp/sbw/vehicles' : 'kubejs/data/dragonrise_reforge/sbw/vehicles';
    let done = false;
    for (let attempt = 1; attempt <= 4 && !done; attempt++) {
      try {
        if (!tasks[dir]) tasks[dir] = await newTask(token, cookie, dir);
        const r = await upload(path.join(NEW, f), name, tasks[dir].daemonUrl, tasks[dir].password);
        if (r.ok) { ok++; log.push(`OK   ${dir}/${name} (${r.size} B)`); done = true; }
        else {
          log.push(`重试${attempt} ${name}: ${r.detail}`);
          tasks[dir] = null;
          await sleep(2000);
          try { ({ token, cookie } = await login()); } catch { }
        }
      } catch (e) {
        log.push(`重试${attempt} ${name} 异常: ${e?.message ?? e}`);
        tasks[dir] = null;
        await sleep(2500);
        try { ({ token, cookie } = await login()); } catch { }
      }
    }
    if (!done) log.push(`FAIL ${name}`);
    await sleep(500);
  }
  log.push(`完成: ${ok}/${changed.length}`);
  fs.writeFileSync(OUT_FILE, log.join('\n'), 'utf8');
  console.log(log.join('\n'));
  if (ok !== changed.length) process.exit(1);
}
main().catch(e => { log.push('ERROR: ' + (e?.stack ?? e)); fs.writeFileSync(OUT_FILE, log.join('\n'), 'utf8'); console.log(log.join('\n')); process.exit(1); });

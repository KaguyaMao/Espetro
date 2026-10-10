// probe-upload.mjs — 探测 daemon 上传不同大小文件的行为(成功/重置/耗时), 用后即删
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
const log = [];

function httpsReq(method, urlStr, headers, body, timeoutMs) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const opts = { method, hostname: url.hostname, port: url.port || 443, path: url.pathname + url.search, headers, rejectUnauthorized: false };
    const req = https.request(opts, (res) => {
      let data = '';
      res.on('data', (c) => (data += c));
      res.on('end', () => resolve({ status: res.statusCode, body: data, cookie: (res.headers['set-cookie'] ?? []).map((l) => l.split(';')[0]).join('; ') }));
    });
    req.on('error', reject);
    req.setTimeout(timeoutMs ?? 90000, () => { req.destroy(new Error('timeout ' + timeoutMs + 'ms')); });
    if (body !== undefined) req.write(body);
    req.end();
  });
}

async function main() {
  const loginRes = await httpsReq('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: USER, password: PASS }));
  const token = String(JSON.parse(loginRes.body).data ?? '');
  const cookie = loginRes.cookie ?? '';
  const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;

  // 构造测试数据(随机字节不可压, 保证真实体积)
  const payloads = [
    { name: 'zz_probe_0_5m.bin', size: 512 * 1024 },
    { name: 'zz_probe_2m.bin', size: 2 * 1024 * 1024 },
    { name: 'zz_probe_8m.bin', size: 8 * 1024 * 1024 },
  ];
  const dir = 'D:/minecraft/modp/Espetro/build/tmp/probe/';
  fs.mkdirSync(dir, { recursive: true });
  const rnd = Buffer.allocUnsafe(256 * 1024);
  for (let i = 0; i < rnd.length; i++) rnd[i] = (i * 31 + (i >> 3)) & 0xff; // 伪随机, 不可压缩

  for (const p of payloads) {
    const local = dir + p.name;
    if (!fs.existsSync(local) || fs.statSync(local).size !== p.size) {
      const fd = fs.openSync(local, 'w');
      let left = p.size;
      while (left > 0) { const chunk = rnd.subarray(0, Math.min(rnd.length, left)); fs.writeSync(fd, chunk); left -= chunk.length; }
      fs.closeSync(fd);
    }
    const buf = fs.readFileSync(local);

    // 1) 创建任务
    const t0 = Date.now();
    let task;
    try {
      const taskRes = await httpsReq('POST', `${PANEL}/api/files/upload?daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&upload_dir=mods&token=${token}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie }, undefined, 30000);
      task = JSON.parse(taskRes.body).data;
    } catch (e) { log.push(p.name + ': 创建任务失败 ' + e.message); continue; }
    const password = String(task?.password ?? '');
    const host = new URL(PANEL).hostname;
    const m = String(task?.addr ?? '').match(/^wss?:\/\/([^/:]+)(:\d+)?/);
    const daemonUrl = 'https://' + host + (m?.[2] ?? '');
    // 2) 直传
    const boundary = '----DSHProbe' + Date.now().toString(16);
    const head = Buffer.from(`--${boundary}\r\nContent-Disposition: form-data; name="file"; filename="${p.name}"\r\nContent-Type: application/octet-stream\r\n\r\n`);
    const tail = Buffer.from(`\r\n--${boundary}--\r\n`);
    const body = Buffer.concat([head, buf, tail]);
    const t1 = Date.now();
    try {
      const upRes = await httpsReq('POST', `${daemonUrl}/upload/${password}?overwrite=true`, { 'X-Requested-With': 'XMLHttpRequest', 'Content-Type': `multipart/form-data; boundary=${boundary}`, 'Content-Length': String(body.length) }, body, 120000);
      const dt = (Date.now() - t1) / 1000;
      log.push(p.name + ' (' + (p.size / 1048576).toFixed(1) + 'MB): HTTP ' + upRes.status + ' body=' + upRes.body.slice(0, 60) + ' 耗时=' + dt.toFixed(1) + 's 速率=' + (p.size / 1048576 / (dt || 0.001)).toFixed(1) + 'MB/s');
    } catch (e) {
      const dt = (Date.now() - t1) / 1000;
      log.push(p.name + ' (' + (p.size / 1048576).toFixed(1) + 'MB): 失败 ' + e.message + ' 耗时=' + dt.toFixed(1) + 's');
    }
    await new Promise((r) => setTimeout(r, 1500));
  }

  // 3) 清理测试文件
  for (const p of payloads) {
    try {
      await httpsReq('DELETE', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target: 'mods/' + p.name }));
    } catch (e) { log.push('清理 ' + p.name + ' 失败: ' + e.message); }
  }
  fs.writeFileSync(OUT_FILE, log.join('\n'), 'utf8');
}
main().catch((e) => { fs.writeFileSync(OUT_FILE, 'ERROR: ' + (e?.stack ?? e), 'utf8'); process.exit(1); });

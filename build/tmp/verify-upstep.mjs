// verify-upstep.mjs — 回读全部 47 个载具 json，确认 UpStep=1.75
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const https = require('https');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const EXPECT = process.argv[2] ?? '1.75';
const OUT = `D:/minecraft/modp/Espetro/build/tmp/verify-upstep-${EXPECT}-out.txt`;

const DR = 'kubejs/data/dragonrise_reforge/sbw/vehicles';
const FCP = 'kubejs/data/fcp/sbw/vehicles';
const DR_FILES = ['ah64', 'bmp3', 'csk181', 'm1126', 'm1128', 'm113', 'm1296', 'm1a2sepv1', 'm1a2sepv2', 'm3a3',
  'mv3_armed', 'mv3_supply', 'mv3', 'sx1_a', 'sx1', 't72b3', 't90mh', 'ural4320_supply', 'ural4320_zu23', 'ural4320',
  'z20', 'zbd04a', 'zbd05', 'zbl08', 'zlt11', 'zsl10', 'ztd05', 'ztz96a', 'ztz99a'];
const FCP_FILES = ['bmp1am', 'bmp2', 'bmp2d', 'bmp2m', 'btr80', 'btr82', 'gaz_tigr_gl', 'gaz_tigr_mg', 'gaz_tigr_rws',
  'matv_9in1', 'matv_crow', 'matv_tow', 'matv', 'stryker_dragoon', 'stryker_m2', 'stryker_mgs', 'stryker_mortar', 't72av'];
const JOBS = [...DR_FILES.map(n => ({ dir: DR, name: n })), ...FCP_FILES.map(n => ({ dir: FCP, name: n }))];

const sleep = ms => new Promise(r => setTimeout(r, ms));
function req(method, urlStr, headers, body, getCookie) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const r = https.request({ method, hostname: url.hostname, port: url.port || 443, path: url.pathname + url.search, headers, rejectUnauthorized: false }, res => {
      let d = ''; res.on('data', c => (d += c));
      res.on('end', () => resolve({ status: res.statusCode, body: d, cookie: getCookie ? (res.headers['set-cookie'] ?? []).map(l => l.split(';')[0]).join('; ') : undefined }));
    });
    r.on('error', reject);
    if (body !== undefined) r.write(body);
    r.end();
  });
}
async function login() {
  const r = await req('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: USER, password: PASS }), true);
  return { token: String(JSON.parse(r.body).data ?? ''), cookie: r.cookie ?? '' };
}

const log = [];
let ctx = await login();
let bad = 0;
const tally = {};
for (const job of JOBS) {
  const rel = `${job.dir}/${job.name}.json`;
  let text = '';
  for (let a = 1; a <= 6; a++) {
    const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${ctx.token}`;
    const res = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': ctx.cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target: rel }));
    try { text = String(JSON.parse(res.body).data ?? ''); } catch { text = ''; }
    if (text.trim().startsWith('{')) break;
    await sleep(2500);
    try { ctx = await login(); } catch { }
  }
  let ok = false, val = '?';
  try {
    const obj = JSON.parse(text);
    val = String(obj.UpStep);
    ok = val === EXPECT;
  } catch (e) { val = 'JSON解析失败'; }
  tally[val] = (tally[val] ?? 0) + 1;
  if (!ok) bad++;
  log.push(`${ok ? 'OK  ' : 'BAD '} ${rel}  UpStep=${val}`);
}
log.push(`统计: ${JSON.stringify(tally)}  异常 ${bad}/${JOBS.length}`);
fs.writeFileSync(OUT, log.join('\n'), 'utf8');
console.log(log.join('\n'));
process.exit(bad === 0 ? 0 : 1);

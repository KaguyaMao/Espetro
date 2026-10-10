// pull-weapon-data.mjs — 从服务端抓取编制在用武器与其附件的 TaCZ 数据文件（含 index）
// 用法: node pull-weapon-data.mjs <本地tacz目录> <编制目录> <输出目录>
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const path = require('path');
const https = require('https');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';

const taczDir = process.argv[2];
const factionDir = process.argv[3];
const OUT = process.argv[4];
fs.mkdirSync(OUT, { recursive: true });

const sleep = (ms) => new Promise(r => setTimeout(r, ms));

/** TaCZ 的 json 允许 // 与 /* *\/ 注释、尾随逗号、": +0.15" 这类写法 */
function lenientParse(text) {
  let out = '', inStr = false, esc = false;
  for (let i = 0; i < text.length; i++) {
    const c = text[i];
    if (inStr) { out += c; if (esc) esc = false; else if (c === '\\') esc = true; else if (c === '"') inStr = false; continue; }
    if (c === '"') { inStr = true; out += c; continue; }
    if (c === '/' && text[i + 1] === '/') { while (i < text.length && text[i] !== '\n') i++; out += '\n'; continue; }
    if (c === '/' && text[i + 1] === '*') { const e = text.indexOf('*/', i + 2); i = e < 0 ? text.length : e + 1; out += '\n'; continue; }
    out += c;
  }
  return JSON.parse(out.replace(/,(\s*[}\]])/g, '$1').replace(/:\s*\+(\d)/g, ': $1'));
}
function req(method, urlStr, headers, body, getCookie) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const r = https.request({ method, hostname: url.hostname, port: url.port || 443, path: url.pathname + url.search, headers, rejectUnauthorized: false }, (res) => {
      let d = '';
      res.on('data', c => (d += c));
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
let { token, cookie } = await login();

// 本地扫描：id -> { pack, ns, relPath(服务器相对路径), kind }
const files = new Map();
for (const pack of fs.readdirSync(taczDir)) {
  const packDir = path.join(taczDir, pack);
  try { if (!fs.statSync(packDir).isDirectory()) continue; } catch { continue; }
  const root = path.join(packDir, 'data');
  if (!fs.existsSync(root)) continue;
  for (const ns of fs.readdirSync(root)) {
    for (const [sub, kind] of [['data/guns', 'gun'], ['data/attachments', 'attach'], ['index/guns', 'index']]) {
      const d = path.join(root, ns, sub);
      if (!fs.existsSync(d)) continue;
      for (const f of fs.readdirSync(d).filter(x => x.endsWith('.json'))) {
        const name = f.replace(/_data\.json$/, '').replace(/\.json$/, '');
        const key = `${kind}|${ns}:${name}`;
        const serverRel = `tacz/${pack}/data/${ns}/${sub}/${f}`;
        files.set(key, { kind, id: `${ns}:${name}`, pack, ns, serverRel, local: path.join(d, f) });
      }
    }
  }
}
console.log(`本地索引到 ${files.size} 个枪/附件/index 文件`);

// 编制里用到的枪与附件
const guns = new Set(), attach = new Set();
for (const f of fs.readdirSync(factionDir).filter(x => x.endsWith('.json'))) {
  const j = JSON.parse(fs.readFileSync(path.join(factionDir, f), 'utf8'));
  for (const cls of Object.values(j.classes || {})) for (const va of Object.values(cls.variants || {}))
    for (const cmd of (va.commands || [])) {
      const s = String(cmd);
      const gm = s.match(/GunId:"([^"]+)"/);
      if (gm) guns.add(gm[1]);
      for (const m of s.matchAll(/AttachmentId:"([^"]+)"/g)) attach.add(m[1]);
    }
}

const want = [];
for (const g of guns) {
  const idx = files.get(`index|${g}`);
  if (idx) want.push(idx);
  const dataId = idx ? (lenientParse(fs.readFileSync(idx.local, 'utf8')).data || `${g}_data`) : `${g}_data`;
  const dataKey = `gun|${dataId.replace(/_data$/, '')}`;
  const data = files.get(dataKey) ?? files.get(`gun|${g}`);
  if (data) want.push(data);
}
for (const a of attach) { const e = files.get(`attach|${a}`); if (e) want.push(e); }

const manifest = [];
console.log(`需要抓取 ${want.length} 个文件（枪 ${guns.size}、附件 ${attach.size}）`);
let ok = 0;
for (const w of want) {
  const local = path.join(OUT, `${w.kind}__${w.id.replace(/[:/]/g, '__')}.json`);
  let text = null;
  for (let a = 0; a < 5 && text === null; a++) {
    const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
    const res = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target: w.serverRel }));
    try {
      const data = String(JSON.parse(res.body).data ?? '');
      if (data.trim().startsWith('{')) text = data; else await sleep(4000);
    } catch { await sleep(4000); }
  }
  if (text === null) { console.log(`FAIL ${w.kind} ${w.id}  (${w.serverRel})`); continue; }
  fs.writeFileSync(local, text, 'utf8');
  manifest.push({ kind: w.kind, id: w.id, pack: w.pack, ns: w.ns, file: path.basename(local), serverRel: w.serverRel, chars: text.length });
  ok++;
  if (ok % 10 === 0) console.log(`  ...已抓取 ${ok}`);
  await sleep(900);
}
fs.writeFileSync(path.join(OUT, 'manifest.json'), JSON.stringify(manifest, null, 2), 'utf8');
console.log(`\n完成 ${ok}/${want.length}，manifest 已写入 ${OUT}/manifest.json`);

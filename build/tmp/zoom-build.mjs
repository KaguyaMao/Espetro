// zoom-build.mjs — 拉取 14 台出场坦克/步战的服务器最新载具 JSON，并把所有武器的 DefaultZoom 改成 8
// 用法: node zoom-build.mjs [--skip-fetch]
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const path = require('path');
const https = require('https');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const OUT = 'zoom';
const TARGET = 8;
const SKIP_FETCH = process.argv.includes('--skip-fetch');

// 编制出场的坦克(6) + 步兵战车(8)
const IDS = [
  'dragonrise_reforge:ztz99a', 'dragonrise_reforge:ztz96a', 'dragonrise_reforge:m1a2sepv1',
  'dragonrise_reforge:m1a2sepv2', 'dragonrise_reforge:t72b3', 'dragonrise_reforge:t90mh',
  'dragonrise_reforge:bmp3', 'dragonrise_reforge:m3a3', 'dragonrise_reforge:zbd04a',
  'dragonrise_reforge:zbd05', 'dragonrise_reforge:zbl08', 'dragonrise_reforge:m1296',
  'fcp:bmp1am', 'fcp:btr82',
];

const sleep = (ms) => new Promise((r) => setTimeout(r, ms));
function req(method, urlStr, headers, body, getCookie, binary) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const r = https.request({ method, hostname: url.hostname, port: url.port || 443, path: url.pathname + url.search, headers, rejectUnauthorized: false }, (res) => {
      const chunks = [];
      res.on('data', (c) => chunks.push(c));
      res.on('end', () => {
        const buf = Buffer.concat(chunks);
        resolve({ status: res.statusCode, buf, body: binary ? null : buf.toString('utf8'), cookie: getCookie ? (res.headers['set-cookie'] ?? []).map((l) => l.split(';')[0]).join('; ') : undefined });
      });
    });
    r.on('error', reject); if (body !== undefined) r.write(body); r.end();
  });
}
const lr = await req('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: USER, password: PASS }), true);
const token = String(JSON.parse(lr.body).data ?? '');
const cookie = lr.cookie ?? '';
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;

async function downloadBin(rel, dest) {
  for (let a = 0; a < 5; a++) {
    try {
      const t = await req('POST', `${PANEL}/api/files/download?${q}&file_name=${encodeURIComponent(rel)}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
      const data = JSON.parse(t.body).data ?? {};
      if (!data.password) { await sleep(2500); continue; }
      const m = String(data.addr ?? '').match(/^wss?:\/\/([^/:]+)(:\d+)?/);
      const daemonUrl = 'https://' + new URL(PANEL).hostname + (m?.[2] ?? '');
      const res = await req('GET', `${daemonUrl}/download/${data.password}/${encodeURIComponent(rel.split('/').pop())}`, {}, undefined, false, true);
      if (res.status === 200 && res.buf.length > 100) { fs.writeFileSync(dest, res.buf); return res.buf; }
    } catch { }
    await sleep(2500);
  }
  return null;
}

/** 找所有「对象形式」的 "Weapons" 武器表（座位里的 "Weapons": [ ... ] 是数组，跳过） */
function matchBrace(text, open) {
  let depth = 0, inStr = false, esc = false;
  for (let j = open; j < text.length; j++) {
    const ch = text[j];
    if (inStr) { if (esc) esc = false; else if (ch === '\\') esc = true; else if (ch === '"') inStr = false; continue; }
    if (ch === '"') { inStr = true; continue; }
    if (ch === '{') depth++;
    else if (ch === '}') { depth--; if (depth === 0) return j; }
  }
  return -1;
}
function findWeaponTables(text) {
  const out = [];
  const re = /"Weapons"\s*:\s*\{/g;
  let m;
  while ((m = re.exec(text)) !== null) {
    const open = text.indexOf('{', m.index + m[0].length - 1);
    const close = matchBrace(text, open);
    if (close > 0) out.push({ open, close });
  }
  return out;
}
/** 列出某个对象体内的直接子对象（key -> [start,end]，end 指向该子对象的 '}'） */
function childObjects(text, open, close) {
  const kids = [];
  let depth = 0, inStr = false, esc = false, keyStart = -1, objStart = -1, lastKey = null;
  for (let j = open + 1; j < close; j++) {
    const ch = text[j];
    if (inStr) { if (esc) esc = false; else if (ch === '\\') esc = true; else if (ch === '"') { inStr = false; if (depth === 0 && keyStart < 0) keyStart = j + 1; } continue; }
    if (ch === '"') { inStr = true; continue; }
    if (ch === '{') {
      if (depth === 0) {
        objStart = j;
        // 往回找最近的字符串作为 key
        const before = text.slice(open + 1, j);
        const km = /"([^"\\]*(?:\\.[^"\\]*)*)"\s*:\s*$/.exec(before);
        lastKey = km ? km[1] : null;
      }
      depth++;
      continue;
    }
    if (ch === '}') {
      depth--;
      if (depth === 0 && objStart >= 0) { kids.push({ key: lastKey, start: objStart, end: j }); objStart = -1; lastKey = null; }
    }
  }
  return kids;
}

fs.mkdirSync(path.join(OUT, 'before'), { recursive: true });
fs.mkdirSync(path.join(OUT, 'after'), { recursive: true });
const manifest = [];
console.log('=== 拉取 + 改写 DefaultZoom -> ' + TARGET + ' ===');
for (const id of IDS) {
  const [ns, name] = id.split(':');
  const rel = `kubejs/data/${ns}/sbw/vehicles/${name}.json`;
  const beforePath = path.join(OUT, 'before', `${ns}__${name}.json`);
  const afterPath = path.join(OUT, 'after', `${ns}__${name}.json`);
  let text = null;
  if (!SKIP_FETCH) {
    const buf = await downloadBin(rel, beforePath);
    if (!buf) { console.log(`  FAIL ${rel}（下载失败）`); manifest.push({ id, rel, status: 'fetch-failed' }); continue; }
    text = buf.toString('utf8');
    await sleep(800);
  } else {
    text = fs.readFileSync(beforePath, 'utf8');
  }
  const tables = findWeaponTables(text);
  if (!tables.length) { console.log(`  FAIL ${id}：找不到对象形式的 Weapons 表`); manifest.push({ id, rel, status: 'no-weapons' }); continue; }
  const kids = [];
  for (const t of tables) kids.push(...childObjects(text, t.open, t.close));
  // 从后往前改，避免位移
  let out = text;
  const changes = [];
  for (const k of kids.slice().sort((a, b) => b.start - a.start)) {
    const body = out.slice(k.start, k.end + 1);
    const m = /"DefaultZoom"\s*:\s*([0-9.]+)/.exec(body);
    if (m) {
      changes.push({ weapon: k.key, from: Number(m[1]), to: TARGET });
      const newBody = body.replace(/"DefaultZoom"\s*:\s*[0-9.]+/, `"DefaultZoom": ${TARGET}`);
      out = out.slice(0, k.start) + newBody + out.slice(k.end + 1);
    } else {
      // 空对象（占位武器，如 NewWeapon{}）跳过：塞字段可能被当成有效武器
      if (/^\{\s*\}$/.test(body)) { changes.push({ weapon: k.key, from: null, to: null, skipped: 'empty' }); continue; }
      changes.push({ weapon: k.key, from: null, to: TARGET });
      const newBody = body.replace(/^\{/, `{\n      "DefaultZoom": ${TARGET},`);
      out = out.slice(0, k.start) + newBody + out.slice(k.end + 1);
    }
  }
  let strict = true, err = null;
  try { JSON.parse(out); } catch (e) { strict = false; err = e.message; }
  fs.writeFileSync(afterPath, out, 'utf8');
  manifest.push({ id, rel, status: strict ? 'ok' : 'INVALID', weapons: changes, err });
  console.log(`  ${strict ? '[OK]' : '[BAD]'} ${id.padEnd(32)} 武器 ${changes.length} 个: ` +
    changes.map((c) => `${c.weapon}${c.from === null ? '(新增)' : ' ' + c.from + '->' + c.to}`).join(', ') + (err ? ' ERR=' + err : ''));
}
fs.writeFileSync(path.join(OUT, 'manifest.json'), JSON.stringify({ target: TARGET, ids: IDS, files: manifest }, null, 2), 'utf8');
const bad = manifest.filter((m) => m.status !== 'ok');
console.log(`\n共处理 ${manifest.length} 个文件，异常 ${bad.length}`);

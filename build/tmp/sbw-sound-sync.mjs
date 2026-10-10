// sbw-sound-sync.mjs — 把本地 GScode 的音效引用同步到服务器 dr 载具文件（保留其余全部内容）
// 用法: node sbw-sound-sync.mjs [--write]
import fs from 'fs';

const LOCAL = 'D:/minecraft/modp/GScode/src/main/resources/data/dragonrise_reforge/sbw/vehicles/';
const SRV = 'D:/minecraft/modp/Espetro/build/tmp/sbw-fresh/dr/';
const OUT = 'D:/minecraft/modp/Espetro/build/tmp/sbw-sound-out/dr/';
const WRITE = process.argv.includes('--write');

// ---------- 位置感知解析 ----------
function parseWithSpans(text) {
  let i = 0;
  const spans = new Map(), members = new Map();
  const skipWs = () => { while (i < text.length && /\s/.test(text[i])) i++; };
  const parseString = () => {
    if (text[i] !== '"') throw new Error('expected string @' + i);
    i++;
    while (i < text.length) {
      if (text[i] === '\\') { i += 2; continue; }
      if (text[i] === '"') { i++; return; }
      i++;
    }
    throw new Error('unterminated string');
  };
  function parseValue(path) {
    skipWs();
    const start = i, c = text[i];
    if (c === '{') {
      i++; skipWs();
      const list = [];
      if (text[i] === '}') { i++; spans.set(path, { start, end: i }); members.set(path, list); return; }
      while (true) {
        skipWs();
        const keyStart = i;
        parseString();
        const key = JSON.parse(text.slice(keyStart, i));
        skipWs();
        if (text[i] !== ':') throw new Error('expected ":" @' + i);
        i++; skipWs();
        parseValue(path + '.' + key);
        list.push({ key, keyStart, valueStart: 0, valueEnd: spans.get(path + '.' + key).end });
        skipWs();
        if (text[i] === ',') { i++; continue; }
        if (text[i] === '}') { i++; break; }
        throw new Error('expected "," or "}" @' + i);
      }
      spans.set(path, { start, end: i }); members.set(path, list); return;
    }
    if (c === '[') {
      i++; skipWs();
      let idx = 0;
      if (text[i] === ']') { i++; spans.set(path, { start, end: i }); return; }
      while (true) {
        skipWs();
        parseValue(path + '[' + idx + ']'); idx++;
        skipWs();
        if (text[i] === ',') { i++; continue; }
        if (text[i] === ']') { i++; break; }
        throw new Error('expected "," or "]" @' + i);
      }
      spans.set(path, { start, end: i }); return;
    }
    if (c === '"') { parseString(); spans.set(path, { start, end: i }); return; }
    const m = /^(true|false|null|-?\d+(?:\.\d+)?(?:[eE][+-]?\d+)?)/.exec(text.slice(i));
    if (!m) throw new Error('bad value @' + i);
    i += m[1].length;
    spans.set(path, { start, end: i });
  }
  parseValue('$');
  return { spans, members };
}

const toPath = (jsonPath) => jsonPath.replace(/^\$\./, '');
const valText = (v) => (typeof v === 'string' ? JSON.stringify(v) : String(v));
const indentOfLine = (text, pos) => (/^[ \t]*/.exec(text.slice(text.lastIndexOf('\n', pos - 1) + 1)))[0];

// 音效叶子：SoundInfo 子树全部 + EngineSound（排除 EngineInfo.EngineSoundVolume、SoundRadius）
function soundLeaves(o, p = '', inSound = false, out = {}) {
  if (o === null || typeof o !== 'object') { if (inSound && p) out[p] = o; return out; }
  if (Array.isArray(o)) { o.forEach((v, i) => soundLeaves(v, `${p}[${i}]`, inSound, out)); return out; }
  for (const [k, v] of Object.entries(o)) {
    const path = p ? `${p}.${k}` : k;
    if (/^EngineInfo\./.test(path)) continue;
    if (/SoundRadius$/.test(path)) continue;
    const sound = inSound || /SoundInfo/.test(k) || k === 'EngineSound';
    soundLeaves(v, path, sound, out);
  }
  return out;
}

const reports = [];
let totalReplace = 0, totalInsert = 0, totalSkip = 0;

for (const f of fs.readdirSync(SRV).filter((x) => x.endsWith('.json')).sort()) {
  if (!fs.existsSync(LOCAL + f)) { reports.push({ file: f, skipped: '本地无此文件' }); continue; }
  const raw = fs.readFileSync(SRV + f, 'utf8');
  const j = JSON.parse(raw);
  const local = JSON.parse(fs.readFileSync(LOCAL + f, 'utf8'));
  const { spans, members } = parseWithSpans(raw);
  const S = soundLeaves(j), L = soundLeaves(local);

  const edits = [], log = [], missingParents = [];
  for (const k of Object.keys(L).sort()) {
    const p = '$.' + k;
    if (S[k] !== undefined && JSON.stringify(S[k]) !== JSON.stringify(L[k])) {
      const sp = spans.get(p);
      if (!sp) { missingParents.push(k); continue; }
      edits.push({ start: sp.start, end: sp.end, text: valText(L[k]), label: k });
      log.push(`改值 ${k}: ${JSON.stringify(S[k])} → ${JSON.stringify(L[k])}`);
      totalReplace++;
    } else if (S[k] === undefined) {
      // 需要新增：父对象必须存在
      const lastDot = k.lastIndexOf('.');
      const parentPath = '$.' + k.slice(0, lastDot);
      const key = k.slice(lastDot + 1);
      const list = members.get(parentPath);
      if (!list) { missingParents.push(k); totalSkip++; continue; }
      const anchor = list[list.length - 1];
      const indent = indentOfLine(raw, anchor.keyStart);
      edits.push({ start: anchor.valueEnd, end: anchor.valueEnd, text: `,\n${indent}${JSON.stringify(key)}: ${valText(L[k])}`, label: k });
      log.push(`新增 ${k} = ${JSON.stringify(L[k])}`);
      totalInsert++;
    }
  }
  if (!edits.length) continue;

  edits.sort((a, b) => b.start - a.start);
  let out = raw;
  for (const e of edits) out = out.slice(0, e.start) + e.text + out.slice(e.end);

  const parsed = JSON.parse(out);
  // 校验：只有音效叶子变化
  const flat = (o, p = '', r = {}) => {
    if (o === null || typeof o !== 'object') { r[p] = o; return r; }
    if (Array.isArray(o)) { o.forEach((v, i) => flat(v, `${p}[${i}]`, r)); return r; }
    for (const [k2, v] of Object.entries(o)) flat(v, p ? `${p}.${k2}` : k2, r);
    return r;
  };
  const A = flat(j), B = flat(parsed);
  const allowed = (k) => /SoundInfo\./.test(k) || /(^|\.)EngineSound$/.test(k);
  const unexpected = [];
  for (const k of new Set([...Object.keys(A), ...Object.keys(B)])) {
    if (JSON.stringify(A[k]) === JSON.stringify(B[k])) continue;
    if (!allowed(k)) unexpected.push(`${k}: ${JSON.stringify(A[k])} → ${JSON.stringify(B[k])}`);
  }
  // 目标值校验
  const S2 = soundLeaves(parsed);
  const bad = Object.keys(L).filter((k) => k in S2 && JSON.stringify(S2[k]) !== JSON.stringify(L[k]));

  reports.push({ file: f, id: j.ID, log, missingParents, unexpected, bad,
    lines: [raw.split('\n').length, out.split('\n').length], chars: [raw.length, out.length] });
  if (WRITE) { fs.mkdirSync(OUT, { recursive: true }); fs.writeFileSync(OUT + f, out, 'utf8'); }
}

for (const r of reports) {
  if (r.skipped) { console.log(`\n▸ ${r.file} 跳过：${r.skipped}`); continue; }
  if (!r.log) continue;
  console.log(`\n▸ ${r.file} (${r.id})  行 ${r.lines[0]}→${r.lines[1]}  字符 ${r.chars[0]}→${r.chars[1]}`);
  for (const l of r.log) console.log('   · ' + l);
  if (r.missingParents.length) console.log(`   ⚠ 服务器已无此容器，跳过 ${r.missingParents.length} 项: ${r.missingParents.slice(0, 4).join(', ')}${r.missingParents.length > 4 ? ' …' : ''}`);
  if (r.unexpected.length) console.log(`   !! 白名单外改动 ${r.unexpected.length} 处: ${r.unexpected.slice(0, 3).join(' | ')}`);
  if (r.bad.length) console.log(`   !! 目标值未生效: ${r.bad.join(', ')}`);
}
console.log(`\n合计：改值 ${totalReplace} 处，新增 ${totalInsert} 处，因容器缺失跳过 ${totalSkip} 处`);
console.log(WRITE ? '已写出 ' + OUT : '（演练，未写盘）');

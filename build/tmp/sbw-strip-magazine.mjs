// sbw-strip-magazine.mjs — 删除所有载具"连发"武器的 Magazine / EmptyReloadTime 字段
// 判定：DefaultFireMode 或 AvailableFireModes 含 "Auto"
// 用法: node sbw-strip-magazine.mjs [--write]
import fs from 'fs';

const ROOTS = [
  ['fcp', 'D:/minecraft/modp/Espetro/build/tmp/sbw-all/fcp/'],
  ['dr', 'D:/minecraft/modp/Espetro/build/tmp/sbw-all/dr/']
];
const OUT = 'D:/minecraft/modp/Espetro/build/tmp/sbw-strip-out/';
const WRITE = process.argv.includes('--write');
const FIELDS = ['Magazine', 'EmptyReloadTime'];

// ---------- 位置感知解析 ----------
function parseWithSpans(text) {
  let i = 0;
  const spans = new Map();
  const members = new Map();
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
    const start = i;
    const c = text[i];
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
        const valueStart = i;
        parseValue(path + '.' + key);
        list.push({ key, keyStart, valueStart, valueEnd: spans.get(path + '.' + key).end });
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

const isAuto = (w) => /auto/i.test([w.DefaultFireMode, w.AvailableFireModes].filter((x) => typeof x === 'string').join(','));

const summary = [];
let totalRemoved = 0, totalFiles = 0, totalSkippedNonAuto = 0;

for (const [ns, dir] of ROOTS) {
  for (const f of fs.readdirSync(dir).filter((x) => x.endsWith('.json')).sort()) {
    const raw = fs.readFileSync(dir + f, 'utf8');
    const j = JSON.parse(raw);
    const { spans, members } = parseWithSpans(raw);

    const cuts = [];     // {start,end,label}
    const removedLabels = [];
    const keptNonAuto = [];

    for (const [wn, w] of Object.entries(j.Weapons ?? {})) {
      if (w === null || typeof w !== 'object') continue;
      const auto = isAuto(w);
      const has = FIELDS.filter((k) => w[k] !== undefined);
      if (!auto) { if (has.length) keptNonAuto.push(`${wn}(${w.DefaultFireMode ?? '-'}: ${has.join('/')})`); continue; }
      for (const k of has) {
        const mem = (members.get('$.Weapons.' + wn) ?? []).find((m) => m.key === k);
        if (!mem) continue;
        const lineStart = raw.lastIndexOf('\n', mem.keyStart - 1) + 1;
        let lineEnd = raw.indexOf('\n', mem.valueEnd);
        lineEnd = lineEnd === -1 ? raw.length : lineEnd + 1;
        cuts.push({ start: lineStart, end: lineEnd, label: `Weapons.${wn}.${k}` });
        removedLabels.push(`${wn}.${k}`);
      }
      // 弹种 override 内的同名字段
      for (const [idx, e] of (Array.isArray(w.AmmoType) ? w.AmmoType : []).entries()) {
        if (!e?.Override) continue;
        for (const k of FIELDS) {
          if (e.Override[k] === undefined) continue;
          const mem = (members.get(`$.Weapons.${wn}.AmmoType[${idx}].Override`) ?? []).find((m) => m.key === k);
          if (!mem) continue;
          const lineStart = raw.lastIndexOf('\n', mem.keyStart - 1) + 1;
          let lineEnd = raw.indexOf('\n', mem.valueEnd);
          lineEnd = lineEnd === -1 ? raw.length : lineEnd + 1;
          cuts.push({ start: lineStart, end: lineEnd, label: `Weapons.${wn}.弹种[${idx}].${k}` });
          removedLabels.push(`${wn}.弹种[${idx}].${k}`);
        }
      }
    }

    if (!cuts.length) { summary.push({ file: `${ns}/${f}`, removed: [], keptNonAuto, unchanged: true }); continue; }

    cuts.sort((a, b) => b.start - a.start);
    let out = raw;
    for (const c of cuts) out = out.slice(0, c.start) + out.slice(c.end);
    // 删除末位成员后可能留下悬空逗号
    const beforeClean = out;
    out = out.replace(/,(\s*[\]}])/g, '$1');
    const commaFixed = out !== beforeClean;

    const parsed = JSON.parse(out);
    const flat = (o, p = '') => {
      const r = {};
      if (o === null || typeof o !== 'object') { r[p] = o; return r; }
      if (Array.isArray(o)) { o.forEach((v, i) => Object.assign(r, flat(v, `${p}[${i}]`))); return r; }
      for (const [k, v] of Object.entries(o)) Object.assign(r, flat(v, p ? `${p}.${k}` : k));
      return r;
    };
    const A = flat(j), B = flat(parsed);
    const removed = Object.keys(A).filter((k) => !(k in B));
    const added = Object.keys(B).filter((k) => !(k in A));
    const changed = Object.keys(A).filter((k) => k in B && JSON.stringify(A[k]) !== JSON.stringify(B[k]));
    const allowed = (k) => /^Weapons\.[^.]+\.(Magazine|EmptyReloadTime)$/.test(k)
      || /^Weapons\.[^.]+\.AmmoType\[\d+\]\.Override\.(Magazine|EmptyReloadTime)$/.test(k);

    const problems = [];
    if (removed.some((k) => !allowed(k))) problems.push('误删: ' + removed.filter((k) => !allowed(k)).join(','));
    if (added.length) problems.push('意外新增: ' + added.join(','));
    if (changed.length) problems.push('意外改值: ' + changed.join(','));

    totalRemoved += removed.length;
    totalFiles++;
    totalSkippedNonAuto += keptNonAuto.length;
    summary.push({ file: `${ns}/${f}`, removed, keptNonAuto, commaFixed, problems, id: j.ID, lines: [raw.split('\n').length, out.split('\n').length] });

    if (WRITE) { fs.mkdirSync(OUT + ns + '/', { recursive: true }); fs.writeFileSync(OUT + ns + '/' + f, out, 'utf8'); }
  }
}

for (const s of summary) {
  if (s.unchanged) continue;
  console.log(`\n▸ ${s.file}  ${s.id}  行 ${s.lines[0]}→${s.lines[1]}${s.commaFixed ? '  (清理了悬空逗号)' : ''}`);
  console.log(`   删除 ${s.removed.length} 个字段: ${s.removed.join(', ')}`);
  if (s.keptNonAuto.length) console.log(`   保留（非连发）: ${s.keptNonAuto.join(', ')}`);
  if (s.problems?.length) console.log(`   !! 校验问题: ${s.problems.join(' | ')}`);
}
const problems = summary.filter((s) => s.problems?.length).length;
console.log(`\n合计: 处理 ${totalFiles} 个文件, 删除 ${totalRemoved} 个字段; 保留的非连发弹夹字段 ${totalSkippedNonAuto} 个`);
console.log(`校验问题文件: ${problems} 个`);
console.log(WRITE ? '已写出 ' + OUT : '（演练，未写盘）');

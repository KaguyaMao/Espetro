import fs from 'node:fs';

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
const flat = (o, p = '', out = {}) => {
  if (o === null || typeof o !== 'object') { out[p] = o; return out; }
  if (Array.isArray(o)) { o.forEach((v, i) => flat(v, `${p}[${i}]`, out)); return out; }
  for (const k of Object.keys(o)) flat(o[k], p ? `${p}.${k}` : k, out);
  return out;
};

const [a, b, label] = process.argv.slice(2);
const ja = flat(lenientParse(fs.readFileSync(a, 'utf8')));
const jb = flat(lenientParse(fs.readFileSync(b, 'utf8')));
const keys = new Set([...Object.keys(ja), ...Object.keys(jb)]);
let n = 0;
console.log(`═══ ${label}`);
for (const k of [...keys].sort()) {
  const va = JSON.stringify(ja[k]), vb = JSON.stringify(jb[k]);
  if (va !== vb) { n++; console.log(`   ${k}: 服务端=${va}  本地=${vb}`); }
}
console.log(`   差异 ${n} 处`);

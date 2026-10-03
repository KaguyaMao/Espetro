// sbw-sound-categories.mjs — 把音效相关差异分类统计（引用/数值/引擎音量等）
import fs from 'fs';

const L = 'D:/minecraft/modp/GScode/src/main/resources/data/dragonrise_reforge/sbw/vehicles/';
const S = 'D:/minecraft/modp/Espetro/build/tmp/sbw-strip-out/dr/';

const cat = (k) => {
  if (/EngineInfo\.EngineSoundVolume/.test(k)) return '引擎音量(数值)';
  if (/SoundRadius/.test(k)) return 'SoundRadius(数值)';
  if (/SoundInfo\.VehicleReloadSoundTime/.test(k)) return 'SoundInfo内计时(数值)';
  if (/SoundInfo\./.test(k)) return 'SoundInfo引用(字符串)';
  if (/EngineSound/.test(k)) return 'EngineSound引用(字符串)';
  return '其他音效键';
};

function col(o, p = '', inS = false, out = {}) {
  if (o === null || typeof o !== 'object') { if (inS && p) out[p] = o; return out; }
  if (Array.isArray(o)) { o.forEach((v, i) => col(v, `${p}[${i}]`, inS, out)); return out; }
  for (const [k, v] of Object.entries(o)) col(v, p ? `${p}.${k}` : k, inS || /sound/i.test(k), out);
  return out;
}

const tally = new Map();
const samples = new Map();
const filesTouched = new Set();
let total = 0;
for (const f of fs.readdirSync(S).filter((x) => x.endsWith('.json')).sort()) {
  const s = col(JSON.parse(fs.readFileSync(S + f, 'utf8')));
  const l = col(JSON.parse(fs.readFileSync(L + f, 'utf8')));
  for (const k of new Set([...Object.keys(s), ...Object.keys(l)])) {
    if (JSON.stringify(s[k]) === JSON.stringify(l[k])) continue;
    total++;
    filesTouched.add(f);
    const c = cat(k);
    tally.set(c, (tally.get(c) ?? 0) + 1);
    if (!samples.has(c)) samples.set(c, []);
    if (samples.get(c).length < 3) samples.get(c).push(`${f}:${k}  ${JSON.stringify(s[k])} → ${JSON.stringify(l[k])}`);
  }
}
console.log(`涉及文件 ${filesTouched.size} 个，音效相关差异合计 ${total} 处\n`);
for (const [c, n] of [...tally.entries()].sort((a, b) => b[1] - a[1])) {
  console.log(`【${c}】${n} 处`);
  for (const s of samples.get(c)) console.log('    ' + s);
}

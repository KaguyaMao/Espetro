// png-info.mjs — 读取 PNG 尺寸（无依赖）
import fs from 'fs';

const dirs = process.argv.slice(2);
for (const d of dirs) {
  console.log('=== ' + d);
  for (const f of fs.readdirSync(d).filter((x) => x.toLowerCase().endsWith('.png')).sort()) {
    const b = fs.readFileSync(d + '/' + f);
    const sig = b.slice(0, 8).toString('hex');
    const ok = sig === '89504e470d0a1a0a';
    let w = '?', h = '?', bitDepth = '?', colorType = '?';
    if (ok && b.slice(12, 16).toString('ascii') === 'IHDR') {
      w = b.readUInt32BE(16); h = b.readUInt32BE(20);
      bitDepth = b[24]; colorType = b[25];
    }
    console.log(`${f.padEnd(36)} ${String(w).padStart(4)}x${String(h).padEnd(4)} bit=${bitDepth} colorType=${colorType} ${b.length}B`);
  }
}

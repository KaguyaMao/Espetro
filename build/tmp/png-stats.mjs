// png-stats.mjs — 无依赖 PNG 解码与统计（仅支持 8bit 非隔行 RGBA/RGB/灰度）
import fs from 'fs';
import zlib from 'zlib';

function decode(buf) {
  if (buf.slice(0, 8).toString('hex') !== '89504e470d0a1a0a') throw new Error('not png');
  let off = 8, w = 0, h = 0, bitDepth = 0, colorType = 0, interlace = 0;
  const idat = [];
  let palette = null, trns = null;
  while (off < buf.length) {
    const len = buf.readUInt32BE(off);
    const type = buf.slice(off + 4, off + 8).toString('ascii');
    const data = buf.slice(off + 8, off + 8 + len);
    if (type === 'IHDR') {
      w = data.readUInt32BE(0); h = data.readUInt32BE(4);
      bitDepth = data[8]; colorType = data[9]; interlace = data[12];
    } else if (type === 'IDAT') idat.push(data);
    else if (type === 'PLTE') palette = data;
    else if (type === 'tRNS') trns = data;
    else if (type === 'IEND') break;
    off += 12 + len;
  }
  if (bitDepth !== 8) throw new Error('only 8-bit supported, got ' + bitDepth);
  if (interlace !== 0) throw new Error('interlaced not supported');
  const channels = colorType === 6 ? 4 : colorType === 2 ? 3 : colorType === 0 ? 1 : colorType === 4 ? 2 : 0;
  if (channels === 0) throw new Error('unsupported colorType ' + colorType + (palette ? ' (palette)' : ''));
  const raw = zlib.inflateSync(Buffer.concat(idat));
  const stride = w * channels;
  const out = Buffer.alloc(h * stride);
  let pos = 0;
  for (let y = 0; y < h; y++) {
    const filter = raw[pos++];
    const line = raw.slice(pos, pos + stride); pos += stride;
    const cur = out.slice(y * stride, (y + 1) * stride);
    const prev = y > 0 ? out.slice((y - 1) * stride, y * stride) : Buffer.alloc(stride);
    for (let x = 0; x < stride; x++) {
      const a = x >= channels ? cur[x - channels] : 0;
      const b = prev[x];
      const c = x >= channels ? prev[x - channels] : 0;
      let v = line[x];
      if (filter === 1) v += a;
      else if (filter === 2) v += b;
      else if (filter === 3) v += (a + b) >> 1;
      else if (filter === 4) {
        const p = a + b - c, pa = Math.abs(p - a), pb = Math.abs(p - b), pc = Math.abs(p - c);
        v += (pa <= pb && pa <= pc) ? a : (pb <= pc ? b : c);
      }
      cur[x] = v & 0xff;
    }
  }
  return { w, h, channels, px: out };
}

function stats(file) {
  const buf = fs.readFileSync(file);
  const img = decode(buf);
  const { w, h, channels, px } = img;
  let opaque = 0, semi = 0, sumR = 0, sumG = 0, sumB = 0;
  let minX = w, minY = h, maxX = -1, maxY = -1;
  let colored = 0, whiteish = 0;
  let sumCX = 0, sumCY = 0;
  const hist = new Map();
  for (let y = 0; y < h; y++) {
    for (let x = 0; x < w; x++) {
      const i = (y * w + x) * channels;
      const r = px[i], g = px[i + 1] ?? r, b = px[i + 2] ?? r;
      const a = channels === 4 ? px[i + 3] : 255;
      if (a === 0) continue;
      if (a < 250) semi++;
      if (a > 32) {
        opaque++;
        if (x < minX) minX = x; if (x > maxX) maxX = x;
        if (y < minY) minY = y; if (y > maxY) maxY = y;
        sumCX += x; sumCY += y;
        sumR += r; sumG += g; sumB += b;
        const mx = Math.max(r, g, b), mn = Math.min(r, g, b);
        if (mx - mn > 24) colored++; else whiteish++;
        const key = `${r >> 5},${g >> 5},${b >> 5}`;
        hist.set(key, (hist.get(key) || 0) + 1);
      }
    }
  }
  const top = [...hist.entries()].sort((a, b) => b[1] - a[1]).slice(0, 3)
    .map(([k, n]) => `rgb(${k.split(',').map((v) => v * 32 + 16).join(',')})×${(n / Math.max(1, opaque) * 100).toFixed(0)}%`);
  return {
    size: `${w}x${h}`, bytes: buf.length,
    alpha: `${(opaque / (w * h) * 100).toFixed(1)}%`, semiPx: semi,
    mean: `${(sumR / Math.max(1, opaque)).toFixed(0)},${(sumG / Math.max(1, opaque)).toFixed(0)},${(sumB / Math.max(1, opaque)).toFixed(0)}`,
    coloredPct: `${(colored / Math.max(1, opaque) * 100).toFixed(0)}%`,
    bbox: `${minX},${minY}-${maxX},${maxY}`,
    center: `${(sumCX / Math.max(1, opaque)).toFixed(1)},${(sumCY / Math.max(1, opaque)).toFixed(1)}`,
    top
  };
}

for (const f of process.argv.slice(2)) {
  try {
    const s = stats(f);
    const name = f.replace(/\\/g, '/').split('/').pop();
    console.log(`${name.padEnd(34)} ${s.size} ${String(s.bytes).padStart(6)}B alpha=${s.alpha.padStart(6)} mean=${s.mean.padStart(11)} color=${s.coloredPct.padStart(4)} bbox=${s.bbox.padEnd(18)} c=${s.center.padEnd(13)} ${s.top.join(' ')}`);
  } catch (e) {
    console.log(`${f}: ERROR ${e.message}`);
  }
}

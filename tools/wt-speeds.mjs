// 从 WT wiki 抓取指定载具的 RB / AB 极速，输出 build/tmp/wt/speeds.json
// 用法：node tools/wt-speeds.mjs ussr_bmp_3 cn_ztz_96a ...
import { mkdir, writeFile } from 'node:fs/promises';
import path from 'node:path';

const OUT_DIR = path.join(process.cwd(), 'build', 'tmp', 'wt');
const UA = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) espetro-wiki-tools';

const slugs = process.argv.slice(2);
if (slugs.length === 0) {
  console.error('用法: node tools/wt-speeds.mjs <slug...>');
  process.exit(1);
}

const NUM = '(\\d+(?:\\.\\d+)?)';

// Max speed 区块里 forward / backward 各有一段；倒车有时只给一个数字（不区分 AB/RB）
function parseSpeeds(html) {
  const at = html.search(/Max speed<\/span>/i);
  if (at < 0) return null;
  const chunk = html.slice(at, at + 8000);
  const fwdAt = chunk.search(/Forward<\/span>/i);
  const bwdAt = chunk.search(/Backward<\/span>/i);
  if (fwdAt < 0 || bwdAt < 0) return null;

  const seg = (start, end) => chunk.slice(start, end);
  const numsIn = (s) => {
    // 取 span 里的纯数字，避免把 "km/h" 或样式里的数字算进来
    const out = [];
    const re = /<span[^>]*>\s*(?:<span[^>]*>)?(\d+(?:\.\d+)?)\s*<\/span>/g;
    for (const m of s.matchAll(re)) out.push(Number(m[1]));
    return out;
  };

  const fwd = numsIn(seg(fwdAt, bwdAt));
  // 倒车段落只取到下一个区块标题，避免把「功重比」等数字算进来
  const nextBlock = chunk.slice(bwdAt).search(/game-unit_chars-block|Power-to-weight/i);
  const bwdEnd = nextBlock > 0 ? bwdAt + nextBlock : Math.min(bwdAt + 600, chunk.length);
  const bwd = numsIn(seg(bwdAt, bwdEnd));
  if (fwd.length === 0 || bwd.length === 0) return null;
  return {
    forwardRb: fwd[0],
    forwardAb: fwd.length > 1 ? fwd[1] : fwd[0],
    backwardRb: bwd[0],
    backwardAb: bwd.length > 1 ? bwd[1] : bwd[0],
  };
}

const titleRe = /<h1[^>]*>([\s\S]{0,200}?)<\/h1>/i;

function decodeEntities(s) {
  return s
    .replace(/&amp;/g, '&')
    .replace(/&lt;/g, '<')
    .replace(/&gt;/g, '>')
    .replace(/&quot;/g, '"')
    .replace(/&#0?39;/g, "'")
    .replace(/&nbsp;/g, ' ')
    .trim();
}

const results = {};
try {
  const prev = JSON.parse(await (await import('node:fs/promises')).readFile(
    path.join(OUT_DIR, 'speeds.json'), 'utf8'));
  Object.assign(results, prev);
} catch {
  // 首次运行没有旧文件
}

for (const slug of slugs) {
  const url = `https://wiki.warthunder.com/unit/${slug}`;
  try {
    const res = await fetch(url, { headers: { 'user-agent': UA } });
    if (!res.ok) {
      results[slug] = { error: `HTTP ${res.status}` };
      console.log(`${slug}: HTTP ${res.status}`);
      continue;
    }
    const html = await res.text();
    const speeds = parseSpeeds(html);
    const title = html.match(titleRe);
    if (!speeds) {
      results[slug] = { error: '未找到 Max speed 段', url };
      console.log(`${slug}: 解析失败`);
      continue;
    }
    results[slug] = {
      title: title ? decodeEntities(title[1].replace(/<[^>]+>/g, '')) : slug,
      ...speeds,
      url,
    };
    console.log(
      `${slug}: ${results[slug].title} | RB ${speeds.forwardRb}/${speeds.backwardRb} km/h | AB ${speeds.forwardAb}/${speeds.backwardAb} km/h`
    );
  } catch (err) {
    results[slug] = { error: String(err) };
    console.log(`${slug}: ${err}`);
  }
  await new Promise((r) => setTimeout(r, 600));
}

await mkdir(OUT_DIR, { recursive: true });
await writeFile(path.join(OUT_DIR, 'speeds.json'), JSON.stringify(results, null, 2), 'utf8');
console.log(`\n写入 ${path.join(OUT_DIR, 'speeds.json')}`);

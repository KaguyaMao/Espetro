// 抓 WT wiki 地面载具索引，输出 build/tmp/wt/units.json
// 用法：node tools/wt-index.mjs
import { mkdtemp, mkdir, writeFile } from 'node:fs/promises';
import { tmpdir } from 'node:os';
import path from 'node:path';

const OUT_DIR = path.join(process.cwd(), 'build', 'tmp', 'wt');
const UA = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) espetro-wiki-tools';

async function get(url) {
  const res = await fetch(url, { headers: { 'user-agent': UA } });
  if (!res.ok) throw new Error(`${res.status} ${url}`);
  return res.text();
}

function decodeEntities(s) {
  return s
    .replace(/&amp;/g, '&')
    .replace(/&lt;/g, '<')
    .replace(/&gt;/g, '>')
    .replace(/&quot;/g, '"')
    .replace(/&#39;/g, "'")
    .replace(/&nbsp;/g, ' ');
}

const html = await get('https://wiki.warthunder.com/ground');
const units = new Map();

// <div class="wt-tree_item" data-unit-id="us_m2a4">…<div class="wt-tree_item-text"><span>M2A4</span></div>
const itemRe =
  /data-unit-id="([a-z0-9_]+)"[\s\S]{0,800}?wt-tree_item-text"><span>([\s\S]*?)<\/span>/g;
for (const m of html.matchAll(itemRe)) {
  const slug = m[1];
  const label = decodeEntities(m[2].replace(/<[^>]+>/g, '').trim());
  if (!label) continue;
  if (!units.has(slug)) units.set(slug, label);
}

await mkdir(OUT_DIR, { recursive: true });
const list = [...units.entries()]
  .map(([slug, label]) => ({ slug, label: decodeEntities(label) }))
  .sort((a, b) => a.slug.localeCompare(b.slug));
await writeFile(path.join(OUT_DIR, 'units.json'), JSON.stringify(list, null, 2), 'utf8');
console.log(`units: ${list.length}`);
console.log(list.slice(0, 5).map((u) => `${u.slug} | ${u.label}`).join('\n'));

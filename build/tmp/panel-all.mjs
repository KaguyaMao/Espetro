// 抓取面板全部 JS chunk 并搜索下载实现
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const https = require('https');
const fs = require('fs');

function get(path) {
  return new Promise((resolve, reject) => {
    https.get({ host: 'www.derpydoge.fun', port: 20000, path, rejectUnauthorized: false, headers: { 'User-Agent': 'Mozilla/5.0' } }, (res) => {
      let d = '';
      res.on('data', (c) => d += c);
      res.on('end', () => resolve(d));
    }).on('error', reject);
  });
}

(async () => {
  const html = await get('/');
  const refs = [...html.matchAll(/(?:src|href)="\.\/assets\/([^"]+)"/g)].map((m) => m[1]);
  console.log('assets:', refs);
  let all = '';
  for (const r of refs) {
    try {
      const js = await get('/assets/' + r);
      all += js;
      console.log('fetched', r, js.length);
    } catch (e) {
      console.log('fail', r, e.message);
    }
  }
  fs.writeFileSync('D:/minecraft/modp/Espetro/build/tmp/panel-all.js', all);
  for (const kw of ['files/download', 'downloadFile', 'remoteMappings', 'wss://', '20443', 'files/read']) {
    const i = all.indexOf(kw);
    console.log(kw, '->', i);
    if (i >= 0) {
      const seg = all.substring(Math.max(0, i - 2500), i + 2500);
      fs.writeFileSync('D:/minecraft/modp/Espetro/build/tmp/panel-seg-' + kw.replace(/[^a-z]/gi, '') + '.txt', seg);
      console.log('  saved segment');
    }
  }
})().catch((e) => { console.error(e); process.exit(1); });

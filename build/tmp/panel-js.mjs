// 下载面板主 JS 并搜索 downloadFile / files/download 实现
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
  const js = await get('/assets/index-52964147.js');
  console.log('index js len:', js.length);
  fs.writeFileSync('D:/minecraft/modp/Espetro/build/tmp/panel-index.js', js);
  const idx = js.indexOf('files/download');
  console.log('files/download idx:', idx);
  if (idx >= 0) {
    const seg = js.substring(Math.max(0, idx - 3000), idx + 3000);
    console.log(seg);
  }
  // 也搜 downloadFile
  const i2 = js.indexOf('downloadFile');
  console.log('downloadFile idx:', i2);
  if (i2 >= 0) console.log(js.substring(Math.max(0, i2 - 1500), i2 + 1500));
})().catch((e) => { console.error(e); process.exit(1); });

// 抓取 mount chunk 及其引用的业务 chunk
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
  const mount = await get('/assets/mount-851da5ee.js');
  console.log('mount len:', mount.length);
  fs.writeFileSync('D:/minecraft/modp/Espetro/build/tmp/panel-mount.js', mount);
  // 找动态 chunk
  const chunks = [...mount.matchAll(/["']\.\/([^"']+)\.js["']/g)].map((m) => m[1] + '.js');
  console.log('chunks:', chunks.slice(0, 30));
  let all = mount;
  for (const c of chunks.slice(0, 60)) {
    try {
      const js = await get('/assets/' + c);
      all += '\n' + js;
      console.log('fetched', c, js.length);
    } catch (e) {
      console.log('fail', c, e.message);
    }
  }
  fs.writeFileSync('D:/minecraft/modp/Espetro/build/tmp/panel-mount-all.js', all);
  for (const kw of ['/api/files', 'files/download', 'downloadFile', 'remoteMappings', '20443', 'wss://', 'fileDownload', 'download']) {
    const i = all.indexOf(kw);
    console.log(kw, '->', i);
    if (i >= 0 && kw !== 'download') {
      fs.writeFileSync('D:/minecraft/modp/Espetro/build/tmp/panel-seg2-' + kw.replace(/[^a-z]/gi, '') + '.txt', all.substring(Math.max(0, i - 2000), i + 2500));
      console.log('  saved');
    }
  }
})().catch((e) => { console.error(e); process.exit(1); });

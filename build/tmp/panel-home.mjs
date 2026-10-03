// 抓取面板首页 JS 资源
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const https = require('https');
https.get({ host: 'www.derpydoge.fun', port: 20000, path: '/', rejectUnauthorized: false, headers: { 'User-Agent': 'Mozilla/5.0' } }, (res) => {
  let data = '';
  res.on('data', (c) => data += c);
  res.on('end', () => {
    console.log('status:', res.statusCode, 'len:', data.length);
    const m = data.match(/src="([^"]+\.js[^"]*)"/g);
    console.log(m ? m.slice(0, 30).join('\n') : 'no js src');
    const l = data.match(/href="([^"]+\.js[^"]*)"/g);
    console.log('href:', l ? l.slice(0, 10).join('\n') : 'none');
  });
}).on('error', (e) => console.log('ERR', e.message));

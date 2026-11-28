const fs = require('fs');
const path = require('path');
const sharp = require('C:/Users/Administrator/.cache/codex-runtimes/codex-primary-runtime/dependencies/node/node_modules/sharp');
(async () => {
  const dir=path.join(__dirname,'assets');
  for (const f of fs.readdirSync(dir).filter(f=>f.endsWith('.svg'))) {
    await sharp(path.join(dir,f)).png().toFile(path.join(dir,f.replace(/\.svg$/,'.png')));
  }
  console.log('Rendered SVG assets to PNG.');
})();

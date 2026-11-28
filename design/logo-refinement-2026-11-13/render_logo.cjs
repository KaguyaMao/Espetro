const fs = require('fs');
const path = require('path');
const sharp = require('C:/Users/Administrator/.cache/codex-runtimes/codex-primary-runtime/dependencies/node/node_modules/sharp');
async function main() {
  for (const file of fs.readdirSync(__dirname).filter(v=>v.endsWith('.svg'))) {
    await sharp(path.join(__dirname,file)).png().toFile(path.join(__dirname,file.replace('.svg','.png')));
  }
  console.log('Rendered SVG masters and layers');
}
main().catch(e=>{ console.error(e); process.exit(1); });

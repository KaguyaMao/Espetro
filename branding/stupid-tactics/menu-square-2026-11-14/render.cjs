const fs = require('node:fs');
const path = require('node:path');
const sharp = require('C:/Users/Administrator/.cache/codex-runtimes/codex-primary-runtime/dependencies/node/node_modules/sharp');

(async () => {
  const source = fs.readFileSync(path.join(__dirname, 'stupid-tactics-square.svg'));
  for (const [name, size] of [
    ['master-2048.png', 2048],
    ['avatar-1024.png', 1024],
    ['avatar-512.png', 512],
    ['client-icon.png', 256],
    ['server-icon.png', 64],
  ]) {
    await sharp(source, { density: Math.max(96, Math.round(72 * size / 1024)) })
      .resize(size, size).flatten({ background: '#000000' })
      .png({ compressionLevel: 9 }).toFile(path.join(__dirname, name));
  }
  console.log('Rendered master, avatar, client and 64px server exports.');
})().catch(error => { console.error(error); process.exitCode = 1; });

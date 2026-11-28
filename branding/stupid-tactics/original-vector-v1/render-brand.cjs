const fs = require('node:fs');
const path = require('node:path');
const sharp = require('sharp');

const root = __dirname;
const assets = [
  ['preview.svg', 'preview.png', 1400],
  ['preview-dark.svg', 'preview-dark.png', 1400],
  ['hunaobattlefield-primary.svg', 'hunaobattlefield-primary-transparent.png', 2480],
  ['hunaobattlefield-reverse.svg', 'hunaobattlefield-reverse-transparent.png', 2480],
  ['hunaobattlefield-avatar.svg', 'avatar.png', 896],
  ['hunaobattlefield-avatar.svg', 'server-icon.png', 64],
  ['hunaobattlefield-symbol.svg', 'symbol-check-32.png', 32],
];

async function render() {
  for (const [source, target, width] of assets) {
    const bytes = fs.readFileSync(path.join(root, source));
    await sharp(bytes, { density: 144 })
      .resize({ width })
      .png({ compressionLevel: 9 })
      .toFile(path.join(root, target));
  }
  console.log('Rendered original mark previews, transparent masters and icon exports.');
}
render().catch(error => { console.error(error); process.exitCode = 1; });

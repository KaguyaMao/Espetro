const fs = require('node:fs');
const path = require('node:path');
const sharp = require('sharp');

const directory = __dirname;
async function render(source, target, width) {
  const input = fs.readFileSync(path.join(directory, source));
  await sharp(input, { density: 144 })
    .resize({ width })
    .png({ compressionLevel: 9 })
    .toFile(path.join(directory, target));
}

async function main() {
  await render('preview.svg', 'preview.png', 1600);
  await render('stupid-tactics-hunaobattlefield.svg', 'stupid-tactics-hunaobattlefield-transparent.png', 2800);
  await render('stupid-tactics-hunaobattlefield-dark-ink.svg', 'stupid-tactics-hunaobattlefield-dark-ink-transparent.png', 2800);
  await render('stupid-tactics-small.svg', 'small-size-check.png', 360);
  console.log('Rendered preview, 2 transparent 2x masters, and a 360px legibility check.');
}
main().catch(error => { console.error(error); process.exitCode = 1; });

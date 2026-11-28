"""Compose the existing title and a menu photograph; no image generation model."""
import base64
from io import BytesIO
import json
from pathlib import Path
import xml.etree.ElementTree as ET

from PIL import Image, ImageDraw

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[2]
BACKGROUND = ROOT / 'src/main/resources/assets/espetro/ui-bundle/files/config/fancymenu/slideshows/myslideshow/images/image_6.png'
TITLE = ROOT / 'design/fancymenu-glass-2026-11-14/assets/logo.svg'


def main():
    with Image.open(BACKGROUND) as source:
        side = min(source.size)
        left = round((source.width - side) * 0.25)
        top = round((source.height - side) * 0.5)
        crop = (left, top, left + side, top + side)
        background = source.crop(crop).convert('RGBA')
        opaque = Image.new('RGBA', background.size, (0, 0, 0, 255))
        opaque.alpha_composite(background)
        encoded = BytesIO()
        opaque.convert('RGB').save(encoded, format='PNG', optimize=True)
    original = TITLE.read_text(encoding='utf-8')
    # Reuse the exact outlined paths; no substitute fonts or redrawn symbol.
    title_paths = original.split('>', 1)[1].rsplit('</svg>', 1)[0]
    x, width = 82, 860
    scale = width / 1170
    y = (1024 - 570 * scale) / 2
    svg = f'''<svg xmlns="http://www.w3.org/2000/svg" width="1024" height="1024" viewBox="0 0 1024 1024">
<title>胡闹战场 · Stupid Tactics Minecraft</title>
<desc>Existing main-menu title over the tank-and-pagoda slideshow photograph with a 56% black scrim.</desc>
<image width="1024" height="1024" href="data:image/png;base64,{base64.b64encode(encoded.getvalue()).decode('ascii')}"/>
<rect width="1024" height="1024" fill="#000000" fill-opacity="0.56"/>
<g transform="translate({x} {y:.6f}) scale({scale:.9f})">{title_paths}</g>
</svg>'''
    ET.fromstring(svg)
    (HERE / 'stupid-tactics-square.svg').write_text(svg, encoding='utf-8')
    (HERE / 'composition.json').write_text(json.dumps(dict(background=str(BACKGROUND), title=str(TITLE),
        source_crop=crop, black_scrim_opacity=0.56, title_box=[x, y, width, 570 * scale],
        tn_color='#06DEE5', title_color='#F0F2E8', source_title_unchanged=True),
        indent=2, ensure_ascii=False), encoding='utf-8')
    print('Composed self-contained SVG from the existing title and photograph.')


if __name__ == '__main__':
    main()

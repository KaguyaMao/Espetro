"""Package icon formats and check square/opaque exports."""
import hashlib
import json
from pathlib import Path
import zipfile

from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parent


def main():
    with Image.open(ROOT / 'avatar-1024.png') as source:
        source.convert('RGBA').save(ROOT / 'client-icon.ico', format='ICO',
            sizes=[(s, s) for s in (16, 24, 32, 48, 64, 128, 256)])
    expected = {'master-2048.png': 2048, 'avatar-1024.png': 1024,
                'avatar-512.png': 512, 'client-icon.png': 256, 'server-icon.png': 64}
    verification = []
    for name, size in expected.items():
        path = ROOT / name
        with Image.open(path) as image:
            image.load()
            assert image.format == 'PNG' and image.size == (size, size), name
            assert image.convert('RGBA').getextrema()[-1] == (255, 255), name
        verification.append(dict(file=name, width=size, height=size, opaque=True,
                                 bytes=path.stat().st_size, sha256=hashlib.sha256(path.read_bytes()).hexdigest()))
    with Image.open(ROOT / 'client-icon.ico') as icon:
        assert icon.ico.sizes() == {(s, s) for s in (16, 24, 32, 48, 64, 128, 256)}
    (ROOT / 'verification.json').write_text(json.dumps(dict(exports=verification,
        ico_sizes=[16, 24, 32, 48, 64, 128, 256], server_icon='64x64 PNG verified'),
        indent=2, ensure_ascii=False), encoding='utf-8')

    font = ImageFont.truetype(r'C:\Windows\Fonts\msyh.ttc', 19)
    preview = Image.new('RGB', (1008, 580), '#171a1b')
    draw = ImageDraw.Draw(preview)
    with Image.open(ROOT / 'avatar-512.png') as image:
        preview.paste(image.convert('RGB'), (24, 24))
        circle = image.resize((248, 248), Image.Resampling.LANCZOS).convert('RGB')
        mask = Image.new('L', circle.size, 0)
        ImageDraw.Draw(mask).ellipse((0, 0, 247, 247), fill=255)
        preview.paste(circle, (604, 24), mask)
    draw.text((24, 544), '方形头像 / 完整标题', font=font, fill='#F0F2E8')
    draw.text((600, 288), '圆形头像裁切检查', font=font, fill='#C0C6B8')
    with Image.open(ROOT / 'server-icon.png') as image:
        preview.paste(image.convert('RGB'), (604, 348))
        preview.paste(image.resize((192, 192), Image.Resampling.NEAREST).convert('RGB'), (748, 348))
    draw.text((600, 428), '64×64 原尺寸', font=font, fill='#C0C6B8')
    draw.text((748, 544), '64×64 放大检查', font=font, fill='#C0C6B8')
    preview.save(ROOT / 'preview.png', optimize=True)

    readme = '''# 胡闹战场 · 主菜单同款方形图标

使用现有主菜单的简约标题矢量路径，保留暖白文字、天青色 TN 标、“胡闹战场 / MINECRAFT”副标题。背景选自主菜单幻灯片 image_6.png（坦克与木塔），裁为正方形，加56%黑色半透明遮罩；没有额外边框或科技装饰。制作过程为代码合成，无生图模型。

| 文件 | 用途 |
| --- | --- |
| avatar-1024.png | 交流群头像及通用方形原图 |
| avatar-512.png | 512像素群头像 |
| client-icon.png | 256像素客户端/启动器图标 |
| client-icon.ico | Windows客户端快捷方式图标，含16/24/32/48/64/128/256像素 |
| server-icon.png | Minecraft服务端图标，已校验为64×64 PNG |
| master-2048.png | 2048像素大图 |
| stupid-tactics-square.svg | 自包含SVG，内嵌背景，标题保持可编辑矢量路径 |
| preview.png | 方形、圆形裁切和小尺寸检查 |

服务端使用时把 server-icon.png 放在 server.properties 所在目录，随服务器重启加载。客户端图标可在启动器中选 client-icon.png，Windows快捷方式选 client-icon.ico。此交付只制作图标文件，未修改服务器或启动器配置，也未加入模组jar。

复现：依次运行 compose.py、render.cjs、finalize.py。依赖Pillow及现有sharp环境。配置、输入来源和校验分别记录在 composition.json、verification.json。
'''
    (ROOT / 'README.md').write_text(readme, encoding='utf-8')
    deliverables = list(expected) + ['client-icon.ico', 'stupid-tactics-square.svg', 'README.md',
                                    'composition.json', 'verification.json', 'preview.png',
                                    'compose.py', 'render.cjs', 'finalize.py']
    archive = ROOT.parent / 'stupid-tactics-menu-square-2026-11-14.zip'
    with zipfile.ZipFile(archive, 'w', compression=zipfile.ZIP_DEFLATED, compresslevel=9) as package:
        for name in deliverables:
            package.write(ROOT / name, name)
    print(f'Checked {len(expected)} PNG exports and 7 ICO frames; packaged {archive.name}.')


if __name__ == '__main__':
    main()

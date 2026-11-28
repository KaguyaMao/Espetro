from pathlib import Path
import shutil, hashlib
from PIL import Image, ImageChops, ImageDraw, ImageFont
from psd_tools import PSDImage
from psd_tools.api.layers import Group, PixelLayer

ROOT=Path(__file__).resolve().parent
SOURCE=Path('C:/Users/Administrator/Desktop/stmlogo.psd')
DEST=Path('C:/Users/Administrator/Desktop/stmlogo_丰富版')
DEST.mkdir(exist_ok=True)
before=hashlib.sha256(SOURCE.read_bytes()).hexdigest()
psd=PSDImage.open(SOURCE)
original=list(psd)
source_group=Group.new(psd,'00 原稿备份（保留原始文字层，默认隐藏）')
for layer in original: source_group.append(layer)
source_group.visible=False
names={
    'background':'01 背景底色（隐藏可导出透明图）',
    'frame':'02 定位框与社区标识',
    'depth':'03 字形厚度与侧影',
    'wordmark':'04 主标题（保留原像素字形）',
    'monogram':'05 原稿青色符号',
    'details':'06 青色识别细节',
    'subtitle':'07 胡闹战场与 MINECRAFT 副标'}
for mode,title,visible in [('light','01 浅色版（切换时隐藏深色版）',False),
                           ('dark','02 深色版（默认显示）',True)]:
    group=Group.new(psd,title)
    for key,name in names.items():
        image=Image.open(ROOT/f'{mode}-{key}.png').convert('RGBA')
        bbox=image.getbbox()
        assert bbox is not None
        PixelLayer.frompil(image.crop(bbox),group,name=name,top=bbox[1],left=bbox[0])
    group.visible=visible
target=DEST/'stmlogo_丰富版.psd'
# PSD keeps Unicode names in the luni tag; use ASCII fallbacks in legacy Pascal names.
for index,layer in enumerate(psd.descendants()):
    layer._record.name=f'Layer-{index:02d}'
psd.save(target)

# Verify the saved file's actual merged image and editable original text layer.
reopened=PSDImage.open(target)
assert len(list(reopened.descendants()))==19
assert any(layer.kind=='type' and 'STUPID' in layer.text for layer in reopened.descendants())
merged=Image.open(target).convert('RGB')
expected=Image.open(ROOT/'stm-logo-dark.png').convert('RGB')
diff=ImageChops.difference(merged,expected)
extrema=diff.getextrema()
print('PSD merged image channel differences:',extrema)
assert all(hi<=3 for lo,hi in extrema), extrema
merged.save(DEST/'stmlogo_深色预览.png')

exports={
    'stm-logo-dark.svg':'stmlogo_深色.svg',
    'stm-logo-light.svg':'stmlogo_浅色.svg',
    'stm-logo-transparent-dark.svg':'stmlogo_透明_适合深底.svg',
    'stm-logo-transparent-light.svg':'stmlogo_透明_适合浅底.svg',
    'stm-logo-light.png':'stmlogo_浅色预览.png',
    'stm-logo-transparent-dark.png':'stmlogo_透明_适合深底.png',
    'stm-logo-transparent-light.png':'stmlogo_透明_适合浅底.png',
}
for name,export in exports.items(): shutil.copy2(ROOT/name,DEST/export)

# A single comparison plate for quick inspection.
sheet=Image.new('RGB',(1440,1620),'#0D1419')
for y,name in [(0,'stm-logo-dark.png'),(810,'stm-logo-light.png')]:
    image=Image.open(ROOT/name).convert('RGB').resize((1440,810),Image.Resampling.LANCZOS)
    sheet.paste(image,(0,y))
sheet.save(DEST/'stmlogo_深浅对照.png')

(DEST/'说明.txt').write_text(
    'STUPID TACTICS / 胡闹战场 — 丰富版\n\n'
    '原始 stmlogo.psd 保持原样，新文件另存于本目录。\n'
    'PSD 包含深色组、浅色组及隐藏的原始图层；原稿文字层仍为文字层。\n'
    '新增画面按背景、定位框、字形厚度、主标题、青色符号、点缀、副标分层；新增图层为像素图层。\n'
    '切换浅色时，隐藏深色组、显示浅色组。隐藏相应组内背景层可导出透明图。\n'
    'SVG 中所有文字均已转路径，无外链图片与字体依赖，可缩放编辑。\n'
    '透明版按深底/浅底分别提供，不含画布底色；PNG 同步提供。\n'
    '本版通过几何路径和字体轮廓绘制完成，未使用生图模型。\n',encoding='utf-8')
assert hashlib.sha256(SOURCE.read_bytes()).hexdigest()==before
print('OUTPUT',DEST)
for f in DEST.iterdir(): print(f.name,f.stat().st_size)

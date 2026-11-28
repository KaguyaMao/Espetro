"""Geometry-based refinement of the supplied PSD. No generated image assets."""
from pathlib import Path
from collections import defaultdict
from fontTools.ttLib import TTFont
from fontTools.pens.svgPathPen import SVGPathPen
from fontTools.pens.transformPen import TransformPen
from PIL import Image
import json, math

OUT = Path(__file__).resolve().parent
W, H = 1920, 1080


def simplify(points, tolerance):
    if len(points) <= 2:
        return points
    a, b = points[0], points[-1]
    dx, dy = b[0]-a[0], b[1]-a[1]
    length = math.hypot(dx, dy)
    distances = [abs(dx*(a[1]-p[1])-(a[0]-p[0])*dy)/length if length
                 else math.hypot(p[0]-a[0],p[1]-a[1]) for p in points]
    largest = max(distances)
    if largest <= tolerance:
        return [a, b]
    index = distances.index(largest)
    return simplify(points[:index+1], tolerance)[:-1] + simplify(points[index:], tolerance)


def contour(cells, unit=1, tolerance=0):
    edges = defaultdict(list)
    for x, y in sorted(cells):
        if (x, y-1) not in cells: edges[(x,y)].append((x+1,y))
        if (x+1, y) not in cells: edges[(x+1,y)].append((x+1,y+1))
        if (x, y+1) not in cells: edges[(x+1,y+1)].append((x,y+1))
        if (x-1, y) not in cells: edges[(x,y+1)].append((x,y))
    paths=[]
    while edges:
        start=next(iter(edges)); p=start; points=[start]
        while True:
            q=edges[p].pop()
            if not edges[p]: del edges[p]
            points.append(q); p=q
            if p==start: break
        # Remove points on the same horizontal or vertical segment.
        compact=[]
        for p in points:
            while len(compact)>1 and ((compact[-2][0]==compact[-1][0]==p[0])
                    or (compact[-2][1]==compact[-1][1]==p[1])):
                compact.pop()
            compact.append(p)
        if tolerance: compact=simplify(compact[:-1],tolerance)
        compact=[(x*unit,y*unit) for x,y in compact]
        paths.append('M'+' '.join(f'{x:g},{y:g}' for x,y in compact)+'Z')
    return ' '.join(paths)


def text_path(text, font, size, x, baseline, spacing=0):
    glyphs=font.getGlyphSet(); cmap=font.getBestCmap()
    scale=size/font['head'].unitsPerEm
    paths=[]
    for char in text:
        glyph=cmap[ord(char)]
        pen=SVGPathPen(glyphs)
        glyphs[glyph].draw(TransformPen(pen,(scale,0,0,-scale,x,baseline)))
        paths.append(pen.getCommands())
        x+=font['hmtx'][glyph][0]*scale+spacing
    return ' '.join(paths), x-spacing


def p(d, fill, **attrs):
    extra=' '.join(f'{k.replace("_","-")}="{v}"' for k,v in attrs.items())
    return f'<path d="{d}" fill="{fill}" {extra}/>'


def svg(body, label):
    return (f'<svg xmlns="http://www.w3.org/2000/svg" width="1920" height="1080" '
            f'viewBox="0 0 1920 1080" role="img" aria-label="{label}">'
            '<title>Stupid Tactics Minecraft — 胡闹战场</title>'+body+'</svg>')


def build():
    alpha=Image.open(OUT/'layer-1.png').getchannel('A')
    # The original Minecraft type is an exact 30 px grid. Preserve its layout.
    cells={(x,y) for y in range(15) for x in range(39)
           if alpha.getpixel((x*30+15,y*30+15))>127}
    word=contour(cells,30)
    icon=Image.open(OUT/'layer-0.png').getchannel('A')
    monogram=contour({(x,y) for y in range(icon.height) for x in range(icon.width)
                      if icon.getpixel((x,y))>127},tolerance=1.05)
    chinese=TTFont('C:/Users/Administrator/AppData/Local/Microsoft/Windows/Fonts/'
                   'RuiZiTaiKongLiXianXiangSuJian-Shan(REEJI-TaikoRiskGB-Flash)-2.ttf')
    latin=TTFont('C:/Windows/Fonts/AGENCYB.TTF')
    micro=TTFont('C:/Windows/Fonts/consola.ttf')
    cn,cn_end=text_path('胡闹战场',chinese,57,375,864,15)
    mc,mc_end=text_path('MINECRAFT',latin,49,0,864,10)
    # Align the secondary name to the exact right edge of the source wordmark.
    mc_shift=1545-mc_end
    issue,_=text_path('MULTIPLAYER / COMMUNITY',micro,13,375,249,2.5)
    product,_=text_path('STM',micro,14,1495,249,2)
    palettes={
        'dark':dict(bg='#0D1419',ink='#EAF0EF',cyan='#06DEE5',soft='#52686F',
                    line='#35505A',extrude='#183942', edge='#749199'),
        'light':dict(bg='#F2F4F0',ink='#17272D',cyan='#008D99',soft='#647C81',
                     line='#BDCFCE',extrude='#C5D4D2',edge='#3D626B')}
    for mode,c in palettes.items():
        layers={}
        layers['background']=f'<rect width="1920" height="1080" fill="{c["bg"]}"/>'
        # The frame is interrupted around its corners and baseline to keep it airy.
        layers['frame']=(
            f'<g fill="none" stroke="{c["line"]}" stroke-width="2">'
            '<path d="M344 291V267H368 M1576 291V267H1552 M344 858V892H378 '
            'M1576 858V892H1542 M344 318V795 M1576 318V795"/>'
            '<path d="M752 264H1447 M375 786H1545"/>'
            '</g>'
            +p(issue,c['soft'])+p(product,c['soft'])
            +f'<g stroke="{c["soft"]}" stroke-width="2">'
            '<path d="M328 529H360M344 513V545 M1560 529H1592M1576 513V545"/>'
            '</g>')
        layers['depth']=(f'<g transform="translate(383 307)" fill-rule="evenodd">'
                         +p(word,c['extrude'])+'</g>')
        layers['wordmark']=(f'<g transform="translate(375 300)" fill-rule="evenodd">'
            +p(word,c['ink'],stroke=c['edge'],stroke_width='1.5',stroke_linejoin='miter')+'</g>')
        layers['monogram']=(f'<g transform="translate(375 346)" fill-rule="evenodd">'
            +p(monogram,c['cyan'])+'</g>')
        layers['details']=(
            f'<g fill="{c["cyan"]}">'
            '<path d="M375 276H447L435 288H375Z M1467 276H1491V288H1467Z '
            'M1497 276H1521V288H1497Z M1527 276H1545V288H1527Z"/>'
            '<path d="M375 560H453V565H375Z M459 560H483V565H459Z M489 560H501V565H489Z"/>'
            '<rect x="1542" y="300" width="3" height="60"/>'
            '<rect x="375" y="750" width="60" height="3"/>'
            '</g>'
            f'<g stroke="{c["cyan"]}" stroke-width="2" fill="none">'
            '<path d="M362 281V300H349 M1558 878V859H1571"/>'
            '</g>')
        layers['subtitle']=(p(cn,c['ink'])
            +f'<g transform="translate({mc_shift:g} 0)">'+p(mc,c['ink'])+'</g>'
            +f'<g stroke="{c["line"]}" stroke-width="2">'
            +f'<path d="M{cn_end+32:g} 844H{mc_shift-36:g}"/>'
            '</g>'+f'<rect x="{cn_end+30:g}" y="841" width="6" height="6" fill="{c["cyan"]}"/>')
        layers={k: v if k=='background' else f'<g transform="translate(0 -32)">{v}</g>'
                for k,v in layers.items()}
        body=''.join(f'<g id="{k}">{v}</g>' for k,v in layers.items())
        (OUT/f'stm-logo-{mode}.svg').write_text(svg(body,f'Stupid Tactics, {mode} edition'),encoding='utf-8')
        clear=''.join(f'<g id="{k}">{v}</g>' for k,v in layers.items() if k!='background')
        transparent=svg(clear,'Stupid Tactics, transparent').replace(
            'width="1920" height="1080" viewBox="0 0 1920 1080"',
            'width="1320" height="720" viewBox="300 180 1320 720"')
        (OUT/f'stm-logo-transparent-{mode}.svg').write_text(transparent,encoding='utf-8')
        for name,body in layers.items():
            (OUT/f'{mode}-{name}.svg').write_text(svg(body,name),encoding='utf-8')
    (OUT/'geometry.json').write_text(json.dumps(dict(wordmark=word,monogram=monogram),indent=2),encoding='utf-8')
    print('SVG masters and individual layers written')


if __name__=='__main__': build()

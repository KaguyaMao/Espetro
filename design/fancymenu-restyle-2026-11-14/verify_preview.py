"""Validate staged FancyMenu resources and reconstruct previews from layout coordinates."""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont, ImageOps
import re,json
OUT=Path(__file__).resolve().parent
CLIENT=Path(r'D:\minecraft\squadMC预发布测试\versions\Squad预发布测试')
FM=CLIENT/'config/fancymenu'
BLOCK=re.compile(r'^([a-z_]+) \{\n(.*?)^\}',re.M|re.S)
def props(body):return dict(re.findall(r'^  (.*?) = (.*)$',body,re.M))
def parse(path):return [(t,props(b)) for t,b in BLOCK.findall(path.read_text('utf-8'))]
def path_for(value):
    local=value.removeprefix('[source:local]')
    if '/assets/stm_restyle/' in local:return OUT/'assets'/Path(local).name
    return CLIENT/local.lstrip('/')
def rect(d,W,H):
    w=int(d['width']);h=int(d['height']);x=int(d['x']);y=int(d['y'])
    a=d['anchor_point']
    if a=='mid-left':y+=H//2
    elif a=='bottom-right':x+=W;y+=H
    elif a=='bottom-centered':x+=W//2;y+=H
    elif a=='bottom-left':y+=H
    elif a=='mid-centered':x+=W//2;y+=H//2
    if d.get('stretch_x')=='true':x=0;w=W
    if d.get('stretch_y')=='true':y=0;h=H
    return x,y,w,h

report=[]
for f in (OUT/'customization').glob('*.txt'):
    data=f.read_bytes();assert not data.startswith(b'\xef\xbb\xbf')
    old=parse(FM/'customization'/f.name);new=parse(f)
    old_widgets={d['instance_identifier']:d for t,d in old if t=='vanilla_button'}
    new_widgets={d['instance_identifier']:d for t,d in new if t=='vanilla_button'}
    assert set(old_widgets)==set(new_widgets)
    for identity,before in old_widgets.items():
        after=new_widgets[identity]
        for k,v in before.items():
            if 'executable' in k or 'requirement' in k:assert after[k]==v,(identity,k)
        assert before['is_hidden']==after['is_hidden'],identity
    for t,d in new:
        for k,v in d.items():
            if v.startswith('[source:local]'):
                p=path_for(v);assert p.is_file(),p
                with Image.open(p) as im:im.verify()
    for W,H in [(320,240),(427,240),(480,270),(569,304),(640,360),(854,480),(1024,576)]:
        buttons=[]
        for identity,d in new_widgets.items():
            if d['is_hidden']=='true':continue
            x,y,w,h=rect(d,W,H)
            assert x>=0 and y>=0 and x+w<=W and y+h<=H,(identity,W,H,(x,y,w,h))
            buttons.append((identity,(x,y,x+w,y+h)))
        for i,(a,ra) in enumerate(buttons):
            for b,rb in buttons[i+1:]:
                assert min(ra[2],rb[2])<=max(ra[0],rb[0]) or min(ra[3],rb[3])<=max(ra[1],rb[1]),(a,b,W,H)
    report.append({'layout':f.name,'native_widgets':len(new_widgets),'resources':'all valid','logical_sizes_checked':7,'actions':'unchanged'})

fontfile=r'C:\Windows\Fonts\msyh.ttc'
def preview(name,W,H,scale,bg,hover=None):
    im=ImageOps.fit(Image.open(bg).convert('RGBA'),(W*scale,H*scale))
    blocks=parse(OUT/'customization'/name)
    # Both layouts explicitly render custom images behind native widgets.
    for t,d in blocks:
        if t!='element':continue
        x,y,w,h=rect(d,W,H)
        asset=Image.open(path_for(d['source'])).convert('RGBA').resize((w*scale,h*scale),Image.Resampling.LANCZOS)
        im.alpha_composite(asset,(x*scale,y*scale))
    for t,d in blocks:
        if t!='vanilla_button' or d['is_hidden']=='true':continue
        x,y,w,h=rect(d,W,H);identity=d['instance_identifier']
        if identity=='progress_bar':
            draw=ImageDraw.Draw(im);draw.rectangle((x*scale,y*scale,(x+w)*scale,(y+h)*scale),outline='#EAF0EF',width=scale)
            draw.rectangle(((x+2)*scale,(y+2)*scale,(x+w*.63)*scale,(y+h-2)*scale),fill='#EAF0EF')
            continue
        if 'backgroundnormal' in d:
            texture=d['backgroundhovered'] if identity==hover else d['backgroundnormal']
            asset=Image.open(path_for(texture)).convert('RGBA').resize((w*scale,h*scale),Image.Resampling.LANCZOS)
            im.alpha_composite(asset,(x*scale,y*scale))
        label=d.get('label','Copyright Mojang AB. Do not distribute!')
        # Native Minecraft text is approximated here; no claim of a live screenshot.
        font=ImageFont.truetype(fontfile,(9 if 'label' in d else 6)*scale)
        draw=ImageDraw.Draw(im);box=draw.textbbox((0,0),label,font=font)
        tw=box[2]-box[0];th=box[3]-box[1]
        color=d.get('label_hover_color' if identity==hover else 'label_base_color','#D7E2E5')[:7]
        draw.text(((x+w/2)*scale-tw/2,(y+h/2)*scale-th/2-box[1]),label,font=font,fill=color)
    return im.convert('RGB')

bg=FM/'slideshows/myslideshow/images/image_12.png'
preview('title_screen_layout.txt',480,270,3,bg).save(OUT/'主菜单_布局预览.png')
preview('title_screen_layout.txt',427,240,2,FM/'slideshows/myslideshow/images/image_9.png','mc_titlescreen_multiplayer_button').save(OUT/'主菜单_紧凑悬停预览.png')
preview('drippy_loading_overlay_layout.txt',480,270,3,FM/'assets/start.png').save(OUT/'加载页_布局预览.png')
(OUT/'verification.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
print(json.dumps(report,ensure_ascii=False))

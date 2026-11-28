"""Validate staged FancyMenu resources and reconstruct previews from layout coordinates."""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont, ImageOps, ImageFilter
import re,json
OUT=Path(__file__).resolve().parent
CLIENT=Path(r'D:\minecraft\squadMC预发布测试\versions\Squad预发布测试')
FM=CLIENT/'config/fancymenu'
BLOCK=re.compile(r'^([a-z_-]+) \{\n(.*?)^\}',re.M|re.S)
def props(body):return dict(re.findall(r'^  (.*?) = (.*)$',body,re.M))
def parse(path):return [(t,props(b)) for t,b in BLOCK.findall(path.read_text('utf-8'))]
def path_for(value):
    local=value.removeprefix('[source:local]')
    if '/assets/stm_glass/' in local:return OUT/'assets'/Path(local).name
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
shade=Image.open(OUT/'assets/shade.png').convert('RGBA')
assert shade.getpixel((0,0))[3]>=235
assert shade.getpixel((shade.width-1,0))[3]==0
assert shade.getpixel((0,0))==shade.getpixel((0,shade.height-1))
primary=Image.open(OUT/'assets/primary_normal.png').convert('RGBA')
primary_pixel=primary.getpixel((primary.width//2,primary.height//2))
assert primary_pixel[3]==56 and all(abs(a-b)<=2 for a,b in zip(primary_pixel[:3],(240,242,232)))
assert primary.getpixel((0,0))==primary.getpixel((primary.width//2,primary.height//2))
for path in (OUT/'assets').glob('*_*.svg'):
    if any(k in path.name for k in ('primary_','secondary_','small_','utility_')):
        assert 'stroke' not in path.read_text('utf-8'),'All button states must be borderless.'
for f in (OUT/'customization').glob('*.txt'):
    data=f.read_bytes();assert not data.startswith(b'\xef\xbb\xbf')
    old=parse(FM/'customization'/f.name);new=parse(f)
    meta=next(d for t,d in new if t=='layout-meta')
    assert meta['render_custom_elements_behind_vanilla']=='true','Background images must draw BEFORE vanilla buttons.'
    loading=f.name.startswith('drippy')
    assert len([d for t,d in new if t=='element'])==(1 if loading else 8)
    shapes=[d for t,d in new if t=='element' and d['element_type']=='shape']
    assert len(shapes)==(0 if loading else 6)
    assert all(d['blur_enabled']=='true' and float(d['blur_radius'])==4 for d in shapes)
    old_widgets={d['instance_identifier']:d for t,d in old if t=='vanilla_button'}
    new_widgets={d['instance_identifier']:d for t,d in new if t=='vanilla_button'}
    assert set(old_widgets)-({'progress_bar'} if loading else set())==set(new_widgets)
    if loading:
        assert 'progress_bar' not in new_widgets,'Progress bar must use its unmodified native renderer.'
        assert all('shade' not in d.get('source','') for t,d in new if t=='element')
    for identity,before in old_widgets.items():
        if loading and identity=='progress_bar':continue
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
        if not f.name.startswith('drippy'):
            for shape in shapes:
                identity=shape['instance_identifier'].removeprefix('stm_glass_')
                assert rect(shape,W,H)==rect(new_widgets[identity],W,H),'Blur area must follow its native button.'
        for shape in shapes:
            x,y,w,h=rect(shape,W,H)
            assert 0<=x and 0<=y and x+w<=W and y+h<=H
    report.append({'layout':f.name,'native_widgets':len(new_widgets),'resources':'all valid','logical_sizes_checked':7,'actions':'unchanged','blur_regions':len(shapes),'render_custom_elements_behind_vanilla':meta['render_custom_elements_behind_vanilla']})

fontfile=r'C:\Windows\Fonts\msyh.ttc'
def preview(name,W,H,scale,bg,hover=None):
    im=ImageOps.fit(Image.open(bg).convert('RGBA'),(W*scale,H*scale))
    blocks=parse(OUT/'customization'/name)
    if name.startswith('drippy'):
        # Native DrippyOverlayScreen.buildProgressBarWidget geometry, verified in installed jar.
        half=int(min(W*.75,H)*.5)
        blocks.append(('vanilla_button',dict(instance_identifier='progress_bar',is_hidden='false',
            anchor_point='top-left',x=str(W//2-half),y=str(int(H*.8325)-5),width=str(half*2),height='10')))
    # Both layouts explicitly render custom images behind native widgets.
    for t,d in blocks:
        if t!='element':continue
        x,y,w,h=rect(d,W,H)
        if d['element_type']=='shape':
            box=(x*scale,y*scale,(x+w)*scale,(y+h)*scale)
            # Gaussian preview approximates FancyMenu's runtime framebuffer blur.
            blurred=im.filter(ImageFilter.GaussianBlur(float(d['blur_radius'])*scale))
            im.paste(blurred.crop(box),box)
            raw=d['color'].lstrip('#')
            tint=tuple(int(raw[i:i+2],16) for i in (0,2,4,6))
            im.alpha_composite(Image.new('RGBA',(w*scale,h*scale),tint),(x*scale,y*scale))
            continue
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

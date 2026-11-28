from pathlib import Path
from PIL import Image,ImageDraw,ImageFont,ImageOps,ImageFilter
import re,json,hashlib
OUT=Path(__file__).resolve().parent
CLIENT=Path(r'D:\minecraft\squadMC预发布测试\versions\Squad预发布测试')
FM=CLIENT/'config/fancymenu'
manifest=json.loads((OUT/'manifest.json').read_text('utf-8'))
BLOCK=re.compile(r'^([a-z_-]+) \{\n(.*?)^\}',re.M|re.S)
def props(b):return dict(re.findall(r'^  (.*?) = (.*)$',b,re.M))
def parse(f):return [(t,props(b)) for t,b in BLOCK.findall(f.read_text('utf-8'))]
def sha(f):return hashlib.sha256(f.read_bytes()).hexdigest()
for name in ('title_screen_layout.txt','drippy_loading_overlay_layout.txt'):
    assert sha(FM/'customization'/name)==manifest['original_sha256'][name],name+' changed unexpectedly'
custom=(OUT/'customizablemenus.txt').read_text('utf-8')
assert custom.startswith((OUT/'originals/customizablemenus.txt').read_text('utf-8'))
for s in manifest['screens']:
    assert s['klass'].replace('/','.')+' {\n}' in custom
    assert '/inventory/' not in s['klass']
    assert s['identifier'] not in ('title_screen','progress_screen','level_loading_screen','receiving_level_screen')
ids=set();condition_modes={};resources=set()
for entry in manifest['layouts']:
    f=OUT/'customization'/entry['file'];data=f.read_bytes();assert not data.startswith(b'\xef\xbb\xbf')
    blocks=parse(f);meta=next(d for t,d in blocks if t=='layout-meta')
    assert meta['identifier']==entry['identifier']
    assert meta['render_custom_elements_behind_vanilla']=='true'
    requirement=[k for k in meta if k.startswith('[loading_requirement:')]
    assert len(requirement)==1
    mode='if' if entry['world'] else 'if_not'
    assert '[requirement_mode:'+mode+']' in requirement[0]
    req_id=re.search(r'\[req_id:([^\]]+)\]',requirement[0])[1]
    containers=[v for k,v in meta.items() if k.startswith('[loading_requirement_container_meta:')]
    assert containers==['[groups:][instances:'+req_id+';]']
    condition_modes.setdefault(entry['identifier'],set()).add(mode)
    backgrounds=[d for t,d in blocks if t=='menu_background' and d['show_background']=='true']
    assert len(backgrounds)==(0 if entry['world'] else 1)
    if backgrounds:assert backgrounds[0]['background_type']=='slideshow' and backgrounds[0]['slideshow_name']=='cool_slideshow'
    elements=[d for t,d in blocks if t=='element']
    assert len(elements)==3 and not any(t=='vanilla_button' for t,d in blocks)
    blur=[d for d in elements if d['element_type']=='shape']
    assert len(blur)==1 and blur[0]['stretch_x']==blur[0]['stretch_y']=='true'
    assert blur[0]['blur_enabled']=='true' and float(blur[0]['blur_radius'])==4
    templates=[d for d in elements if d['element_type']=='custom_button']
    assert {d['template_share_with'] for d in templates}=={'buttons','sliders'}
    for d in templates:
        assert d['is_template']=='true' and d['navigatable']=='true' and d['is_hidden']=='false'
        for field in ('width','height','posx','posy','opacity','visibility','label'):
            assert d['template_apply_'+field]=='false'
        assert not any(k in d for k in ('label','hoverlabel','clicksound','hoversound','tooltip'))
        actions=[v for k,v in d.items() if k.startswith('[executable_block:')]
        assert actions==['[executables:]'],'No menu actions may be introduced.'
    for t,d in blocks:
        if t=='element':
            assert d['instance_identifier'] not in ids
            ids.add(d['instance_identifier'])
        for key,value in d.items():
            if value.startswith('[source:local]'):
                local=value.removeprefix('[source:local]')
                if '/assets/stm_vanilla/' in local:
                    resource=OUT/'assets'/Path(local).name
                else:resource=CLIENT/local.lstrip('/')
                assert resource.is_file(),resource
                resources.add(str(resource))
                with Image.open(resource) as im:im.verify()
assert all(modes=={'if','if_not'} for modes in condition_modes.values())
for f in (OUT/'assets').glob('*.png'):
    im=Image.open(f).convert('RGBA');pixel=im.getpixel((0,0))
    assert im.getextrema()==tuple((v,v) for v in pixel),'No borders/gradients may be baked into controls.'
    assert 0<pixel[3]<255
# Fullscreen blur remains aligned at all supported sizes; native widget rectangles never change.
sizes=[(320,240),(427,240),(480,270),(569,304),(640,360),(854,480),(1024,576)]
report=dict(screens=len(manifest['screens']),layouts=len(manifest['layouts']),resources=len(resources),
            background_conditions='exclusive world/menu pairs',native_geometry_labels_actions='unchanged',
            title_loading='unchanged',supported_logical_sizes=sizes,verification='static; not in-game')
(OUT/'verification.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')

# Coordinate illustration using vanilla's centered two-column menu arrangement.
W,H,S=480,270,3
font=ImageFont.truetype(r'C:\Windows\Fonts\msyh.ttc',9*S)
title_font=ImageFont.truetype(r'C:\Windows\Fonts\msyh.ttc',11*S)
ink=manifest['palette']['text']
def text(draw,x,y,label,font):
    box=draw.textbbox((0,0),label,font=font);draw.text((x-(box[2]-box[0])/2,y-box[1]),label,font=font,fill=ink)
def button(im,x,y,w,h,label,hover=False,slider=False):
    alpha=round((manifest['palette']['hover_alpha'] if hover else manifest['palette']['button_alpha'])*255)
    rgb=tuple(int(ink[i:i+2],16) for i in (1,3,5))
    im.alpha_composite(Image.new('RGBA',(w*S,h*S),rgb+(alpha,)),(x*S,y*S))
    if slider:
        ImageDraw.Draw(im).rectangle(((x+int(w*.62))*S,(y+1)*S,(x+int(w*.62)+6)*S,(y+h-1)*S),fill=rgb+(160,))
    text(ImageDraw.Draw(im),(x+w/2)*S,(y+(h-9)/2)*S,label,font)
def backdrop(world=False):
    path=FM/('slideshows/myslideshow/images/image_6.png' if world else 'assets/start.png')
    im=ImageOps.fit(Image.open(path).convert('RGBA'),(W*S,H*S)).filter(ImageFilter.GaussianBlur(4*S))
    im.alpha_composite(Image.new('RGBA',im.size,(17,20,16,119 if world else 153)))
    return im
im=backdrop();text(ImageDraw.Draw(im),W*S/2,17*S,'选项',title_font)
button(im,85,43,150,20,'视野：正常',slider=True);button(im,245,43,150,20,'难度：普通')
labels=['自定义皮肤','音乐和声音','视频设置','控制','语言','聊天设置','资源包','辅助功能设置','在线选项','遥测数据']
for i,label in enumerate(labels):button(im,85+(i%2)*160,79+(i//2)*24,150,20,label,hover=i==3)
button(im,140,H-28,200,20,'完成')
im.convert('RGB').save(OUT/'选项菜单_样式示意.png')
im=backdrop(True);text(ImageDraw.Draw(im),W*S/2,38*S,'游戏菜单',title_font)
button(im,140,72,200,20,'返回游戏')
for i,label in enumerate(['进度','统计信息','发送反馈','报告漏洞','选项','对局域网开放']):
    button(im,140+(i%2)*102,96+(i//2)*24,98,20,label,hover=i==4)
button(im,140,172,200,20,'断开连接')
im.convert('RGB').save(OUT/'暂停菜单_样式示意.png')
print(json.dumps(report,ensure_ascii=False))

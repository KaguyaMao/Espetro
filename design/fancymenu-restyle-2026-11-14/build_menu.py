"""Build code-drawn FancyMenu assets and staged layouts; never edit live files."""
from pathlib import Path
from copy import deepcopy
import re, json, uuid, importlib.util
from PIL import Image, ImageDraw, ImageFont, ImageOps

OUT = Path(__file__).resolve().parent
CLIENT = Path(r'D:\minecraft\squadMC预发布测试\versions\Squad预发布测试')
FM = CLIENT/'config/fancymenu'
ASSETS = OUT/'assets'
STAGE = OUT/'customization'
ASSETS.mkdir(exist_ok=True); STAGE.mkdir(exist_ok=True)
TOKENS = dict(background='#0D1419', panel='#101C23', ink='#EAF0EF', cyan='#06DEE5',
              border='#35505A', muted='#91ABB3', hover='#203D48', disabled='#17252C')
(OUT/'theme.json').write_text(json.dumps(TOKENS,indent=2)+'\n',encoding='utf-8')
PREFIX = '[source:local]/config/fancymenu/assets/stm_restyle/'
BLOCK = re.compile(r'^([a-z_]+) \{\n(.*?)^\}',re.M|re.S)

def properties(body):
    return dict(re.findall(r'^  (.*?) = (.*)$',body,re.M))

def setprops(body, changes):
    for key,value in changes.items():
        value = str(value).lower() if isinstance(value,bool) else str(value)
        pat = re.compile(r'^  '+re.escape(key)+r' = .*$',re.M)
        if pat.search(body): body=pat.sub(lambda m:'  '+key+' = '+value,body)
        else: body+='  '+key+' = '+value+'\n'
    return body

def write_svg(name,w,h,body):
    (ASSETS/(name+'.svg')).write_text(f'<svg xmlns="http://www.w3.org/2000/svg" width="{w*4}" height="{h*4}" viewBox="0 0 {w} {h}">{body}</svg>',encoding='utf-8')

# All new visuals are geometry or font outlines, with no image-generation model.
write_svg('panel',192,210,
    f'<path d="M0 0H184L192 8V210H0Z" fill="{TOKENS["panel"]}" fill-opacity=".96" stroke="{TOKENS["border"]}" stroke-width=".5"/>'
    f'<path d="M0 0H40M0 0V25M172 210H192V190" fill="none" stroke="{TOKENS["cyan"]}" stroke-width="1"/>')
for kind,w,h in [('primary',164,26),('secondary',164,22),('small',80,20),('utility',80,18)]:
    for state in ['normal','hover','disabled']:
        primary=kind=='primary'
        fill=(TOKENS['cyan'] if primary else TOKENS['panel']) if state=='normal' else ('#6CF5F4' if primary else TOKENS['hover']) if state=='hover' else TOKENS['disabled']
        border=TOKENS['cyan'] if state=='hover' else TOKENS['border']
        ink= TOKENS['background'] if primary and state!='disabled' else TOKENS['cyan'] if state=='hover' else TOKENS['muted']
        shape=(f'<path d="M0 0H{w-5}L{w} 5V{h}H0Z" fill="{fill}" stroke="{border}" stroke-width=".6"/>'
               f'<path d="M8 {h/2-3}l3 3-3 3" fill="none" stroke="{ink}" stroke-width="1"/>')
        if state=='hover':shape+=f'<path d="M0 0V{h}" stroke="{TOKENS["cyan"]}" stroke-width="2"/>'
        write_svg(kind+'_'+state,w,h,shape)

# A full-screen alpha veil keeps every existing slideshow frame legible.
write_svg('shade',640,360,
    '<defs><linearGradient id="side"><stop stop-color="#071219" stop-opacity=".40"/><stop offset=".6" stop-color="#071219" stop-opacity=".04"/><stop offset="1" stop-color="#071219" stop-opacity=".06"/></linearGradient>'
    '<linearGradient id="edge" x1="0" y1="0" x2="0" y2="1"><stop stop-color="#071219" stop-opacity=".12"/><stop offset=".55" stop-color="#071219" stop-opacity="0"/><stop offset="1" stop-color="#071219" stop-opacity=".56"/></linearGradient></defs>'
    '<rect width="640" height="360" fill="url(#side)"/><rect width="640" height="360" fill="url(#edge)"/>')
source_logo=Path(r'C:\Users\Administrator\Desktop\stmlogo_丰富版\stmlogo_透明_适合深底.svg')
(ASSETS/'logo.svg').write_bytes(source_logo.read_bytes())

spec=importlib.util.spec_from_file_location('logo_geometry',OUT.parent/'logo-refinement-2026-11-13/refine_logo.py')
mod=importlib.util.module_from_spec(spec); spec.loader.exec_module(mod)
font=mod.TTFont(r'C:\Windows\Fonts\msyh.ttc',fontNumber=0)
micro=mod.TTFont(r'C:\Windows\Fonts\consola.ttf')
cn,_=mod.text_path('正在加载资源',font,9,0,11,1)
en,end=mod.text_path('LOADING RESOURCES',micro,5,0,22,.7)
write_svg('loading_caption',248,26,
    f'<path d="{cn}" fill="{TOKENS["ink"]}"/>'
    f'<path d="{en}" fill="{TOKENS["muted"]}"/>'
    f'<path d="M230 9h18M238 1v16" stroke="{TOKENS["cyan"]}" stroke-width=".6"/>')

original_dir=OUT/'originals'
original_dir.mkdir(exist_ok=True)
for f in (FM/'customization').glob('*.txt'):
    saved=original_dir/f.name
    if not saved.exists():saved.write_bytes(f.read_bytes())
originals={f.name:f.read_text('utf-8') for f in original_dir.glob('*.txt')}
title=originals['title_screen_layout.txt']
image_template=next(b for t,b in BLOCK.findall(title) if t=='element')
def image_element(name,anchor,x,y,w,h,**extra):
    identifier='stm_'+name
    body=setprops(image_template,dict(instance_identifier=identifier,anchor_point=anchor,x=x,y=y,width=w,height=h,
        source=PREFIX+name+'.png',auto_sizing=False,stay_on_screen=False,**extra))
    # Give each new image its own empty requirement container.
    old=properties(body)['element_loading_requirement_container_identifier']
    body=body.replace(old,str(uuid.uuid5(uuid.NAMESPACE_URL,'stm/fancymenu/'+name)))
    return 'element {\n'+body+'}\n\n'

buttons={
 'mc_titlescreen_multiplayer_button':(30,0,164,26,'primary','多人游戏'),
 'mc_titlescreen_singleplayer_button':(30,30,164,22,'secondary','单人游戏'),
 'mc_titlescreen_options_button':(30,56,80,20,'small','设置'),
 'forge_titlescreen_mods_button':(114,56,80,20,'small','模组'),
 'mc_titlescreen_language_button':(30,80,80,18,'utility','语言'),
 'mc_titlescreen_quit_button':(114,80,80,18,'utility','退出游戏'),
}
def transform_title(m):
    typ,body=m.groups();d=properties(body);identity=d.get('instance_identifier')
    if typ=='layout-meta':body=setprops(body,{'render_custom_elements_behind_vanilla':True})
    if typ=='element':
        return (image_element('shade','top-left',0,0,640,360,stretch_x=True,stretch_y=True)+
                image_element('panel','mid-left',16,-105,192,210)+
                image_element('logo','mid-left',28,-92,168,92))
    if typ=='vanilla_button' and identity in buttons:
        x,y,w,h,kind,label=buttons[identity]
        body=setprops(body,dict(anchor_point='mid-left',x=x,y=y,width=w,height=h,label=label,
            auto_sizing=False,sticky_anchor=False,is_hidden=False,label_scale=1,label_shadow=False,
            label_base_color=TOKENS['background'] if kind=='primary' else TOKENS['ink'],
            label_hover_color=TOKENS['background'] if kind=='primary' else TOKENS['cyan'],
            backgroundnormal=PREFIX+kind+'_normal.png',backgroundhovered=PREFIX+kind+'_hover.png',
            background_texture_inactive=PREFIX+kind+'_disabled.png',nine_slice_custom_background=False))
    if identity=='title_screen_copyright_button':
        body=setprops(body,dict(anchor_point='bottom-right',x=-200,y=-12,width=196,height=10,label_shadow=True))
    return typ+' {\n'+body+'}'

(STAGE/'title_screen_layout.txt').write_text(BLOCK.sub(transform_title,title),encoding='utf-8')

def transform_loading(m):
    typ,body=m.groups();d=properties(body);identity=d.get('instance_identifier')
    if typ=='layout-meta':body=setprops(body,{'render_custom_elements_behind_vanilla':True})
    if typ=='element':
        return (image_element('shade','top-left',0,0,640,360,stretch_x=True,stretch_y=True)+
                image_element('logo','top-left',20,20,152,83)+
                image_element('loading_caption','bottom-centered',-124,-62,248,26))
    if identity=='progress_bar':body=setprops(body,dict(anchor_point='bottom-centered',x=-124,y=-27,width=248,height=5))
    return typ+' {\n'+body+'}'
(STAGE/'drippy_loading_overlay_layout.txt').write_text(BLOCK.sub(transform_loading,originals['drippy_loading_overlay_layout.txt']),encoding='utf-8')

manifest=dict(client=str(CLIENT),palette=TOKENS,buttons=buttons,notes=['Code-drawn SVG/PNG assets; no image generation.',
 'Existing slideshow, native button IDs and executable blocks retained.',
 'Layouts use logical GUI coordinates; minimum supported GUI size 320x240.',
 'Live multiplayer session is not interrupted. Preview is reconstructed, not an in-game screenshot.'])
(OUT/'manifest.json').write_text(json.dumps(manifest,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
print('Staged layouts and SVG assets:',OUT)

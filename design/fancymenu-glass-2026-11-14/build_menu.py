"""Minimal FancyMenu skin matching Espetro UiTheme.DARK. Geometry only."""
from pathlib import Path
import re,json,uuid,importlib.util

OUT=Path(__file__).resolve().parent
CLIENT=Path(r'D:\minecraft\squadMC预发布测试\versions\Squad预发布测试')
FM=CLIENT/'config/fancymenu'
ASSETS=OUT/'assets';STAGE=OUT/'customization'
ASSETS.mkdir(exist_ok=True);STAGE.mkdir(exist_ok=True)
PREFIX='[source:local]/config/fancymenu/assets/stm_glass/'
# layout-meta contains a hyphen and MUST be included.
BLOCK=re.compile(r'^([a-z_-]+) \{\n(.*?)^\}',re.M|re.S)
theme_path=OUT.parents[1]/'src/main/java/org/espetro/client/gui/UiTheme.java'
theme_source=theme_path.read_text('utf-8')
def theme(name):
    argb=re.search(r'public static int '+name+r' = 0x([0-9A-Fa-f]{8});',theme_source)[1]
    return '#'+argb[2:]
TOKENS=dict(background=theme('BACKDROP_TOP'),button=theme('BUTTON_BLACK'),ink=theme('TEXT'),
            muted=theme('TEXT_SECOND'),disabled_text=theme('TEXT_DISABLED'),
            hover=theme('SURFACE_2'),disabled=theme('SUNKEN'),accent=theme('ACCENT'),
            primary=theme('INK_FILL'),primary_ink=theme('ON_INK'),brand_cyan='#06DEE5',
            glass_primary_alpha=.22,glass_secondary_alpha=.10,glass_hover_alpha=.30,
            glass_disabled_alpha=.04,glass_blur_radius=4.0)
(OUT/'theme.json').write_text(json.dumps(dict(source=str(theme_path),mode='dark',colors=TOKENS),indent=2)+'\n',encoding='utf-8')
def properties(body):return dict(re.findall(r'^  (.*?) = (.*)$',body,re.M))
def setprops(body,changes):
    for k,v in changes.items():
        v=str(v).lower() if isinstance(v,bool) else str(v)
        p=re.compile(r'^  '+re.escape(k)+r' = .*$',re.M)
        if p.search(body):body=p.sub(lambda m:'  '+k+' = '+v,body)
        else:body+='  '+k+' = '+v+'\n'
    return body
def svg(name,w,h,body):
    (ASSETS/(name+'.svg')).write_text(f'<svg xmlns="http://www.w3.org/2000/svg" width="{w*4}" height="{h*4}" viewBox="0 0 {w} {h}">{body}</svg>',encoding='utf-8')

# Full height, with the right half returning to the unmodified photograph.
svg('shade',640,360,
    '<defs><linearGradient id="left"><stop stop-color="#000000" stop-opacity=".94"/>'
    '<stop offset=".15" stop-color="#000000" stop-opacity=".88"/>'
    '<stop offset=".32" stop-color="#000000" stop-opacity=".63"/>'
    '<stop offset=".48" stop-color="#000000" stop-opacity=".18"/>'
    '<stop offset=".60" stop-color="#000000" stop-opacity="0"/>'
    '<stop offset="1" stop-color="#000000" stop-opacity="0"/></linearGradient></defs>'
    '<rect width="640" height="360" fill="url(#left)"/>')
for kind,w,h in [('primary',164,26),('secondary',164,22),('small',80,20),('utility',80,18)]:
    for state in ('normal','hover','disabled'):
        primary=kind=='primary'
        alpha=TOKENS['glass_disabled_alpha'] if state=='disabled' else TOKENS['glass_hover_alpha'] if state=='hover' else TOKENS['glass_primary_alpha'] if primary else TOKENS['glass_secondary_alpha']
        body=f'<rect width="{w}" height="{h}" fill="{TOKENS["ink"]}" fill-opacity="{alpha}"/>'
        svg(kind+'_'+state,w,h,body)

# Retain the supplied wordmark and monogram, remove all framing/micro labels.
spec=importlib.util.spec_from_file_location('logo_geometry',OUT.parent/'logo-refinement-2026-11-13/refine_logo.py')
mod=importlib.util.module_from_spec(spec);spec.loader.exec_module(mod)
geometry=json.loads((OUT.parent/'logo-refinement-2026-11-13/geometry.json').read_text('utf-8'))
cnfont=mod.TTFont(r'C:\Users\Administrator\AppData\Local\Microsoft\Windows\Fonts\RuiZiTaiKongLiXianXiangSuJian-Shan(REEJI-TaikoRiskGB-Flash)-2.ttf')
latin=mod.TTFont(r'C:\Windows\Fonts\AGENCYB.TTF')
cn,_=mod.text_path('胡闹战场',cnfont,61,0,548,16)
mc,mc_end=mod.text_path('MINECRAFT',latin,52,0,548,10)
logo=(f'<path d="{geometry["wordmark"]}" fill="{TOKENS["ink"]}"/>'
      f'<path d="{geometry["monogram"]}" transform="translate(0 46)" fill="{TOKENS["brand_cyan"]}"/>'
      f'<path d="{cn}" fill="{TOKENS["ink"]}"/>'
      f'<path d="{mc}" transform="translate({1170-mc_end} 0)" fill="{TOKENS["muted"]}"/>')
svg('logo',1170,570,logo)
font=mod.TTFont(r'C:\Windows\Fonts\msyh.ttc',fontNumber=0)
caption,_=mod.text_path('正在加载资源',font,9,0,11,1)
svg('loading_caption',248,16,f'<path d="{caption}" fill="{TOKENS["ink"]}"/>')

# Preserve the immediate pre-revision files for comparison and deployment guards.
original_dir=OUT/'originals';original_dir.mkdir(exist_ok=True)
for f in (FM/'customization').glob('*.txt'):
    saved=original_dir/f.name
    if not saved.exists():saved.write_bytes(f.read_bytes())
# Build from the original user-authored layout to avoid accumulating old elements.
base=OUT.parent/'fancymenu-restyle-2026-11-14/originals'
originals={f.name:f.read_text('utf-8') for f in base.glob('*.txt')}
image_template=next(b for t,b in BLOCK.findall(originals['title_screen_layout.txt']) if t=='element')
def image_element(name,anchor,x,y,w,h,**extra):
    changes=dict(instance_identifier='stm_simple_'+name,anchor_point=anchor,x=x,y=y,width=w,height=h,
        source=PREFIX+name+'.png',auto_sizing=False,stay_on_screen=False,stretch_x=False,stretch_y=False)
    changes.update(extra)
    body=setprops(image_template,changes)
    old=properties(body)['element_loading_requirement_container_identifier']
    body=body.replace(old,str(uuid.uuid5(uuid.NAMESPACE_URL,'stm/simple/'+name)))
    return 'element {\n'+body+'}\n\n'

def glass_element(name,anchor,x,y,w,h,color='#FFFFFF01'):
    # Native FancyMenu shape blur, not an image pretending to blur the scene.
    body=image_template
    for key in ('source','repeat_texture','nine_slice_texture','nine_slice_texture_border_x',
                'nine_slice_texture_border_y','restart_animated_on_menu_load','image_tint',
                'rounding_radius_top_left','rounding_radius_top_right',
                'rounding_radius_bottom_right','rounding_radius_bottom_left'):
        body=re.sub(r'^  '+key+r' = .*\n','',body,flags=re.M)
    body=setprops(body,dict(element_type='shape',instance_identifier='stm_glass_'+name,
        anchor_point=anchor,x=x,y=y,width=w,height=h,auto_sizing=False,stay_on_screen=False,
        stretch_x=False,stretch_y=False,color=color,blur_enabled=True,
        blur_radius=TOKENS['glass_blur_radius'],corner_radius_top_left=0,
        corner_radius_top_right=0,corner_radius_bottom_right=0,corner_radius_bottom_left=0))
    old=properties(body)['element_loading_requirement_container_identifier']
    body=body.replace(old,str(uuid.uuid5(uuid.NAMESPACE_URL,'stm/glass/'+name)))
    return 'element {\n'+body+'}\n\n'
buttons={
 'mc_titlescreen_multiplayer_button':(28,6,164,26,'primary','多人游戏'),
 'mc_titlescreen_singleplayer_button':(28,36,164,22,'secondary','单人游戏'),
 'mc_titlescreen_options_button':(28,62,80,20,'small','设置'),
 'forge_titlescreen_mods_button':(112,62,80,20,'small','模组'),
 'mc_titlescreen_language_button':(28,86,80,18,'utility','语言'),
 'mc_titlescreen_quit_button':(112,86,80,18,'utility','退出游戏'),
}
def transform(m,loading=False):
    typ,body=m.groups();d=properties(body);identity=d.get('instance_identifier')
    if typ=='layout-meta':body=setprops(body,dict(render_custom_elements_behind_vanilla=True))
    if typ=='element':
        if loading:
            return image_element('logo','top-left',20,20,152,74)
        return (image_element('shade','top-left',0,0,640,360,stretch_x=True,stretch_y=True)+
                ''.join(glass_element(identity,'mid-left',x,y,w,h) for identity,(x,y,w,h,kind,label) in buttons.items())+
                image_element('logo','mid-left',28,-94,164,80))
    if not loading and typ=='vanilla_button' and identity in buttons:
        x,y,w,h,kind,label=buttons[identity]
        body=setprops(body,dict(anchor_point='mid-left',x=x,y=y,width=w,height=h,label=label,
            auto_sizing=False,sticky_anchor=False,is_hidden=False,label_scale=1,label_shadow=False,
            label_base_color=TOKENS['ink'],label_hover_color=TOKENS['ink'],
            backgroundnormal=PREFIX+kind+'_normal.png',backgroundhovered=PREFIX+kind+'_hover.png',
            background_texture_inactive=PREFIX+kind+'_disabled.png',nine_slice_custom_background=False))
    if not loading and identity=='title_screen_copyright_button':
        body=setprops(body,dict(anchor_point='bottom-right',x=-200,y=-12,width=196,height=10,label_shadow=True))
    if loading and identity=='progress_bar':
        # No customization record: Drippy/Minecraft owns original size, position and drawing.
        return ''
    return typ+' {\n'+body+'}'
for name,source in originals.items():
    loading=name.startswith('drippy')
    (STAGE/name).write_text(BLOCK.sub(lambda m:transform(m,loading),source),encoding='utf-8')
(OUT/'manifest.json').write_text(json.dumps(dict(buttons=buttons,palette=TOKENS,theme='Espetro dark',
    layer_fix='Parse layout-meta including hyphen; verify render_custom_elements_behind_vanilla=true.'),ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
print('Minimal layouts staged, with explicit background render ordering.')

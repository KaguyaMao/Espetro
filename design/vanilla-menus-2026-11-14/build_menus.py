"""Scoped vanilla menu skins: native controls, per-screen templates, conditional backgrounds."""
from pathlib import Path
from zipfile import ZipFile
from PIL import Image
import re,json,uuid,struct,hashlib

OUT=Path(__file__).resolve().parent
CLIENT=Path(r'D:\minecraft\squadMC预发布测试\versions\Squad预发布测试')
FM=CLIENT/'config/fancymenu'
STAGE=OUT/'customization';ASSETS=OUT/'assets';ORIGINALS=OUT/'originals'
for d in (STAGE,ASSETS,ORIGINALS):d.mkdir(exist_ok=True)
for name in ('customizablemenus.txt',):
    saved=ORIGINALS/name
    if not saved.exists():saved.write_bytes((FM/name).read_bytes())
for name in ('title_screen_layout.txt','drippy_loading_overlay_layout.txt'):
    saved=ORIGINALS/name
    if not saved.exists():saved.write_bytes((FM/'customization'/name).read_bytes())
PREFIX='[source:local]/config/fancymenu/assets/stm_vanilla/'
BLOCK=re.compile(r'^([a-z_-]+) \{\n(.*?)^\}',re.M|re.S)
def props(b):return dict(re.findall(r'^  (.*?) = (.*)$',b,re.M))
def body(d):return ''.join('  '+k+' = '+(str(v).lower() if isinstance(v,bool) else str(v))+'\n' for k,v in d.items())
def block(t,d):return t+' {\n'+body(d)+'}\n\n'
def uid(k):return str(uuid.uuid5(uuid.NAMESPACE_URL,'stm/vanilla/'+k))

# Registry keys are extracted from the installed FancyMenu build, not guessed.
registry=(OUT/'UniversalScreenIdentifierRegistry.javap.txt').read_text('utf-8',errors='replace')
registered=dict(re.findall(r'// String ([a-z_]+)\n\s+\d+: ldc(?:_w)?\s+#[0-9]+\s+// class ([^\n]+)',registry))
registered={k:v.strip() for k,v in registered.items()}
mcjar=Path(r'C:\Users\Administrator\.gradle\caches\forge_gradle\minecraft_user_repo\net\minecraftforge\forge\1.20.1-47.4.22_mapped_official_1.20.1\forge-1.20.1-47.4.22_mapped_official_1.20.1-recomp.jar')
archives=[ZipFile(mcjar)]
archives.extend(ZipFile(f) for f in (CLIENT/'mods').glob('embeddium-*.jar'))
def class_info(data):
    offset=8
    def u2():
        nonlocal offset
        n=struct.unpack_from('>H',data,offset)[0];offset+=2;return n
    count=u2();pool={};i=1
    while i<count:
        tag=data[offset];offset+=1
        if tag==1:
            n=u2();pool[i]=data[offset:offset+n].decode('utf-8',errors='replace');offset+=n
        elif tag==7:pool[i]=u2()
        elif tag in (8,16,19,20):offset+=2
        elif tag in (3,4,9,10,11,12,17,18):offset+=4
        elif tag in (5,6):offset+=8;i+=1
        elif tag==15:offset+=3
        else:raise ValueError(tag)
        i+=1
    flags=u2();this=u2();sup=u2()
    return flags,pool[pool[sup]] if sup else None
class_cache={}
def info(name):
    if name not in class_cache:
        result=None
        for z in archives:
            try:result=class_info(z.read(name+'.class'));break
            except KeyError:pass
        class_cache[name]=result
    return class_cache[name]
def is_screen(name):
    seen=set()
    while name and name not in seen:
        if name=='net/minecraft/client/gui/screens/Screen':return True
        seen.add(name);v=info(name)
        if not v:return False
        name=v[1]
    return False
excluded={'title_screen','chat_screen','in_bed_chat_screen','level_loading_screen','progress_screen',
 'receiving_level_screen','generic_dirt_message_screen','generic_waiting_screen','out_of_memory_screen',
 'win_screen','demo_intro_screen','game_mode_switcher_screen','options_sub_screen'}
screens=[]
for identifier,klass in registered.items():
    if identifier in excluded or '/inventory/' in klass or '/debug/' in klass:continue
    v=info(klass)
    if v and not v[0]&0x0400 and is_screen(klass):
        screens.append(dict(identifier=identifier,klass=klass,kind='vanilla'))
# FancyMenu 3.9.9 maps key_binds_screen to KeyBindsList; use the actual screen's class fallback.
for klass,kind in [('net/minecraft/client/gui/screens/controls/KeyBindsScreen','vanilla'),
                   ('net/minecraftforge/client/gui/ModListScreen','forge'),
                   ('me/jellysquid/mods/sodium/client/gui/SodiumOptionsGUI','embeddium'),
                   ('org/embeddedt/embeddium/gui/EmbeddiumVideoOptionsScreen','embeddium')]:
    if info(klass) and is_screen(klass):
        identifier=next((k for k,v in registered.items() if v==klass),klass.replace('/','.'))
        if not any(s['identifier']==identifier for s in screens):screens.append(dict(identifier=identifier,klass=klass,kind=kind))

theme=json.loads((OUT.parent/'fancymenu-glass-2026-11-14/theme.json').read_text('utf-8'))['colors']
palette=dict(text=theme['ink'],muted=theme['muted'],button_alpha=theme['glass_secondary_alpha'],
             hover_alpha=theme['glass_hover_alpha'],disabled_alpha=theme['glass_disabled_alpha'],blur=4.0)
def png(name,color,alpha):
    rgb=tuple(int(color[i:i+2],16) for i in (1,3,5))
    Image.new('RGBA',(16,16),rgb+(round(alpha*255),)).save(ASSETS/(name+'.png'))
for name,alpha in [('button_normal',palette['button_alpha']),('button_hover',palette['hover_alpha']),
                   ('button_disabled',palette['disabled_alpha']),('slider_track',.10),
                   ('slider_track_hover',.16),('handle_normal',.62),('handle_hover',.86),('handle_disabled',.16)]:
    png(name,palette['text'],alpha)
png('list_header',theme['background'],.20)

title=(FM/'customization/title_screen_layout.txt').read_text('utf-8')
root=[(t,props(b)) for t,b in BLOCK.findall(title) if t not in ('element','vanilla_button')]
button_base=next(props(b) for t,b in BLOCK.findall(title) if t=='vanilla_button' and props(b).get('instance_identifier')=='mc_titlescreen_multiplayer_button')
shape_base=next(props(b) for t,b in BLOCK.findall(title) if t=='element' and props(b).get('element_type')=='shape')
def empty_requirements(d,seed):
    d={k:v for k,v in d.items() if not k.startswith('[loading_requirement') and not k.startswith('[executable_block:')}
    for field in ('element_loading_requirement_container_identifier','widget_active_state_requirement_container_identifier'):
        if field in d:
            value=uid(seed+'/'+field);d[field]=value;d['[loading_requirement_container_meta:'+value+']']='[groups:][instances:]'
    return d
def template(seed,sliders=False):
    d=empty_requirements(dict(button_base),seed)
    for k in ('label','hoverlabel','description','tooltip','hoversound','clicksound','iconnormal','iconhovered','icon_texture_inactive'):
        d.pop(k,None)
    d.update(element_type='custom_button',instance_identifier=uid(seed),anchor_point='top-left',x=0,y=0,
        width=100,height=20,is_template=True,template_share_with='sliders' if sliders else 'buttons',
        label_shadow=sliders,label_base_color=palette['text'],label_hover_color=palette['text'],
        is_hidden=False,navigatable=True,base_opacity=1,auto_sizing=False,
        transparent_background=False,nine_slice_custom_background=False,nine_slice_slider_handle=False)
    for field in ('width','height','posx','posy','opacity','visibility','label'):d['template_apply_'+field]=False
    for key,state in [('backgroundnormal','normal'),('backgroundhovered','hover'),('background_texture_inactive','disabled')]:
        d[key]=PREFIX+('handle_' if sliders else 'button_')+state+'.png'
    if sliders:
        d['slider_background_texture_normal']=PREFIX+'slider_track.png'
        d['slider_background_texture_highlighted']=PREFIX+'slider_track_hover.png'
    executable=uid(seed+'/actions');d['button_element_executable_block_identifier']=executable
    d['[executable_block:'+executable+'][type:generic]']='[executables:]'
    return block('element',d)
def backdrop(seed,world):
    d=empty_requirements(dict(shape_base),seed)
    d.update(instance_identifier=uid(seed),anchor_point='top-left',x=0,y=0,width=640,height=360,
        stretch_x=True,stretch_y=True,color='#11141077' if world else '#11141099',blur_enabled=True,
        blur_radius=palette['blur'],auto_sizing=False,stay_on_screen=False)
    return block('element',d)
outputs=[]
for screen in screens:
    for world in (False,True):
        seed=screen['identifier']+('/world' if world else '/menu')
        chunks=['type = fancymenu_layout\n\n']
        for typ,data in root:
            d=dict(data)
            if typ=='layout-meta':
                d={k:v for k,v in d.items() if not k.startswith('[loading_requirement')}
                req=uid(seed+'/condition');container=uid(seed+'/layout')
                d.update(identifier=screen['identifier'],render_custom_elements_behind_vanilla=True,layout_index=0 if world else 1)
                d['[loading_requirement_container_meta:'+container+']']='[groups:][instances:'+req+';]'
                d['[loading_requirement:fancymenu_loading_requirement_is_world_loaded][requirement_mode:'+('if' if world else 'if_not')+'][req_id:'+req+']']=''
            elif typ=='menu_background':
                d['instance_identifier']=uid(seed+'/bg/'+d['background_type'])
                d['show_background']=not world and d['background_type']=='slideshow'
            elif typ=='scroll_list_customization':
                d.update(render_scroll_list_header_shadow=False,render_scroll_list_footer_shadow=False,
                    scroll_list_header_texture=PREFIX+'list_header.png',scroll_list_footer_texture=PREFIX+'list_header.png',
                    show_screen_background_overlay_on_custom_background=False,apply_vanilla_background_blur=False)
            chunks.append(block(typ,d))
        chunks.extend([backdrop(seed+'/blur',world),template(seed+'/buttons'),template(seed+'/sliders',True)])
        filename='stm_vanilla_'+uid(screen['identifier'])[:12]+('_world' if world else '_menu')+'.txt'
        content=''.join(chunks)
        (STAGE/filename).write_text(content,encoding='utf-8')
        outputs.append(dict(file=filename,identifier=screen['identifier'],world=world,klass=screen['klass']))
custom=(ORIGINALS/'customizablemenus.txt').read_text('utf-8')
for klass in sorted({s['klass'].replace('/','.') for s in screens}):
    if not re.search(r'^'+re.escape(klass)+r' \{',custom,re.M):custom+=klass+' {\n}\n\n'
(OUT/'customizablemenus.txt').write_text(custom,encoding='utf-8')
manifest=dict(screens=screens,layouts=outputs,palette=palette,original_sha256={
    f.name:hashlib.sha256(f.read_bytes()).hexdigest() for f in ORIGINALS.iterdir()},
    limitations=['Embeddium draws some controls itself; FancyMenu styles its backdrop and native widgets only.',
                 'Native text boxes, tabs, list rows and inventory textures retain their native drawing.',
                 'Screens are not repositioned; blur applies to the background before native content.'])
(OUT/'manifest.json').write_text(json.dumps(manifest,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
print(json.dumps(dict(screens=len(screens),layouts=len(outputs),kinds={k:sum(s['kind']==k for s in screens) for k in ('vanilla','forge','embeddium')})))

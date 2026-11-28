from pathlib import Path
from datetime import datetime
import shutil,hashlib,json,os
OUT=Path(__file__).resolve().parent
CLIENT=Path(r'D:\minecraft\squadMC预发布测试\versions\Squad预发布测试')
FM=CLIENT/'config/fancymenu'
manifest=json.loads((OUT/'manifest.json').read_text('utf-8'))
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
for name,digest in manifest['original_sha256'].items():
    live=FM/name if name=='customizablemenus.txt' else FM/'customization'/name
    assert sha(live)==digest,'Client configuration changed during editing: '+name
for entry in manifest['layouts']:assert not (FM/'customization'/entry['file']).exists()
assets=FM/'assets/stm_vanilla';assert not assets.exists()
backup=CLIENT/'fancymenu-backups'/(datetime.now().strftime('%Y%m%d-%H%M%S')+'-vanilla-menus')
backup.mkdir(parents=True)
shutil.copy2(FM/'customizablemenus.txt',backup/'customizablemenus.txt')
for name in ('title_screen_layout.txt','drippy_loading_overlay_layout.txt'):shutil.copy2(FM/'customization'/name,backup/name)
assets.mkdir()
for f in (OUT/'assets').iterdir():shutil.copy2(f,assets/f.name)
for entry in manifest['layouts']:
    src=OUT/'customization'/entry['file'];dst=FM/'customization'/entry['file']
    tmp=dst.with_suffix('.stm-tmp');shutil.copy2(src,tmp);os.replace(tmp,dst)
    assert sha(src)==sha(dst)
custom=FM/'customizablemenus.txt';tmp=custom.with_suffix('.stm-tmp')
shutil.copy2(OUT/'customizablemenus.txt',tmp);os.replace(tmp,custom)
assert sha(custom)==sha(OUT/'customizablemenus.txt')
for f in (OUT/'assets').iterdir():assert sha(f)==sha(assets/f.name)
for name in ('title_screen_layout.txt','drippy_loading_overlay_layout.txt'):
    assert sha(FM/'customization'/name)==manifest['original_sha256'][name]
record=dict(backup=str(backup),screens=len(manifest['screens']),layouts=len(manifest['layouts']),
            added_files=[str(FM/'customization'/e['file']) for e in manifest['layouts']],
            unchanged=['title_screen_layout.txt','drippy_loading_overlay_layout.txt','options.txt','mods/'])
(OUT/'deployment.json').write_text(json.dumps(record,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
(backup/'deployment.json').write_text(json.dumps(record,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
# Rollback disables only these added layouts; keep files/resources for inspection.
restore=["$menuConfig = '"+str(FM)+"'", "Copy-Item -LiteralPath '"+str(backup/'customizablemenus.txt')+"' -Destination ($menuConfig + '\\customizablemenus.txt') -Force"]
for entry in manifest['layouts']:
    dst=str(FM/'customization'/entry['file']).replace("'","''")
    restore.extend(["$layoutPath = '"+dst+"'", "$layoutText = [System.IO.File]::ReadAllText($layoutPath)",
       "$layoutText = $layoutText.Replace('is_enabled = true', 'is_enabled = false')",
       "[System.IO.File]::WriteAllText($layoutPath, $layoutText, [System.Text.UTF8Encoding]::new($false))"])
(backup/'restore.ps1').write_text('\n'.join(restore)+'\n',encoding='utf-8-sig')
print(json.dumps({k:v for k,v in record.items() if k!='added_files'},ensure_ascii=False))

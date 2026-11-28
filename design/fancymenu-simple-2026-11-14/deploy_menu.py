"""Back up the two edited layouts, install assets first, then atomically replace layouts."""
from pathlib import Path
from datetime import datetime
import shutil,hashlib,json,os
OUT=Path(__file__).resolve().parent
CLIENT=Path(r'D:\minecraft\squadMC预发布测试\versions\Squad预发布测试')
FM=CLIENT/'config/fancymenu'
stamp=datetime.now().strftime('%Y%m%d-%H%M%S')
BACKUP=CLIENT/'fancymenu-backups'/stamp
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
records=[]
for staged in (OUT/'customization').glob('*.txt'):
    live=FM/'customization'/staged.name
    expected=OUT/'originals'/staged.name
    assert sha(live)==sha(expected),'Live layout changed during editing: '+str(live)
    backup=BACKUP/'customization'/staged.name
    backup.parent.mkdir(parents=True,exist_ok=True)
    shutil.copy2(live,backup)
    records.append({'file':str(live),'before_sha256':sha(live),'after_sha256':sha(staged)})

assetdir=FM/'assets/stm_simple'
assert not assetdir.exists(),'New asset folder already exists; inspect before replacing it.'
assetdir.mkdir()
for asset in (OUT/'assets').iterdir():
    shutil.copy2(asset,assetdir/asset.name)
for staged in (OUT/'customization').glob('*.txt'):
    live=FM/'customization'/staged.name
    temp=live.with_suffix('.stm-tmp')
    shutil.copyfile(staged,temp)
    os.replace(temp,live)
    assert sha(live)==sha(staged)
for asset in (OUT/'assets').iterdir():assert sha(assetdir/asset.name)==sha(asset)
manifest=dict(backup=str(BACKUP),files=records,assets=str(assetdir),new_assets=len(list(assetdir.iterdir())))
(BACKUP/'deployment.json').write_text(json.dumps(manifest,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
(OUT/'deployment.json').write_text(json.dumps(manifest,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
restore=("$menuConfig = '"+str(FM)+"'\n$menuBackup = '"+str(BACKUP)+"'\n"+
    "Copy-Item -LiteralPath ($menuBackup + '\\customization\\title_screen_layout.txt') -Destination ($menuConfig + '\\customization\\title_screen_layout.txt') -Force\n"+
    "Copy-Item -LiteralPath ($menuBackup + '\\customization\\drippy_loading_overlay_layout.txt') -Destination ($menuConfig + '\\customization\\drippy_loading_overlay_layout.txt') -Force\n")
(BACKUP/'restore.ps1').write_text(restore,encoding='utf-8-sig')
print(json.dumps(manifest,ensure_ascii=False))

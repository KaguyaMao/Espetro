# 编制载具部署自带弹药全表（47 台，与补给站同规则）

规则：有 `VehicleOverrides` 用专属；未列出的弹药走规则回退（FIXED→`FixedAmount`/100，MAGAZINE→一匣）。

| 载具 | 配置 | 出生自带 |
|---|---|---|
| dragonrise_reforge:ah64 | Default | small_shell_ap×24 , small_rocket×24 , medium_anti_ground_missile×24 , large_anti_ground_missile×4 , medium_anti_air_missile×4 , medical_kit×2 |
| dragonrise_reforge:bmp3 | 专属 | large_shell_he×22 , medium_anti_ground_missile×4 , rifle_ammo×500 , small_shell_ap×200 , small_shell_he×300 , medical_kit×2 |
| dragonrise_reforge:csk181 | 专属 | heavy_ammo×1000 , grenade_40mm×60 , medical_kit×2 |
| dragonrise_reforge:m1126 | 专属 | heavy_ammo×600 , medical_kit×2 |
| dragonrise_reforge:m1128 | 专属 | large_shell_ap×9 , large_shell_he×9 , rifle_ammo×500 , heavy_ammo×600 , medical_kit×2 |
| dragonrise_reforge:m113 | 专属 | heavy_ammo×1000 , medical_kit×2 |
| dragonrise_reforge:m1296 | 专属 | small_shell_ap×100 , small_shell_he×200 , rifle_ammo×500 , medical_kit×2 |
| dragonrise_reforge:m1a2sepv1 | 专属 | large_shell_ap×21 , large_shell_he×21 , rifle_ammo×500 , heavy_ammo×400 , medical_kit×2 |
| dragonrise_reforge:m1a2sepv2 | 专属 | large_shell_ap×21 , large_shell_he×21 , rifle_ammo×500 , heavy_ammo×400 , medical_kit×2 |
| dragonrise_reforge:m3a3 | 专属 | small_shell_ap×70 , small_shell_he×230 , rifle_ammo×500 , medium_anti_ground_missile×2 , medical_kit×2 |
| dragonrise_reforge:mv3 | Default | heavy_ammo×240 , medical_kit×2 |
| dragonrise_reforge:mv3_armed | 专属 | heavy_ammo×1000 , medical_kit×2 |
| dragonrise_reforge:mv3_supply | Default | medical_kit×2 |
| dragonrise_reforge:sx1 | Default | heavy_ammo×240 , medical_kit×2 |
| dragonrise_reforge:sx1_a | Default | medical_kit×2 |
| dragonrise_reforge:t72b3 | 专属 | large_shell_ap×21 , large_shell_he×18 , rifle_ammo×500 , heavy_ammo×500 , medical_kit×2 |
| dragonrise_reforge:t90mh | 专属 | large_shell_ap×21 , large_shell_he×18 , rifle_ammo×500 , heavy_ammo×500 , medical_kit×2 |
| dragonrise_reforge:ural4320 | Default | small_shell_aa×24 , medical_kit×2 |
| dragonrise_reforge:ural4320_supply | Default | small_shell_aa×24 , medical_kit×2 |
| dragonrise_reforge:ural4320_zu23 | Default | small_shell_aa×24 , medical_kit×2 |
| dragonrise_reforge:z20 | Default | medical_kit×2 |
| dragonrise_reforge:zbd04a | 专属 | large_shell_he×22 , medium_anti_ground_missile×4 , rifle_ammo×500 , small_shell_ap×200 , small_shell_he×300 , medical_kit×2 |
| dragonrise_reforge:zbd05 | 专属 | small_shell_ap×160 , small_shell_he×300 , rifle_ammo×500 , medium_anti_ground_missile×2 , medical_kit×2 |
| dragonrise_reforge:zbl08 | 专属 | small_shell_ap×160 , small_shell_he×300 , rifle_ammo×500 , medium_anti_ground_missile×2 , medical_kit×2 |
| dragonrise_reforge:zlt11 | 专属 | large_shell_ap×10 , large_shell_he×10 , rifle_ammo×500 , heavy_ammo×500 , medical_kit×2 |
| dragonrise_reforge:zsl10 | 专属 | heavy_ammo×1000 , medical_kit×2 |
| dragonrise_reforge:ztd05 | 专属 | large_shell_ap×10 , large_shell_he×10 , medium_anti_ground_missile×100 , rifle_ammo×500 , heavy_ammo×500 , medical_kit×2 |
| dragonrise_reforge:ztz96a | 专属 | large_shell_ap×21 , large_shell_he×18 , medium_anti_ground_missile×4 , heavy_ammo×500 , rifle_ammo×500 , medical_kit×2 |
| dragonrise_reforge:ztz99a | 专属 | large_shell_ap×21 , large_shell_he×18 , medium_anti_ground_missile×4 , rifle_ammo×500 , heavy_ammo×500 , medical_kit×2 |
| fcp:bmp1am | 专属 | small_shell_ap×160 , small_shell_he×300 , rifle_ammo×500 , medical_kit×2 |
| fcp:bmp2 | 专属 | medium_anti_ground_missile×3 , small_shell_ap×200 , small_shell_he×300 , rifle_ammo×500 , medical_kit×2 |
| fcp:bmp2d | 专属 | medium_anti_ground_missile×4 , small_shell_ap×200 , small_shell_he×300 , rifle_ammo×500 , medical_kit×2 |
| fcp:bmp2m | 专属 | medium_anti_ground_missile×4 , small_shell_ap×200 , small_shell_he×300 , rifle_ammo×500 , medical_kit×2 |
| fcp:btr80 | 专属 | rifle_ammo×100 , medical_kit×2 |
| fcp:btr82 | 专属 | small_shell_ap×160 , small_shell_he×300 , rifle_ammo×500 , medical_kit×2 |
| fcp:gaz_tigr_gl | 专属 | grenade_40mm×100 , medical_kit×2 |
| fcp:gaz_tigr_mg | 专属 | rifle_ammo×100 , medical_kit×2 |
| fcp:gaz_tigr_rws | 专属 | rifle_ammo×100 , medical_kit×2 |
| fcp:matv | 专属 | rifle_ammo×100 , medical_kit×2 |
| fcp:matv_9in1 | 专属 | rifle_ammo×100 , medical_kit×2 |
| fcp:matv_crow | 专属 | rifle_ammo×100 , medical_kit×2 |
| fcp:matv_tow | 专属 | medium_anti_ground_missile×6 , medical_kit×2 |
| fcp:stryker_dragoon | Default | small_shell_ap×24 , small_shell_he×24 , rifle_ammo×240 , medical_kit×2 |
| fcp:stryker_m2 | Default | rifle_ammo×240 , medical_kit×2 |
| fcp:stryker_mgs | Default | large_shell_ap×12 , large_shell_he×12 , rifle_ammo×240 , medical_kit×2 |
| fcp:stryker_mortar | Default | mortar_shell×24 , medical_kit×2 |
| fcp:t72av | Default | small_shell_ap×1 , small_shell_he×1 , rifle_ammo×240 , medical_kit×2 |

## 没有专属配置的车型（14 台）

- **dragonrise_reforge:ah64**：small_shell_ap×24 , small_rocket×24 , medium_anti_ground_missile×24 , large_anti_ground_missile×4 , medium_anti_air_missile×4 , medical_kit×2
- **dragonrise_reforge:mv3**：heavy_ammo×240 , medical_kit×2
- **dragonrise_reforge:mv3_supply**：medical_kit×2
- **dragonrise_reforge:sx1**：heavy_ammo×240 , medical_kit×2
- **dragonrise_reforge:sx1_a**：medical_kit×2
- **dragonrise_reforge:ural4320**：small_shell_aa×24 , medical_kit×2
- **dragonrise_reforge:ural4320_supply**：small_shell_aa×24 , medical_kit×2
- **dragonrise_reforge:ural4320_zu23**：small_shell_aa×24 , medical_kit×2
- **dragonrise_reforge:z20**：medical_kit×2
- **fcp:stryker_dragoon**：small_shell_ap×24 , small_shell_he×24 , rifle_ammo×240 , medical_kit×2
- **fcp:stryker_m2**：rifle_ammo×240 , medical_kit×2
- **fcp:stryker_mgs**：large_shell_ap×12 , large_shell_he×12 , rifle_ammo×240 , medical_kit×2
- **fcp:stryker_mortar**：mortar_shell×24 , medical_kit×2
- **fcp:t72av**：small_shell_ap×1 , small_shell_he×1 , rifle_ammo×240 , medical_kit×2
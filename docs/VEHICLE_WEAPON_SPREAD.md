# 服务端载具武器散布（Spread）清单

数据源：`dragonrise_reforge-1.5.1-20261102.jar` 内 198 台载具默认值 + 服务端 `kubejs/data/dragonrise_reforge/sbw/vehicles/` 44 份覆盖（后者优先）。

`Spread` 是**腰射散布系数**（越小越准）；`每弹种` 列为该武器 `AmmoType[].Override.Spread` 覆盖值。

| 载具 | 类型 | 槽位 | 武器 | Spread | 每弹种覆盖 | 伤害 | RPM | 在用编制 |
|---|---|---|---|---|---|---|---|---|
| 2s25m | Tank | Cannon | dragonrise_reforge:3BM60 | 0.02 |  | 450 |  |  |
| 2s25m | Tank | MachineGun | dragonrise_reforge:pktm | 0.25 |  | 10 | 700 |  |
| 2s25m | Tank | PassengerMachineGun | dragonrise_reforge:PKTM | 0.25 |  | 40 | 700 |  |
| 2s38 | APC | Cannon | dragonrise_reforge:3UBM22 | 0.02 |  | 550 | 60 |  |
| 2s38 | APC | MachineGun | dragonrise_reforge:pktm | 0.25 |  | 10 | 600 |  |
| 625e | APC | Cannon | weapon.superbwarfare.25mm_cannon_aa | 0.75 |  | 23 | 1200 |  |
| 625e | APC | DriverAAMissile | dragonrise_reforge:missile_fb10 | — |  | 100 |  |  |
| 9m133 | Defense | Missile | dragonrise_reforge:9m133 | — |  | 700 | 8 |  |
| aav7a1 | APC | Mk19 | dragonrise_reforge:mk19 | 1.2 |  | 20 | 350 | usmc_1th_tank、usmc_31th、usmc_4th |
| aav7a1 | APC | M2HB | dragonrise_reforge:M2HB | 0.25 |  | 40 | 600 | usmc_1th_tank、usmc_31th、usmc_4th |
| ac130 | Helicopter | @MG |  | 0.5 |  | 9.5 | 600 |  |
| ac130 | Helicopter | MG1 |  | — |  |  |  |  |
| ac130 | Helicopter | Cannon105 | dragonrise_reforge:105mmHowitzer | 0.03 |  | 300 |  |  |
| ac130 | Helicopter | Cannon40 | dragonrise_reforge:40mmBofors | 0.15 |  | 120 | 120 |  |
| ac130 | Helicopter | Gatling25 | dragonrise_reforge:25mmGatling | 0.3 |  | 12 | 2000 |  |
| ac130 | Helicopter | SeekMissile | dragonrise_reforge:dyh | — |  | 1100 |  |  |
| ah1f | Helicopter | Cannon | dragonrise_reforge:30mmM230 | 0.25 |  | 55 | 625 |  |
| ah1f | Helicopter | Rocket | dragonrise_reforge:70 mm Hydra-70 M247 rocket | 0.25 |  | 85 | 450 |  |
| ah1f | Helicopter | @Missile | dragonrise_reforge:dyh | — |  | 650 | 20 |  |
| ah1f | Helicopter | PassengerMissile | dragonrise_reforge:dyh | — |  | 650 | 20 |  |
| ah1f | Helicopter | SeekMissile | dragonrise_reforge:dyh | — |  | 1100 |  |  |
| ah1f | Helicopter | DriverAAMissile | dragonrise_reforge:aim9 | — |  | 260 |  |  |
| ah64 | Helicopter | Cannon | dragonrise_reforge:30mmM230 | 0.25 |  | 55 | 625 |  |
| ah64 | Helicopter | Rocket | dragonrise_reforge:70 mm Hydra-70 M247 rocket | 0.25 |  | 85 | 450 |  |
| ah64 | Helicopter | @Missile | dragonrise_reforge:dyh | — |  | 650 | 20 |  |
| ah64 | Helicopter | PassengerMissile |  | — |  |  |  |  |
| ah64 | Helicopter | SeekMissile | dragonrise_reforge:ba11_2 | — |  | 1100 |  |  |
| ah64 | Helicopter | DriverAAMissile | dragonrise_reforge:ty90 | — |  | 260 |  |  |
| akm | APC | AAMissile | dragonrise_reforge:missile_9m37 | — |  | 100 |  |  |
| amx56 | Tank | Cannon | dragonrise_reforge:OFL_120_F1 | 0.02 |  | 500 |  |  |
| amx56 | Tank | MachineGun | dragonrise_reforge:CRC_F1 | 0.25 |  | 40 | 575 |  |
| amx56 | Tank | PassengerMachineGun | CRC F1 12,7mm | 0.25 |  | 40 | 600 |  |
| av8b | Airplane | Cannon | dragonrise_reforge:gau12 | 0.5 |  | 35 | 800 |  |
| av8b | Airplane | Rocket | dragonrise_reforge:70 mm Hydra-70 M247 rocket | 0.4 |  | 80 | 450 |  |
| av8b | Airplane | Bomb | dragonrise_reforge:500kg | 0.5 |  |  |  |  |
| av8b | Airplane | Missile | dragonrise_reforge:aim9 | — |  | 260 |  |  |
| ba10 | APC | Cannon | dragonrise_reforge:37mm_ap | 0.03 |  | 90 |  |  |
| ba10 | APC | MachineGun | dragonrise_reforge:dt | — |  | 7 | 600 |  |
| bmd4m | APC | 100MM_Cannon | dragonrise_reforge:3of70 | 0.02 | superbwarfare:medium_anti_ground_missile=0.5 | 200 |  |  |
| bmd4m | APC | MainMachineGun | dragonrise_reforge:pkt | 0.25 |  | 10 | 800 |  |
| bmd4m | APC | Cannon | dragonrise_reforge:30mm_apt_ap | 0.25 |  | 65 | 330 |  |
| bmp3 | APC | 100MM_Cannon | dragonrise_reforge:3of70 | 0.02 | superbwarfare:medium_anti_ground_missile=0.5 | 200 |  | ru_205th、ru_49th |
| bmp3 | APC | MainMachineGun | dragonrise_reforge:pkt | 0.25 |  | 12 | 600 | ru_205th、ru_49th |
| bmp3 | APC | Cannon | dragonrise_reforge:30mm_3ubr8_apds | 0.25 |  | 65 | 330 | ru_205th、ru_49th |
| bmpt72 | APC | Cannon1 | dragonrise_reforge:30mm_apt_ap | 1 |  | 65 | 550 |  |
| bmpt72 | APC | Cannon2 | dragonrise_reforge:30mm_apt_ap | 0.25 |  | 65 | 550 |  |
| bmpt72 | APC | MainMachineGun | weapon.superbwarfare.7_62mm_coax | 0.5 |  | 9.5 | 600 |  |
| bmpt72 | APC | Missile | weapon.superbwarfare.9m120_missile | 0.5 |  | 600 |  |  |
| btr90 | APC | Cannon | dragonrise_reforge:30mm_3ubr8_apds | 0.25 |  | 65 | 550 |  |
| btr90 | APC | MachineGun | dragonrise_reforge:pkt | 0.25 |  | 10 | 600 |  |
| btr90 | APC | Missile | dragonrise_reforge:9m113 | 0.5 |  | 600 | 12 |  |
| btr90 | APC | GrenadeLauncher | dragonrise_reforge:ag17 | 1.2 |  | 55 | 280 |  |
| camel | Airplane | Cannon | dragonrise_reforge:vickers_303 | 0.5 |  | 10 | 600 |  |
| cav | APC | Cannon | weapon.dragonrise_reforge.cav_cannon | 0.03 |  | 380 |  |  |
| challenger_ds | Tank | Cannon | dragonrise_reforge:L27A1 | 0.02 |  | 500 |  |  |
| challenger_ds | Tank | MachineGun | dragonrise_reforge:M240 | 0.25 |  | 10 | 700 |  |
| challenger_ds | Tank | PassengerMachineGun | dragonrise_reforge:M2HB | 0.25 |  | 40 | 575 |  |
| chonmaho5 | Tank | Cannon | dragonrise_reforge:chonmaho5_115mm | 0.03 |  | 400 |  |  |
| chonmaho5 | Tank | MachineGun | dragonrise_reforge:pkt | 0.25 |  | 10 | 700 |  |
| chonmaho5 | Tank | PassengerMachineGun | dragonrise_reforge:kpvt | 0.25 |  | 40 | 575 |  |
| churchill_vii | Tank | Cannon | dragonrise_reforge:75mmAPHE | 0.02 |  | 420 |  |  |
| churchill_vii | Tank | MachineGun | dragonrise_reforge:besa | 0.25 |  | 5 | 900 |  |
| churchill_vii | Tank | PassengerMachineGun | dragonrise_reforge:besa | 0.25 |  | 5 | 900 |  |
| cm34 | APC | Cannon | dragonrise_reforge:30mm_mk258_apfsds | 0.167 |  | 65 | 330 |  |
| cm34 | APC | MachineGun | dragonrise_reforge:t74 | 0.25 |  | 10 | 700 |  |
| cm34 | APC | PassengerMachineGun | dragonrise_reforge:t74 | 0.25 |  | 10 | 800 |  |
| comet | Tank | Cannon | dragonrise_reforge:77mmQF17pdrAPHE | 0.02 |  | 350 |  |  |
| comet | Tank | MachineGun | dragonrise_reforge:besa | 0.25 |  | 5 | 950 |  |
| comet | Tank | PassengerMachineGun | dragonrise_reforge:besa | 0.25 |  | 5 | 950 |  |
| csk181 | Car | PassengerMachineGun | dragonrise_reforge:54T | 0.25 |  | 20 | 600 | pla_118th_brigade |
| csk181 | Car | AutoCannon | dragonrise_reforge:87 | 1.2 |  | 0 | 280 | pla_118th_brigade |
| cv90 | APC | Cannon | dragonrise_reforge:30mm_mk258_apfsds | 0.167 |  | 65 | 200 |  |
| cv90 | APC | MainMachineGun | dragonrise_reforge:m39c | 0.25 |  | 10 | 650 |  |
| cv90 | APC | Missile | dragonrise_reforge::spike | 0.5 |  | 400 | 10 |  |
| cyborg_tank | Tank | Cannon | dragonrise_reforge:fusion_battle_cannon_beam | 0.04 |  | 1500 |  |  |
| cyborg_tank | Tank | MachineGun | dragonrise_reforge:coax_fusion_cycler | 1.0 |  | 35 | 1200 |  |
| cyborg_tank | Tank | PassengerMachineGun | dragonrise_reforge:heavy_fusion_repeater | 1.0 |  | 60 | 400 |  |
| darkbear | Tank | Cannon | dragonrise_reforge:M829A1 | 0.02 |  | 500 |  |  |
| darkbear | Tank | MachineGun | dragonrise_reforge:M240 | 0.25 |  | 10 | 700 |  |
| darkbear | Tank | subcannon | dragonrise_reforge:30mm_mk258_apfsds | 0.167 | superbwarfare:small_shell_gs=3 | 65 | 560 |  |
| darkbear | Tank | PassengerMachineGun | dragonrise_reforge:M2HB | 0.25 |  | 40 | 100 |  |
| dgsw_mk_l2 | Drone | Cannon | weapon.superbwarfare.30mm_2a42_ap | 1 | superbwarfare:small_shell_gs=3 | 45 | 600 |  |
| dgsw_mk_l2 | Drone | Grenade | dragonrise_reforge:mk19 | 1.2 |  | 55 | 280 |  |
| dshk | Defense | Cannon | dragonrise_reforge:DShK | 0.25 |  | 40 | 600 |  |
| ec665 | Helicopter | Cannon | dragonrise_reforge:m871cannon | 0.25 |  | 55 | 750 |  |
| ec665 | Helicopter | Rocket | dragonrise_reforge:23sneb | 0.25 |  | 85 | 450 |  |
| ec665 | Helicopter | @Missile | dragonrise_reforge:dyh | — |  | 650 | 20 |  |
| ec665 | Helicopter | PassengerMissile | dragonrise_reforge:dyh | — |  | 650 | 20 |  |
| ec665 | Helicopter | SeekMissile | dragonrise_reforge:dyh | — |  | 1100 |  |  |
| ec665 | Helicopter | DriverAAMissile | dragonrise_reforge:mistral | — |  | 260 |  |  |
| ef2000 | Airplane | Cannon | dragonrise_reforge:bk27 | 0.5 |  | 35 | 1200 |  |
| ef2000 | Airplane | Pylon1 | dragonrise_reforge:aim9 | — |  | 260 |  |  |
| ef2000 | Airplane | Pylon2 | dragonrise_reforge:aim9 | — |  | 260 |  |  |
| ef2000 | Airplane | Pylon3 | dragonrise_reforge:gbu24 | — |  | 275 |  |  |
| ef2000 | Airplane | Pylon4 | dragonrise_reforge:aim120 | — |  | 1100 |  |  |
| ef2000 | Airplane | Pylon5 | dragonrise_reforge:aim120 | — |  | 1100 |  |  |
| f14 | Airplane | Cannon | dragonrise_reforge:m61a1 | 0.5 |  | 35 | 1200 |  |
| f14 | Airplane | Missile | dragonrise_reforge:aim54 | — |  | 1100 |  |  |
| f14 | Airplane | Missile2 | dragonrise_reforge:aim9 | — |  | 260 |  |  |
| f15e | Airplane | Cannon | dragonrise_reforge:m61a1 | 0.5 |  | 35 | 1200 |  |
| f15e | Airplane | Pylon1 | dragonrise_reforge:aim9 | — |  | 260 |  |  |
| f15e | Airplane | Pylon2 | dragonrise_reforge:aim9 | — |  | 260 |  |  |
| f15e | Airplane | Pylon3 | dragonrise_reforge:aim9 | — |  | 260 |  |  |
| f15e | Airplane | Pylon4 | dragonrise_reforge:aim120 | — |  | 1100 |  |  |
| f15e | Airplane | Pylon5 | dragonrise_reforge:gbu24 | — |  |  |  |  |
| f16c | Airplane | Cannon | dragonrise_reforge:m61a1 | 0.5 |  | 35 | 1200 |  |
| f16c | Airplane | Bomb | dragonrise_reforge:gbu24 | 0.5 |  |  |  |  |
| f16c | Airplane | SeekMissile | weapon.superbwarfare.agm_65_missile_entity | — |  | 1100 |  |  |
| f16c | Airplane | Missile | dragonrise_reforge:aim9 | — |  | 260 |  |  |
| f16c | Airplane | Missile2 | dragonrise_reforge:aim120 | — |  | 1100 |  |  |
| f4u | Airplane | Cannon | dragonrise_reforge:20mm_anm2 | 0.5 |  | 45 | 600 |  |
| f4u | Airplane | Rocket | dragonrise_reforge:hvar | 0.4 |  | 80 | 450 |  |
| f4u | Airplane | Bomb | dragonrise_reforge:airbomb500kg | 0.5 |  |  |  |  |
| fa18e | Airplane | Cannon | dragonrise_reforge:m61a1 | 0.5 |  | 35 | 1200 |  |
| fa18e | Airplane | Missile | dragonrise_reforge:aim9 | — |  | 260 |  |  |
| fa18e | Airplane | Missile2 | dragonrise_reforge:aim120 | — |  | 1100 |  |  |
| fa18e | Airplane | Missile3 | dragonrise_reforge:aim120 | — |  | 1100 |  |  |
| fa18e | Airplane | Missile4 | dragonrise_reforge:aim120 | — |  | 1100 |  |  |
| fav_a | Car | Cannon | dragonrise_reforge:m2hb | 0.6 |  | 5 | 450 |  |
| flarakpz1 | APC | AAMissile | dragonrise_reforge:roland | — |  | 100 |  |  |
| hj8 | Defense | Missile | dragonrise_reforge:hj8 | — |  | 700 | 15 |  |
| humvee | Car | PassengerMachineGun | dragonrise_reforge:m2hb | 0.25 |  | 40 | 600 | usmc_31th、usmc_3th_scout |
| humveetow | Car | Missile | weapon.superbwarfare.tow_missile | — |  | 600 |  |  |
| hyr0 | Tank | Cannon | dragonrise_reforge:hyr0ap | 0.02 |  | 1400 |  |  |
| hyr0 | Tank | MainMachineGun | dragonrise_reforge:hyr020 | 0.5 |  | 55 | 330 |  |
| hyr0 | Tank | PassengerMachineGun | dragonrise_reforge:hyr030 | 0.02 |  | 550 | 60 |  |
| ik_fightcat | Airplane | Cannon | dragonrise_reforge:23mm | 0.25 |  | 55 | 800 |  |
| ik_locust | Airplane | Cannon | dragonrise_reforge:23mm | 0.25 |  | 55 | 400 |  |
| ik_locust | Airplane | Rocket | dragonrise_reforge:huo_fa6 | 0.25 |  | 85 | 450 |  |
| ik_locust | Airplane | @Missile | dragonrise_reforge:akd10 | — |  | 650 | 12 |  |
| ik_locust | Airplane | PassengerMissile |  | — |  |  |  |  |
| ik_locust | Airplane | SeekMissile | dragonrise_reforge:ba11_2 | — |  | 1100 |  |  |
| ik_locust | Airplane | DriverAAMissile | dragonrise_reforge:ty90 | — |  | 260 |  |  |
| is2 | Tank | Cannon | dragonrise_reforge:122mmAPHE | 0.025 |  | 400 |  |  |
| is2 | Tank | MachineGun | dragonrise_reforge:dtm | 0.25 |  | 7 | 600 |  |
| is2 | Tank | PassengerMachineGun | dragonrise_reforge:dshk | 0.25 |  | 40 | 600 |  |
| j10 | Airplane | Cannon | dragonrise_reforge:23mm | 0.5 |  | 35 | 1200 |  |
| j10 | Airplane | Rocket | dragonrise_reforge:huo_fa6 | 0.4 |  | 80 | 450 |  |
| j10 | Airplane | Bomb | dragonrise_reforge:500kg | 0.5 |  |  |  |  |
| j10 | Airplane | Missile | dragonrise_reforge:pl8 | — |  | 260 |  |  |
| j10c | Airplane | Cannon | dragonrise_reforge:23mm | 0.5 |  | 35 | 1200 |  |
| j10c | Airplane | Missile | dragonrise_reforge:pl8 | — |  | 260 |  |  |
| j10c | Airplane | Missile2 | dragonrise_reforge:pl12 | — |  | 1100 |  |  |
| j11 | Airplane | Cannon | dragonrise_reforge:23mm | 0.5 |  | 35 | 1200 |  |
| j11 | Airplane | Missile | dragonrise_reforge:pl8 | — |  | 260 |  |  |
| j11 | Airplane | Missile2 | dragonrise_reforge:pl12 | — |  | 1100 |  |  |
| j11 | Airplane | SeekMissile | dragonrise_reforge:yj88c | — |  | 1100 |  |  |
| j15t | Airplane | Cannon | dragonrise_reforge:23mm | 0.5 |  | 35 | 1200 |  |
| j15t | Airplane | Missile | dragonrise_reforge:pl8 | — |  | 260 |  |  |
| j15t | Airplane | Missile2 | dragonrise_reforge:pl12 | — |  | 1100 |  |  |
| j15t | Airplane | SeekMissile | dragonrise_reforge:yj88c | — |  | 1100 |  |  |
| j16 | Airplane | Cannon | dragonrise_reforge:30mm | 0.5 |  | 35 | 1200 |  |
| j16 | Airplane | Pylon1 | dragonrise_reforge:pl10 | — |  | 260 |  |  |
| j16 | Airplane | Pylon2 | dragonrise_reforge:pl12 | — |  | 1100 |  |  |
| j16 | Airplane | Pylon3 | dragonrise_reforge:pl12 | — |  | 1100 |  |  |
| j16 | Airplane | Pylon4 | dragonrise_reforge:pl15 | — |  | 1100 |  |  |
| j16 | Airplane | Pylon6 | dragonrise_reforge:pl15 | — |  | 1100 |  |  |
| j16 | Airplane | Pylon5 | dragonrise_reforge:pl15 | — |  | 1100 |  |  |
| j20 | Airplane | Missile | dragonrise_reforge:pl10 | — |  | 260 |  |  |
| j20 | Airplane | Missile2 | dragonrise_reforge:pl15 | — |  | 1100 |  |  |
| j20vtol | Airplane | Missile | dragonrise_reforge:pl10 | — |  | 260 |  |  |
| j20vtol | Airplane | Missile2 | dragonrise_reforge:pl15 | — |  | 1100 |  |  |
| j35 | Airplane | Missile2 | dragonrise_reforge:pl15 | — |  | 1100 |  |  |
| j8 | Airplane | Cannon | dragonrise_reforge:23mm | 0.5 |  | 35 | 1200 |  |
| j8 | Airplane | Missile | dragonrise_reforge:pl8 | — |  | 260 |  |  |
| jas39e | Airplane | Cannon | dragonrise_reforge:27mm | 0.5 |  | 35 | 1200 |  |
| jas39e | Airplane | Missile | dragonrise_reforge:aim9 | — |  | 260 |  |  |
| jas39e | Airplane | Missile2 | dragonrise_reforge:aim120 | — |  | 1100 |  |  |
| jas39e | Airplane | Missile3 | dragonrise_reforge:aim120 | — |  | 1100 |  |  |
| jas39e | Airplane | Missile4 | dragonrise_reforge:aim120 | — |  | 1100 |  |  |
| jf17 | Airplane | Cannon | dragonrise_reforge:23mm | 0.5 |  | 35 | 1200 |  |
| jf17 | Airplane | Missile | dragonrise_reforge:pl8 | — |  | 260 |  |  |
| jf17 | Airplane | Missile2 | dragonrise_reforge:pl12 | — |  | 1100 |  |  |
| jh7 | Airplane | Cannon | dragonrise_reforge:23mm | 0.5 |  | 35 | 1200 |  |
| jh7 | Airplane | Pylon1 | dragonrise_reforge:kh29 | — |  | 1100 |  |  |
| jh7 | Airplane | Pylon2 | dragonrise_reforge:gb_250 | — |  | 275 |  |  |
| jh7 | Airplane | Pylon3 | dragonrise_reforge:pl10 | — |  | 260 |  |  |
| jh7 | Airplane | Pylon4 | dragonrise_reforge:gb_250 | — |  | 275 |  |  |
| k1a1 | Tank | Cannon | dragonrise_reforge:K279 | 0.02 |  | 500 |  |  |
| k1a1 | Tank | MachineGun | dragonrise_reforge:M60E2 | 0.25 |  | 10 | 700 |  |
| k1a1 | Tank | PassengerMachineGun | dragonrise_reforge:K6 | 0.25 |  | 40 | 575 |  |
| ka50 | Helicopter | Cannon | weapon.superbwarfare.30mm_2a42 | 0.25 |  | 55 | 600 |  |
| ka50 | Helicopter | Rocket | weapon.superbwarfare.80mm_rocket | 0.5 |  | 125 | 800 |  |
| ka50 | Helicopter | Missile | dragonrise_reforge:9m127 | 0.5 |  | 400 | 120 |  |
| kv1 | Tank | Cannon | dragonrise_reforge:76mmAPHE | 0.025 |  | 350 |  |  |
| kv1 | Tank | MachineGun | dragonrise_reforge:dt | 0.25 |  | 7 | 600 |  |
| kv2 | Tank | Cannon | dragonrise_reforge:152mm_he | 0.05 |  | 300 |  |  |
| kv2 | Tank | MachineGun | dragonrise_reforge:dt | — |  | 7 | 600 |  |
| l1a2 | APC | Cannon | dragonrise_reforge:cannon_35mm_aa | 0.75 |  | 23 | 1200 |  |
| l1a2 | APC | MachineGun | weapon.superbwarfare.7_62mm_coax | 0.5 |  | 9.5 | 600 |  |
| l1a2 | APC | Missile | weapon.superbwarfare.fim_92_missile | — |  | 100 |  |  |
| lcm | Boat | MachineGun | weapon.superbwarfare.50_cal_gun | — |  | 40 | 450 |  |
| lcm | Boat | MachineGun2 | weapon.superbwarfare.50_cal_gun | — |  | 40 | 450 |  |
| leopard2a4 | Tank | Cannon | dragonrise_reforge:DM33 | 0.02 |  | 500 |  |  |
| leopard2a4 | Tank | MachineGun | dragonrise_reforge:MG3 | 0.25 |  | 10 | 700 |  |
| leopard2a4 | Tank | PassengerMachineGun | dragonrise_reforge:M2HB | 0.25 |  | 40 | 575 |  |
| lvt | Tank | Cannon | dragonrise_reforge:M74 | 0.02 |  | 100 |  |  |
| lvt | Tank | MachineGun | dragonrise_reforge:m1919 | 0.25 |  | 5 | 500 |  |
| lvt | Tank | PassengerMachineGun | dragonrise_reforge:m2 | 0.25 |  | 40 | 600 |  |
| m10booker | Tank | Cannon | dragonrise_reforge:M900 | 0.02 |  | 450 |  |  |
| m10booker | Tank | MachineGun | dragonrise_reforge:m240 | 0.25 |  | 10 | 700 |  |
| m10booker | Tank | PassengerMachineGun | dragonrise_reforge:M2HB | 0.25 |  | 40 | 700 |  |
| m1126 | APC | MachineGun | dragonrise_reforge:M2HB | 0.25 |  | 12 | 500 | us_2nd_stryker |
| m1128 | APC | Cannon | dragonrise_reforge:M900 | 0.02 |  | 500 |  | us_2nd_stryker |
| m1128 | APC | MachineGun | dragonrise_reforge:M240 | 0.25 |  | 12 | 650 | us_2nd_stryker |
| m1128 | APC | PassengerMachineGun | dragonrise_reforge:M2HB | 0.25 |  | 20 | 500 | us_2nd_stryker |
| m113 | APC | Cannon | dragonrise_reforge:mk19 | 0.6 |  | 20 | 450 | us_1th_ar、us_1th_ri |
| m1296 | APC | Cannon | dragonrise_reforge:30mm_mk258_apfsds | 0.5 |  | 90 | 200 | us_2nd_stryker、us_redone |
| m1296 | APC | MachineGun | dragonrise_reforge:M240 | 0.25 |  | 12 | 650 | us_2nd_stryker、us_redone |
| m1296 | APC | PassengerMachineGun | dragonrise_reforge:M2HB | 0.25 |  | 10 | 800 | us_2nd_stryker、us_redone |
| m1296 | APC | NewWeapon |  | — |  |  |  | us_2nd_stryker、us_redone |
| m1296 | APC | NewWeapon1 |  | — |  |  |  | us_2nd_stryker、us_redone |
| m1296 | APC | NewWeapon2 |  | — |  |  |  | us_2nd_stryker、us_redone |
| m1a1hc | Tank | Cannon | dragonrise_reforge:M829A1 | 0.02 |  | 500 |  | usmc_1th_tank、usmc_31th、usmc_3th_scout、usmc_4th |
| m1a1hc | Tank | MachineGun | dragonrise_reforge:M240 | 0.25 |  | 10 | 700 | usmc_1th_tank、usmc_31th、usmc_3th_scout、usmc_4th |
| m1a1hc | Tank | PassengerMachineGun | dragonrise_reforge:M2HB | 0.25 |  | 40 | 575 | usmc_1th_tank、usmc_31th、usmc_3th_scout、usmc_4th |
| m1a2sepv1 | Tank | Cannon | dragonrise_reforge:M829A1 | 0.02 |  | 500 |  | us_1th_ri、us_2nd_stryker、us_redone |
| m1a2sepv1 | Tank | MachineGun | dragonrise_reforge:M240 | 0.25 |  | 10 | 700 | us_1th_ri、us_2nd_stryker、us_redone |
| m1a2sepv1 | Tank | hMachineGun | dragonrise_reforge:M2HB | 0.25 |  | 40 | 575 | us_1th_ri、us_2nd_stryker、us_redone |
| m1a2sepv1 | Tank | PassengerMachineGun | dragonrise_reforge:M2HB | 0.25 |  | 40 | 575 | us_1th_ri、us_2nd_stryker、us_redone |
| m1a2sepv2 | Tank | Cannon | dragonrise_reforge:M829A1 | 0.02 |  | 577 |  | us_1th_ar |
| m1a2sepv2 | Tank | MachineGun | dragonrise_reforge:M240 | 0.25 |  | 12 | 700 | us_1th_ar |
| m1a2sepv2 | Tank | PassengerMachineGun | dragonrise_reforge:M2HB | 0.25 |  | 20 | 575 | us_1th_ar |
| m2 | Defense | Cannon | dragonrise_reforge:M2HB | 0.25 |  | 40 | 600 |  |
| m270 | APC | Rocket | dragonrise_reforge:mlrs_rocket | 0.25 |  | 85 | 60 |  |
| m270 | APC | Cannon | dragonrise_reforge:M2HB | 0.6 |  | 5 | 450 |  |
| m3a3 | APC | Cannon | dragonrise_reforge:25mm_m791_apds | 0.07 |  | 115 | 200 | us_1th_ri、us_redone |
| m3a3 | APC | MainMachineGun | dragonrise_reforge:m240 | 0.25 |  | 12 | 650 | us_1th_ri、us_redone |
| m3a3 | APC | Missile | dragonrise_reforge:tow | 0.5 |  | 1000 | 40 | us_1th_ri、us_redone |
| m3stuart | Tank | Cannon | dragonrise_reforge:37mm_m6 | 0.02 |  | 100 |  |  |
| m3stuart | Tank | MachineGun | dragonrise_reforge:m1919 | 0.25 |  | 5 | 500 |  |
| m3stuart | Tank | PassengerMachineGun | dragonrise_reforge:m2 | 0.25 |  | 40 | 600 |  |
| m4a2 | Tank | Cannon | dragonrise_reforge:M61 | 0.02 |  | 250 |  |  |
| m4a2 | Tank | MachineGun | dragonrise_reforge:m1919 | 0.25 |  | 5 | 500 |  |
| m4a2 | Tank | PassengerMachineGun | dragonrise_reforge:m2 | 0.25 |  | 40 | 600 |  |
| m4a2_105 | Tank | @MG |  | 0.5 |  | 9.5 | 600 |  |
| m4a2_105 | Tank | MG1 |  | — |  |  |  |  |
| m4a2_105 | Tank | Cannon | dragonrise_reforge:M1he | 0.02 |  | 150 |  |  |
| m4a2_105 | Tank | MachineGun | dragonrise_reforge:m1919 | 0.25 |  | 5 | 500 |  |
| m4a2_105 | Tank | PassengerMachineGun | dragonrise_reforge:m2 | 0.25 |  | 40 | 600 |  |
| markv | Tank | Cannon | dragonrise_reforge:of6 | 0.02 | superbwarfare:large_shell_gs=5 | 500 |  |  |
| markv | Tank | Cannon1 | dragonrise_reforge:of6 | 0.02 |  | 500 |  |  |
| markv | Tank | @MG |  | 0.5 |  | 9.5 | 600 |  |
| markv | Tank | MG1 |  | — |  |  |  |  |
| markv | Tank | MG2 |  | — |  |  |  |  |
| markv | Tank | MG3 |  | — |  |  |  |  |
| maus | Tank | Cannon | dragonrise_reforge:128mmKwK44APHE | 0.015 |  | 550 |  |  |
| maus | Tank | MachineGun | dragonrise_reforge:mg34 | 0.25 |  | 5 | 950 |  |
| maus | Tank | PassengerMachineGun | dragonrise_reforge:mg34 | 0.25 |  | 5 | 950 |  |
| merkava_mk4 | Tank | Cannon | dragonrise_reforge:M829A1 | 0.02 |  | 500 |  |  |
| merkava_mk4 | Tank | MachineGun | dragonrise_reforge:M240 | 0.25 |  | 10 | 700 |  |
| merkava_mk4 | Tank | hMachineGun | dragonrise_reforge:M2HB | 0.25 |  | 40 | 575 |  |
| merkava_mk4 | Tank | PassengerMachineGun | dragonrise_reforge:M2HB | 0.25 |  | 40 | 575 |  |
| mi24v | Helicopter | Cannon | dragonrise_reforge:mi24v_cannon | 0.25 |  | 18 | 900 |  |
| mi24v | Helicopter | Rocket | dragonrise_reforge:mi24v_rocket | 0.25 |  | 50 | 450 |  |
| mi24v | Helicopter | @Missile | dragonrise_reforge:mi24v_atgm | — |  | 650 | 12 |  |
| mi24v | Helicopter | PassengerMissile | dragonrise_reforge:mi24v_atgm | — |  | 650 | 12 |  |
| mi24v | Helicopter | SeekMissile | dragonrise_reforge:mi24v_asm_entity | — |  | 1100 |  |  |
| mi24v | Helicopter | DriverAAMissile | dragonrise_reforge:mi24v_aam | — |  | 260 |  |  |
| mig29m | Airplane | Cannon | dragonrise_reforge:gsh30 | 0.5 |  | 35 | 1200 |  |
| mig29m | Airplane | Pylon1 | dragonrise_reforge:r73 | — |  | 260 |  |  |
| mig29m | Airplane | Pylon2 | dragonrise_reforge:r77 | — |  | 1100 |  |  |
| mig29m | Airplane | Pylon3 | dragonrise_reforge:r77 | — |  | 1100 |  |  |
| mig29m | Airplane | Pylon4 | dragonrise_reforge:kh29 | — |  | 1100 |  |  |
| mk19 | Defense | Cannon | dragonrise_reforge:mk19 | 1.2 |  | 55 | 280 |  |
| mv3 | Car | Cannon | dragonrise_reforge:mk19 | 0.6 |  | 5 | 450 |  |
| mv3_armed | Car | Cannon | dragonrise_reforge:QJZ89 | 0.25 |  | 40 | 600 |  |
| nh90 | Helicopter | Rocket |  | — |  |  |  |  |
| npds114 | Defense | Main | dragonrise_reforge:type59ap | 0.02 |  | 500 |  |  |
| npds514 | Defense | Main | dragonrise_reforge:type59ap | 0.02 |  | 500 | 60 |  |
| npds810 | Defense | Main | dragonrise_reforge:type59ap | 0.02 |  | 500 |  |  |
| pak40 | Defense | Cannon | dragonrise_reforge:75mmAPHE | 0.02 |  | 280 | 60 |  |
| panzer4 | Tank | Cannon | dragonrise_reforge:75mmAPHE | 0.02 |  | 250 |  |  |
| panzer4 | Tank | MachineGun | dragonrise_reforge:mg42 | 0.25 |  | 5 | 975 |  |
| panzer4 | Tank | PassengerMachineGun | dragonrise_reforge:mg34 | 0.25 |  | 40 | 600 |  |
| pershing | Tank | Cannon | dragonrise_reforge:90mmM3APHE | 0.018 |  | 420 |  |  |
| pershing | Tank | MachineGun | dragonrise_reforge:bmachinegun | 0.26 |  | 5 | 900 |  |
| pershing | Tank | PassengerMachineGun | dragonrise_reforge:bmachinegun | 0.26 |  | 5 | 900 |  |
| project640 | Tank | Cannon | dragonrise_reforge:3BM60 | 0.02 |  | 500 |  |  |
| project640 | Tank | MachineGun | dragonrise_reforge:PKT | 0.25 |  | 10 | 700 |  |
| project640 | Tank | PassengerMachineGun | dragonrise_reforge:NSVT | 0.25 |  | 40 | 700 |  |
| pzbjy | Tank | Cannon | dragonrise_reforge:3BM60 | 0.02 |  | 500 |  |  |
| pzbjy | Tank | PassengerMachineGun | dragonrise_reforge:88C | 0.25 |  | 40 | 700 |  |
| q5 | Airplane | Cannon | dragonrise_reforge:30mm | 0.5 |  | 60 | 800 |  |
| q5 | Airplane | Rocket | dragonrise_reforge:hs70 | 0.4 |  | 120 | 1000 |  |
| q5 | Airplane | Bomb | dragonrise_reforge:500kg | 0.5 |  |  |  |  |
| q5 | Airplane | Missile | dragonrise_reforge:pl8 | — |  | 260 |  |  |
| qjz89 | Defense | Cannon | dragonrise_reforge:QJZ89 | 0.25 |  | 40 | 600 |  |
| refale | Airplane | Cannon | dragonrise_reforge:ref_cannon | 0.5 |  | 35 | 1200 |  |
| refale | Airplane | Pylon1 | dragonrise_reforge:r550 | — |  | 260 |  |  |
| refale | Airplane | Pylon2 | dragonrise_reforge:mica | — |  | 1100 |  |  |
| refale | Airplane | Pylon3 | dragonrise_reforge:aasm | — |  | 275 |  |  |
| refale | Airplane | Pylon4 | dragonrise_reforge:gbu24 | — |  | 275 |  |  |
| sd905 | Helicopter | Cannon | dragonrise_reforge:30mmM230 | 2 |  | 8 | 400 |  |
| sd905 | Helicopter | Bomb | dragonrise_reforge:500kg | 0.5 |  |  | 60 |  |
| shuangweixie | Airplane | Cannon | dragonrise_reforge:23mm | 0.5 |  | 35 | 1200 |  |
| shuangweixie | Airplane | Rocket | dragonrise_reforge:huo_fa6 | 0.4 |  | 80 | 450 |  |
| shuangweixie | Airplane | Bomb | dragonrise_reforge:500kg | 0.5 |  |  |  |  |
| shuangweixie | Airplane | Missile | dragonrise_reforge:pl8 | — |  | 260 |  |  |
| silverhand_mk_iv | Tank | Cannon | dragonrise_reforge:silverhand_40mm_ap | 0.04 |  | 160 |  |  |
| silverhand_mk_iv | Tank | MachineGun | dragonrise_reforge:silverhand_68mm_ap | 0.02 |  | 260 |  |  |
| spacebag | Airplane | Cannon | dragonrise_reforge:23mm | 0.5 |  | 35 | 1200 |  |
| spacebag | Airplane | Rocket | dragonrise_reforge:huo_fa6 | 0.4 |  | 80 | 450 |  |
| spacebag | Airplane | Bomb | dragonrise_reforge:500kg | 0.5 |  |  |  |  |
| spacebag | Airplane | Missile | dragonrise_reforge:pl8 | — |  | 260 |  |  |
| spg9 | Defense | Cannon | dragonrise_reforge:PG9 | 0.02 |  | 240 |  |  |
| strv103 | Tank | Cannon | dragonrise_reforge:m66 | 0.02 |  | 500 |  |  |
| strv103 | Tank | MachineGun | dragonrise_reforge:58mg | 0.25 |  | 10 | 700 |  |
| strv103 | Tank | PassengerMachineGun | dragonrise_reforge:Ksp_m/39 | 0.25 |  | 40 | 700 |  |
| su24m | Airplane | Cannon | dragonrise_reforge:gsh_23 | 0.5 |  | 32 | 800 |  |
| su24m | Airplane | MissileAG | dragonrise_reforge:kh25 | — |  | 1000 |  |  |
| su24m | Airplane | MissileAA | dragonrise_reforge:r60 | — |  | 240 |  |  |
| su30sm | Airplane | Cannon | dragonrise_reforge:30mm | 0.5 |  | 35 | 1200 |  |
| su30sm | Airplane | Pylon1 | dragonrise_reforge:r73 | — |  | 260 |  |  |
| su30sm | Airplane | Pylon2 | dragonrise_reforge:r77 | — |  | 1100 |  |  |
| su30sm | Airplane | Pylon3 | dragonrise_reforge:kh29 | — |  | 1100 |  |  |
| su30sm | Airplane | Pylon4 | dragonrise_reforge:r77 | — |  | 1100 |  |  |
| sx1 | Car | Cannon | dragonrise_reforge:mk19 | 0.6 |  | 5 | 450 |  |
| syy651 | Airplane | Cannon | dragonrise_reforge:23mm | 0.5 |  | 35 | 1200 |  |
| syy651 | Airplane | Missile | dragonrise_reforge:pl12 | — |  | 1100 |  |  |
| t26b | Tank | Cannon | dragonrise_reforge:45mm_ap | 0.03 |  | 140 |  |  |
| t26b | Tank | MachineGun | dragonrise_reforge:dt | — |  | 7 | 600 |  |
| t3476 | Tank | Cannon | dragonrise_reforge:76mmAPHE | 0.02 |  | 250 |  |  |
| t3476 | Tank | MachineGun | dragonrise_reforge:dt | 0.25 |  | 7 | 675 |  |
| t3476 | Tank | PassengerMachineGun | dragonrise_reforge:dtmachinegun | 0.25 |  | 40 | 600 |  |
| t3485 | Tank | Cannon | dragonrise_reforge:85mmZIS-S-53APHE | 0.02 |  | 380 |  |  |
| t3485 | Tank | MachineGun | dragonrise_reforge:dtmachinegun | 0.25 |  | 5 | 950 |  |
| t3485 | Tank | PassengerMachineGun | dragonrise_reforge:dtmachinegun | 0.25 |  | 5 | 950 |  |
| t72b3 | Tank | Cannon | dragonrise_reforge:3BM60 | 0.02 |  | 450 |  | ru_205th、ru_3th、ru_49th、ru_6th_tank |
| t72b3 | Tank | MachineGun | dragonrise_reforge:PKMT | 0.25 |  | 12 | 700 | ru_205th、ru_3th、ru_49th、ru_6th_tank |
| t72b3 | Tank | PassengerMachineGun | dragonrise_reforge:Kord | 0.25 |  | 20 | 700 | ru_205th、ru_3th、ru_49th、ru_6th_tank |
| t80 | Tank | Cannon | dragonrise_reforge:3BM60 | 0.02 |  | 500 |  |  |
| t80 | Tank | MachineGun | dragonrise_reforge:PKT | 0.25 |  | 10 | 700 |  |
| t80 | Tank | PassengerMachineGun | dragonrise_reforge:NSVT | 0.25 |  | 40 | 700 |  |
| t80b | Tank | Cannon | dragonrise_reforge:3BM60 | 0.02 |  | 500 |  |  |
| t80b | Tank | MachineGun | dragonrise_reforge:PKT | 0.25 |  | 10 | 700 |  |
| t80b | Tank | PassengerMachineGun | dragonrise_reforge:NSVT | 0.25 |  | 40 | 700 |  |
| t90mh | Tank | Cannon | dragonrise_reforge:3BM60 | 0.02 |  | 450 |  | ru_6th_tank |
| t90mh | Tank | MachineGun | dragonrise_reforge:PKMT | 0.25 |  | 12 | 700 | ru_6th_tank |
| t90mh | Tank | PassengerMachineGun | dragonrise_reforge:Kord | 0.25 |  | 20 | 700 | ru_6th_tank |
| test | Tank | Cannon | dragonrise_reforge:ofl120f1 | 0.02 |  | 500 |  |  |
| test | Tank | MachineGun | dragonrise_reforge:crcf1 | 0.25 |  | 40 | 575 |  |
| test | Tank | PassengerMachineGun | CRC F1 12,7mm | 0.25 |  | 40 | 600 |  |
| test_ship | Helicopter | Cannon | dragonrise_reforge:30mmM230 | 0.25 |  | 55 | 625 |  |
| test_ship | Helicopter | Rocket | dragonrise_reforge:70 mm Hydra-70 M247 rocket | 0.25 |  | 85 | 450 |  |
| test_ship | Helicopter | @Missile | dragonrise_reforge:dyh | — |  | 650 | 20 |  |
| test_ship | Helicopter | PassengerMissile |  | — |  |  |  |  |
| test_ship | Helicopter | SeekMissile | dragonrise_reforge:ba11_2 | — |  | 1100 |  |  |
| test_ship | Helicopter | DriverAAMissile | dragonrise_reforge:ty90 | — |  | 260 |  |  |
| tiger | Tank | Cannon | dragonrise_reforge:88mmAPHE | 0.02 |  | 400 |  |  |
| tiger | Tank | MachineGun | dragonrise_reforge:mg34 | 0.25 |  | 5 | 975 |  |
| tiger | Tank | PassengerMachineGun | dragonrise_reforge:mg34 | 0.25 |  | 5 | 975 |  |
| tjgc | Airplane | Laser | weapon.superbwarfare.laser_semi | 0 |  | 1200 | 120 |  |
| tjgc | Airplane | Rocket | dragonrise_reforge:tjrocket | 0.4 |  | 960 | 450 |  |
| tjgc | Airplane | BigRocket | dragonrise_reforge:tjbigrocket | 0.4 |  | 160 | 100 |  |
| tjgc | Airplane | Bomb | dragonrise_reforge:neo | 0.5 |  |  |  |  |
| tjgc | Airplane | AAMissile | dragonrise_reforge:tjaa | — |  | 3100 | 400 |  |
| tjgc | Airplane | ATMissile | dragonrise_reforge:tjat | — |  | 6100 |  |  |
| tjgc | Airplane | BigATMissile | dragonrise_reforge:tjbigat | — |  | 9100 |  |  |
| tunguska | APC | Cannon | dragonrise_reforge:cannon_30mm_aa | 0.75 |  | 23 | 2000 |  |
| tunguska | APC | MachineGun | weapon.superbwarfare.7_62mm_coax | 0.5 |  | 9.5 | 600 |  |
| tunguska | APC | Missile | dragonrise_reforge:missile_9m311 | — |  | 120 |  |  |
| type100 | Tank | Cannon | dragonrise_reforge:dtc10105 | 0.02 |  | 500 |  |  |
| type100 | Tank | MachineGun | dragonrise_reforge:QJT | 0.25 |  | 10 | 700 |  |
| type100 | Tank | PassengerMachineGun | dragonrise_reforge:88C | 0.25 |  | 40 | 700 |  |
| type3 | Tank | Cannon | dragonrise_reforge:47mm_type1 | 0.02 |  | 100 |  |  |
| type3 | Tank | MachineGun | dragonrise_reforge:type97_mg | 0.25 |  | 5 | 500 |  |
| type3 | Tank | PassengerMachineGun | dragonrise_reforge:type97_mg | 0.25 |  | 40 | 600 |  |
| type97 | Tank | Cannon | dragonrise_reforge:type94 | 0.02 |  | 100 |  |  |
| type97 | Tank | MachineGun | dragonrise_reforge:type11 | 0.25 |  | 5 | 500 |  |
| type97 | Tank | PassengerMachineGun | dragonrise_reforge:type97_mg | 0.25 |  | 40 | 600 |  |
| type97q | Tank | Cannon | dragonrise_reforge:type1 | 0.02 |  | 100 |  |  |
| type97q | Tank | MachineGun | dragonrise_reforge:type11 | 0.25 |  | 5 | 500 |  |
| type97q | Tank | PassengerMachineGun | dragonrise_reforge:type97_mg | 0.25 |  | 40 | 600 |  |
| uh60 | Helicopter | Rocket |  | — |  |  |  | us_1th_ri、us_2nd_stryker、us_redone、usmc_31th |
| ural4320 | Car | Cannon | weapon.superbwarfare.25mm_cannon_aa | 0.75 |  | 23 | 800 |  |
| ural4320_supply | Car | Cannon | weapon.superbwarfare.25mm_cannon_aa | 0.75 |  | 23 | 800 | ru_205th、ru_3th、ru_49th、ru_6th_tank |
| ural4320_zu23 | Car | Cannon | weapon.superbwarfare.25mm_cannon_aa | 0.75 |  | 23 | 800 |  |
| vt4a1 | Tank | Cannon | dragonrise_reforge:dtc10125 | 0.02 | superbwarfare:medium_anti_ground_missile=0.5 | 500 |  |  |
| vt4a1 | Tank | MachineGun | dragonrise_reforge:QJT | 0.25 |  | 10 | 800 |  |
| vt4a1 | Tank | PassengerMachineGun | dragonrise_reforge:88C | 0.25 |  | 40 | 600 |  |
| vt4b | Tank | Cannon | dragonrise_reforge:dtc10125 | 0.02 | superbwarfare:medium_anti_ground_missile=0.5 | 500 |  |  |
| vt4b | Tank | MachineGun | dragonrise_reforge:QJT | 0.25 |  | 10 | 800 |  |
| vt4b | Tank | PassengerMachineGun | dragonrise_reforge:88C | 0.25 |  | 40 | 600 |  |
| wlhgzu23 | Car | Cannon | dragonrise_reforge:cannon_23mm_aa | 0.75 |  | 23 | 800 |  |
| z10a | Helicopter | Cannon | dragonrise_reforge:23mm | 0.25 |  | 55 | 400 |  |
| z10a | Helicopter | Rocket | dragonrise_reforge:huo_fa6 | 0.25 |  | 85 | 450 |  |
| z10a | Helicopter | @Missile | dragonrise_reforge:akd10 | — |  | 650 | 12 |  |
| z10a | Helicopter | PassengerMissile | dragonrise_reforge:akd10 | — |  | 650 | 12 |  |
| z10a | Helicopter | SeekMissile | dragonrise_reforge:ba11_2 | — |  | 1100 |  |  |
| z10a | Helicopter | DriverAAMissile | dragonrise_reforge:ty90 | — |  | 260 |  |  |
| z10me | Helicopter | Cannon | dragonrise_reforge:23mm | 0.25 |  | 55 | 400 |  |
| z10me | Helicopter | Rocket | dragonrise_reforge:huo_fa6 | 0.25 |  | 85 | 450 |  |
| z10me | Helicopter | @Missile | dragonrise_reforge:akd10 | — |  | 650 | 12 |  |
| z10me | Helicopter | PassengerMissile | dragonrise_reforge:akd10 | — |  | 650 | 12 |  |
| z10me | Helicopter | SeekMissile | dragonrise_reforge:ba11_2 | — |  | 1100 |  |  |
| z10me | Helicopter | DriverAAMissile | dragonrise_reforge:ty90 | — |  | 260 |  |  |
| z20 | Helicopter | Rocket |  | — |  |  |  | pla_112th_brigade、pla_112th_brigade_mesh、pla_118th_brigade、plamc_3th_mesh、plamc_5th、plamc_5th_amphibious_assault、plamc_7th |
| z9 | Helicopter | Rocket |  | — |  |  |  |  |
| zbd04a | APC | 100MM_Cannon | dragonrise_reforge:3of70 | 0.02 | superbwarfare:medium_anti_ground_missile=0.5 | 200 |  | pla_112th_brigade_mesh、pla_118th_brigade |
| zbd04a | APC | MainMachineGun | dragonrise_reforge:QJT | 0.25 |  | 8 | 800 | pla_112th_brigade_mesh、pla_118th_brigade |
| zbd04a | APC | Cannon | dragonrise_reforge:30mm_dtc04_30_apds | 0.167 |  | 75 | 330 | pla_112th_brigade_mesh、pla_118th_brigade |
| zbd05 | APC | Cannon | dragonrise_reforge:30mm_dtc04_30_apds | 0.167 |  | 75 | 330 | plamc_5th、plamc_5th_amphibious_assault |
| zbd05 | APC | MachineGun | dragonrise_reforge:QJT | 0.25 |  | 8 | 800 | plamc_5th、plamc_5th_amphibious_assault |
| zbd05 | APC | Missile | dragonrise_reforge:hj73c | 0.5 |  | 1000 | 30 | plamc_5th、plamc_5th_amphibious_assault |
| zbl08 | APC | Cannon | dragonrise_reforge:30mm_dtc04_30_apds | 0.167 |  | 75 | 330 | pla_112th_brigade、pla_118th_brigade、plamc_7th |
| zbl08 | APC | MachineGun | dragonrise_reforge:QJT | 0.25 |  | 8 | 800 | pla_112th_brigade、pla_118th_brigade、plamc_7th |
| zbl08 | APC | Missile | dragonrise_reforge:hj73c | 0.5 |  | 1000 | 30 | pla_112th_brigade、pla_118th_brigade、plamc_7th |
| zlt11 | APC | Cannon | dragonrise_reforge:dtc10105 | 0.02 | superbwarfare:medium_anti_ground_missile=0.5 | 500 |  | pla_112th_brigade |
| zlt11 | APC | MachineGun | dragonrise_reforge:QJT | 0.25 |  | 8 | 800 | pla_112th_brigade |
| zlt11 | APC | PassengerMachineGun | dragonrise_reforge:W85 | 0.25 |  | 20 | 600 | pla_112th_brigade |
| zsd05 | APC | Cannon | dragonrise_reforge:88C | 0.25 |  | 40 | 600 | plamc_3th_mesh、plamc_5th、plamc_5th_amphibious_assault |
| zsd89a | APC | Cannon | dragonrise_reforge:88C | 0.25 |  | 40 | 600 |  |
| zsd89a_covered | APC | Cannon | dragonrise_reforge:88C | 0.25 |  | 40 | 600 | pla_112th_brigade_mesh、pla_195th |
| zsl10 | APC | MachineGun | dragonrise_reforge:W85 | 0.25 |  | 20 | 600 | pla_112th_brigade、plamc_7th |
| zsu234 | APC | Cannon | dragonrise_reforge:cannon_23mm_aa | 0.75 |  | 23 | 1200 |  |
| zsu234 | APC | DriverAAMissile | dragonrise_reforge:missile_9m37 | — |  | 100 |  |  |
| ztd05 | APC | Cannon | dragonrise_reforge:dtc10105 | 0.02 | superbwarfare:medium_anti_ground_missile=0.5 | 500 |  | plamc_3th_mesh、plamc_5th、plamc_5th_amphibious_assault、plamc_7th |
| ztd05 | APC | MachineGun | dragonrise_reforge:QJT | 0.25 |  | 8 | 800 | plamc_3th_mesh、plamc_5th、plamc_5th_amphibious_assault、plamc_7th |
| ztd05 | APC | PassengerMachineGun | dragonrise_reforge:W85 | 0.25 |  | 20 | 600 | plamc_3th_mesh、plamc_5th、plamc_5th_amphibious_assault、plamc_7th |
| ztq15 | Tank | Cannon | dragonrise_reforge:dtc10105 | 0.02 | superbwarfare:medium_anti_ground_missile=0.5 | 500 |  | plamc_3th_mesh、plamc_5th |
| ztq15 | Tank | MachineGun | dragonrise_reforge:QJT | 0.25 |  | 8 | 800 | plamc_3th_mesh、plamc_5th |
| ztq15 | Tank | PassengerMachineGun | dragonrise_reforge:W85 | 0.25 |  | 40 | 600 | plamc_3th_mesh、plamc_5th |
| ztq15 | Tank | AutoCannon | dragonrise_reforge:87 | 1.2 |  | 0 | 500 | plamc_3th_mesh、plamc_5th |
| ztz59a | Tank | Cannon | dragonrise_reforge:type59ap | 0.02 |  | 500 | 1000 |  |
| ztz59a | Tank | MachineGun | dragonrise_reforge:type54lmg | 0.25 |  | 10 | 800 |  |
| ztz59a | Tank | PassengerMachineGun | dragonrise_reforge:54T | 0.25 |  | 40 | 600 |  |
| ztz96a | Tank | Cannon | dragonrise_reforge:dtc10125 | 0.02 | superbwarfare:medium_anti_ground_missile=0.5 | 500 |  | pla_112th_brigade、pla_112th_brigade_mesh、pla_118th_brigade、pla_195th |
| ztz96a | Tank | MachineGun | dragonrise_reforge:QJT | 0.25 |  | 10 | 700 | pla_112th_brigade、pla_112th_brigade_mesh、pla_118th_brigade、pla_195th |
| ztz96a | Tank | PassengerMachineGun | dragonrise_reforge:88C | 0.25 |  | 40 | 700 | pla_112th_brigade、pla_112th_brigade_mesh、pla_118th_brigade、pla_195th |
| ztz99a | Tank | Cannon | dragonrise_reforge:dtc10125 | 0.02 | superbwarfare:medium_anti_ground_missile=0.5 | 500 |  | pla_195th |
| ztz99a | Tank | MachineGun | dragonrise_reforge:QJT | 0.25 |  | 10 | 800 | pla_195th |
| ztz99a | Tank | PassengerMachineGun | dragonrise_reforge:88C | 0.25 |  | 40 | 600 | pla_195th |
| ztz99ah | Tank | Cannon | dragonrise_reforge:dtc10125 | 0.02 | superbwarfare:medium_anti_ground_missile=0.5 | 500 |  |  |
| ztz99ah | Tank | MachineGun | dragonrise_reforge:QJT | 0.25 |  | 10 | 800 |  |
| ztz99ah | Tank | PassengerMachineGun | dragonrise_reforge:88C | 0.25 |  | 40 | 600 |  |
| zu23 | Defense | Cannon | weapon.superbwarfare.25mm_cannon_aa | 0.75 |  | 35 | 800 |  |

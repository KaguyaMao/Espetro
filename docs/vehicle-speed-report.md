# 服务端载具动力参数（Superb Warfare 物理换算）

数值来源：`kubejs/data/<ns>/sbw/vehicles/*.json`（服主覆盖优先）+ 各 mod jar 内 `data/<ns>/sbw/vehicles/*.json`。

换算依据 `VehicleEngineUtils.kt`（Sound 直行、平地、满油门、无伤）：
- 地面 Track/Wheel：`v = (v + 0.05v) × (0.54 + 0.25|dot|) + 0.15·R·power`
- WheelChair：阻力基数 0.63；Ship（水中）：`v × 1.04 × (0.835 − 0.04·浸没 + 0.005) + 0.11·R·power`（浸没按 0.5 近似）；AirShip：`clamp(0.91499 − 0.01v² + 0.07 + 0.031|dot|, 0.01, 0.99)`、`0.01·R·power`，`powerAdd = Increment × 0.05`（AirShip 为近似值）
- `Increment`/`Decrement` 是**油门爬升速率**（不是车速加速度）；车速加速度 = `推力系数 × R`。
- 换算：1 block/tick = 20 block/s = 72 km/h；1 block/tick² = 400 m/s²（1 格 = 1 米）。

## Track（109 台）

| 载具 | 命名空间:ID | Increment | Decrement | 前进档 | 倒车档 | 极速 km/h | 冲刺 km/h | 倒车 km/h | 0-1s 加速 m/s² | 油门到底 s | 达 95% s |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| 光棱坦克 | superbwarfare:prism_tank | 0.03 | 0.02 | 1.2 | 0.75 | 76.0 | 95.0 | 47.5 | 7.72 | 5.95 | 4.80 |
| ZTZ-99A | dragonrise_reforge:ztz99a | 0.006894 | 0.01 | 1.184 | 0.5368 | 75.0 | 93.7 | 34.0 | 2.08 | 26.00 | 20.05 |
| YX-100 主战坦克 | superbwarfare:yx_100 | 0.02 | 0.01 | 1.15 | 0.6 | 72.8 | 91.1 | 38.0 | 5.31 | 8.95 | 7.05 |
| BMP-3 | dragonrise_reforge:bmp3 | 0.006894 | 0.015 | 1.1051 | 0.3157 | 70.0 | 87.5 | 20.0 | 1.94 | 26.00 | 20.05 |
| ZBD-04A | dragonrise_reforge:zbd04a | 0.006894 | 0.015 | 1.1051 | 0.2684 | 70.0 | 87.5 | 17.0 | 1.94 | 26.00 | 20.05 |
| M1A2 SEPv1 | dragonrise_reforge:m1a2sepv1 | 0.009889 | 0.01 | 1.0735 | 0.6315 | 68.0 | 85.0 | 40.0 | 2.64 | 18.10 | 14.00 |
| M1A2 SEPv2 | dragonrise_reforge:m1a2sepv2 | 0.009889 | 0.01 | 1.0735 | 0.6315 | 68.0 | 85.0 | 40.0 | 2.64 | 18.10 | 14.00 |
| M3A3 布雷德利 | dragonrise_reforge:m3a3 | 0.006894 | 0.018 | 1.0419 | 0.2368 | 66.0 | 82.5 | 15.0 | 1.83 | 26.00 | 20.05 |
| ZBD-05 | dragonrise_reforge:zbd05 | 0.006894 | 0.015 | 1.0262 | 0.35 | 65.0 | 81.3 | 22.2 | 1.80 | 26.00 | 20.05 |
| ZTD-05 | dragonrise_reforge:ztd05 | 0.006894 | 0.015 | 1.0262 | 0.35 | 65.0 | 81.3 | 22.2 | 1.80 | 26.00 | 20.05 |
| BMP-1AM | fcp:bmp1am | 0.006894 | 0.013 | 1.0262 | 0.1737 | 65.0 | 81.3 | 11.0 | 1.80 | 26.00 | 20.05 |
| BMP-2 | fcp:bmp2 | 0.006894 | 0.013 | 1.0262 | 0.1737 | 65.0 | 81.3 | 11.0 | 1.80 | 26.00 | 20.05 |
| BMP-2D | fcp:bmp2d | 0.006894 | 0.013 | 1.0262 | 0.1737 | 65.0 | 81.3 | 11.0 | 1.80 | 26.00 | 20.05 |
| BMP-2M | fcp:bmp2m | 0.006894 | 0.013 | 1.0262 | 0.1737 | 65.0 | 81.3 | 11.0 | 1.80 | 26.00 | 20.05 |
| M113 | dragonrise_reforge:m113 | 0.006894 | 0.015 | 1.0104 | 0.2368 | 64.0 | 80.0 | 15.0 | 1.77 | 26.00 | 20.05 |
| K1A1 主战坦克 | dragonrise_reforge:k1a1 | 0.02 | 0.01 | 0.95 | 0.55 | 60.2 | 75.2 | 34.8 | 4.38 | 8.95 | 7.05 |
| Type 100 | dragonrise_reforge:type100 | 0.018 | 0.055 | 0.95 | 0.405 | 60.2 | 75.2 | 25.7 | 4.00 | 9.95 | 7.80 |
| T-72B3 主战坦克 | dragonrise_reforge:t72b3 | 0.006894 | 0.028 | 0.9472 | 0.0631 | 60.0 | 75.0 | 4.0 | 1.66 | 26.00 | 20.05 |
| T-90M | dragonrise_reforge:t90mh | 0.006894 | 0.028 | 0.9472 | 0.0631 | 60.0 | 75.0 | 4.0 | 1.66 | 26.00 | 20.05 |
| ZSD-89A 履带式装甲输送车（半封闭炮塔） | dragonrise_reforge:zsd89a_covered | 0.006894 | 0.015 | 0.9472 | 0.1421 | 60.0 | 75.0 | 9.0 | 1.66 | 26.00 | 20.05 |
| T72-AV | fcp:t72av | 0.006894 | 0.01 | 0.9472 | 0.0631 | 60.0 | 75.0 | 4.0 | 1.66 | 26.00 | 20.05 |
| ZTZ-96A 主战坦克 | dragonrise_reforge:ztz96a | 0.006894 | 0.028 | 0.9314 | 0.0947 | 59.0 | 73.7 | 6.0 | 1.63 | 26.00 | 20.05 |
| BRM-3K | vvp:brm | 0.025 | 0.015 | 0.9 | 0.5 | 57.0 | 71.3 | 31.7 | 5.00 | 7.15 | 5.70 |
| HYR0 | dragonrise_reforge:hyr0 | 0.0087 | 0.005 | 0.885 | 0.54 | 56.1 | 70.1 | 34.2 | 1.93 | 20.60 | 15.90 |
| 测试实体 | dragonrise_reforge:test | 0.0087 | 0.01 | 0.885 | 0.54 | 56.1 | 70.1 | 34.2 | 1.93 | 20.60 | 15.90 |
| AMX-56 | dragonrise_reforge:amx56 | 0.02 | 0.01 | 0.85 | 0.5 | 53.8 | 67.3 | 31.7 | 3.92 | 8.95 | 7.05 |
| 挑战者DS 主战坦克 | dragonrise_reforge:challenger_ds | 0.02 | 0.01 | 0.85 | 0.5 | 53.8 | 67.3 | 31.7 | 3.92 | 8.95 | 7.05 |
| 灰熊坦克 | dragonrise_reforge:darkbear | 0.02 | 0.01 | 0.85 | 0.5 | 53.8 | 67.3 | 31.7 | 3.92 | 8.95 | 7.05 |
| 豹2A4 主战坦克 | dragonrise_reforge:leopard2a4 | 0.02 | 0.01 | 0.85 | 0.5 | 53.8 | 67.3 | 31.7 | 3.92 | 8.95 | 7.05 |
| M10 布克 | dragonrise_reforge:m10booker | 0.02 | 0.01 | 0.85 | 0.5 | 53.8 | 67.3 | 31.7 | 3.92 | 8.95 | 7.05 |
| M1A1HC | dragonrise_reforge:m1a1hc | 0.02 | 0.01 | 0.85 | 0.5 | 53.8 | 67.3 | 31.7 | 3.92 | 8.95 | 7.05 |
| VT4A1 | dragonrise_reforge:vt4a1 | 0.02 | 0.01 | 0.85 | 0.5 | 53.8 | 67.3 | 31.7 | 3.92 | 8.95 | 7.05 |
| VT4B | dragonrise_reforge:vt4b | 0.02 | 0.01 | 0.85 | 0.5 | 53.8 | 67.3 | 31.7 | 3.92 | 8.95 | 7.05 |
| ZTQ-15 | dragonrise_reforge:ztq15 | 0.02 | 0.01 | 0.85 | 0.5 | 53.8 | 67.3 | 31.7 | 3.92 | 8.95 | 7.05 |
| ZTZ-99AH 主战坦克 | dragonrise_reforge:ztz99ah | 0.02 | 0.01 | 0.85 | 0.5 | 53.8 | 67.3 | 31.7 | 3.92 | 8.95 | 7.05 |
| ZTZ-99B | dragonrise_reforge:ztz99bh | 0.02 | 0.01 | 0.85 | 0.5 | 53.8 | 67.3 | 31.7 | 3.92 | 8.95 | 7.05 |
| BMP-3 | vvp:bmp_3 | 0.025 | 0.015 | 0.85 | 0.5 | 53.8 | 67.3 | 31.7 | 4.73 | 7.15 | 5.70 |
| 2s25 章鱼 | dragonrise_reforge:2s25m | 0.01 | 0.028 | 0.825 | 0.175 | 52.3 | 65.3 | 11.1 | 2.05 | 17.90 | 13.85 |
| Project 640 | dragonrise_reforge:project640 | 0.02 | 0.038 | 0.825 | 0.175 | 52.3 | 65.3 | 11.1 | 3.81 | 8.95 | 7.05 |
| 破障北金鹰 | dragonrise_reforge:pzbjy | 0.01 | 0.028 | 0.825 | 0.175 | 52.3 | 65.3 | 11.1 | 2.05 | 17.90 | 13.85 |
| T-80BVM | dragonrise_reforge:t80 | 0.01 | 0.028 | 0.825 | 0.175 | 52.3 | 65.3 | 11.1 | 2.05 | 17.90 | 13.85 |
| T-80B | dragonrise_reforge:t80b | 0.01 | 0.028 | 0.825 | 0.175 | 52.3 | 65.3 | 11.1 | 2.05 | 17.90 | 13.85 |
| 2S38 | dragonrise_reforge:2s38 | 0.025 | 0.015 | 0.82 | 0.35 | 51.9 | 64.9 | 22.2 | 4.56 | 7.15 | 5.70 |
| AAV7A1 两栖突击车 | dragonrise_reforge:aav7a1 | 0.025 | 0.015 | 0.82 | 0.35 | 51.9 | 64.9 | 22.2 | 4.56 | 7.15 | 5.70 |
| AAVC7C1 两栖指挥车 | dragonrise_reforge:aavc7c1 | 0.025 | 0.015 | 0.82 | 0.35 | 51.9 | 64.9 | 22.2 | 4.56 | 7.15 | 5.70 |
| BMD-4M | dragonrise_reforge:bmd4m | 0.025 | 0.015 | 0.82 | 0.35 | 51.9 | 64.9 | 22.2 | 4.56 | 7.15 | 5.70 |
| BMPT-72 | dragonrise_reforge:bmpt72 | 0.025 | 0.015 | 0.82 | 0.15 | 51.9 | 64.9 | 9.5 | 4.56 | 7.15 | 5.70 |
| CV90 | dragonrise_reforge:cv90 | 0.025 | 0.015 | 0.82 | 0.35 | 51.9 | 64.9 | 22.2 | 4.56 | 7.15 | 5.70 |
| 罗兰3 | dragonrise_reforge:flarakpz1 | 0.009 | 0.039 | 0.82 | 0.27 | 51.9 | 64.9 | 17.1 | 1.85 | 19.90 | 15.40 |
| 猎豹 1A2 自行防空炮 | dragonrise_reforge:l1a2 | 0.025 | 0.015 | 0.82 | 0.35 | 51.9 | 64.9 | 22.2 | 4.56 | 7.15 | 5.70 |
| 2K22通古斯卡 | dragonrise_reforge:tunguska | 0.025 | 0.015 | 0.82 | 0.35 | 51.9 | 64.9 | 22.2 | 4.56 | 7.15 | 5.70 |
| ZSD-89A 履带式装甲输送车 | dragonrise_reforge:zsd89a | 0.025 | 0.015 | 0.82 | 0.35 | 51.9 | 64.9 | 22.2 | 4.56 | 7.15 | 5.70 |
| ZSU-23-4M | dragonrise_reforge:zsu234 | 0.025 | 0.015 | 0.82 | 0.35 | 51.9 | 64.9 | 22.2 | 4.56 | 7.15 | 5.70 |
| BMP-2 履带式步兵战车 | superbwarfare:bmp_2 | 0.025 | 0.015 | 0.82 | 0.52 | 51.9 | 64.9 | 32.9 | 4.56 | 7.15 | 5.70 |
| M2 “布拉德利”履带式步兵战车 | superbwarfare:bradley | 0.025 | 0.015 | 0.82 | 0.52 | 51.9 | 64.9 | 32.9 | 4.56 | 7.15 | 5.70 |
| AJAX | vvp:ajax | 0.025 | 0.015 | 0.82 | 0.4 | 51.9 | 64.9 | 25.3 | 4.56 | 7.15 | 5.70 |
| BMP-2M | vvp:bmp_2m | 0.025 | 0.015 | 0.82 | 0.4 | 51.9 | 64.9 | 25.3 | 4.56 | 7.15 | 5.70 |
| BMPT-3K | vvp:bmpt_3k | 0.025 | 0.015 | 0.82 | 0.2 | 51.9 | 64.9 | 12.7 | 4.56 | 7.15 | 5.70 |
| CV90 | vvp:cv_90 | 0.025 | 0.015 | 0.82 | 0.4 | 51.9 | 64.9 | 25.3 | 4.56 | 7.15 | 5.70 |
| M1A2 Trophy HV | vvp:m1a2_hv | 0.025 | 0.015 | 0.82 | 0.6 | 51.9 | 64.9 | 38.0 | 4.56 | 7.15 | 5.70 |
| M1A2 SEP TUSK II | vvp:m1a2_sep_ii | 0.025 | 0.015 | 0.82 | 0.6 | 51.9 | 64.9 | 38.0 | 4.56 | 7.15 | 5.70 |
| BMPT Terminator | vvp:terminator | 0.025 | 0.015 | 0.82 | 0.2 | 51.9 | 64.9 | 12.7 | 4.56 | 7.15 | 5.70 |
| 9M123 | vvp:chryzantema | 0.025 | 0.015 | 0.8 | 0.5 | 50.7 | 63.3 | 31.7 | 4.45 | 7.15 | 5.70 |
| M1A2主战坦克 | superbwarfare:m_1a_2 | 0.02 | 0.01 | 0.78 | 0.5 | 49.4 | 61.8 | 31.7 | 3.60 | 8.95 | 7.05 |
| T-90A主战坦克 | superbwarfare:t_90a | 0.02 | 0.01 | 0.78 | 0.5 | 49.4 | 61.8 | 31.7 | 3.60 | 8.95 | 7.05 |
| ZTZ-99A主战坦克 | superbwarfare:ztz_99a | 0.02 | 0.01 | 0.78 | 0.5 | 49.4 | 61.8 | 31.7 | 3.60 | 8.95 | 7.05 |
| Bkan 1C 自行火炮 | dragonrise_reforge:bkan1c | 0.012 | 0.007 | 0.77 | 0.4 | 48.8 | 61.0 | 25.3 | 2.26 | 14.90 | 11.60 |
| PLZ-05自行火炮 | superbwarfare:plz_05 | 0.012 | 0.007 | 0.77 | 0.4 | 48.8 | 61.0 | 25.3 | 2.26 | 14.90 | 11.60 |
| Challenger 2 | vvp:challenger | 0.02 | 0.01 | 0.77 | 0.64 | 48.8 | 61.0 | 40.5 | 3.55 | 8.95 | 7.05 |
| Leopard 2A4 | vvp:leopard_2a4 | 0.02 | 0.01 | 0.77 | 0.64 | 48.8 | 61.0 | 40.5 | 3.55 | 8.95 | 7.05 |
| Leopard 2A7V | vvp:leopard_2a7v | 0.02 | 0.01 | 0.77 | 0.64 | 48.8 | 61.0 | 40.5 | 3.55 | 8.95 | 7.05 |
| BMP-2 | vvp:bmp_2 | 0.025 | 0.015 | 0.75 | 0.4 | 47.5 | 59.4 | 25.3 | 4.17 | 7.15 | 5.70 |
| BMP-2 Bakhcha | vvp:bmp_2_bakhcha | 0.025 | 0.015 | 0.75 | 0.4 | 47.5 | 59.4 | 25.3 | 4.17 | 7.15 | 5.70 |
| PUMA VJTF | vvp:puma | 0.025 | 0.015 | 0.75 | 0.2 | 47.5 | 59.4 | 12.7 | 4.17 | 7.15 | 5.70 |
| M270铁雨 | dragonrise_reforge:m270 | 0.0087 | 0.01 | 0.7 | 0.27 | 44.3 | 55.4 | 17.1 | 1.53 | 20.60 | 15.90 |
| T-80 Oplot | vvp:oplot | 0.02 | 0.01 | 0.7 | 0.3 | 44.3 | 55.4 | 19.0 | 3.23 | 8.95 | 7.05 |
| T-90M | vvp:t90_m | 0.02 | 0.01 | 0.7 | 0.3 | 44.3 | 55.4 | 19.0 | 3.23 | 8.95 | 7.05 |
| T-34/76 | dragonrise_reforge:t3476 | 0.0087 | 0.01 | 0.685 | 0.34 | 43.4 | 54.2 | 21.5 | 1.50 | 20.60 | 15.90 |
| M3A3 Bradley | vvp:bradley | 0.025 | 0.015 | 0.67 | 0.4 | 42.4 | 53.0 | 25.3 | 3.72 | 7.15 | 5.70 |
| 天马虎-5 | dragonrise_reforge:chonmaho5 | 0.01 | 0.01 | 0.6 | 0.3 | 38.0 | 47.5 | 19.0 | 1.49 | 17.90 | 13.85 |
| T-72B3M | vvp:t72_b3m | 0.02 | 0.01 | 0.6 | 0.1 | 38.0 | 47.5 | 6.3 | 2.77 | 8.95 | 7.05 |
| LVT | dragonrise_reforge:lvt | 0.015 | 0.01 | 0.585 | 0.34 | 37.1 | 46.3 | 21.5 | 2.10 | 11.95 | 9.30 |
| M3斯图亚特 | dragonrise_reforge:m3stuart | 0.015 | 0.01 | 0.585 | 0.34 | 37.1 | 46.3 | 21.5 | 2.10 | 11.95 | 9.30 |
| M4A2 谢尔曼 | dragonrise_reforge:m4a2 | 0.0077 | 0.01 | 0.585 | 0.34 | 37.1 | 46.3 | 21.5 | 1.14 | 23.30 | 17.95 |
| M4A2 (105mm) | dragonrise_reforge:m4a2_105 | 0.0077 | 0.01 | 0.585 | 0.34 | 37.1 | 46.3 | 21.5 | 1.14 | 23.30 | 17.95 |
| Pz.Kpfw. IV | dragonrise_reforge:panzer4 | 0.0077 | 0.01 | 0.585 | 0.34 | 37.1 | 46.3 | 21.5 | 1.14 | 23.30 | 17.95 |
| 特三式内火艇 | dragonrise_reforge:type3 | 0.015 | 0.01 | 0.585 | 0.34 | 37.1 | 46.3 | 21.5 | 2.10 | 11.95 | 9.30 |
| 九五式战车 | dragonrise_reforge:type97 | 0.015 | 0.01 | 0.585 | 0.34 | 37.1 | 46.3 | 21.5 | 2.10 | 11.95 | 9.30 |
| 九七式中战车 | dragonrise_reforge:type97q | 0.004 | 0.01 | 0.585 | 0.34 | 37.1 | 46.3 | 21.5 | 0.61 | 44.90 | 34.35 |
| AAVP | fcp:aavp | 0.02 | 0.014 | 0.57 | 0.22 | 36.1 | 45.1 | 13.9 | 2.63 | 8.95 | 7.05 |
| BMP-1 | fcp:bmp1 | 0.022 | 0.013 | 0.57 | 0.13 | 36.1 | 45.1 | 8.2 | 2.85 | 8.10 | 6.45 |
| BMP-1P | fcp:bmp1p | 0.022 | 0.013 | 0.57 | 0.13 | 36.1 | 45.1 | 8.2 | 2.85 | 8.10 | 6.45 |
| BMP-1U | fcp:bmp1u | 0.022 | 0.013 | 0.57 | 0.13 | 36.1 | 45.1 | 8.2 | 2.85 | 8.10 | 6.45 |
| BMP-2 | fcp:bmp2_noatgm | 0.022 | 0.013 | 0.57 | 0.13 | 36.1 | 45.1 | 8.2 | 2.85 | 8.10 | 6.45 |
| Comet | dragonrise_reforge:comet | 0.008 | 0.01 | 0.48 | 0.22 | 30.4 | 38.0 | 13.9 | 0.97 | 22.40 | 17.30 |
| T-34-85 | dragonrise_reforge:t3485 | 0.008 | 0.012 | 0.48 | 0.22 | 30.4 | 38.0 | 13.9 | 0.97 | 22.40 | 17.30 |
| M26 Pershing | dragonrise_reforge:pershing | 0.0075 | 0.013 | 0.46 | 0.21 | 29.1 | 36.4 | 13.3 | 0.87 | 23.90 | 18.40 |
| Tiger I | dragonrise_reforge:tiger | 0.007 | 0.01 | 0.42 | 0.2 | 26.6 | 33.3 | 12.7 | 0.75 | 25.65 | 19.75 |
| ZTZ-59A | dragonrise_reforge:ztz59a | 0.02 | 0.07 | 0.4 | 0.1 | 25.3 | 31.7 | 6.3 | 1.85 | 8.95 | 7.05 |
| Churchill VII | dragonrise_reforge:churchill_vii | 0.006 | 0.012 | 0.35 | 0.18 | 22.2 | 27.7 | 11.4 | 0.54 | 29.90 | 23.00 |
| KV-1 | dragonrise_reforge:kv1 | 0.008 | 0.015 | 0.35 | 0.25 | 22.2 | 27.7 | 15.8 | 0.71 | 22.40 | 17.30 |
| Maus | dragonrise_reforge:maus | 0.005 | 0.012 | 0.35 | 0.18 | 22.2 | 27.7 | 11.4 | 0.45 | 35.90 | 27.55 |
| 银手 - Mk. IV 突击坦克 | dragonrise_reforge:silverhand_mk_iv | 0.008 | 0.01 | 0.35 | 0.18 | 22.2 | 27.7 | 11.4 | 0.71 | 22.40 | 17.30 |
| T-26B 轻型坦克 | dragonrise_reforge:t26b | 0.008 | 0.01 | 0.35 | 0.18 | 22.2 | 27.7 | 11.4 | 0.71 | 22.40 | 17.30 |
| 湮灭坦克 | dragonrise_reforge:cyborg_tank | 0.02 | 0.03 | 0.3 | 0.2 | 19.0 | 23.8 | 12.7 | 1.38 | 8.95 | 7.05 |
| IS-2 | dragonrise_reforge:is2 | 0.007 | 0.01 | 0.3 | 0.22 | 19.0 | 23.8 | 13.9 | 0.53 | 25.65 | 19.75 |
| KV-2 重型坦克 | dragonrise_reforge:kv2 | 0.008 | 0.01 | 0.3 | 0.18 | 19.0 | 23.8 | 11.4 | 0.61 | 22.40 | 17.30 |
| 马克5(雄性) | dragonrise_reforge:markv | 0.018 | 0.055 | 0.25 | 0.15 | 15.8 | 19.8 | 9.5 | 1.05 | 9.95 | 7.80 |
| Pak40型反坦克炮 | dragonrise_reforge:pak40 | 0.08 | 0.08 | 0.1 | 0.05 | 6.3 | 7.9 | 3.2 | 1.23 | 2.20 | 2.00 |

## Wheel（91 台）

| 载具 | 命名空间:ID | Increment | Decrement | 前进档 | 倒车档 | 极速 km/h | 冲刺 km/h | 倒车 km/h | 0-1s 加速 m/s² | 油门到底 s | 达 95% s |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| SX-1 | dragonrise_reforge:sx1 | 0.006894 | 0.02 | 1.9 | 0.7 | 120.4 | 156.5 | 44.3 | 3.33 | 26.00 | 20.05 |
| 虎式 MK19 | fcp:gaz_tigr_gl | 0.006894 | 0.02 | 1.8944 | 0.2 | 120.0 | 156.0 | 12.7 | 3.32 | 26.00 | 20.05 |
| 虎式 机枪 | fcp:gaz_tigr_mg | 0.006894 | 0.02 | 1.8944 | 0.2 | 120.0 | 156.0 | 12.7 | 3.32 | 26.00 | 20.05 |
| 虎式 RWS | fcp:gaz_tigr_rws | 0.006894 | 0.02 | 1.8944 | 0.2 | 120.0 | 156.0 | 12.7 | 3.32 | 26.00 | 20.05 |
| humveem2 | fcp:humveem2 | 0.006894 | 0.02 | 1.7839 | 0.33 | 113.0 | 146.9 | 20.9 | 3.13 | 26.00 | 20.05 |
| CSK-181 | dragonrise_reforge:csk181 | 0.006894 | 0.039 | 1.7366 | 0.5 | 110.0 | 143.0 | 31.7 | 3.05 | 26.00 | 20.05 |
| MATV | fcp:matv | 0.006894 | 0.02 | 1.6576 | 0.2 | 105.0 | 136.5 | 12.7 | 2.91 | 26.00 | 20.05 |
| matv_9in1 | fcp:matv_9in1 | 0.006894 | 0.02 | 1.6576 | 0.2 | 105.0 | 136.5 | 12.7 | 2.91 | 26.00 | 20.05 |
| MATV CROWS | fcp:matv_crow | 0.006894 | 0.02 | 1.6576 | 0.2 | 105.0 | 136.5 | 12.7 | 2.91 | 26.00 | 20.05 |
| MATV TOW | fcp:matv_tow | 0.006894 | 0.02 | 1.6576 | 0.2 | 105.0 | 136.5 | 12.7 | 2.91 | 26.00 | 20.05 |
| t14_armata | fcp:t14_armata | 0.025 | 0.02 | 1.62 | 0.33 | 102.6 | 133.4 | 20.9 | 9.01 | 7.15 | 5.70 |
| 丰田海拉克斯 | fcp:toyota_hilux | 0.025 | 0.02 | 1.62 | 0.33 | 102.6 | 133.4 | 20.9 | 9.01 | 7.15 | 5.70 |
| 丰田海拉克斯 BMP | fcp:toyota_hilux_bmp | 0.025 | 0.02 | 1.62 | 0.33 | 102.6 | 133.4 | 20.9 | 9.01 | 7.15 | 5.70 |
| 丰田海拉克斯迫击炮 | fcp:toyota_hilux_mortar | 0.025 | 0.02 | 1.62 | 0.33 | 102.6 | 133.4 | 20.9 | 9.01 | 7.15 | 5.70 |
| 丰田海拉克斯火箭吊舱 | fcp:toyota_hilux_rocket_pod | 0.025 | 0.02 | 1.62 | 0.33 | 102.6 | 133.4 | 20.9 | 9.01 | 7.15 | 5.70 |
| 丰田海拉克斯 SPG-9 | fcp:toyota_hilux_spg9 | 0.025 | 0.02 | 1.62 | 0.33 | 102.6 | 133.4 | 20.9 | 9.01 | 7.15 | 5.70 |
| toyota_hilux_zu23 | fcp:toyota_hilux_zu23 | 0.025 | 0.02 | 1.62 | 0.33 | 102.6 | 133.4 | 20.9 | 9.01 | 7.15 | 5.70 |
| UAZ | fcp:uaz | 0.025 | 0.02 | 1.62 | 0.33 | 102.6 | 133.4 | 20.9 | 9.01 | 7.15 | 5.70 |
| UAZ DSHKA | fcp:uaz_dshka | 0.025 | 0.02 | 1.62 | 0.33 | 102.6 | 133.4 | 20.9 | 9.01 | 7.15 | 5.70 |
| uaz_spg9 | fcp:uaz_spg9 | 0.025 | 0.02 | 1.62 | 0.33 | 102.6 | 133.4 | 20.9 | 9.01 | 7.15 | 5.70 |
| M1126 斯特赖克 | dragonrise_reforge:m1126 | 0.006894 | 0.005 | 1.5787 | 0.2052 | 100.0 | 130.0 | 13.0 | 2.77 | 26.00 | 20.05 |
| M1296 龙骑兵 | dragonrise_reforge:m1296 | 0.006894 | 0.012 | 1.5787 | 0.2052 | 100.0 | 130.0 | 13.0 | 2.77 | 26.00 | 20.05 |
| ZBL-08 | dragonrise_reforge:zbl08 | 0.006894 | 0.012 | 1.5787 | 0.1421 | 100.0 | 130.0 | 9.0 | 2.77 | 26.00 | 20.05 |
| ZLT-11 | dragonrise_reforge:zlt11 | 0.006894 | 0.039 | 1.5787 | 0.1421 | 100.0 | 130.0 | 9.0 | 2.77 | 26.00 | 20.05 |
| 斯特赖克 龙骑兵 | fcp:stryker_dragoon | 0.006894 | 0.02 | 1.5787 | 0.2052 | 100.0 | 130.0 | 13.0 | 2.77 | 26.00 | 20.05 |
| 斯特赖克 M2 | fcp:stryker_m2 | 0.006894 | 0.02 | 1.5787 | 0.2052 | 100.0 | 130.0 | 13.0 | 2.77 | 26.00 | 20.05 |
| 斯特赖克 迫击炮 | fcp:stryker_mortar | 0.006894 | 0.02 | 1.5787 | 0.2052 | 100.0 | 130.0 | 13.0 | 2.77 | 26.00 | 20.05 |
| M1128 机动火炮系统 | dragonrise_reforge:m1128 | 0.006894 | 0.005 | 1.5156 | 0.1894 | 96.0 | 124.8 | 12.0 | 2.66 | 26.00 | 20.05 |
| 斯特赖克 MGS | fcp:stryker_mgs | 0.006894 | 0.02 | 1.5156 | 0.1894 | 96.0 | 124.8 | 12.0 | 2.66 | 26.00 | 20.05 |
| 悍马 | dragonrise_reforge:humvee | 0.015 | 0.039 | 1.5 | 0.5 | 95.0 | 123.5 | 31.7 | 5.39 | 11.95 | 9.30 |
| 陶悍马 | dragonrise_reforge:humveetow | 0.015 | 0.039 | 1.5 | 0.5 | 95.0 | 123.5 | 31.7 | 5.39 | 11.95 | 9.30 |
| 速德优 TenEven9型 皮卡车 | superbwarfare:sodayo_pick_up | 0.024 | 0.012 | 1.48 | 1.1 | 93.7 | 121.9 | 69.7 | 7.96 | 7.45 | 5.90 |
| 速德优 TenEven9型 皮卡车(重机枪) | superbwarfare:sodayo_pick_up_hmg | 0.024 | 0.012 | 1.48 | 1.1 | 93.7 | 121.9 | 69.7 | 7.96 | 7.45 | 5.90 |
| 速德优 TenEven9型 皮卡车(火箭炮) | superbwarfare:sodayo_pick_up_rocket | 0.024 | 0.012 | 1.48 | 1.1 | 93.7 | 121.9 | 69.7 | 7.96 | 7.45 | 5.90 |
| 速德优 TenEven9型 皮卡车(陶氏) | superbwarfare:sodayo_pick_up_tow | 0.024 | 0.012 | 1.48 | 1.1 | 93.7 | 121.9 | 69.7 | 7.96 | 7.45 | 5.90 |
| ZSL-10 轮式装甲车 | dragonrise_reforge:zsl10 | 0.006894 | 0.012 | 1.3893 | 0.1105 | 88.0 | 114.4 | 7.0 | 2.44 | 26.00 | 20.05 |
| BTR-80 | fcp:btr80 | 0.006894 | 0.018 | 1.3735 | 0.1894 | 87.0 | 113.1 | 12.0 | 2.41 | 26.00 | 20.05 |
| MV3 武装战术卡车 | dragonrise_reforge:mv3_armed | 0.015 | 0.015 | 1.35 | 0.5 | 85.5 | 111.2 | 31.7 | 4.85 | 11.95 | 9.30 |
| MV3 补给卡车 | dragonrise_reforge:mv3_supply | 0.015 | 0.015 | 1.35 | 0.5 | 85.5 | 111.2 | 31.7 | 4.85 | 11.95 | 9.30 |
| 诺瓦托尔 | fcp:novator | 0.025 | 0.02 | 1.35 | 0.2 | 85.5 | 111.2 | 12.7 | 7.51 | 7.15 | 5.70 |
| 虎式 | fcp:gaz_tigr | 0.025 | 0.02 | 1.27 | 0.2 | 80.4 | 104.6 | 12.7 | 7.06 | 7.15 | 5.70 |
| BTR82A | fcp:btr82 | 0.006894 | 0.018 | 1.263 | 0.1737 | 80.0 | 104.0 | 11.0 | 2.22 | 26.00 | 20.05 |
| humvee | fcp:humvee | 0.025 | 0.02 | 1.26 | 0.33 | 79.8 | 103.8 | 20.9 | 7.00 | 7.15 | 5.70 |
| humvee_tow | fcp:humvee_tow | 0.025 | 0.02 | 1.26 | 0.33 | 79.8 | 103.8 | 20.9 | 7.00 | 7.15 | 5.70 |
| humveemk19 | fcp:humveemk19 | 0.025 | 0.02 | 1.26 | 0.33 | 79.8 | 103.8 | 20.9 | 7.00 | 7.15 | 5.70 |
| 乌拉尔-4320 | dragonrise_reforge:ural4320 | 0.015 | 0.015 | 1.25 | 0.55 | 79.2 | 102.9 | 34.8 | 4.49 | 11.95 | 9.30 |
| 乌拉尔-4320 补给车 | dragonrise_reforge:ural4320_supply | 0.015 | 0.015 | 1.25 | 0.55 | 79.2 | 102.9 | 34.8 | 4.49 | 11.95 | 9.30 |
| 乌拉尔-4320 ZU-23 自行高炮 | dragonrise_reforge:ural4320_zu23 | 0.015 | 0.015 | 1.25 | 0.55 | 79.2 | 102.9 | 34.8 | 4.49 | 11.95 | 9.30 |
| FH-77BW “弓箭手”自行火炮 | superbwarfare:fh_77bw | 0.01 | 0.009 | 1.25 | 0.75 | 79.2 | 102.9 | 47.5 | 3.11 | 17.90 | 13.85 |
| 泥头车 | superbwarfare:truck | 0.01 | 0.009 | 1.25 | 0.75 | 79.2 | 102.9 | 47.5 | 3.11 | 17.90 | 13.85 |
| MV3 战术卡车 | dragonrise_reforge:mv3 | 0.02 | 0.025 | 1.15 | 0.8 | 72.8 | 94.7 | 50.7 | 5.31 | 8.95 | 7.05 |
| CM-34 | dragonrise_reforge:cm34 | 0.01 | 0.012 | 1.1 | 0.5 | 69.7 | 90.6 | 31.7 | 2.73 | 17.90 | 13.85 |
| 约翰迪尔拖拉机 | fcp:john_deere | 0.025 | 0.02 | 1.1 | 0.2 | 69.7 | 90.6 | 12.7 | 6.12 | 7.15 | 5.70 |
| 卡玛兹 | fcp:kamaz | 0.018 | 0.013 | 1.1 | 0.2 | 69.7 | 90.6 | 12.7 | 4.64 | 9.95 | 7.80 |
| fmtv | fcp:fmtv | 0.012 | 0.009 | 1.08 | 0.4 | 68.4 | 88.9 | 25.3 | 3.17 | 14.90 | 11.60 |
| 乌拉尔 | fcp:ural | 0.012 | 0.009 | 1.08 | 0.4 | 68.4 | 88.9 | 25.3 | 3.17 | 14.90 | 11.60 |
| 乌拉尔 冰雹 | fcp:ural_grad | 0.012 | 0.009 | 1.08 | 0.4 | 68.4 | 88.9 | 25.3 | 3.17 | 14.90 | 11.60 |
| M142 HIMARS | vvp:m142_himars | 0.018 | 0.011 | 1.05 | 0.45 | 66.5 | 86.5 | 28.5 | 4.42 | 9.95 | 7.80 |
| 斯特赖克 MK19 | fcp:stryker_mk19 | 0.025 | 0.02 | 1.02375 | 0.33 | 64.8 | 84.3 | 20.9 | 5.69 | 7.15 | 5.70 |
| 斯特赖克 TOW | fcp:stryker_tow | 0.025 | 0.02 | 1.02375 | 0.33 | 64.8 | 84.3 | 20.9 | 5.69 | 7.15 | 5.70 |
| 625E | dragonrise_reforge:625e | 0.01 | 0.01 | 1 | 0.5 | 63.3 | 82.3 | 31.7 | 2.49 | 17.90 | 13.85 |
| AKM 黄蜂 | dragonrise_reforge:akm | 0.01 | 0.01 | 1 | 0.5 | 63.3 | 82.3 | 31.7 | 2.49 | 17.90 | 13.85 |
| LAV-150 轮式步兵战车 | superbwarfare:lav_150 | 0.03 | 0.02 | 1 | 0.6 | 63.3 | 82.3 | 38.0 | 6.43 | 5.95 | 4.80 |
| Pantsir-S1 | vvp:pantsir_s1 | 0.03 | 0.02 | 1 | 0.5 | 63.3 | 82.3 | 31.7 | 6.43 | 5.95 | 4.80 |
| Pautina | vvp:pautina | 0.03 | 0.02 | 1 | 0.5 | 63.3 | 82.3 | 31.7 | 6.43 | 5.95 | 4.80 |
| FAV-A | dragonrise_reforge:fav_a | 0.015 | 0.02 | 0.98 | 0.7 | 62.1 | 80.7 | 44.3 | 3.52 | 11.95 | 9.30 |
| Motorcycle with Sidecar | dragonrise_reforge:motuo | 0.015 | 0.02 | 0.98 | 0.7 | 62.1 | 80.7 | 44.3 | 3.52 | 11.95 | 9.30 |
| Toyota Century | dragonrise_reforge:toyota_seiki | 0.015 | 0.02 | 0.98 | 0.7 | 62.1 | 80.7 | 44.3 | 3.52 | 11.95 | 9.30 |
| 五菱宏光ZU-23 | dragonrise_reforge:wlhgzu23 | 0.015 | 0.02 | 0.98 | 0.7 | 62.1 | 80.7 | 44.3 | 3.52 | 11.95 | 9.30 |
| 五菱宏光 | dragonrise_reforge:wlsc | 0.015 | 0.02 | 0.98 | 0.7 | 62.1 | 80.7 | 44.3 | 3.52 | 11.95 | 9.30 |
| BTR-3E | fcp:btr3e | 0.025 | 0.018 | 0.95 | 0.32 | 60.2 | 78.2 | 20.3 | 5.28 | 7.15 | 5.70 |
| BTR-4MV1 | fcp:btr4mv1 | 0.025 | 0.018 | 0.95 | 0.32 | 60.2 | 78.2 | 20.3 | 5.28 | 7.15 | 5.70 |
| BTR-80 (防护笼) | fcp:btr80_cope | 0.025 | 0.018 | 0.95 | 0.32 | 60.2 | 78.2 | 20.3 | 5.28 | 7.15 | 5.70 |
| BTR-82A (防护笼) | fcp:btr82_cope | 0.025 | 0.018 | 0.95 | 0.32 | 60.2 | 78.2 | 20.3 | 5.28 | 7.15 | 5.70 |
| LAV-25 | fcp:lav25 | 0.025 | 0.018 | 0.95 | 0.32 | 60.2 | 78.2 | 20.3 | 5.28 | 7.15 | 5.70 |
| LAV-25 轮式步兵战车 | superbwarfare:lav_25 | 0.027 | 0.017 | 0.95 | 0.6 | 60.2 | 78.2 | 38.0 | 5.62 | 6.60 | 5.30 |
| LAV-AD 防空车 | superbwarfare:lav_ad | 0.027 | 0.017 | 0.95 | 0.6 | 60.2 | 78.2 | 38.0 | 5.62 | 6.60 | 5.30 |
| Bushmaster PMV | vvp:bushmaster | 0.022 | 0.012 | 0.95 | 0.45 | 60.2 | 78.2 | 28.5 | 4.75 | 8.10 | 6.45 |
| Humvee (Mk 19) | vvp:humvee_mk19 | 0.03 | 0.015 | 0.95 | 0.5 | 60.2 | 78.2 | 31.7 | 6.11 | 5.95 | 4.80 |
| GAZ Tigr | vvp:gaz_tigr | 0.025 | 0.005 | 0.9 | 0.45 | 57.0 | 74.1 | 28.5 | 5.00 | 7.15 | 5.70 |
| Varta | vvp:varta | 0.025 | 0.015 | 0.9 | 0.45 | 57.0 | 74.1 | 28.5 | 5.00 | 7.15 | 5.70 |
| Varta PTRK | vvp:varta_ptrk | 0.025 | 0.015 | 0.9 | 0.45 | 57.0 | 74.1 | 28.5 | 5.00 | 7.15 | 5.70 |
| FMTV | vvp:fmtv | 0.012 | 0.009 | 0.89 | 0.4 | 56.4 | 73.3 | 25.3 | 2.61 | 14.90 | 11.60 |
| Ural | vvp:ural | 0.012 | 0.009 | 0.89 | 0.4 | 56.4 | 73.3 | 25.3 | 2.61 | 14.90 | 11.60 |
| BTR-3 | vvp:btr_3 | 0.025 | 0.015 | 0.75 | 0.5 | 47.5 | 61.8 | 31.7 | 4.17 | 7.15 | 5.70 |
| BTR-4 | vvp:btr_4 | 0.025 | 0.015 | 0.75 | 0.5 | 47.5 | 61.8 | 31.7 | 4.17 | 7.15 | 5.70 |
| BA-10 装甲汽车 | dragonrise_reforge:ba10 | 0.01 | 0.012 | 0.7 | 0.4 | 44.3 | 57.6 | 25.3 | 1.74 | 17.90 | 13.85 |
| BRDM-2 | dragonrise_reforge:brdm2 | 0.015 | 0.02 | 0.7 | 0.4 | 44.3 | 57.6 | 25.3 | 2.51 | 11.95 | 9.30 |
| Stryker M1128 | vvp:stryker | 0.025 | 0.015 | 0.7 | 0.5 | 44.3 | 57.6 | 31.7 | 3.89 | 7.15 | 5.70 |
| Stryker M1296 Dragoon | vvp:stryker_m1296 | 0.025 | 0.015 | 0.7 | 0.5 | 44.3 | 57.6 | 31.7 | 3.89 | 7.15 | 5.70 |
| Centauro | vvp:centauro | 0.025 | 0.015 | 0.65 | 0.52 | 41.2 | 53.5 | 32.9 | 3.61 | 7.15 | 5.70 |

## WheelChair（2 台）

| 载具 | 命名空间:ID | Increment | Decrement | 前进档 | 倒车档 | 极速 km/h | 冲刺 km/h | 倒车 km/h | 0-1s 加速 m/s² | 油门到底 s | 达 95% s |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| Strv 103 | dragonrise_reforge:strv103 | 0.0097 | 0.01 | 0.825 | 0.175 | 117.2 | 146.5 | 24.9 | 3.06 | 18.50 | 14.70 |
| 轮椅 | superbwarfare:wheel_chair | 0.025 | 0.015 | 0.82 | 0.4 | 116.5 | 145.7 | 56.8 | 7.06 | 7.15 | 6.15 |

## Ship（3 台）

| 载具 | 命名空间:ID | Increment | Decrement | 前进档 | 倒车档 | 极速 km/h | 冲刺 km/h | 倒车 km/h | 0-1s 加速 m/s² | 油门到底 s | 达 95% s |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| 快艇 | superbwarfare:speedboat | 0.035 | 0.022 | 1.5 | 1 | 80.7 | 100.9 | 53.8 | 8.79 | 5.05 | 4.20 |
| 迷你快艇 | superbwarfare:tiny_speedboat | 0.035 | 0.022 | 1.4 | 0.5 | 75.3 | 94.2 | 26.9 | 8.20 | 5.05 | 4.20 |
| LCM 机械化登陆艇 | dragonrise_reforge:lcm | 0.035 | 0.022 | 1.2 | 0.7 | 64.6 | 80.7 | 37.7 | 7.03 | 5.05 | 4.20 |

## AirShip（5 台）

| 载具 | 命名空间:ID | Increment | Decrement | 前进档 | 倒车档 | 极速 km/h | 冲刺 km/h | 倒车 km/h | 0-1s 加速 m/s² | 油门到底 s | 达 95% s |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| 极乐恶魂 | superbwarfare:happiest_ghast | 1 | 1 | 1 | 1 | 72.0 | 90.0 | 72.0 | 1.47 | 3.55 | 15.90 |
| 基洛夫空艇 | superbwarfare:kirov | 0.4 | 0.3 | 0.7 | 0.6 | 50.4 | 63.0 | 43.2 | 0.49 | 8.95 | 17.75 |
| 飞羊羊 | superbwarfare:air_sheep | 0.7 | 0.6 | 0.5 | 0.5 | 36.0 | 45.0 | 36.0 | 0.56 | 5.05 | 16.40 |
| 测试舰船 | dragonrise_reforge:test_ship | 0.01 | 0.01 | 0.4 | 0.2 | 24.9 | 31.2 | 12.5 | 0.01 | — | 172.35 |
| SD 905 | dragonrise_reforge:sd905 | 0.0014 | 0.0014 | 0.4 | 0.2 | 6.8 | 8.5 | 3.4 | 0.00 | — | 186.60 |

## 飞行器（直升机/固定翼，65 台；速度不按地面公式换算）

直升机的水平速度由**俯仰角**决定（`VehicleEngineUtils.helicopterEngine`：`deltaMovement += view * 0.061 * SpeedRate * power`，同时叠加 `0.022 * Lift * sin(俯仰)` 的分量；阻力 `clamp(0.93499 − 0.01|v|² + 0.07·Speed + 0.031|dot|, 0.01, 0.99)`），固定翼则按 `force * SpeedRate` 持续推进，因此不换算成 km/h，只给原始配置。

| 载具 | 命名空间:ID | 类型 | Increment | Decrement | Speed | SpeedRate | 备注 |
|---|---|---|---:|---:|---:|---:|---|
| AC-130 | dragonrise_reforge:ac130 | Aircraft | 0.125 | 0.1 | — | 0.95 |  |
| AH-1F 眼镜蛇 | dragonrise_reforge:ah1f | Helicopter | 0.8 | 0.8 | 0.97 | — |  |
| AH-64 阿帕奇 | dragonrise_reforge:ah64 | Helicopter | 0.8 | 0.8 | 1.2 | — | kubejs/data |
| AV-8B | dragonrise_reforge:av8b | Aircraft | 0.4 | 1 | — | 1.2 |  |
| Camel | dragonrise_reforge:camel | Aircraft | 0.8 | 0.8 | — | 0.5 |  |
| EC665 | dragonrise_reforge:ec665 | Helicopter | 0.8 | 0.8 | 0.97 | — |  |
| F-14 | dragonrise_reforge:f14 | Aircraft | 0.5 | 0.5 | — | 1.2 |  |
| F-15E 攻击鹰 | dragonrise_reforge:f15e | Aircraft | 0.5 | 0.5 | — | 1.2 |  |
| F-16C 战隼 | dragonrise_reforge:f16c | Aircraft | 0.5 | 0.5 | — | 1.2 |  |
| F4U 海盗 | dragonrise_reforge:f4u | Aircraft | 1 | 1 | — | 1 |  |
| F/A-18E 超级大黄蜂 | dragonrise_reforge:fa18e | Aircraft | 0.5 | 0.5 | — | 1.2 |  |
| I.K.-战猫 | dragonrise_reforge:ik_fightcat | Aircraft | 0.8 | 0.8 | — | 1.2 |  |
| I.K.-蝗虫工程船 | dragonrise_reforge:ik_locust | Aircraft | 0.8 | 0.8 | — | 1.2 |  |
| J-10 | dragonrise_reforge:j10 | Aircraft | 0.5 | 0.5 | — | 1.2 |  |
| J-10C | dragonrise_reforge:j10c | Aircraft | 0.5 | 0.5 | — | 1.2 |  |
| J-11B | dragonrise_reforge:j11 | Aircraft | 0.5 | 0.5 | — | 1.2 |  |
| J-15T | dragonrise_reforge:j15t | Aircraft | 0.5 | 0.5 | — | 1.2 |  |
| J-16 | dragonrise_reforge:j16 | Aircraft | 0.5 | 0.5 | — | 1.2 |  |
| J-20 | dragonrise_reforge:j20 | Aircraft | 1.2 | 1 | — | 1.2 |  |
| J-20C (VTOL) | dragonrise_reforge:j20vtol | Aircraft | 1.2 | 1 | — | 0.6 |  |
| J-35 | dragonrise_reforge:j35 | Aircraft | 1.2 | 1 | — | 1.2 |  |
| J-8B | dragonrise_reforge:j8 | Aircraft | 0.5 | 0.5 | — | 1.2 |  |
| JAS 39E | dragonrise_reforge:jas39e | Aircraft | 0.5 | 0.5 | — | 1.2 |  |
| JF-17 | dragonrise_reforge:jf17 | Aircraft | 0.5 | 0.5 | — | 1.2 |  |
| Ka-50 | dragonrise_reforge:ka50 | Helicopter | 0.8 | 0.8 | 0.97 | — |  |
| 米-24P 雌鹿 | dragonrise_reforge:mi24p | Helicopter | 0.8 | 0.8 | 0.97 | — |  |
| 米-24V 雌鹿 | dragonrise_reforge:mi24v | Helicopter | 0.8 | 0.8 | 0.97 | — |  |
| NH90 | dragonrise_reforge:nh90 | Helicopter | 0.8 | 0.8 | 0.97 | — |  |
| Q-5 | dragonrise_reforge:q5 | Aircraft | 1.2 | 1.2 | — | 0.6 |  |
| Rafale | dragonrise_reforge:refale | Aircraft | 0.5 | 0.5 | — | 1.2 |  |
| Rafale | dragonrise_reforge:refaleaa | Aircraft | 0.5 | 0.5 | — | 1.2 |  |
| 太空包 | dragonrise_reforge:spacebag | Aircraft | 0.5 | 0.5 | — | 1.2 |  |
| Su-24M | dragonrise_reforge:su24m | Aircraft | 0.5 | 0.5 | — | 1.15 |  |
| SYY-651 | dragonrise_reforge:syy651 | Aircraft | 0.5 | 0.5 | — | 1.2 |  |
| "TJP-F01“天机”" | dragonrise_reforge:tjgc | Aircraft | 0.5 | 0.5 | — | 3 |  |
| UH-60 | dragonrise_reforge:uh60 | Helicopter | 0.8 | 0.8 | 0.97 | — | kubejs/data |
| Z-10A | dragonrise_reforge:z10a | Helicopter | 0.8 | 0.8 | 0.97 | — |  |
| Z-10ME | dragonrise_reforge:z10me | Helicopter | 0.8 | 0.8 | 0.97 | — |  |
| Z-20 | dragonrise_reforge:z20 | Helicopter | 0.8 | 0.8 | 0.97 | — | kubejs/data |
| Z-9 | dragonrise_reforge:z9 | Helicopter | 0.8 | 0.8 | 0.97 | — |  |
| bigbird | fcp:bigbird | Helicopter | 0.9 | 0.9 | — | — |  |
| 休伊 | fcp:huey | Helicopter | 0.9 | 0.9 | — | — |  |
| 休伊 M134 | fcp:huey_door_gunner_m134 | Helicopter | 0.9 | 0.9 | — | — |  |
| 休伊 M60 | fcp:huey_door_gunner_m60 | Helicopter | 0.9 | 0.9 | — | — |  |
| 休伊 火箭弹 | fcp:huey_rockets | Helicopter | 0.9 | 0.9 | — | — |  |
| la_humvee | fcp:la_humvee | Helicopter | 0.7 | 0.7 | — | — |  |
| MH-6 小鸟 | fcp:littlebird | Helicopter | 0.9 | 0.9 | — | — |  |
| AH-6 小鸟 | fcp:littlebird_armed | Helicopter | 0.9 | 0.9 | — | — |  |
| MI-17 | fcp:mi17 | Helicopter | 0.9 | 0.9 | — | — |  |
| 毒液 | fcp:venom | Helicopter | 0.9 | 0.9 | — | — |  |
| 毒蛇 | fcp:viper | Helicopter | 0.9 | 0.9 | — | — |  |
| A-10 “雷电II” 攻击机 | superbwarfare:a_10a | Aircraft | 1 | 1 | — | 1 |  |
| AC-130H 空中炮艇 | superbwarfare:ac_130h | Aircraft | 0.7 | 0.5 | — | 0.6 |  |
| AH-6 小鸟直升机 | superbwarfare:ah_6 | Helicopter | 1 | 1 | 1.05 | — |  |
| j_16 | superbwarfare:j_16 | Aircraft | 1 | 1 | — | 1 |  |
| Ju-87 斯图卡轰炸机 | superbwarfare:ju_87 | Aircraft | 1.3 | 0.7 | — | 0.7 |  |
| KV-16 “幽灵” 战斗机 | superbwarfare:kv_16 | Aircraft | 2 | 1 | — | 0.6 |  |
| Mi-28 武装直升机 | superbwarfare:mi_28 | Helicopter | 0.8 | 0.8 | 0.97 | — |  |
| 汤姆 F6F | superbwarfare:tom_6 | Tom6 | 1 | 1 | — | 1 |  |
| AH-64 Apache | vvp:ah_64 | Helicopter | 1.08 | 0.8 | 1.35 | — |  |
| AH-1Z | vvp:cobra | Helicopter | 1.22 | 0.9 | 1.35 | — |  |
| Mil Mi-24 | vvp:mi_24 | Helicopter | 1.08 | 0.8 | 1.35 | — |  |
| Mi-28N | vvp:mi_28 | Helicopter | 1.08 | 0.8 | 1.35 | — |  |
| Mi-8MTV | vvp:mi_8 | Helicopter | 1.15 | 0.8 | 1.35 | — | kubejs/data |
| nh90 | vvp:nh90 | Helicopter | 1.01 | 0.75 | 1.35 | — |  |

## 其他（固定武器/无引擎，31 台）

- 短号 反坦克导弹 — dragonrise_reforge:9m133（Fixed）
- DShK — dragonrise_reforge:dshk（Fixed）
- 红箭-8 反坦克导弹 — dragonrise_reforge:hj8（Fixed）
- M2HB — dragonrise_reforge:m2（Fixed）
- MK-19 — dragonrise_reforge:mk19（Fixed）
- NPDS114 自动炮塔 — dragonrise_reforge:npds114（Fixed）
- NPDS514 自动炮塔 — dragonrise_reforge:npds514（Fixed）
- NPDS810 自动炮塔 — dragonrise_reforge:npds810（Fixed）
- QJZ-89 — dragonrise_reforge:qjz89（Fixed）
- 便携式护盾 — dragonrise_reforge:shield（Fixed）
- SPG-9 无后坐力炮 — dragonrise_reforge:spg9（Fixed）
- sx1_a — dragonrise_reforge:sx1_a（无引擎字段）
- T-90M (高架) — dragonrise_reforge:t90mhigh（无引擎字段）
- ZU-23 — dragonrise_reforge:zu23（Fixed）
- example_trailer — fcp:example_trailer（Empty）
- seeder — fcp:seeder（Empty）
- 歼灭者能量炮 — superbwarfare:annihilator（Fixed）
- 130mm/58 BL-132 — superbwarfare:bl_132（Fixed）
- 无人机 — superbwarfare:drone（无引擎字段）
- H/PJ-11近防炮 — superbwarfare:hpj_11（Fixed）
- 激光防御塔 — superbwarfare:laser_tower（Fixed）
- 5''/54 Mk42 — superbwarfare:mk_42（Fixed）
- 138.6mm50 Mle1934 R1938 — superbwarfare:mle_1934（Fixed）
- 迫击炮 — superbwarfare:mortar（Fixed）
- 陶式反坦克导弹发射器 — superbwarfare:tow（Fixed）
- 63式107mm多管火箭炮 — superbwarfare:type_63（无引擎字段）
- 载具装配台 — superbwarfare:vehicle_assembling_table（无引擎字段）
- 波能塔 — superbwarfare:waveforce_tower（Fixed）
- AGS-30 — vvp:ags_30（Fixed）
- D-30 122mm Howitzer — vvp:d30（无引擎字段）
- 9M133 Kornet — vvp:kornet（Fixed）

# 服务端载具动力参数（Superb Warfare 物理换算）

数值来源：`kubejs/data/<ns>/sbw/vehicles/*.json`（服主覆盖优先）+ 各 mod jar 内 `data/<ns>/sbw/vehicles/*.json`。

换算依据 `VehicleEngineUtils.kt`（Sound 直行、平地、满油门、无伤）：
- 地面 Track/Wheel：`v = (v + 0.05v) × (0.54 + 0.25|dot|) + 0.15·R·power`
- WheelChair：阻力基数 0.63；Ship（水中）：`v × 1.04 × (0.835 − 0.04·浸没 + 0.005) + 0.11·R·power`（浸没按 0.5 近似）；AirShip：`clamp(0.91499 − 0.01v² + 0.07 + 0.031|dot|, 0.01, 0.99)`、`0.01·R·power`，`powerAdd = Increment × 0.05`（AirShip 为近似值）
- `Increment`/`Decrement` 是**油门爬升速率**（不是车速加速度）；车速加速度 = `推力系数 × R`。
- 换算：1 block/tick = 20 block/s = 72 km/h；1 block/tick² = 400 m/s²（1 格 = 1 米）。

## Track（18 台）

| 载具 | 命名空间:ID | Increment | Decrement | 前进档 | 倒车档 | 极速 km/h | 冲刺 km/h | 倒车 km/h | 0-1s 加速 m/s² | 油门到底 s | 达 95% s |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| M1A2 SEPv1 | dragonrise_reforge:m1a2sepv1 | 0.02 | 0.01 | 0.85 | 0.5 | 53.8 | 67.3 | 31.7 | 3.92 | 8.95 | 7.05 |
| M1A2 SEPv2 | dragonrise_reforge:m1a2sepv2 | 0.02 | 0.01 | 0.85 | 0.5 | 53.8 | 67.3 | 31.7 | 3.92 | 8.95 | 7.05 |
| ZTZ-99A | dragonrise_reforge:ztz99a | 0.02 | 0.01 | 0.85 | 0.5 | 53.8 | 67.3 | 31.7 | 3.92 | 8.95 | 7.05 |
| T-72B3 主战坦克 | dragonrise_reforge:t72b3 | 0.01 | 0.028 | 0.825 | 0.175 | 52.3 | 65.3 | 11.1 | 2.05 | 17.90 | 13.85 |
| T-90M | dragonrise_reforge:t90mh | 0.01 | 0.028 | 0.825 | 0.1 | 52.3 | 65.3 | 6.3 | 2.05 | 17.90 | 13.85 |
| ZTZ-96A 主战坦克 | dragonrise_reforge:ztz96a | 0.01 | 0.028 | 0.825 | 0.175 | 52.3 | 65.3 | 11.1 | 2.05 | 17.90 | 13.85 |
| BMP-3 | dragonrise_reforge:bmp3 | 0.025 | 0.015 | 0.82 | 0.35 | 51.9 | 64.9 | 22.2 | 4.56 | 7.15 | 5.70 |
| M113 | dragonrise_reforge:m113 | 0.025 | 0.015 | 0.82 | 0.35 | 51.9 | 64.9 | 22.2 | 4.56 | 7.15 | 5.70 |
| ZBD-05 | dragonrise_reforge:zbd05 | 0.025 | 0.015 | 0.82 | 0.35 | 51.9 | 64.9 | 22.2 | 4.56 | 7.15 | 5.70 |
| ZSD-89A 履带式装甲输送车（半封闭炮塔） | dragonrise_reforge:zsd89a_covered | 0.025 | 0.015 | 0.82 | 0.35 | 51.9 | 64.9 | 22.2 | 4.56 | 7.15 | 5.70 |
| ZTD-05 | dragonrise_reforge:ztd05 | 0.025 | 0.015 | 0.82 | 0.35 | 51.9 | 64.9 | 22.2 | 4.56 | 7.15 | 5.70 |
| ZBD-04A | dragonrise_reforge:zbd04a | 0.02 | 0.015 | 0.7 | 0.35 | 44.3 | 55.4 | 22.2 | 3.23 | 8.95 | 7.05 |
| M3A3 布雷德利 | dragonrise_reforge:m3a3 | 0.018 | 0.018 | 0.65 | 0.2 | 41.2 | 51.5 | 12.7 | 2.74 | 9.95 | 7.80 |
| T72-AV | fcp:t72av | 0.0125 | 0.01 | 0.65 | 0.05 | 41.2 | 51.5 | 3.2 | 1.98 | 14.30 | 11.15 |
| BMP-1AM | fcp:bmp1am | 0.022 | 0.013 | 0.57 | 0.13 | 36.1 | 45.1 | 8.2 | 2.85 | 8.10 | 6.45 |
| BMP-2 | fcp:bmp2 | 0.022 | 0.013 | 0.57 | 0.13 | 36.1 | 45.1 | 8.2 | 2.85 | 8.10 | 6.45 |
| BMP-2D | fcp:bmp2d | 0.022 | 0.013 | 0.57 | 0.13 | 36.1 | 45.1 | 8.2 | 2.85 | 8.10 | 6.45 |
| BMP-2M | fcp:bmp2m | 0.022 | 0.013 | 0.57 | 0.13 | 36.1 | 45.1 | 8.2 | 2.85 | 8.10 | 6.45 |

## Wheel（28 台）

| 载具 | 命名空间:ID | Increment | Decrement | 前进档 | 倒车档 | 极速 km/h | 冲刺 km/h | 倒车 km/h | 0-1s 加速 m/s² | 油门到底 s | 达 95% s |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| SX-1 | dragonrise_reforge:sx1 | 0.031 | 0.02 | 1.9 | 0.7 | 120.4 | 156.5 | 44.3 | 12.54 | 5.75 | 4.65 |
| CSK-181 | dragonrise_reforge:csk181 | 0.015 | 0.039 | 1.5 | 0.5 | 95.0 | 123.5 | 31.7 | 5.39 | 11.95 | 9.30 |
| ZLT-11 | dragonrise_reforge:zlt11 | 0.015 | 0.039 | 1.5 | 0.5 | 95.0 | 123.5 | 31.7 | 5.39 | 11.95 | 9.30 |
| MV3 武装战术卡车 | dragonrise_reforge:mv3_armed | 0.015 | 0.015 | 1.35 | 0.5 | 85.5 | 111.2 | 31.7 | 4.85 | 11.95 | 9.30 |
| MV3 补给卡车 | dragonrise_reforge:mv3_supply | 0.015 | 0.015 | 1.35 | 0.5 | 85.5 | 111.2 | 31.7 | 4.85 | 11.95 | 9.30 |
| 虎式 MK19 | fcp:gaz_tigr_gl | 0.025 | 0.02 | 1.27 | 0.2 | 80.4 | 104.6 | 12.7 | 7.06 | 7.15 | 5.70 |
| 虎式 机枪 | fcp:gaz_tigr_mg | 0.025 | 0.02 | 1.27 | 0.2 | 80.4 | 104.6 | 12.7 | 7.06 | 7.15 | 5.70 |
| 虎式 RWS | fcp:gaz_tigr_rws | 0.025 | 0.02 | 1.27 | 0.2 | 80.4 | 104.6 | 12.7 | 7.06 | 7.15 | 5.70 |
| humveem2 | fcp:humveem2 | 0.025 | 0.02 | 1.26 | 0.33 | 79.8 | 103.8 | 20.9 | 7.00 | 7.15 | 5.70 |
| 乌拉尔-4320 | dragonrise_reforge:ural4320 | 0.015 | 0.015 | 1.25 | 0.55 | 79.2 | 102.9 | 34.8 | 4.49 | 11.95 | 9.30 |
| 乌拉尔-4320 补给车 | dragonrise_reforge:ural4320_supply | 0.015 | 0.015 | 1.25 | 0.55 | 79.2 | 102.9 | 34.8 | 4.49 | 11.95 | 9.30 |
| 乌拉尔-4320 ZU-23 自行高炮 | dragonrise_reforge:ural4320_zu23 | 0.015 | 0.015 | 1.25 | 0.55 | 79.2 | 102.9 | 34.8 | 4.49 | 11.95 | 9.30 |
| MV3 战术卡车 | dragonrise_reforge:mv3 | 0.02 | 0.025 | 1.15 | 0.8 | 72.8 | 94.7 | 50.7 | 5.31 | 8.95 | 7.05 |
| M1126 斯特赖克 | dragonrise_reforge:m1126 | 0.0087 | 0.005 | 1.1 | 0.5 | 69.7 | 90.6 | 31.7 | 2.40 | 20.60 | 15.90 |
| M1128 机动火炮系统 | dragonrise_reforge:m1128 | 0.0087 | 0.005 | 1.1 | 0.5 | 69.7 | 90.6 | 31.7 | 2.40 | 20.60 | 15.90 |
| M1296 龙骑兵 | dragonrise_reforge:m1296 | 0.01 | 0.012 | 1.1 | 0.5 | 69.7 | 90.6 | 31.7 | 2.73 | 17.90 | 13.85 |
| ZBL-08 | dragonrise_reforge:zbl08 | 0.01 | 0.012 | 1.1 | 0.5 | 69.7 | 90.6 | 31.7 | 2.73 | 17.90 | 13.85 |
| ZSL-10 轮式装甲车 | dragonrise_reforge:zsl10 | 0.01 | 0.012 | 1.1 | 0.5 | 69.7 | 90.6 | 31.7 | 2.73 | 17.90 | 13.85 |
| MATV | fcp:matv | 0.025 | 0.02 | 1.1 | 0.2 | 69.7 | 90.6 | 12.7 | 6.12 | 7.15 | 5.70 |
| matv_9in1 | fcp:matv_9in1 | 0.025 | 0.02 | 1.1 | 0.2 | 69.7 | 90.6 | 12.7 | 6.12 | 7.15 | 5.70 |
| MATV CROWS | fcp:matv_crow | 0.025 | 0.02 | 1.1 | 0.2 | 69.7 | 90.6 | 12.7 | 6.12 | 7.15 | 5.70 |
| MATV TOW | fcp:matv_tow | 0.025 | 0.02 | 1.1 | 0.2 | 69.7 | 90.6 | 12.7 | 6.12 | 7.15 | 5.70 |
| 斯特赖克 龙骑兵 | fcp:stryker_dragoon | 0.025 | 0.02 | 1.02375 | 0.33 | 64.8 | 84.3 | 20.9 | 5.69 | 7.15 | 5.70 |
| 斯特赖克 M2 | fcp:stryker_m2 | 0.025 | 0.02 | 1.02375 | 0.33 | 64.8 | 84.3 | 20.9 | 5.69 | 7.15 | 5.70 |
| 斯特赖克 MGS | fcp:stryker_mgs | 0.025 | 0.02 | 1.02375 | 0.33 | 64.8 | 84.3 | 20.9 | 5.69 | 7.15 | 5.70 |
| 斯特赖克 迫击炮 | fcp:stryker_mortar | 0.025 | 0.02 | 1.02375 | 0.33 | 64.8 | 84.3 | 20.9 | 5.69 | 7.15 | 5.70 |
| BTR-80 | fcp:btr80 | 0.025 | 0.018 | 0.95 | 0.32 | 60.2 | 78.2 | 20.3 | 5.28 | 7.15 | 5.70 |
| BTR82A | fcp:btr82 | 0.025 | 0.018 | 0.95 | 0.32 | 60.2 | 78.2 | 20.3 | 5.28 | 7.15 | 5.70 |

## 飞行器（直升机/固定翼，4 台；速度不按地面公式换算）

直升机的水平速度由**俯仰角**决定（`VehicleEngineUtils.helicopterEngine`：`deltaMovement += view * 0.061 * SpeedRate * power`，同时叠加 `0.022 * Lift * sin(俯仰)` 的分量；阻力 `clamp(0.93499 − 0.01|v|² + 0.07·Speed + 0.031|dot|, 0.01, 0.99)`），固定翼则按 `force * SpeedRate` 持续推进，因此不换算成 km/h，只给原始配置。

| 载具 | 命名空间:ID | 类型 | Increment | Decrement | Speed | SpeedRate | 备注 |
|---|---|---|---:|---:|---:|---:|---|
| AH-64 阿帕奇 | dragonrise_reforge:ah64 | Helicopter | 0.8 | 0.8 | 1.2 | — | kubejs/data |
| UH-60 | dragonrise_reforge:uh60 | Helicopter | 0.8 | 0.8 | 0.97 | — | kubejs/data |
| Z-20 | dragonrise_reforge:z20 | Helicopter | 0.8 | 0.8 | 0.97 | — | kubejs/data |
| Mi-8MTV | vvp:mi_8 | Helicopter | 1.15 | 0.8 | 1.35 | — | kubejs/data |

## 其他（固定武器/无引擎，10 台）

- 短号 反坦克导弹 — dragonrise_reforge:9m133（Fixed）
- DShK — dragonrise_reforge:dshk（Fixed）
- 红箭-8 反坦克导弹 — dragonrise_reforge:hj8（Fixed）
- M2HB — dragonrise_reforge:m2（Fixed）
- QJZ-89 — dragonrise_reforge:qjz89（Fixed）
- sx1_a — dragonrise_reforge:sx1_a（无引擎字段）
- ZU-23 — dragonrise_reforge:zu23（Fixed）
- 歼灭者能量炮 — superbwarfare:annihilator（Fixed）
- 无人机 — superbwarfare:drone（无引擎字段）
- 陶式反坦克导弹发射器 — superbwarfare:tow（Fixed）

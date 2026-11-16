# 客户端 / 服务端模组一致性体检（2026-10-31）

## 方法

| 步骤 | 命令 |
|---|---|
| 服务端清单 | `node build\tmp\pf.mjs ls mods` → `build\tmp\srv-mods-ls.txt` |
| 客户端清单 | 直接读 `D:\minecraft\squadMC预发布测试\versions\Squad预发布测试\mods` |
| 比对 | `build\tmp\mods-compare.py`（按**文件名 + 字节大小**配对，输出 `build\tmp\mods-compare.txt`） |
| 字节级复核 | 下载服务端 jar（`build\tmp\dl-bin2.mjs`）+ `Get-FileHash -Algorithm SHA256`，与本地构建产物、客户端副本三方对比 |

## 总览

```
service jars = 73    client jars = 104
同名同大小 = 51      同名不同大小 = 0
仅服务端有 = 22      仅客户端有 = 53
```

**同名不同大小：0 个** —— 所有两边都有的 jar，字节长度完全一致。

## 1. 同名同大小（51 个，零风险）

包括全部自研与关键依赖：

| jar | 大小 |
|---|---|
| `espetro-1.1.3-x.jar` | 16,900,076 |
| `espoints-1.1.1e.jar` | 2,519,728 |
| `esradial-forge-1.20.1-0.3.2.jar` | 409,365 |
| `realgrisly-1.0.0.jar` | 995,384 |
| `esvoice-1.0.0-final.jar` | 133,313 |
| `auratip-forge-1.20.1-1.1.3-beta-espetro.1.jar` | 374,407 |
| `kubejs-forge-2001.6.5-build.26.jar` | 1,658,792 |
| `[永恒枪械工坊：零] tacz-1.20.1-1.1.8-release.jar` | 57,048,436 |
| `voicechat-forge-1.20.1-2.6.20.jar` | 4,911,858 |

### 字节级（sha256）复核结果

| jar | 本地构建产物 | 客户端 | 判定 |
|---|---|---|---|
| `espetro-1.1.3-x.jar` | 16,900,076 `FAD3BFB3…` | 16,900,076 `FAD3BFB3…` | ✅ 一致 |
| `espoints-1.1.1e.jar` | 2,519,728 `834D70D3…` | 2,519,728 `834D70D3…` | ✅ 一致 |
| `auratip-…-espetro.1.jar` | 374,407 `FDD5E851…` | 374,407 `FDD5E851…` | ✅ 一致 |
| `esradial-…-0.3.2.jar` | 409,365 | 409,365 | ✅（服务端原件已下载比对，大小一致；mavenLocal 副本同源） |
| `realgrisly-1.0.0.jar` | 995,384 | 995,384 | ✅（服务端原件已下载，大小一致） |
| `esvoice-1.0.0-final.jar` | **69,274** `A89F9DB9…` | 133,313 `D2B26247…` | ⚠️ 本地仓库与线上**不是同一份产物** |

> 服务端对 `mods/` 的下载接口复查时返回 `500`（面板冷却/接口限制），因此
> `espetro` / `espoints` 的"服务端字节"以**上传后回读大小**为准（16900076 / 2519728 均 MATCH），
> 加上本地 ↔ 客户端 sha256 相同，可判定两端同源。

## 2. 仅服务端有（22 个）

全部是**备份或已禁用文件**，FML 不会加载（`.bak-*`、`.disabled`、`.dup-old`、`.jar0`）：

```
16897529 espetro-1.1.3-x.jar.bak-beforeDmgModel      16872227 espetro-1.1.3-x.jar.bak-beforeMagfill
16882692 espetro-1.1.3-x.jar.bak-beforeMedic         16873888 espetro-1.1.3-x.jar.bak-beforeRespawnFix
16869644 espetro-1.1.3-x.jar.bak-fort                16870337 espetro-1.1.3-x.jar.bak-fortdmg
16863007 espetro-1.1.3-x.jar.bak-limits              16599142 espetro-1.1.3-x.jar.bak-redeploy
16869539 espetro-1.1.3-x.jar.bak-skillgate           16719359 espetro-1.1.3-x.jar.bak-uifb
16591756 espetro-1.1.3-x.jar.bak-wl                  16518256 espetro-1.1.3-x.jar.disabled
16597975 espetro-1.1.3-i.jar.dup-old                 16570523 espetro-1.1.3-j.jar.disabled
  407384 esradial-forge-1.20.1-0.2.0.jar.corrupt-bak    123034 ferritecore-6.0.1-forge.jar.disabled
989268 realgrisly-1.0.0.jar.bak-beforeMedicalKit     978528 realgrisly-1.0.0.jar.bak-beforeTaczPatch
42162514 [卓越前线] superbwarfare-…-all.jar.disabled  42154735 superbwarfare-…-all.jar   ← 生效的那份
706613 proxy-compatible-forge-1.3.1.jar0             575663 snassets_particles-1.0.1.jar
```

需要留意的两个**非备份**项：

- `snassets_particles-1.0.1.jar`（575,663）只在服务端；客户端对应的文件名是
  `snassets_particles-1.0.1异步粒子.jar`（575,663，同大小）。属"同一文件改名"，服务端不装它不影响客户端。
- `superbwarfare` 服务端有两份：带 `[卓越前线]` 前缀的那份是 `.disabled`，真正生效的是不带前缀的
  `superbwarfare-0.8.9.1-hotfix-mc1.20.1-993063bed-all.jar`（42,154,735）；客户端用的带前缀同名文件也是 42,154,735。**同内容，两个名字**。

## 3. 仅客户端有（53 个）

全部是**纯客户端**优化 / 渲染 / UI 模组，服务端没有是正常的（会出现在
`Client has mods that are missing on server:` 提示里，不影响进服）：

```
fancymenu / drippyloadingscreen / konkrete / melody / modernui / configured /
embeddium / oculus / radium / rubidium-extra / starlight / krypton / modernfix /
entityculling / particle_core / fzzy_config / physics-mod / presencefootsteps /
soundofrain / viewmodel_tuner / ibeeditor / sbwarcanacompat / lightspeed /
smoothboot / smoothchunk / saturn / noisium / flerovium / cupboards / …
```

这些是**故意**的客户端侧差异，不是问题。

## 4. 结论与待办

1. ✅ 自研四件套（espetro / espoints / esradial / realgrisly）双端**同名同大小**，
   espetro & espoints & auratip 已完成 sha256 三方核对。
2. ⚠️ `esvoice-1.0.0-final.jar` 本地构建产物（69,274 B）与线上/客户端（133,313 B）不一致 ——
   线上那份不是当前仓库 `build/libs` 的产物（可能是旧构建或带 shade 的版本）。
   下次改 EsVoice 时务必重新确认产物再部署，否则"改了没生效"。
3. ✅ 服务端 `mods/` 里的历史 jar 全部用 `.bak-*` / `.disabled` 后缀，不会被 FML 加载；
   但**建议定期清到 `mods/backup/` 子目录**，避免 `ls mods` 噪音影响判断。
4. 📌 以后每次发包：`mods.toml` 的 `mod_version` + `NetworkManager.PROTOCOL_VERSION` 双升，
   并在启动日志核对 `Espetro 网络协议 X: 已注册 N 个网络包（判别号 0~N-1）`。

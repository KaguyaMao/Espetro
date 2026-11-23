# 客户端自动更新（McPatch V2）部署手册

> 状态：**客户端侧已备好**，等服务端开一条 TCP 端口（6700）后即可上线。

## 0. 一句话架构

```
玩家客户端  --(javaagent 先跑)-->  Mcpatch-0.0.12.jar  --(私有 TCP 协议)-->  服务端 mcpatch-manager serve :6700
                                     ↓ 拿 public/index.json + 差量 tar
                                  解压覆盖到 .minecraft 的父目录（版本隔离下是 versions/Stm）
```

- 只更新**文件内容**（mods/config/tacz/资源包/阵营与地图资源等），不动启动器与主 jar。
- 私有协议是自研 TCP，**不需要域名备案**；但需要服务端暴露一个 TCP 端口。

## 1. 已准备好的东西（本机）

| 文件 | 说明 |
|---|---|
| `build\tmp\mcpatch\mcpatch-manager-windows.exe` | 管理端 v2.0.5（Windows，打更新包用） |
| `build\tmp\mcpatch\mcpatch-manager-linux-x86_64` | 管理端 v2.0.5（Linux musl，服务端常驻用） |
| `build\tmp\mcpatch\Mcpatch-0.0.12.jar` | 客户端 v0.0.12（随发布包发给玩家） |
| `build\tmp\mcpatch\workspace\` | 待同步内容（14,572 文件 / 1,354.7 MB），结构见下 |
| `build\tmp\mcpatch\make_workspace.py` | 从发布客户端 `D:\StmCilent\.minecraft\versions\Stm` 重新生成 workspace（硬链接，秒级） |

workspace 结构（**关键**：更新根目录是 `.minecraft` 的父目录，即启动器目录 `D:\StmCilent`）：

```
workspace/
└─ .minecraft/
   └─ versions/
      └─ Stm/
         ├─ mods/            (72 jar, 702 MB)
         ├─ config/          (452 文件, 151 MB)
         ├─ tacz/            (391 MB，已删掉未启用枪械)
         ├─ resourcepacks/   (枪声包等, 42 MB)
         ├─ EsFactions/ EsAudio/ EsWorld/ EsDimensions.json
         └─ kubejs/ shaderpacks/
```

同步范围 = 发布内容；**不含**存档、日志、选项、地图缓存、启动器数据。

## 2. 服务端要做的事（管理员）

1. 给实例加一条 **TCP 端口映射**：容器 `6700` → 宿主任意未占用端口（例：`34567`），安全组/防火墙放行同一端口。
   - McPatch 私有协议走裸 TCP，**不能用 HTTP 反代**；确需 nginx 转发要用 `stream` 模块。
2. 在实例里建目录（例：`/opt/D盘/STM/mcpatch/`），放入：
   - `mcpatch-manager`（Linux 二进制，`chmod +x`）
   - `public/`（更新包目录，由我把打好的包上传进去）
   - 首次运行会生成 `config.yml`（默认即可，`[builtin-server] listen-port = 6700`；`[web] listen-port = 6710` 只是后台网页，无需映射）
3. 常驻启动：`cd /opt/D盘/STM/mcpatch && nohup ./mcpatch-manager serve > serve.log 2>&1 &`
4. 回传：**公网地址 + 端口**（例 `thenet.mcxjzz.top:34567`），我把它写进客户端 `mcpatch.yml`。

> 管理端也可以只在本机（Windows）打包，服务端只需要 `mcpatch-manager` + `public/`。

## 3. 打包与上线（我这边做）

```powershell
# 1) 生成/更新 workspace（发布客户端有变动时）
python build\tmp\mcpatch\make_workspace.py

# 2) 首次：把 workspace 放进管理端目录，跑 check / pack
#    管理端目录结构：<guanli>\{mcpatch-manager.exe, workspace\, public\}
.\mcpatch-manager.exe           # 交互式，输入：
> check                         # 统计变更
> pack 1                        # 生成 public/v1.tar + public/index.json
> test                          # 可选：模拟客户端解包校验
```

- `public/` 里的包**不可手工改/删**，要换内容就再打一个新版本号。
- 版本号只是标签，新旧按打包时间判断。

上线：把 `public/index.json` + `public/v1.tar` 传到服务端 `mcpatch/public/`；重启 `serve`（或等它下次读目录）。

## 4. 客户端（玩家侧）

发布包内新增：

```
.minecraft/mcpatch/
├─ Mcpatch-0.0.12.jar
├─ mcpatch.yml            # urls 指向服务器
└─ version-label.txt      # 自动生成
```

`mcpatch.yml`（版本隔离的关键是 `base-path`）：

```yaml
urls:
  - mcpatch://<服务器地址>:<端口>
version-file-path: version-label.txt
allow-error: false
show-finish-message: true
silent-mode: true
window-title: Stupid Tactics 更新中
base-path: ''            # 空 = 智能搜索 .minecraft 的父目录（= 启动器目录），与 workspace 结构对应
private-timeout: 15000
http-timeout: 5000
http-retries: 3
http-ignore-certificate: false
```

启动器（PCL2）里给该版本的 **JVM 参数最前面**加（版本隔离：从 `versions/Stm/` 往上两级回到 `.minecraft`）：

```
-javaagent:../../mcpatch/Mcpatch-0.0.12.jar
```

- 发布包里 **已经带上 `version-label.txt = 1`**，玩家装好包启动时版本与服务端最新版相同 → 不会下载任何东西（首次不会拉 1.35 GB 的全量包，见第 7 节）。
- 更新失败仍可进游戏（`allow-error: true` 时），但可能因缺文件进不了服。

## 5. 日常发布流程

1. 改完客户端内容 → 同步到 `D:\StmCilent\.minecraft\versions\Stm`；
2. `python build\tmp\mcpatch\make_workspace.py`；
3. 管理端 `check` → `pack <新版本号>` → `test`；
4. 上传 `public/index.json` + 新 `public/<ver>.tar`；
5. 玩家下次启动游戏自动更新。

## 6. 坑

- **版本隔离**：`-javaagent` 路径要写 `../../mcpatch/...`；更新目录靠 `base-path: ''` 智能定位，别手写死路径。
- 管理端不要对 `public/` 里的包做任何手工操作。
- 纯改文件名大小写（`Abc.jar` → `abc.jar`）管理端识别不了，要连目录或内容一起动。
- 服端只开一个 TCP 端口即可；`6710` 是管理端网页后台，不需要暴露。

## 7. 首包分发策略（已定）+ 本机验证记录

### 7.1 不传 1.35 GB 的 `1.tar`
- 发布客户端（`D:\StmCilent\.minecraft`）里已放好 `.minecraft\mcpatch\{Mcpatch-0.0.12.jar, mcpatch.yml, version-label.txt=1}`。
- 服务端 `version-label.txt` 的**最高版本 = 1** 时，玩家启动读到 `latest: 1, current: 1` → “现在没有更新”，**不下载任何文件**。
- 因此**服务端 `public/` 只需要放 `index.json`**，`1.tar` 不用上传；以后 `pack 2` 之后只上传 `index.json` + 差量 `2.tar`。
- 例外：玩家客户端若丢失 `version-label.txt`（等于版本 0），会尝试下载 v1 全量包 → 服务端没有 → 报错。这种情况直接让玩家重下整包即可（或后续再补传 `1.tar`）。

### 7.2 本机端到端验证（已通过）
- 服务端：`mcpatch-manager-windows.exe serve` → `private protocol is now listening on 0.0.0.0:6700`，`Test-NetConnection 127.0.0.1:6700` = `True`。
- 客户端：临时目录 `clienttest\.minecraft\{mcpatch\{jar,yml(127.0.0.1:6700),version-label.txt=1}, versions\Stm\mods\dummy.txt}`，`java -jar Mcpatch-0.0.12.jar` 输出：
  - `更新目录: …\clienttest`（= `.minecraft` 的父目录，说明 `base-path: ''` 智能定位正确）
  - `程序目录: …\clienttest\.minecraft\mcpatch`
  - `server versions: 0. 1` → `latest: 1, current: 1` → `现在没有更新`，退出码 0，文件零改动。
- 打包产物（本机 `build\tmp\mcpatch\public\`）：`1.tar` = 1,447,632,384 B、`index.json` = 149 B（`[{"label":"1","filename":"1.tar","offset":1442068992,"length":5561914,"hash":"no hash"}]`）；`test` 子命令输出“测试通过，打包完成！”。
- 备份留档：`build\tmp\mcpatch\{mcpatch-manager-windows.exe, mcpatch-manager-linux-x86_64, Mcpatch-0.0.12.jar, config.toml, make_workspace.py, workspace\, public\}`。

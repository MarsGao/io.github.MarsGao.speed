# 仓库维护指南

## 📋 仓库说明

本项目有两个仓库需要维护：

1. **你的个人仓库**（开发仓库）:
   - URL: https://github.com/MarsGao/io.github.MarsGao.speed
   - 用途: 日常开发、测试、Issue 管理
   - Remote 名称: `origin`

2. **Xposed-Modules-Repo 官方仓库**（发布仓库）:
   - URL: https://github.com/Xposed-Modules-Repo/io.github.MarsGao.speed
   - 用途: LSPosed 模块仓库同步，用户通过 LSPosed 管理器安装
   - Remote 名称: `official`

## 🔄 为什么需要两个仓库？

### 原因说明

1. **Xposed-Modules-Repo 要求**:
   - Xposed-Modules-Repo 要求模块必须托管在他们的组织下
   - 仓库名必须等于包名（`io.github.MarsGao.speed`）
   - 这是 LSPosed 模块仓库的标准流程

2. **工作流程**:
   - 你在个人仓库进行开发和测试
   - 稳定版本推送到官方仓库
   - LSPosed 管理器从官方仓库读取模块信息

3. **好处**:
   - 个人仓库：自由开发，不受限制
   - 官方仓库：自动同步到 LSPosed，用户可直接搜索安装

## 🚀 日常维护流程

## 📱 设备侧 LSPosed / Vector 维护记录

### X 当前版本复测入口

X 的新 APK 或模块候选版本须按 [X 上线验收](docs/X-RELEASE-ACCEPTANCE.md)执行。使用 `scripts/Test-XDevicePreflight.ps1` 核对设备与包版本；可选 `-CollectHookEvidence` 提取当前进程证据。2026-10-12 OnePlus 13 / X `12.28.0-prod.01` 已确认 `1.2.10` 模块加载、两个 Media3 Hook 注册及默认 1.75x 实际播放。原生按钮显示与临时 1x 尚有限制，完整上线门禁未通过；按机主指示保留现有实现，详细计时和缺项见验收记录。

### 术语

- LSPosed 中的中文名称：**模块**
- 对应英文名称：**Module**
- 不建议称为“插件 / Plugin”，避免和浏览器插件、Gradle 插件、系统扩展混淆。

### OnePlus Ace 5 基线（2026-06-01）

- 设备：OnePlus `PKG110`
- Android：`16 (API 36)`
- Slot：`_a`
- Kernel：`6.1.118-android14-OP-Wild`
- Root 栈：SukiSU Ultra / Wild Kernel 环境，ADB shell 中 `su` 不可直接访问，但 MMRL 已获得 Root 授权
- LSPosed：`1.9.2 (7024) - Zygisk`
- Xposed API：`100`
- LSPosed 管理器入口：隐藏在 `com.android.shell` pinned shortcut，启动类别为 `org.lsposed.manager.LAUNCH_MANAGER`

### OnePlus 13 基线（2026-06-01）

- 设备：OnePlus `PJZ110`
- Android：`16 (API 36)`
- Slot：`_a`
- Kernel：`6.6.89-android15-OP-WILD`
- Root 栈：KernelSU Next / Wild Kernel 环境，ADB shell 中 `su` 不可直接访问
- LSPosed：`1.11.0 (7209) - Zygisk`
- Xposed API：`100`
- LSPosed 管理器入口：同样隐藏在 `com.android.shell` pinned shortcut，启动类别为 `org.lsposed.manager.LAUNCH_MANAGER`

### OnePlus 13 微信 8.0.69 VideoSpeed 适配记录（2026-06-04）

#### 设备与包状态

- 设备：OnePlus 13 `PJZ110 / OP5D0DL1`
- 系统：Android `16` / API `36`
- 当前 slot：`_a`
- 内核：`6.6.89-android15-OP-WILD`
- 微信：`com.tencent.mm 8.0.69`，`versionCode=3022`，installer `com.android.vending`
- VideoSpeed 更新前：`io.github.MarsGao.speed 1.2.1`，installer `com.android.packageinstaller`
- 旧包：`com.veo.hook.bili.speed` 未安装
- 进程基线：`lspd` 正常在跑，微信主进程和 `:push`、`:appbrand0`、`:appbrand1` 可启动

#### 运行期诊断

执行过：

```powershell
& $Adb logcat -c
& $Adb shell am force-stop com.tencent.mm
& $Adb shell monkey -p com.tencent.mm -c android.intent.category.LAUNCHER 1
& $Adb shell logcat -d -v time | Select-String "\[VideoSpeed\]|LSPosed|lspd|com.tencent.mm"
```

诊断结论：

- 微信能被 force-stop 并重新启动。
- 旧版 `1.2.1` 在本轮微信主进程启动日志中未出现 `[VideoSpeed]` 初始化记录。
- 因此后续验证必须先确认新版模块在 LSPosed 中启用且作用域包含 `com.tencent.mm`，必要时重启或软重启后再判断 hook 是否失效。
- `adb shell su -c id` 在该设备返回 `su: inaccessible or not found`，不能作为 OnePlus 13 / KernelSU Next / Zygisk 栈 root 失效的单一判据。

#### 1.2.2 代码变更

- 微信入口新增 `ClassLoader.loadClass` 探针，只记录并扫描类名包含 `finder`、`video`、`player`、`liteav`、`thumb`、`play` 的候选类，日志上限 `120` 条。
- 对候选类扫描 `float` 入参倍速方法，覆盖 `setRate`、`setSpeed`、`setPlaySpeed`、`setPlaybackSpeed`、`setPlaybackRate` 和 speed/rate/playback 命名。
- 对 `start`、`play`、`resume`、`prepare`、`startVodPlay`、`startPlay`、`startLivePlay` 后主动调用目标倍速 API。
- LiteAV 和 Finder 通用路径增加递归保护，避免主动设速调用再次被误判成手动修改。
- 手动倍速判断只认可明确点击、触摸、`performClick` 或速度 UI 调用栈；普通播放初始化不再阻止自动设速。

#### 验证判据

安装 `1.2.2` 后，微信视频号诊断日志理想情况下应看到：

- `[VideoSpeed] Starting WeChat hook with multi-strategy approach`
- `[VideoSpeed] ClassLoader.loadClass probe installed`
- `[VideoSpeed] [Discover] loadClass candidate:` 或 `Found player class:`
- `[VideoSpeed] [Discover] hooked ...` 或 `Hooked TXVodPlayer.setRate`
- 播放视频时出现 `Auto speed set`、`Init speed corrected`、`setRate target` 或 `setPlaybackParameters target`

如果安装成功但没有任何 `[VideoSpeed]` 微信初始化日志，优先检查 LSPosed 模块启用和 `com.tencent.mm` 作用域，不直接继续改播放器方法名。最终通过标准以视频号播放页实际自动应用默认倍速为准；日志缺失只能说明当前日志出口或过滤方式不能证明 hook 过程。

#### 本轮安装与验证结果

- `.\gradlew.bat :app:assembleDebug` 已通过，输出 `app/build/outputs/apk/debug/app-debug.apk`。
- 已用 `adb install -r` 安装到 OnePlus 13，设备侧显示 `io.github.MarsGao.speed 1.2.2` / `versionCode=1002002`。
- 安装后已正常 `adb reboot`，重启后 `sys.boot_completed=1`、slot 仍为 `_a`、`lspd` 正常运行。
- 重启后 force-stop 并启动微信，`logcat -b all` 未出现 `[VideoSpeed]` 初始化记录。
- Twitter/X 对照启动也未出现 `[VideoSpeed]`，说明当前未能证明模块已被 LSPosed 加载到目标进程。
- APK 内已确认存在 `assets/xposed_init` 和 `classes.dex`；`dumpsys package io.github.MarsGao.speed` 的系统可见性列表包含 `com.tencent.mm`。
- 设备停在 PIN 锁屏，无法自动进入 LSPosed 管理器确认模块启用和 `com.tencent.mm` 作用域；下一步需要手动解锁后检查 LSPosed UI，再进入视频号播放页复测日志和体感。
- 手动解锁后进入 LSPosed 管理器已确认：VideoSpeed `1.2.2` 模块开关为启用，Twitter/X `com.twitter.android` 已勾选，微信 `com.tencent.mm 8.0.69` 原先未勾选。
- 已勾选微信 `com.tencent.mm` 作用域并重启设备。
- 重启并解锁后，已 force-stop 微信、重新启动并进入 `com.tencent.mm.plugin.finder.ui.FinderHomeAffinityUI`；窗口焦点确认在视频号，UI 层级确认存在 `TextureView` 和 `SeekBar`。
- `logcat -b all` 仍未出现 `[VideoSpeed]`、候选类探针或主动设速日志，因此本机当前 logcat 不能作为模块命中的可靠唯一证据。
- 用户在视频号播放页体感确认：当前播放已经按 biliSpeed 调整后的速度运行。本轮 OnePlus 13 / 微信 `8.0.69 (3022) GP` 运行期目标判定为通过。

可用 ADB 启动命令：

```powershell
$Adb = Join-Path $env:LOCALAPPDATA 'Android\Sdk\platform-tools\adb.exe'
& $Adb shell am start -a android.intent.action.MAIN -c org.lsposed.manager.LAUNCH_MANAGER -n com.android.shell/.BugreportWarningActivity
```

### Xiaomi 14 Pro 微信 8.0.69 / versionCode 3040 VideoSpeed 适配记录（2026-06-19）

#### 设备与包状态

- 设备：Xiaomi 14 Pro `shennong`
- 系统：`BP2A.250605.031.A3`
- 当前 slot：`_a`
- 微信：`com.tencent.mm 8.0.69`，`versionCode=3040`
- VideoSpeed：`io.github.MarsGao.speed 1.2.3`，`versionCode=1002003`
- Root / 框架：Root 可用，Vector / LSPosed fork 的 `lspd` 正常运行

#### 根因与修复

- 视频号 feed 流在该版本不走旧的 `FinderThumbPlayerProxy.play/setPlaySpeed` 被动 hook 路径，旧版会出现注册成功但运行期零命中的情况。
- 实际可控点为当前 feed holder 中 `FinderVideoLayout.getVideoView()` 返回的 `com.tencent.mm.plugin.finder.video.FinderThumbPlayerProxy`。
- `1.2.3` 在 `FinderHome` Activity `onResume` 后和 `dispatchTouchEvent ACTION_UP` 后延迟定位当前 holder，并主动调用 `setPlaySpeed(targetSpeed)`。
- 设置页修复 `SharedPreferences` 保存逻辑，并给应用数据目录和 `shared_prefs` 目录设置可遍历权限，保证 `XSharedPreferences` 可跨进程读取。
- 微信分支使用微信专用兜底默认值 `2.0f`，避免配置不可读时回退到历史默认 `1.5f`。

#### 验证结果

- 用户确认打开微信视频号默认即为 `2.0x` 播放。
- 将 BiliSpeed 设置页默认速度调整为 `1.5x` 后，微信视频号运行日志确认注入目标变为 `1.5`。
- 关键日志：
  - `FinderActivity.onResume current com.tencent.mm.plugin.finder.video.FinderThumbPlayerProxy setRate target via setPlaySpeed: 2.0`
  - `FinderActivity.touchUp current com.tencent.mm.plugin.finder.video.FinderThumbPlayerProxy setRate target via setPlaySpeed: 2.0`
  - `FinderActivity.onResume current com.tencent.mm.plugin.finder.video.FinderThumbPlayerProxy setRate target via setPlaySpeed: 1.5`
  - `FinderActivity.touchUp current com.tencent.mm.plugin.finder.video.FinderThumbPlayerProxy setRate target via setPlaySpeed: 1.5`

注意：微信视频号界面不一定显示 `2x` / `1.5x` 文案，本实现是直接对真实播放器实例注入倍速；最终以体感和 LSPosed 运行日志为准。

### 当前 LSPosed 模块清单（2026-06-01）

#### OnePlus Ace 5

| 模块 | 版本 | 状态 |
|------|------|------|
| 红薯猪手 | `1.2.7` | 未提示更新 |
| 视频调速 VideoSpeed | `1.2.1` | 已验证用于 X/Piko 调速 |
| Freely | `25.1024.0042` | 未提示更新 |
| LuckyTool | `1.3.4` | 已从 `1.3.3` 升级 |
| sing-box | `1.13.12` | 提示需要较新的 Xposed API `101` |
| Telegram Speed Hook | `2.1 AR` | 未提示更新 |
| 隐藏应用列表 / Hide My Applist | `3.8.r499.3a346c0` | 已从 `3.6.1.r462.4524dde` 升级 |

#### OnePlus 13

| 模块 | 版本 | 状态 |
|------|------|------|
| 红薯猪手 | `1.2.7` | 未提示更新 |
| Bili调速 | `1.1.9` | 旧包名保留，未卸载 |
| 视频调速 VideoSpeed | `1.2.1` | 已启用并勾选 `com.twitter.android` |
| LuckyTool | `1.3.4` | 已从 `1.3.3` 升级 |
| OPCameraPro | `Katrina_3.0.42` | `3.1.22` 提示需要 LSPosed v2.0+ / API `101`，暂不升级 |
| sing-box | `1.13.12` | 提示需要较新的 Xposed API `101`，功能正常时暂不迁移框架 |
| Telegram Speed Hook | `2.1 AR` | 未提示更新 |
| 隐藏应用列表 / Hide My Applist | `3.8.r499.3a346c0` | 已从 `3.6.1.r462.4524dde` 升级 |
| NewMiko | `1.8.0` | 未提示更新 |
| WAuxiliary | `1.2.7.r1418.e65079c` | 已从 `1.2.7.r1255.7b778d2` 升级 |

### 已完成的 APK 层更新（2026-06-01）

#### OnePlus Ace 5

- MMRL: `v34242-release` -> `v34296-release`
- Wild KSU Manager: `v0.0.220` -> `v3.1.2`
- LuckyTool: `1.3.3` -> `1.3.4`
- Hide My Applist: `3.6.1.r462.4524dde` -> `3.8.r499.3a346c0`

#### OnePlus 13

- MMRL: `v34265-release` -> `v34296-release`
- LuckyTool: `1.3.3` -> `1.3.4`
- Hide My Applist: `3.6.1.r462.4524dde` -> `3.8.r499.3a346c0`
- WAuxiliary: `1.2.7.r1255.7b778d2` -> `1.2.7.r1418.e65079c`
- VideoSpeed: 安装新版 `io.github.MarsGao.speed 1.2.1`，并在 LSPosed 中启用、勾选 `com.twitter.android`

OnePlus 13 保留项：

- KernelSU Next Manager `v3.1.0` 未升级到 `v3.2.0`，因为新版管理器提高最低内核侧版本要求，不能只升级 APK。
- OPCameraPro `Katrina_3.0.42` 未升级到 `3.1.22`，因为该版本要求 LSPosed v2.0+ / API `101`。
- 旧包名 `com.veo.hook.bili.speed 1.1.9` 未卸载，避免破坏既有作用域；新版 `io.github.MarsGao.speed 1.2.1` 已并存启用。

每次更新后至少确认：

```powershell
& $Adb devices -l
& $Adb shell getprop sys.boot_completed
& $Adb shell ps -A | Select-String "lspd|zygisk|Tricky|integrity"
& $Adb shell dumpsys package <package.name> | Select-String "versionCode=|versionName="
```

### 迁移 Vector / 新 LSPosed 的触发条件

原版 LSPosed `1.9.2 (7024)` 当前还能驱动 VideoSpeed，但它已经不适合作为 Android 16 长期维护基线。满足以下任一条件时，再迁移到活跃分支 Vector / LSPosed：

- LSPosed 模块明确要求 Xposed API `101` 或更高，并且功能已受影响。
- Hide My Applist `3.8+` 因系统服务 native library 加载限制失效。
- Android 系统 OTA 后 `lspd`、`zygisk_lsposed` 不再正常运行。
- VideoSpeed 或其他关键模块在目标应用中无法注入，且日志显示框架层问题。

迁移原则：

- 不在功能正常时盲目替换 LSPosed 核心。
- 只从官方或可信上游下载 Vector / LSPosed zip。
- 一次只替换一个框架模块，重启后验证 `lspd`、`zygisk_lsposed`、LSPosed 管理器、VideoSpeed 和 HMA。
- 若更新后 bootloop，优先从 recovery/fastboot 禁用最近替换的模块，不继续叠加新模块。

### 场景 1: 日常代码更新

当你完成代码修改并提交到个人仓库后：

```bash
# 1. 推送到个人仓库（开发仓库）
git push origin master

# 2. 同步到官方仓库（发布仓库）
git push official master
```

### 场景 2: 发布新版本

当你准备发布新版本时：

```bash
# 1. 更新版本号（在 gradle.properties 中修改 appVersionName）
# 例如: appVersionName=1.2.1

# 2. 提交版本更新
git add gradle.properties
git commit -m "chore: 更新版本号到 1.2.1"
git push origin master

# 3. 创建 tag（LSPosed 格式: VersionCode-VersionName）
# 例如: 1002001-1.2.1
git tag 1002001-1.2.1
git tag v1.2.1  # 可选：同时创建 v 前缀的 tag

# 4. 推送代码和 tags 到两个仓库
git push origin master
git push origin --tags
git push official master
git push official --tags
```

### 场景 3: 快速同步脚本

你可以创建一个 PowerShell 脚本来自动同步：

```powershell
# sync-repos.ps1
Write-Host "正在同步到个人仓库..." -ForegroundColor Green
git push origin master
git push origin --tags

Write-Host "正在同步到官方仓库..." -ForegroundColor Green
git push official master
git push official --tags

Write-Host "同步完成！" -ForegroundColor Green
```

使用方法：
```bash
.\sync-repos.ps1
```

## ⚠️ 重要注意事项

### 1. 仓库描述

官方仓库的描述用于在 LSPosed 中显示模块名称，必须设置：

- 访问: https://github.com/Xposed-Modules-Repo/io.github.MarsGao.speed/settings
- 在 **Description** 字段输入: `视频调速 VideoSpeed - 视频播放速度调节 Xposed 模块`
- 点击 **Save changes**

### 2. Release 要求

- 官方仓库必须至少有一个 Release
- Release tag 格式: `VersionCode-VersionName` (如 `1002000-1.2.0`)
- GitHub Actions 会自动创建 Release（如果配置了 workflow）

### 3. 分支管理

- 当前使用 `master` 分支（官方仓库也使用 master）
- 如果官方仓库要求 `main` 分支，可以创建并推送：
  ```bash
  git checkout -b main
  git push official main
  ```

### 4. 同步时机

- **开发阶段**: 只需推送到个人仓库
- **测试通过后**: 同步到官方仓库
- **发布版本**: 必须同时推送到两个仓库

## 📦 Release 同步（重要）

⚠️ **注意**: 官方仓库的 GitHub Actions 被组织管理员禁用，无法自动构建 Release。

### 自动化同步（推荐）

使用 `sync-release.ps1` 脚本自动同步 Release：

```powershell
# 1. 设置 GitHub Token（首次使用）
$env:GITHUB_TOKEN = "your_token_here"  # 从 https://github.com/settings/tokens 创建

# 2. 同步最新 Release
.\sync-release.ps1

# 或指定特定 Tag
.\sync-release.ps1 1002000-1.2.0
```

详细说明请查看: `sync-release-manual.md`

### 手动同步步骤

1. **下载 APK**: 从个人仓库 Releases 下载 APK
2. **创建 Release**: 在官方仓库创建同名 Release 并上传 APK

## 📝 检查清单

每次更新后，检查以下项目：

- [ ] 代码已推送到个人仓库 (`origin`)
- [ ] 代码已推送到官方仓库 (`official`)
- [ ] Tags 已推送到两个仓库
- [ ] **Release 已同步到官方仓库**（重要！）
- [ ] 官方仓库描述已设置（首次）
- [ ] 等待 10 分钟后，在 LSPosed 中搜索验证

## 🔗 相关链接

- 个人仓库: https://github.com/MarsGao/io.github.MarsGao.speed
- 官方仓库: https://github.com/Xposed-Modules-Repo/io.github.MarsGao.speed
- Xposed-Modules-Repo: https://github.com/Xposed-Modules-Repo
- LSPosed 模块仓库: https://modules.lsposed.org/

## ❓ 常见问题

**Q: 为什么不能直接在官方仓库开发？**  
A: 可以，但个人仓库更灵活，可以自由实验，稳定后再同步到官方仓库。

**Q: 如果忘记同步会怎样？**  
A: 用户无法在 LSPosed 中看到最新版本，但可以通过个人仓库的 Releases 手动下载。

**Q: 两个仓库的代码必须完全一致吗？**  
A: 建议保持一致，特别是 Release 版本。开发中的代码可以先只在个人仓库。

**Q: 如何查看两个仓库的差异？**  
A: 
```bash
# 查看官方仓库的更新
git fetch official
git log master..official/master

# 查看个人仓库的更新
git fetch origin
git log official/master..master
```


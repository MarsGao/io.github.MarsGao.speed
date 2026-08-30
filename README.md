# 视频调速 VideoSpeed

[![GitHub stars](https://img.shields.io/github/stars/MarsGao/io.github.MarsGao.speed?style=social)](https://github.com/MarsGao/io.github.MarsGao.speed/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/MarsGao/io.github.MarsGao.speed?style=social)](https://github.com/MarsGao/io.github.MarsGao.speed/network/members)
[![GitHub release](https://img.shields.io/github/v/release/MarsGao/io.github.MarsGao.speed)](https://github.com/MarsGao/io.github.MarsGao.speed/releases)
[![License](https://img.shields.io/github/license/MarsGao/io.github.MarsGao.speed)](LICENSE.md)

视频默认播放速度调节 - 一个基于 Xposed 的 Android 模块，用于调节多个应用的视频播放速度。

> 🌟 **如果您觉得 VideoSpeed（视频调速）好用且解决了您的痛点，请在右上角帮作者点个 ⭐ Star 收藏支持一下！**
> 您的每一个 Star 都是作者持续跟进微信、B站、抖音、X/Twitter 等各主流应用新版本适配、长期无偿开源维护的最大动力！
> 🎉 作为非计算机专业学习者维护的开源项目，非常感谢开源社区与大家的鼓励和支持！

> 📘 想系统理解模块加载、跨进程配置、各应用 Hook 模式以及微信视频号主动注入原理，请阅读 [《VideoSpeed 技术全景与微信视频号适配指南》](TECHNICAL_OVERVIEW.md)。该文档也是版本适配和外部 AI 技术讨论的首要上下文。

## ✨ 功能特性

- 🚀 支持多款主流应用：
  - 哔哩哔哩 (B站)
  - 微信视频号
  - 抖音
  - 快手
  - 微博
  - 小红书
  - Instagram
  - Telegram

- ⚡ 智能速度调节
- 🎯 区分自动播放和手动设置
- 🔧 易于使用的设置界面

## 📱 支持版本

本项目基于原作者 [V-E-O](https://github.com/V-E-O) 的适配工作，支持以下应用版本：

| 应用 | 适配版本 | 兼容性 | 备注 |
|------|----------|--------|------|
| 哔哩哔哩 B站 | 7.25.0 / **3.20.4 (GP)** | ✅ 兼容新老版本 | 推荐 Google Play 版本 |
| 微信视频号 WeChat | **8.0.69 (3022 GP / 3040) ~ 8.0.77 (3160 大陆版)** | ✅ 已适配 | 视频号 feed 默认倍速主动注入，动态资源解析与通用 ViewHolder |
| 抖音 Douyin | 25.6.0 | ✅ 兼容新版本 | 含极速版 |
| 小红书 | 8.23.0.5 | ✅ 兼容新老版本 | |
| 推特 Twitter/X | 12.7.1-release.0 / Piko v3.4.0 | ✅ 已适配 | Media3 1.9.3 混淆实现；推荐 [Piko](https://github.com/crimera/piko) |
| Instagram | 315.0.0.29.109 | ✅ 兼容新老版本 | 含 Instander |
| Telegram | - | ✅ 不上混淆兼容 | |
| 微博 Weibo | 14.6.0 | ✅ 理论兼容新老版本 | |

> 📝 **说明**: 本人主要使用的应用均来自 **Google Play**，因此主要考虑适配 Google Play 版本。如需其他版本适配，欢迎提 [Issue](https://github.com/MarsGao/io.github.MarsGao.speed/issues) 反馈！

> 💡 **推特用户推荐**: 如果你使用 Twitter/X，强烈推荐 [crimera/piko](https://github.com/crimera/piko) 项目，它提供了 X/Twitter 的 Piko patches。

## 🙏 致谢

### 原项目作者
特别感谢原项目作者 **[V-E-O](https://github.com/V-E-O)** 的 [biliSpeed](https://github.com/V-E-O/biliSpeed) 项目，为本项目提供了基础框架和灵感。

### AI 辅助开发
本项目在开发过程中得到了以下 AI 工具的大力支持：

- 🤖 **[Cursor](https://cursor.sh/)** - 智能代码编辑器
- 🧠 **[Claude Opus 4.5](https://www.anthropic.com/)** - Anthropic 的大语言模型

作为非计算机专业的学习者，正是这些优秀的 AI 工具让我有机会深入理解代码逻辑、学习 Android Hook 原理，并最终将这个项目落地。感谢 AI 时代为普通人打开的技术学习大门！

### 开源社区
感谢所有为 Xposed 生态做出贡献的开发者们！

## 📥 下载安装

### 方式一：LSPosed 模块仓库（推荐）

本模块已提交至 [Xposed-Modules-Repo](https://github.com/Xposed-Modules-Repo/)，审核通过后可直接在 LSPosed 管理器中搜索安装。

### 方式二：GitHub Releases

前往 [Releases](https://github.com/MarsGao/io.github.MarsGao.speed/releases) 页面下载最新版本的 APK。

### 方式三：自行构建

#### 🚀 使用 GitHub Actions 自动构建

1. **Fork 此项目** 到您的 GitHub 账户

2. **推送代码** 到 main 分支，或手动触发 Actions：
   - 访问您的仓库
   - 点击 "Actions" 标签
   - 点击 "Build Android APK" 工作流
   - 点击 "Run workflow" 按钮

3. **下载 APK**：
   - 工作流完成后，点击对应的运行
   - 在 "Artifacts" 部分下载 APK 文件

#### 🔐 设置稳定签名密钥（发布必需）

公开 Release 必须使用同一套稳定签名密钥；不要发布 CI 临时生成的 debug APK，否则已安装用户无法通过覆盖安装更新。

1. **生成密钥库**：
   ```bash
   keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias
   ```

2. **转换为 Base64**：
   ```bash
   base64 my-release-key.jks
   ```

3. **设置 GitHub Secrets**：
   - 访问您的仓库 Settings > Secrets and variables > Actions
   - 添加以下 secrets：
     - `SIGNING_KEYSTORE_BASE64`: 上面生成的 base64 字符串
     - `SIGNING_KEY_ALIAS`: 您的密钥别名
     - `SIGNING_KEY_PASSWORD`: 密钥密码
     - `SIGNING_STORE_PASSWORD`: 密钥库密码

## 💻 本地开发

### 环境要求

- Java JDK 17
- Android Studio Arctic Fox 或更高版本
- Android SDK API 33

### 本地构建

```bash
# 克隆项目
git clone https://github.com/MarsGao/io.github.MarsGao.speed.git
cd io.github.MarsGao.speed

# 构建 Debug 版本
./gradlew assembleDebug

# 构建 Release 版本（需要签名配置）
./gradlew assembleRelease
```

### 安装和测试

1. **启用 USB 调试**：
   - 手机设置 > 开发者选项 > USB 调试

2. **安装 APK**：
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

3. **激活模块**：
   - 打开 Xposed 管理器 (如 LSPosed)
   - 启用 "视频调速 VideoSpeed" 模块
   - 勾选需要 Hook 的目标应用
   - 重启目标应用

4. **设置速度**：
   - 打开 "视频调速 VideoSpeed" 应用
   - 输入期望速度 (如 1.5)
   - 点击设置

5. **测试**：
   - 打开支持的应用播放视频
   - 观察播放速度是否生效

## 🔧 技术架构

- **框架**: Xposed Framework
- **语言**: Java
- **构建工具**: Gradle
- **CI/CD**: GitHub Actions

## 🧩 Xposed / LSPosed 说明

在 LSPosed 管理器中，这类应用一般称为 **模块**，英文是 **Module**；不是插件（Plugin）。本项目在 LSPosed 中显示为 **视频调速 VideoSpeed** 模块。

截至 2026-06-01，原版 LSPosed 最新正式版仍为 `1.9.2 (7024)`，但上游仓库已经归档。Android 15/16 设备后续如遇到 Xposed API 版本不足、模块提示需要 API 101、或系统服务 native hook 兼容问题，建议按维护文档迁移到仍活跃维护的 Vector / LSPosed 分支。

当前 Android 16 验证基线：

- OnePlus Ace 5: LSPosed `1.9.2 (7024) - Zygisk`, Xposed API `100`
- OnePlus 13: Vector/Zygisk，X `12.7.1-release.0` 已观察到 Media3 Hook 初始化
- Mi 14 Pro (`23116PN5BC`): Android 16、Vector/Zygisk，X `12.7.1-release.0` 已验证 `1.0x → 1.8x` 的设速与 `prepare` 补设日志
- VideoSpeed: `1.2.8`
- Twitter/X: `12.7.1-release.0` / Piko `v3.4.0`
- X `12.7.1-release.0` 已在 Mi 14 Pro 真机触发设速与 `prepare` 补设；后续上游播放器重构仍须按此流程复测
- `sing-box` 与 `OPCameraPro` 等模块提示需要 Xposed API `101` 时，按维护文档评估是否迁移 Vector，不建议在核心功能正常时盲目替换框架。

## 🎯 Hook 策略

项目采用多重 Hook 策略确保兼容性：

1. **通用播放器 Hook**: 动态查找播放器相关类
2. **腾讯视频SDK Hook**: 支持 TXVodPlayer、TXLivePlayer 等底层播放器
3. **方法签名 Hook**: 匹配所有可能的播放速度设置方法
4. **系统MediaPlayer Hook**: 作为兜底方案
5. **智能判断**: 通过调用栈分析区分自动播放和手动设置

## 📋 更新日志

### v1.2.9 (2026-08-30)

**🔧 微信 8.0.77 (3160 大陆版) 适配与动态资源架构重构**

- ✅ **动态资源 ID 解析**：彻底替换硬编码十进制 ID，改用 `Resources.getIdentifier(...)` 动态解析 `id/m6e` 与 `id/e_k`，并辅以 DecorView 递归扫描兜底，无缝自适应后续版本编译
- ✅ **通用 ViewHolder 与布局解耦**：彻底消除对混淆类名 `me5.s0` 的硬依赖，通用探查 `yr5.s0` 等各类 `RecyclerView.ViewHolder` 中的 `FinderVideoLayout` 及播放器
- ✅ **倍速菜单 UIC 支持**：适配微信 8.0.77 的 `FinderSpeedControlUIC` 点击拦截
- ✅ **构建系统升级**：升级 AGP 至 `8.7.0` 与 Gradle `8.9`，全面适配系统 JDK 21 环境
- ⭐ **社区支持**：欢迎各位 Xposed / LSPosed 社区开发者与使用者体验并给予 ⭐ **Star** 支持！

### v1.2.8 (2026-08-23)

**🔧 X `12.7.1-release.0` Media3 适配**

- ✅ 适配 X 随 APK 打包的 Media3 `1.9.3` 混淆播放器 `androidx.media3.exoplayer.i1`
- ✅ Hook 混淆后的 `prepare`、播放参数和直接设速入口，在 `prepare` 后补设模块配置的速度
- ✅ 保留旧版 X/Piko 与已知 Media3 类名作为回退路径
- ⚠️ 新版本 X 采用混淆实现；每次上游播放器重构后均需以真机日志和播放页复测为准

### v1.2.6 (2026-06-25)

**🔧 Provider 初始化时机修复**

- ✅ 当模块加载晚于目标进程 `Application.attach()` 时，使用 `ActivityThread.currentApplication()` 获取上下文，保证 Provider 桥接仍可用

### v1.2.5 (2026-06-25)

**🔧 Android 16 跨进程配置桥接修复**

- ✅ 使用只读 `ContentProvider` 向所有 Hook 目标进程提供倍速配置，不再依赖跨 UID 读取私有 `speed.xml`
- ✅ 微信视频号与其他受支持应用统一优先读取 Provider；旧 `XSharedPreferences` 仅作为兼容回退
- ✅ 微信读取失败时统一回退到产品默认的 `1.5x`，不再隐式使用 `2.0x`

### v1.2.4 (2026-06-19)

**🔧 跨进程速度配置可见性修复**

- ✅ 修正应用数据目录、`shared_prefs` 与 `speed.xml` 的最小读取/穿越权限
- ✅ 解决设置页已写入新速度，但微信进程的 `XSharedPreferences` 仍读取旧值或兜底值的问题
- ✅ 保持应用数据根目录不可被其他进程列出，避免过度放宽权限

### v1.2.3 (2026-06-19)

**🔧 微信视频号默认倍速稳定性修复**

- ✅ 适配微信 `8.0.69` / `versionCode 3040` 视频号 feed 流
- ✅ 进入视频号和滑到下一个视频后，主动对当前 `FinderThumbPlayerProxy` 调用 `setPlaySpeed`
- ✅ 支持 BiliSpeed 设置页调整默认倍速，已验证 `2.0x` 和 `1.5x` 均可生效
- ✅ 增加手动倍速菜单点击冷却，避免用户临时手动调速后被立即覆盖
- ✅ 修复设置页保存与 XSharedPreferences 可读性问题，提高跨进程读取稳定性

### v1.2.1 (2026-06-01)

**🔧 Twitter/X Piko 适配更新**

- 新增 Media3 / ExoPlayer 播放器 Hook，适配 X `11.81.0-release.0` / Piko `v3.4.0`
- 拦截 `setPlaybackSpeed(1.0f)` 和 `setPlaybackParameters(...)` 的自动重置，恢复模块默认倍速
- 在 `prepare`、`play`、`setPlayWhenReady(true)`、`setMediaItem(s)` 后补设播放速度
- 保留旧版 Twitter/X Hook 作为 legacy fallback，并增加异常隔离和日志前缀

### v1.2.0 (2025-12-01)

**🔄 重大更新：包名重构**

- **包名变更**: `com.veo.hook.bili.speed` → `io.github.MarsGao.speed`
  - 符合 [Xposed-Modules-Repo](https://github.com/Xposed-Modules-Repo/) 提交要求
  - 使用规范的命名空间避免冲突
- **项目名称统一**: "Bili调速" → "视频调速 VideoSpeed"
  - 更准确反映项目功能（支持多个视频应用，不限于B站）
- **APK 命名**: `biliSpeed_*.apk` → `VideoSpeed_*.apk`

> ⚠️ **注意**: 由于包名变更，升级前请先卸载旧版本，然后在 LSPosed 中重新激活模块。

### v1.1.9 (2025-11-30)

**🔧 微信视频号速度设置逻辑修复**

- **问题诊断**: 通过LSPosed日志分析，发现 `isManualSpeedChange()` 函数存在**误判**问题
  - Hook成功且被正确触发
  - 但 `dispatch` 等常见Android方法名被误判为用户手动操作
  - 导致自动速度设置被错误跳过

**修复内容**:
- ✅ **改进调用栈检测逻辑**: 
  - 移除 `dispatch` 等误导性关键词检测
  - 仅检测明确的用户交互方法: `onClick`, `onTouchEvent`, `performClick`
  - 添加调用栈日志输出，便于调试
- ✅ **新增速度设置缓存机制**:
  - 使用 `ConcurrentHashMap` 追踪每个播放器实例的速度状态
  - 新增冷却期机制 (3秒)，避免用户手动调整后被自动覆盖
  - 智能判断：目标速度为1.0时不做修改
- ✅ **Telegram Hook 兼容性修复**:
  - 新增多版本方法签名兼容
  - 支持 `VideoPlayer.setPlaybackSpeed` 备选方案
  - 修复 `NoSuchMethodError` 异常

### v1.2.2 (2026-06-04)

**🔧 微信视频号 8.0.69 GP 兼容性修复**

- ✅ 新增微信候选播放器类加载探针，记录 Finder、video、player、liteav、thumb、play 相关类命中
- ✅ 新增候选播放器类通用方法扫描，自动 Hook `setRate` / `setSpeed` / `setPlaySpeed` / `setPlaybackSpeed` 等 float 倍速方法
- ✅ 播放 `start` / `play` / `resume` / `prepare` 后主动补设目标倍速
- ✅ LiteAV 路径不再只依赖入参 `1.0f`，普通播放初始化速度会自动纠正为目标倍速
- ✅ 收紧手动倍速判断，只有明确点击、触摸或速度 UI 调用栈才视为用户手动修改

### v1.1.8 (2025-11-30)

**🔧 微信视频号兼容性修复**

- **问题诊断**: 通过日志分析发现 `FinderThumbPlayerProxy.setPlaySpeed` 虽然Hook成功，但从未被调用
- **根本原因**: 微信视频号使用腾讯视频SDK (liteav) 作为底层播放器

**新增功能**:
- ✅ 新增腾讯视频SDK (liteav) Hook支持
  - `TXVodPlayer.setRate` - 点播播放器
  - `TXLivePlayer.setRate` - 直播播放器
  - ExoPlayer 备用方案
- ✅ 新增播放开始时设置速度 (`startVodPlay`、`resume`、`start`、`play`)
- ✅ 新增系统 `MediaPlayer.setPlaybackParams` Hook作为兜底
- ✅ 扩展了播放器类列表，包括 Kinda 框架视频组件
- ✅ 增强日志输出，便于问题诊断

**支持的播放器类**:
- FinderThumbPlayerProxy, FinderVideoPlayer, FinderVideoCore
- SnsVideoPlayer, MMVideoPlayer, AppBrandVideoPlayer
- TXVodPlayer, TXLivePlayer (腾讯视频SDK)
- android.media.MediaPlayer (系统播放器)

### v1.1.7

- 初始微信视频号支持
- 多策略Hook架构

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

- 🐛 **发现 Bug？** 请提交 [Issue](https://github.com/MarsGao/io.github.MarsGao.speed/issues)
- 💡 **有新想法？** 欢迎讨论和建议
- 🔧 **想要贡献代码？** 欢迎提交 PR

## 📄 许可证

本项目采用 [GPL-3.0](LICENSE.md) 许可证。

## ⚠️ 免责声明

本项目仅用于学习和研究目的，请遵守相关法律法规。使用本模块造成的任何后果由使用者自行承担。

---

<p align="center">
  🌟 如果这个项目对你有帮助，请在 GitHub 右上角点一个 <b>⭐ Star</b> 鼓励作者！<br>
  Made with ❤️ by <a href="https://github.com/MarsGao">MarsGao</a>
</p>

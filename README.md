# 视频调速 VideoSpeed

[![GitHub stars](https://img.shields.io/github/stars/MarsGao/io.github.MarsGao.speed?style=social)](https://github.com/MarsGao/io.github.MarsGao.speed/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/MarsGao/io.github.MarsGao.speed?style=social)](https://github.com/MarsGao/io.github.MarsGao.speed/network/members)
[![GitHub release](https://img.shields.io/github/v/release/MarsGao/io.github.MarsGao.speed)](https://github.com/MarsGao/io.github.MarsGao.speed/releases)
[![License](https://img.shields.io/github/license/MarsGao/io.github.MarsGao.speed)](LICENSE.md)

视频默认播放速度调节 - 一个基于 Xposed 的 Android 模块，用于调节多个应用的视频播放速度。

> 🎉 **这是我的第一个开源项目！** 作为非计算机专业的学习者，感谢开源社区让我有机会参与项目开发和学习。如果这个项目对你有帮助，请给我一个 ⭐ Star，这将是对我最大的鼓励！

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
| 微信视频号 WeChat | **8.0.62 (GP)** | ✅ 已测试 | 当前支持 Google Play 版本 |
| 抖音 Douyin | 25.6.0 | ✅ 兼容新版本 | 含极速版 |
| 小红书 | 8.23.0.5 | ✅ 兼容新老版本 | |
| 推特 Twitter/X | 11.20.0 | ✅ 兼容新版本 | 推荐 [twitter-apk](https://github.com/crimera/twitter-apk) |
| Instagram | 315.0.0.29.109 | ✅ 兼容新老版本 | 含 Instander |
| Telegram | - | ✅ 不上混淆兼容 | |
| 微博 Weibo | 14.6.0 | ✅ 理论兼容新老版本 | |

> 📝 **说明**: 本人主要使用的应用均来自 **Google Play**，因此主要考虑适配 Google Play 版本。如需其他版本适配，欢迎提 [Issue](https://github.com/MarsGao/io.github.MarsGao.speed/issues) 反馈！

> 💡 **推特用户推荐**: 如果你使用 Twitter/X，强烈推荐 [crimera/twitter-apk](https://github.com/crimera/twitter-apk) 项目，它提供了基于 Piko patches 的 APK 构建。

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

#### 🔐 可选：设置签名密钥 (用于 Release 版本)

如果您想构建签名版本的 APK：

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

# 构建 Release 版本 (需要签名配置)
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

## 🎯 Hook 策略

项目采用多重 Hook 策略确保兼容性：

1. **通用播放器 Hook**: 动态查找播放器相关类
2. **腾讯视频SDK Hook**: 支持 TXVodPlayer、TXLivePlayer 等底层播放器
3. **方法签名 Hook**: 匹配所有可能的播放速度设置方法
4. **系统MediaPlayer Hook**: 作为兜底方案
5. **智能判断**: 通过调用栈分析区分自动播放和手动设置

## 📋 更新日志

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
  如果这个项目对你有帮助，请给我一个 ⭐ Star！<br>
  Made with ❤️ by <a href="https://github.com/MarsGao">MarsGao</a>
</p>

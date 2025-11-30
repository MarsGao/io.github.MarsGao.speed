# Bili调速 (biliSpeed)

Bili默认播放速度调节 - 一个基于Xposed的Android模块，用于调节多个应用的播放速度。

## 功能特性

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

## 在线构建 APK

### 🚀 使用 GitHub Actions 自动构建

1. **Fork 此项目** 到您的 GitHub 账户

2. **推送代码** 到 main 分支，或手动触发 Actions：
   - 访问您的仓库
   - 点击 "Actions" 标签
   - 点击 "Build Android APK" 工作流
   - 点击 "Run workflow" 按钮

3. **下载 APK**：
   - 工作流完成后，点击对应的运行
   - 在 "Artifacts" 部分下载 APK 文件

### 🔐 可选：设置签名密钥 (用于 Release 版本)

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

## 本地开发

### 环境要求

- Java JDK 17
- Android Studio Arctic Fox 或更高版本
- Android SDK API 33

### 本地构建

```bash
# 克隆项目
git clone https://github.com/your-username/biliSpeed.git
cd biliSpeed

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
   - 打开 Xposed 管理器
   - 启用 "Bili调速" 模块
   - 重启设备

4. **设置速度**：
   - 打开 "Bili调速" 应用
   - 输入期望速度 (如 1.5)
   - 点击设置

5. **测试**：
   - 打开支持的应用播放视频
   - 观察播放速度是否生效

## 技术架构

- **框架**: Xposed Framework
- **语言**: Java
- **构建工具**: Gradle
- **CI/CD**: GitHub Actions

## Hook 策略

项目采用多重 Hook 策略确保兼容性：

1. **通用播放器 Hook**: 动态查找播放器相关类
2. **腾讯视频SDK Hook**: 支持 TXVodPlayer、TXLivePlayer 等底层播放器
3. **方法签名 Hook**: 匹配所有可能的播放速度设置方法
4. **系统MediaPlayer Hook**: 作为兜底方案
5. **智能判断**: 通过调用栈分析区分自动播放和手动设置

## 更新日志

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

## 许可证

本项目采用 GPL-3.0 许可证。

## 贡献

欢迎提交 Issue 和 Pull Request！

## 免责声明

本项目仅用于学习和研究目的，请遵守相关法律法规。使用本模块造成的任何后果由使用者自行承担。

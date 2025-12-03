# 快速同步 Release 指南

## 🎯 当前情况

- ✅ 个人仓库 Actions 正常工作，已自动构建 Release `1002000-1.2.0`
- ❌ 官方仓库 Actions 被禁用，需要手动同步 Release

## 🚀 最快方式：手动同步（5 分钟）

### 步骤 1: 下载 APK（1 分钟）

1. 访问: https://github.com/MarsGao/io.github.MarsGao.speed/releases/tag/1002000-1.2.0
2. 下载: `VideoSpeed_1.2.0.apk` (约 167 KB)

### 步骤 2: 创建官方仓库 Release（2 分钟）

1. 访问: https://github.com/Xposed-Modules-Repo/io.github.MarsGao.speed/releases/new
2. 填写信息:
   - **Tag**: `1002000-1.2.0`（选择 "Create new tag: 1002000-1.2.0 on publish"）
   - **Release title**: `1002000-1.2.0`
   - **Description**: 复制以下内容：

```markdown
## 视频调速 VideoSpeed v1.2.0

### 📦 下载
- **VideoSpeed_1.2.0.apk** - Android APK 安装包

### 📱 支持应用
- 哔哩哔哩 B站 (7.25.0 / 3.20.4 GP)
- 微信视频号 WeChat (8.0.62 GP)
- 抖音 Douyin (25.6.0)
- 小红书 (8.23.0.5)
- Twitter/X (11.20.0)
- Instagram (315.0.0.29.109)
- Telegram
- 微博 Weibo (14.6.0)

### ⚠️ 注意
- 需要 Android 8.0 (API 26) 或更高版本
- 需要 LSPosed/Xposed 框架支持

### 🙏 致谢
- 原项目: [V-E-O/biliSpeed](https://github.com/V-E-O/biliSpeed)
- AI辅助: Cursor + Claude Opus 4.5
```

3. **Attach binaries**: 上传刚才下载的 `VideoSpeed_1.2.0.apk`
4. 点击 **Publish release**

### 完成！

✅ Release 已同步，等待 10-30 分钟后可在 LSPosed 中搜索到模块。

---

## 🔄 后续自动化（可选）

### 方式一：使用 PowerShell 脚本

1. **创建 GitHub Token**:
   - 访问: https://github.com/settings/tokens
   - 点击 "Generate new token (classic)"
   - 勾选 `repo` 权限
   - 复制 Token

2. **设置环境变量**:
   ```powershell
   $env:GITHUB_TOKEN = "your_token_here"
   ```

3. **运行脚本**:
   ```powershell
   .\sync-release.ps1
   ```

### 方式二：安装 GitHub CLI（更简单）

```powershell
# 安装
winget install GitHub.cli

# 登录
gh auth login

# 同步 Release（需要先下载 APK）
gh release create 1002000-1.2.0 --repo Xposed-Modules-Repo/io.github.MarsGao.speed --title "1002000-1.2.0" --notes "..." VideoSpeed_1.2.0.apk
```

---

## 📋 完整发布流程总结

### 1. 开发阶段
```bash
# 在个人仓库开发、测试
git push origin master
```

### 2. 发布版本
```bash
# 更新版本号 → 创建 tag → 触发 Actions 构建
git tag 1002001-1.2.1
git push origin --tags
```

### 3. 同步 Release（重要！）
```bash
# 方式一：手动（见上方步骤）
# 方式二：使用脚本
.\sync-release.ps1 1002001-1.2.1
```

### 4. 同步代码（可选）
```bash
# 同步代码到官方仓库
git push official master
git push official --tags
```

---

## ❓ 常见问题

**Q: 为什么官方仓库不能自动构建？**  
A: Xposed-Modules-Repo 组织管理员禁用了 Actions，这是组织策略，无法修改。

**Q: 每次都要手动同步吗？**  
A: 设置 GitHub Token 后，可以使用 `sync-release.ps1` 脚本一键同步。

**Q: 可以完全自动化吗？**  
A: 可以在个人仓库的 Actions 中添加自动同步步骤，但需要配置 GitHub Token Secret。




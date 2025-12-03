# Release 同步指南（手动方式）

由于官方仓库的 GitHub Actions 被组织管理员禁用，无法自动构建，需要手动同步 Release。

## 🎯 方案说明

### 工作流程

1. **个人仓库**（`MarsGao/io.github.MarsGao.speed`）:
   - ✅ GitHub Actions 正常工作
   - ✅ 自动构建 APK
   - ✅ 自动创建 Release

2. **官方仓库**（`Xposed-Modules-Repo/io.github.MarsGao.speed`）:
   - ❌ GitHub Actions 被禁用
   - ✅ 需要手动同步 Release

## 🚀 自动化方案（推荐）

### 方式一：使用 PowerShell 脚本（需要 GitHub Token）

1. **设置 GitHub Personal Access Token**:
   ```powershell
   # 创建 Token: https://github.com/settings/tokens
   # 需要权限: repo (全部权限)
   $env:GITHUB_TOKEN = "your_token_here"
   ```

2. **运行同步脚本**:
   ```powershell
   # 同步最新 Release
   .\sync-release.ps1
   
   # 或指定特定 Tag
   .\sync-release.ps1 1002000-1.2.0
   ```

### 方式二：使用 GitHub CLI（推荐，更简单）

如果你安装了 GitHub CLI (`gh`):

```bash
# 1. 登录 GitHub CLI
gh auth login

# 2. 从个人仓库下载最新 Release APK
$latestRelease = gh release view --repo MarsGao/io.github.MarsGao.speed --json tagName,assets
$tagName = $latestRelease.tagName
$apkUrl = ($latestRelease.assets | Where-Object { $_.name -like "*.apk" }).url

# 3. 下载 APK
Invoke-WebRequest -Uri $apkUrl -OutFile "VideoSpeed.apk"

# 4. 创建官方仓库 Release
gh release create $tagName --repo Xposed-Modules-Repo/io.github.MarsGao.speed --title "$tagName" --notes "从个人仓库同步" VideoSpeed.apk
```

## 📋 手动同步步骤

如果自动化脚本无法使用，可以手动操作：

### 步骤 1: 下载 APK

1. 访问个人仓库 Releases: https://github.com/MarsGao/io.github.MarsGao.speed/releases
2. 找到最新 Release（如 `1002000-1.2.0`）
3. 下载 APK 文件（如 `VideoSpeed_1.2.0.apk`）

### 步骤 2: 创建官方仓库 Release

1. 访问官方仓库: https://github.com/Xposed-Modules-Repo/io.github.MarsGao.speed
2. 点击 **Releases** → **Create a new release**
3. 填写信息:
   - **Tag**: 输入 tag 名称（如 `1002000-1.2.0`）
   - **Release title**: 输入标题（如 `1002000-1.2.0`）
   - **Description**: 复制个人仓库 Release 的描述
4. 上传 APK 文件
5. 点击 **Publish release**

## 🔄 完整发布流程

### 1. 在个人仓库发布新版本

```bash
# 1. 更新版本号
# 编辑 gradle.properties: appVersionName=1.2.1

# 2. 提交并推送
git add gradle.properties
git commit -m "chore: 更新版本号到 1.2.1"
git push origin master

# 3. 创建 tag（触发 GitHub Actions 构建）
git tag 1002001-1.2.1
git tag v1.2.1
git push origin --tags

# 4. 等待 GitHub Actions 完成构建（约 5-10 分钟）
# 检查: https://github.com/MarsGao/io.github.MarsGao.speed/actions
```

### 2. 同步到官方仓库

```bash
# 方式一：使用脚本（推荐）
.\sync-release.ps1 1002001-1.2.1

# 方式二：使用 GitHub CLI
gh release create 1002001-1.2.1 --repo Xposed-Modules-Repo/io.github.MarsGao.speed --title "1002001-1.2.1" --notes "..." VideoSpeed_1.2.1.apk

# 方式三：手动操作（见上方步骤）
```

### 3. 同步代码（可选）

```bash
# 同步代码到官方仓库（不含 Release）
git push official master
git push official --tags
```

## ⚙️ 设置 GitHub Token（用于自动化）

### 创建 Personal Access Token

1. 访问: https://github.com/settings/tokens
2. 点击 **Generate new token** → **Generate new token (classic)**
3. 设置:
   - **Note**: `Release Sync Script`
   - **Expiration**: 根据需要选择（建议 90 天或更长）
   - **Scopes**: 勾选 `repo` (全部权限)
4. 点击 **Generate token**
5. **复制 Token**（只显示一次，请保存）

### 在 PowerShell 中设置

```powershell
# 临时设置（当前会话）
$env:GITHUB_TOKEN = "ghp_xxxxxxxxxxxxxxxxxxxx"

# 永久设置（用户环境变量）
[System.Environment]::SetEnvironmentVariable("GITHUB_TOKEN", "ghp_xxxxxxxxxxxxxxxxxxxx", "User")
```

### 在脚本中使用

脚本会自动读取 `$env:GITHUB_TOKEN` 环境变量。

## 🔍 验证同步

同步完成后，检查：

1. **官方仓库 Release**: https://github.com/Xposed-Modules-Repo/io.github.MarsGao.speed/releases
2. **LSPosed 模块仓库**: https://modules.lsposed.org/（等待 10-30 分钟同步）

## ❓ 常见问题

**Q: 为什么官方仓库不能使用 Actions？**  
A: Xposed-Modules-Repo 组织管理员禁用了 Actions，这是组织级别的设置，无法修改。

**Q: 每次都要手动同步吗？**  
A: 使用自动化脚本后，只需运行一条命令即可。

**Q: 可以自动触发同步吗？**  
A: 可以在个人仓库的 GitHub Actions 中添加一个步骤，在构建完成后自动调用同步脚本。但这需要配置 GitHub Token 作为 Secret。

**Q: 同步脚本失败怎么办？**  
A: 检查：
- GitHub Token 是否正确设置
- Token 是否有 `repo` 权限
- 网络连接是否正常
- 官方仓库是否已有同名 Release（需要先删除）

## 📝 后续优化建议

1. **在个人仓库 Actions 中添加自动同步步骤**:
   - 在 `.github/workflows/android-build.yml` 中添加同步步骤
   - 使用 GitHub Token Secret

2. **使用 GitHub CLI 简化操作**:
   - 安装: `winget install GitHub.cli`
   - 更简单的命令和更好的错误处理

3. **创建 Release 模板**:
   - 在 `.github/release-template.md` 中创建模板
   - 确保两个仓库的 Release 格式一致




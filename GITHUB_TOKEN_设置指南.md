# GitHub Token 设置指南（小白版）

## 📌 重要说明

**GitHub Token 不是填写在脚本文件里的！**  
它是通过 PowerShell 命令临时设置的，或者永久保存在系统环境变量中。

---

## 🎯 方法一：临时设置（推荐，最简单）

### 步骤 1：创建 GitHub Token

1. **打开浏览器，登录 GitHub**
   - 访问：https://github.com/login

2. **进入 Token 设置页面**
   - 点击右上角头像 → **Settings**（设置）
   - 左侧菜单最下方 → **Developer settings**（开发者设置）
   - 左侧菜单 → **Personal access tokens** → **Tokens (classic)**（经典令牌）

3. **创建新 Token**
   - 点击 **Generate new token** → **Generate new token (classic)**
   - **Note（备注）**：填写 `VideoSpeed 上传 APK`（随便写，方便识别）
   - **Expiration（过期时间）**：选择 `90 days` 或 `No expiration`（无过期）
   - **Select scopes（选择权限）**：**必须勾选以下权限**
     - ✅ `repo`（完整仓库权限，包括上传 Release）
     - ✅ `write:packages`（可选，如果以后需要）

4. **生成并复制 Token**
   - 点击页面最下方的 **Generate token**（生成令牌）
   - **⚠️ 重要：立即复制 Token！** 页面关闭后就看不到了
   - Token 格式类似：`ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`

### 步骤 2：在 PowerShell 中使用

**打开 PowerShell**（在项目文件夹中右键 → "在终端中打开" 或 "Open in Terminal"）

**执行以下命令：**

```powershell
# 设置 Token（替换成你刚才复制的 Token）
$env:GITHUB_TOKEN = "ghp_你的token在这里"

# 验证是否设置成功（会显示你的 Token，确认一下）
echo $env:GITHUB_TOKEN

# 运行上传脚本
.\upload-apk-to-release.ps1 v1.2.0
```

**⚠️ 注意：**
- 这个设置只在**当前 PowerShell 窗口**有效
- 关闭窗口后需要重新设置
- Token 不会保存到脚本文件中，很安全

---

## 🎯 方法二：永久设置（可选）

如果你不想每次都输入，可以永久保存到系统环境变量：

### Windows 设置步骤：

1. **打开系统环境变量设置**
   - 按 `Win + R`，输入 `sysdm.cpl`，回车
   - 点击 **高级** → **环境变量**

2. **添加用户环境变量**
   - 在 **用户变量** 区域点击 **新建**
   - **变量名**：`GITHUB_TOKEN`
   - **变量值**：你的 Token（`ghp_xxxxx...`）
   - 点击 **确定** 保存

3. **重启 PowerShell**
   - 关闭所有 PowerShell 窗口
   - 重新打开，Token 就会自动加载

---

## 📝 完整使用示例

```powershell
# 1. 进入项目目录（如果还没进入）
cd C:\GkDesktop\OneDrive\GkObsidian\5-GitProjects\biliSpeed

# 2. 设置 Token（替换成你的真实 Token）
$env:GITHUB_TOKEN = "ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"

# 3. 运行脚本上传 APK
.\upload-apk-to-release.ps1 v1.2.0
```

---

## ❓ 常见问题

### Q1: Token 在哪里看？
**A:** 创建后只显示一次，如果丢失了，需要重新创建：
- 进入 Token 列表：https://github.com/settings/tokens
- 找到你创建的 Token，点击 **Regenerate**（重新生成）

### Q2: Token 会泄露吗？
**A:** 
- 如果只是临时设置（`$env:GITHUB_TOKEN`），只在当前窗口有效，很安全
- 不要提交到 Git 仓库
- 不要分享给他人

### Q3: 权限不够怎么办？
**A:** 
- 确保 Token 有 `repo` 权限
- 确保你对 `Xposed-Modules-Repo/io.github.MarsGao.speed` 仓库有写入权限（已接受邀请）

### Q4: 脚本报错 "未设置 GITHUB_TOKEN"？
**A:** 
- 检查是否在 PowerShell 中设置了：`echo $env:GITHUB_TOKEN`
- 如果显示为空，重新设置一次
- 确保在**同一个 PowerShell 窗口**中先设置 Token，再运行脚本

---

## 🚀 快速开始（3 步）

1. **创建 Token**：https://github.com/settings/tokens → Generate new token (classic) → 勾选 `repo` → 复制 Token
2. **设置 Token**：在 PowerShell 中执行 `$env:GITHUB_TOKEN = "你的token"`
3. **运行脚本**：`.\upload-apk-to-release.ps1 v1.2.0`

---

## 📌 总结

- ✅ Token 在 **PowerShell 中设置**，不在脚本文件中
- ✅ 使用 `$env:GITHUB_TOKEN = "你的token"` 命令
- ✅ 每次打开新的 PowerShell 窗口需要重新设置（或使用永久设置方法）
- ✅ Token 需要 `repo` 权限



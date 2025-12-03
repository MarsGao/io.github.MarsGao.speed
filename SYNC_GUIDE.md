# 同步到 Xposed-Modules-Repo 官方仓库指南

## 📋 情况说明

Xposed-Modules-Repo 已经为你创建了官方仓库：
- **官方仓库**: https://github.com/Xposed-Modules-Repo/io.github.MarsGao.speed
- **你的仓库**: https://github.com/MarsGao/io.github.MarsGao.speed

## ✅ 需要完成的任务

1. **接受邀请**（如果还没接受）
2. **推送代码**到官方仓库
3. **设置仓库描述**（用于显示模块名称）
4. **确保有 Release**（已有 v1.2.0 ✅）

## 🔧 操作步骤

### 步骤 1: 接受邀请

1. 访问: https://github.com/Xposed-Modules-Repo/io.github.MarsGao.speed/invitations
2. 或者检查邮箱中的邀请邮件
3. 点击 **Accept** 接受邀请

### 步骤 2: 推送代码（接受邀请后执行）

在项目根目录执行：

```bash
# 添加官方仓库为 remote（如果还没添加）
git remote add official https://github.com/Xposed-Modules-Repo/io.github.MarsGao.speed.git

# 推送 master 分支
git push official master

# 推送所有 tags（包括 v1.2.0）
git push official --tags
```

### 步骤 3: 设置仓库描述

访问: https://github.com/Xposed-Modules-Repo/io.github.MarsGao.speed/settings

在 **Repository name** 下方找到 **Description** 字段，输入：
```
视频调速 VideoSpeed - 视频播放速度调节 Xposed 模块
```

然后点击 **Save changes**。

### 步骤 4: 验证

1. 检查仓库是否显示描述: https://github.com/Xposed-Modules-Repo/io.github.MarsGao.speed
2. 检查是否有 Release: https://github.com/Xposed-Modules-Repo/io.github.MarsGao.speed/releases
3. 等待 10 分钟后，在 LSPosed 管理器中搜索 "视频调速" 或 "VideoSpeed"

## 🔄 后续同步

以后每次更新代码时，需要同时推送到两个仓库：

```bash
# 推送到你的仓库
git push origin master

# 推送到官方仓库
git push official master

# 推送 tags
git push origin --tags
git push official --tags
```

或者设置一个脚本自动同步两个仓库。

## ❓ 常见问题

**Q: 推送时提示权限被拒绝？**  
A: 请先接受邀请（步骤 1）

**Q: 仓库描述在哪里设置？**  
A: 仓库 Settings 页面，在 Repository name 下方

**Q: 模块什么时候会在 LSPosed 中显示？**  
A: 满足条件后（描述不为空 + 有 Release），通常 10 分钟内会显示




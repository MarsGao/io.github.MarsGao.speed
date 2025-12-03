# 同步脚本：将代码和 tags 同步到个人仓库和官方仓库
# 使用方法: .\sync-repos.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  仓库同步脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查当前分支
$currentBranch = git branch --show-current
Write-Host "当前分支: $currentBranch" -ForegroundColor Yellow
Write-Host ""

# 检查是否有未提交的更改
$status = git status --porcelain
if ($status) {
    Write-Host "⚠️  警告: 检测到未提交的更改" -ForegroundColor Red
    Write-Host "请先提交更改后再同步" -ForegroundColor Red
    exit 1
}

# 同步到个人仓库
Write-Host "📤 正在同步到个人仓库 (origin)..." -ForegroundColor Green
$result1 = git push origin $currentBranch 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ 个人仓库同步成功" -ForegroundColor Green
} else {
    Write-Host "❌ 个人仓库同步失败" -ForegroundColor Red
    Write-Host $result1
    exit 1
}

# 同步 tags 到个人仓库
Write-Host "📤 正在推送 tags 到个人仓库..." -ForegroundColor Green
$result2 = git push origin --tags 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Tags 推送成功" -ForegroundColor Green
} else {
    Write-Host "⚠️  Tags 推送可能失败或无需更新" -ForegroundColor Yellow
}

Write-Host ""

# 同步到官方仓库
Write-Host "📤 正在同步到官方仓库 (official)..." -ForegroundColor Green
$result3 = git push official $currentBranch 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ 官方仓库同步成功" -ForegroundColor Green
} else {
    Write-Host "❌ 官方仓库同步失败" -ForegroundColor Red
    Write-Host $result3
    exit 1
}

# 同步 tags 到官方仓库
Write-Host "📤 正在推送 tags 到官方仓库..." -ForegroundColor Green
$result4 = git push official --tags 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Tags 推送成功" -ForegroundColor Green
} else {
    Write-Host "⚠️  Tags 推送可能失败或无需更新" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "✅ 同步完成！" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "个人仓库: https://github.com/MarsGao/io.github.MarsGao.speed" -ForegroundColor Cyan
Write-Host "官方仓库: https://github.com/Xposed-Modules-Repo/io.github.MarsGao.speed" -ForegroundColor Cyan
Write-Host ""




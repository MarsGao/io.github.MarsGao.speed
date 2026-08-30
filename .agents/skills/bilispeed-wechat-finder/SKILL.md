---
name: bilispeed-wechat-finder
description: BiliSpeed 微信视频号默认倍速复测与适配维护流程。Use when working in the biliSpeed repo on WeChat/微信视频号/default playback speed/LSPosed/Vector/FinderHome/FinderThumbPlayerProxy regressions, updating VideoSpeed APK on rooted Android devices, or rechecking after WeChat version changes.
---

# BiliSpeed WeChat Finder

Use this project skill to avoid re-discovering the WeChat Video Channels path from scratch. The stable route for WeChat `8.0.69` ~ `8.0.77+` is LSPosed Java hook into Finder feed, not ExoPlayer and not native FFmpeg/ThumbPlayer symbols.

## Known Good Path

- Default speed is applied by actively injecting on the current feed player after playback/navigation events.
- Runtime player class: `com.tencent.mm.plugin.finder.video.FinderThumbPlayerProxy`.
- Feed entry activity: `com.tencent.mm.plugin.finder.ui.FinderHomeAffinityUI`.
- Current holder path:
  - Dynamically resolves resource IDs via `Resources.getIdentifier(...)`:
    - `id/m6e` (`RefreshLoadMoreLayout` / `RecyclerView`): `0x7f093e42` (8.0.69) -> `0x7f095aa3` (8.0.77).
    - `id/e_k` (`FinderVideoLayout`): `0x7f090930` (8.0.69) -> `0x7f0922cb` (8.0.77).
  - Dynamically traverses `decorView` if resource ID lookup fails.
  - Inspects `holder.itemView.findViewById(e_k_id)` or traverses `itemView` directly, decoupling from obfuscated class names (`me5.s0` in 8.0.69, `yr5.s0` in 8.0.77).
  - Calls `FinderVideoLayout.getVideoView()` to obtain `FinderThumbPlayerProxy`.
- Setter proven on WeChat `8.0.69` (`versionCode 3040`) and `8.0.77` (`versionCode 3160`): `setPlaySpeed(float)` & `setPlaySpeedRatio(float)`.
- Speed menu UIC hook: `q40.onClick` (8.0.69) and `FinderSpeedControlUIC.onClick` (8.0.77).
- Injection triggers: `Activity.onResume` delayed and `Activity.dispatchTouchEvent(ACTION_UP)` delayed.
- Settings bridge: Target processes query `content://io.github.MarsGao.speed.config/speed` first, with graceful fallback to `XSharedPreferences`.

## Fast Verification

Always use an explicit device serial when more than one Android device may be connected:

```powershell
$serial = '4236fdb8' # mi14pro
adb -s $serial devices -l
adb -s $serial shell getprop ro.product.device
adb -s $serial shell getprop ro.build.display.id
adb -s $serial shell dumpsys package io.github.MarsGao.speed | Select-String -Pattern 'versionName=|versionCode=|lastUpdateTime='
adb -s $serial shell dumpsys package com.tencent.mm | Select-String -Pattern 'versionName=|versionCode=|lastUpdateTime='
```

Build and install debug APK:

```powershell
$env:JAVA_HOME = 'C:\GkDesktop\Tools\WeChatLocationBridge\jdk-21.0.8+9'
$env:ANDROID_HOME = 'C:\GkDesktop\Tools\WeChatLocationBridge\android-sdk'
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
.\gradlew.bat --no-daemon -Dorg.gradle.java.home=$env:JAVA_HOME :app:assembleDebug
adb -s $serial install -r -d 'app\build\outputs\apk\debug\app-debug.apk'
```

Enter WeChat Video Channels:

```powershell
adb -s $serial shell am force-stop com.tencent.mm
Start-Sleep -Seconds 1
adb -s $serial shell "su -c 'am start -n com.tencent.mm/com.tencent.mm.plugin.finder.ui.FinderHomeAffinityUI'"
Start-Sleep -Seconds 2
adb -s $serial shell input swipe 500 1500 500 500 300
```

Success log patterns:

```text
[VideoSpeed] [FinderInject] Activity resume/touch hooks installed
[VideoSpeed] [ResId] resolved id/m6e = 0x7f095aa3 (2131319459)
[VideoSpeed] [ResId] resolved id/e_k = 0x7f0922cb (2131305163)
[VideoSpeed] FinderActivity.onResume current com.tencent.mm.plugin.finder.video.FinderThumbPlayerProxy setRate target via setPlaySpeed: 1.65
[VideoSpeed] FinderActivity.touchUp current com.tencent.mm.plugin.finder.video.FinderThumbPlayerProxy setRate target via setPlaySpeed: 1.65
```

Pull only the relevant tail:

```powershell
$out = adb -s $serial shell su -c 'cat /data/adb/lspd/log/modules_*.log /data/adb/lspd/log/verbose_*.log 2>/dev/null | tail -n 5000'
$out | Select-String -Pattern '\[VideoSpeed\].*(FinderInject|setRate target|manual speed|ResId)' | Select-Object -Last 60
```

## Automated DEX & Resource Reverse Engineering Pipeline

When a new WeChat version is released:

1. **Pull APK & Resources from Device**:
   ```powershell
   $apkPath = (adb -s $serial shell pm path com.tencent.mm).Replace('package:', '').Trim()
   adb -s $serial shell "su -c 'cp $apkPath /sdcard/base.apk && chmod 644 /sdcard/base.apk'"
   adb -s $serial pull /sdcard/base.apk ./wx_base.apk
   ```

2. **Extract & Inspect Resource Table (`resources.arsc`)**:
   Use `aapt2` to dump resource symbols and extract current `id/m6e` and `id/e_k`:
   ```powershell
   aapt2 dump resources ./wx_base.apk | Select-String -Pattern 'id/m6e|id/e_k'
   ```

3. **Scan DEX for Player Classes & Speed Setters**:
   Use Python zipfile to extract `classes*.dex` and parse string/type IDs for `FinderThumbPlayerProxy`, `FinderVideoLayout`, `FinderSpeedControlUIC`, and `setPlaySpeed`.

4. **Verify Dynamic Discovery Fallback**:
   Because `getWeChatResId` dynamically invokes `Resources.getIdentifier(...)`, minor WeChat updates with unchanged resource names (`m6e`, `e_k`) adapt automatically with zero code modification.

## Release Checklist

- If the hook architecture, Finder path, configuration sharing, supported version matrix, or build baseline changes, update `TECHNICAL_OVERVIEW.md` and `README.md`.
- Update `gradle.properties` `appVersionName` (e.g. `1.2.9`).
- Build with JDK 21 and confirm Gradle prints the expected versionCode (`1002009`).
- Install on rooted test device and verify WeChat Video Channels behavior.
- Tag format: `VersionCode-VersionName`, for example `1002009-1.2.9`.
- Push to GitHub repository.

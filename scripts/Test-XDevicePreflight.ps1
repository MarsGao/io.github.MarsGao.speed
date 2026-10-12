# Read-only preflight. A successful preflight is NOT playback acceptance.
[CmdletBinding()]
param(
    [Parameter(Mandatory)][ValidateNotNullOrEmpty()][string]$Serial,
    [string]$ExpectedModel = 'PJZ110',
    [string]$Adb = 'adb',
    [switch]$CollectHookEvidence,
    [string]$OutputPath
)
$ErrorActionPreference = 'Stop'

function Invoke-AdbRead([string[]]$Arguments) {
    $result = & $Adb -s $Serial @Arguments 2>&1
    if ($LASTEXITCODE -ne 0) { throw "ADB read failed: $($result -join ' ')" }
    return ($result -join "`n").Trim()
}

$report = [ordered]@{
    schemaVersion = 1
    capturedAt = [DateTimeOffset]::Now.ToString('o')
    serial = $Serial
    status = 'BLOCKED'
    playbackAcceptance = 'NOT_TESTED'
    reason = $null
}
try {
    $state = Invoke-AdbRead -Arguments @('get-state')
    if ($state -ne 'device') { throw "Device state is $state" }
    $report.model = Invoke-AdbRead -Arguments @('shell', 'getprop', 'ro.product.model')
    if ($report.model -ne $ExpectedModel) {
        throw "Wrong target: expected $ExpectedModel, found $($report.model)"
    }
    $report.android = Invoke-AdbRead -Arguments @('shell', 'getprop', 'ro.build.version.release')
    $report.build = Invoke-AdbRead -Arguments @('shell', 'getprop', 'ro.build.display.id')
    $report.bootCompleted = Invoke-AdbRead -Arguments @('shell', 'getprop', 'sys.boot_completed')
    if ($report.bootCompleted -ne '1') { throw 'Boot is not complete' }
    foreach ($package in @('com.twitter.android', 'io.github.MarsGao.speed')) {
        $dump = Invoke-AdbRead -Arguments @('shell', 'dumpsys', 'package', $package)
        $version = [regex]::Match($dump, 'versionName=([^\r\n]+)')
        $code = [regex]::Match($dump, 'versionCode=(\d+)')
        if (!$version.Success -or !$code.Success) { throw "Package missing: $package" }
        $report[$package] = [ordered]@{
            versionName = $version.Groups[1].Value.Trim()
            versionCode = $code.Groups[1].Value
        }
    }
    # Shell IPC is supporting evidence only; injected X UID must also be checked.
    $report.shellConfigQuery = Invoke-AdbRead -Arguments @('shell', 'content', 'query', '--uri',
        'content://io.github.MarsGao.speed.config/speed')
    $report.shellConfigReadable = $report.shellConfigQuery -match 'Row: \d+ speed=(\d+(\.\d+)?)'
    if ($CollectHookEvidence) {
        # Opt-in root read: only module messages from the current X PID are retained.
        # Historical hook registration or speed messages must never pass a new run.
        $pidText = Invoke-AdbRead -Arguments @('shell', 'pidof', 'com.twitter.android')
        if ($pidText -notmatch '^\d+$') { throw 'Expected exactly one running X main process' }
        $report.xPid = $pidText
        $log = Invoke-AdbRead -Arguments @('shell', "su -c 'tail -n 5000 /data/adb/lspd/log/modules_*.log'")
        $report.currentProcessMessages = @($log -split "`n" | Where-Object {
            $_ -match ":\s*${pidText}:" -and $_ -match '\[VideoSpeed\]'
        })
        $report.hookRegistrationObserved = @($report.currentProcessMessages | Where-Object {
            $_ -match '\[Twitter\] hooked '
        }).Count -gt 0
        $report.speedCallObserved = @($report.currentProcessMessages | Where-Object {
            $_ -match '\[Twitter\].*(setPlaybackSpeed 1\.0 ->|prepare ->)'
        }).Count -gt 0
        $report.evidenceLimit = 'PID-filtered log tail; verify timestamps and actual playback separately'
    }
    $report.status = 'PREFLIGHT_OK'
} catch {
    $report.reason = $_.Exception.Message
}
$json = $report | ConvertTo-Json -Depth 5
if ($OutputPath) {
    $json | Set-Content -LiteralPath $OutputPath -Encoding utf8NoBOM
    $saved = Get-Item -LiteralPath $OutputPath
    if ($saved.Length -eq 0) { throw 'Empty report' }
    $null = Get-Content -LiteralPath $OutputPath -Raw | ConvertFrom-Json
}
$json
if ($report.status -ne 'PREFLIGHT_OK') { exit 1 }

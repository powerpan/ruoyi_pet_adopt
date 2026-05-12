Add-Type -AssemblyName System.Windows.Forms
Add-Type -AssemblyName System.Drawing

$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $Root

$AppName = "宠物领养平台"
$EnvFile = Join-Path $Root "deploy\.env"
$EnvExample = Join-Path $Root "deploy\.env.example"

function Get-EnvValue {
  param(
    [string]$Key,
    [string]$Fallback
  )

  if (-not (Test-Path $EnvFile)) {
    return $Fallback
  }

  $line = Get-Content $EnvFile | Where-Object { $_ -match "^$([regex]::Escape($Key))=" } | Select-Object -Last 1
  if (-not $line) {
    return $Fallback
  }

  $value = $line.Substring($Key.Length + 1).Trim()
  if ([string]::IsNullOrWhiteSpace($value)) {
    return $Fallback
  }

  return $value
}

function Ensure-EnvFile {
  if (-not (Test-Path $EnvFile)) {
    Copy-Item $EnvExample $EnvFile
    Write-Log "已从 deploy\.env.example 生成 deploy\.env。"
  }
}

function Write-Log {
  param([string]$Message)
  $timestamp = Get-Date -Format "HH:mm:ss"
  $LogBox.AppendText("[$timestamp] $Message`r`n")
  $LogBox.SelectionStart = $LogBox.TextLength
  $LogBox.ScrollToCaret()
  [System.Windows.Forms.Application]::DoEvents()
}

function Set-Busy {
  param([bool]$Busy)
  foreach ($button in $ActionButtons) {
    $button.Enabled = -not $Busy
  }
  $Progress.Visible = $Busy
  [System.Windows.Forms.Application]::DoEvents()
}

function Invoke-Cmd {
  param([string]$Command)

  Write-Log "> $Command"
  $process = New-Object System.Diagnostics.Process
  $process.StartInfo.FileName = "cmd.exe"
  $process.StartInfo.Arguments = "/c $Command 2>&1"
  $process.StartInfo.WorkingDirectory = $Root
  $process.StartInfo.UseShellExecute = $false
  $process.StartInfo.RedirectStandardOutput = $true
  $process.StartInfo.RedirectStandardError = $true
  $process.StartInfo.CreateNoWindow = $true

  [void]$process.Start()
  $stdoutTask = $process.StandardOutput.ReadToEndAsync()
  $stderrTask = $process.StandardError.ReadToEndAsync()
  Write-Log "命令执行中，请稍候..."

  while (-not $process.HasExited) {
    Start-Sleep -Milliseconds 100
    [System.Windows.Forms.Application]::DoEvents()
  }

  $stdout = $stdoutTask.Result
  $stderr = $stderrTask.Result

  foreach ($line in ($stdout -split "`r?`n")) {
    if (-not [string]::IsNullOrWhiteSpace($line)) {
      Write-Log $line
    }
  }

  foreach ($line in ($stderr -split "`r?`n")) {
    if (-not [string]::IsNullOrWhiteSpace($line)) {
      Write-Log $line
    }
  }

  if ($process.ExitCode -ne 0) {
    Write-Log "命令退出码：$($process.ExitCode)"
  }

  return $process.ExitCode
}

function Get-ComposePrefix {
  $code = Invoke-Cmd "docker compose version"
  if ($code -eq 0) {
    return "docker compose"
  }

  $code = Invoke-Cmd "docker-compose version"
  if ($code -eq 0) {
    return "docker-compose"
  }

  return $null
}

function Ensure-Docker {
  $docker = Get-Command docker -ErrorAction SilentlyContinue
  if (-not $docker) {
    [System.Windows.Forms.MessageBox]::Show("未检测到 Docker。请先安装并启动 Docker Desktop。", $AppName, "OK", "Warning") | Out-Null
    return $false
  }

  $composePrefix = Get-ComposePrefix
  if (-not $composePrefix) {
    [System.Windows.Forms.MessageBox]::Show("未检测到 Docker Compose。请更新 Docker Desktop。", $AppName, "OK", "Warning") | Out-Null
    return $false
  }

  $script:ComposePrefix = $composePrefix
  return $true
}

function Ensure-DockerRunning {
  if (-not (Ensure-Docker)) {
    return $false
  }

  $code = Invoke-Cmd "docker info"
  if ($code -ne 0) {
    [System.Windows.Forms.MessageBox]::Show("Docker Desktop 还没有运行。请先打开 Docker Desktop，等它启动完成后再回来点击启动。", $AppName, "OK", "Warning") | Out-Null
    return $false
  }

  return $true
}

function Compose-Command {
  param([string]$Args)
  return "$script:ComposePrefix --env-file deploy\.env -f deploy\docker-compose.yml $Args"
}

function Wait-Backend {
  $backendPort = Get-EnvValue "BACKEND_PORT" "8080"
  Write-Log "等待后端启动：http://localhost:$backendPort"

  for ($i = 1; $i -le 90; $i++) {
    try {
      Invoke-WebRequest -UseBasicParsing -TimeoutSec 2 -Uri "http://localhost:$backendPort/app/pet/topics?pageNum=1&pageSize=1" | Out-Null
      Write-Log "后端已就绪。"
      return $true
    } catch {
      Start-Sleep -Seconds 2
      [System.Windows.Forms.Application]::DoEvents()
    }
  }

  Write-Log "后端仍在启动中。"
  return $false
}

function Open-Pages {
  $adminPort = Get-EnvValue "ADMIN_PORT" "8081"
  $petPort = Get-EnvValue "PET_APP_PORT" "8088"
  Start-Process "http://localhost:$adminPort"
  Start-Process "http://localhost:$petPort"
}

function Start-Stack {
  Set-Busy $true
  try {
    if (-not (Ensure-DockerRunning)) {
      return
    }

    Ensure-EnvFile
    $code = Invoke-Cmd (Compose-Command "up -d --build")
    if ($code -ne 0) {
      [System.Windows.Forms.MessageBox]::Show("启动失败，请查看日志。", $AppName, "OK", "Error") | Out-Null
      return
    }

    $ready = Wait-Backend
    if ($OpenAfterStart.Checked) {
      Open-Pages
    }

    if ($ready) {
      [System.Windows.Forms.MessageBox]::Show("服务已启动。", $AppName, "OK", "Information") | Out-Null
    } else {
      [System.Windows.Forms.MessageBox]::Show("服务已提交启动，但后端还没完全就绪。如果页面显示 502，请稍等片刻后刷新。", $AppName, "OK", "Warning") | Out-Null
    }
  } finally {
    Set-Busy $false
  }
}

function Stop-Stack {
  Set-Busy $true
  try {
    if (-not (Ensure-Docker)) {
      return
    }

    Ensure-EnvFile
    $code = Invoke-Cmd (Compose-Command "down")
    if ($code -eq 0) {
      [System.Windows.Forms.MessageBox]::Show("服务已停止。数据库和上传文件卷会保留。", $AppName, "OK", "Information") | Out-Null
    } else {
      [System.Windows.Forms.MessageBox]::Show("停止失败，请查看日志。", $AppName, "OK", "Error") | Out-Null
    }
  } finally {
    Set-Busy $false
  }
}

function Show-Status {
  Set-Busy $true
  try {
    if (-not (Ensure-Docker)) {
      return
    }

    Ensure-EnvFile
    Invoke-Cmd (Compose-Command "ps")
  } finally {
    Set-Busy $false
  }
}

function Open-Admin {
  Ensure-EnvFile
  $adminPort = Get-EnvValue "ADMIN_PORT" "8081"
  Start-Process "http://localhost:$adminPort"
}

function Open-Client {
  Ensure-EnvFile
  $petPort = Get-EnvValue "PET_APP_PORT" "8088"
  Start-Process "http://localhost:$petPort"
}

function Open-Config {
  Ensure-EnvFile
  Start-Process notepad.exe $EnvFile
}

$Form = New-Object System.Windows.Forms.Form
$Form.Text = $AppName
$Form.Size = New-Object System.Drawing.Size(820, 560)
$Form.StartPosition = "CenterScreen"
$Form.MinimumSize = New-Object System.Drawing.Size(760, 500)

$Title = New-Object System.Windows.Forms.Label
$Title.Text = "宠物领养平台启动面板"
$Title.Font = New-Object System.Drawing.Font("Microsoft YaHei UI", 16, [System.Drawing.FontStyle]::Bold)
$Title.AutoSize = $true
$Title.Location = New-Object System.Drawing.Point(18, 16)
$Form.Controls.Add($Title)

$Hint = New-Object System.Windows.Forms.Label
$Hint.Text = "启动会自动构建并拉起后端、管理端、用户端、MySQL 和 Redis。首次启动耗时较久。"
$Hint.AutoSize = $true
$Hint.Location = New-Object System.Drawing.Point(22, 54)
$Form.Controls.Add($Hint)

$ButtonPanel = New-Object System.Windows.Forms.FlowLayoutPanel
$ButtonPanel.Location = New-Object System.Drawing.Point(18, 86)
$ButtonPanel.Size = New-Object System.Drawing.Size(770, 42)
$ButtonPanel.WrapContents = $false
$Form.Controls.Add($ButtonPanel)

function New-ActionButton {
  param(
    [string]$Text,
    [scriptblock]$Action
  )

  $button = New-Object System.Windows.Forms.Button
  $button.Text = $Text
  $button.Width = 104
  $button.Height = 32
  $button.Margin = New-Object System.Windows.Forms.Padding(0, 0, 8, 0)
  $button.Add_Click($Action)
  $ButtonPanel.Controls.Add($button)
  return $button
}

$ActionButtons = New-Object System.Collections.ArrayList
[void]$ActionButtons.Add((New-ActionButton "启动服务" { Start-Stack }))
[void]$ActionButtons.Add((New-ActionButton "停止服务" { Stop-Stack }))
[void]$ActionButtons.Add((New-ActionButton "查看状态" { Show-Status }))
[void]$ActionButtons.Add((New-ActionButton "打开管理端" { Open-Admin }))
[void]$ActionButtons.Add((New-ActionButton "打开用户端" { Open-Client }))
[void]$ActionButtons.Add((New-ActionButton "打开配置" { Open-Config }))

$OpenAfterStart = New-Object System.Windows.Forms.CheckBox
$OpenAfterStart.Text = "启动后打开页面"
$OpenAfterStart.Checked = $true
$OpenAfterStart.Width = 140
$OpenAfterStart.Height = 32
$ButtonPanel.Controls.Add($OpenAfterStart)

$Progress = New-Object System.Windows.Forms.ProgressBar
$Progress.Style = "Marquee"
$Progress.MarqueeAnimationSpeed = 35
$Progress.Location = New-Object System.Drawing.Point(22, 140)
$Progress.Size = New-Object System.Drawing.Size(760, 12)
$Progress.Visible = $false
$Form.Controls.Add($Progress)

$LogBox = New-Object System.Windows.Forms.TextBox
$LogBox.Multiline = $true
$LogBox.ScrollBars = "Vertical"
$LogBox.ReadOnly = $true
$LogBox.WordWrap = $false
$LogBox.Font = New-Object System.Drawing.Font("Consolas", 9)
$LogBox.Location = New-Object System.Drawing.Point(22, 166)
$LogBox.Size = New-Object System.Drawing.Size(760, 330)
$LogBox.Anchor = "Top,Bottom,Left,Right"
$Form.Controls.Add($LogBox)

$Form.Add_Shown({
  Write-Log "请选择操作。"
  Write-Log "项目目录：$Root"
})

[void]$Form.ShowDialog()

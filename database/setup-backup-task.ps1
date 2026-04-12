# ============================================================
# setup-backup-task.ps1 - Registra la tarea programada en Windows
# Ejecutar UNA SOLA VEZ como Administrador.
# Programa el backup diario a las 14:00.
# ============================================================

$taskName   = "MiniAcademia - Backup DB diario"
$scriptPath = "$PSScriptRoot\backup.ps1"
$logPath    = "$PSScriptRoot\backups\backup.log"

# Accion: ejecutar PowerShell con el script de backup
$action = New-ScheduledTaskAction `
    -Execute "powershell.exe" `
    -Argument "-NonInteractive -ExecutionPolicy Bypass -File `"$scriptPath`" >> `"$logPath`" 2>&1"

# Disparador: cada dia a las 14:00
$trigger = New-ScheduledTaskTrigger -Daily -At "14:00"

# Configuracion: ejecutar aunque no haya sesion abierta, maxima prioridad
$settings = New-ScheduledTaskSettingsSet `
    -ExecutionTimeLimit (New-TimeSpan -Hours 1) `
    -StartWhenAvailable `
    -RunOnlyIfNetworkAvailable:$false

# Registrar la tarea (requiere privilegios de administrador)
Register-ScheduledTask `
    -TaskName $taskName `
    -Action $action `
    -Trigger $trigger `
    -Settings $settings `
    -RunLevel Highest `
    -Force | Out-Null

Write-Host "[OK] Tarea programada registrada: '$taskName'"
Write-Host "[INFO] Se ejecutara todos los dias a las 14:00"
Write-Host "[INFO] Log en: $logPath"
Write-Host ""
Write-Host "Para ejecutar ahora manualmente:"
Write-Host "  Start-ScheduledTask -TaskName '$taskName'"
Write-Host ""
Write-Host "Para eliminar la tarea:"
Write-Host "  Unregister-ScheduledTask -TaskName '$taskName' -Confirm:`$false"

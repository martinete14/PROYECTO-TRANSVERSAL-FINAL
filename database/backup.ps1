# ============================================================
# backup.ps1 - Backup automatico de la DB miniacademy
# Guarda un .sql con fecha en database/backups/ y mantiene
# los ultimos 10 backups, borrando los mas antiguos.
# ============================================================

$mysqldump  = "C:\xampp\mysql\bin\mysqldump.exe"
$backupDir  = "$PSScriptRoot\backups"
$timestamp  = Get-Date -Format "yyyy-MM-dd_HH-mm"
$outputFile = "$backupDir\miniacademy_$timestamp.sql"
$maxBackups = 10

# Crear carpeta de backups si no existe
if (-not (Test-Path $backupDir)) {
    New-Item -ItemType Directory -Path $backupDir | Out-Null
}

# Ejecutar mysqldump
& $mysqldump -u root -P 3307 --protocol=TCP --single-transaction --routines --triggers miniacademy | Out-File -FilePath $outputFile -Encoding UTF8

if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] Backup creado: $outputFile"
} else {
    Write-Host "[ERROR] Fallo el backup. Revisa que XAMPP este corriendo." -ForegroundColor Red
    exit 1
}

# Borrar backups viejos si hay mas de $maxBackups
$backups = Get-ChildItem -Path $backupDir -Filter "miniacademy_*.sql" | Sort-Object LastWriteTime
if ($backups.Count -gt $maxBackups) {
    $toDelete = $backups | Select-Object -First ($backups.Count - $maxBackups)
    foreach ($file in $toDelete) {
        Remove-Item $file.FullName -Force
        Write-Host "[LIMPIEZA] Eliminado backup antiguo: $($file.Name)"
    }
}

Write-Host "[INFO] Backups disponibles: $((Get-ChildItem $backupDir -Filter '*.sql').Count)/$maxBackups"

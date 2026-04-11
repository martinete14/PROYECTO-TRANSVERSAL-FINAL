# Crear sesión para mantener cookies
$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession

# Hacer login
Write-Host "1. Intentando login..."
$loginData = @{
    email = "alumno.demo@miniacademia.local"
    password = "cliente123"
}

try {
    $response = Invoke-WebRequest -Uri 'http://127.0.0.1:8081/web/auth/login' -Method POST -UseBasicParsing -WebSession $session -Body $loginData -ErrorAction Stop
    Write-Host "2. Login exitoso"
} catch {
    Write-Host "Error en login: $_"
}

# Acceder a /web/cursos con la sesión
Write-Host "3. Obteniendo página de cursos..."
try {
    $cursosPage = Invoke-WebRequest -Uri 'http://127.0.0.1:8081/web/cursos' -UseBasicParsing -WebSession $session -ErrorAction Stop
    $cursosPage.Content | Out-File c:\Users\oscar\Desktop\cursos-authenticated.txt -Encoding UTF8
    Write-Host "4. Página de cursos guardada en c:\Users\oscar\Desktop\cursos-authenticated.txt"
    Write-Host "Tamaño: $($cursosPage.Content.Length) bytes"
} catch {
    Write-Host "Error obteniendo cursos: $_"
}

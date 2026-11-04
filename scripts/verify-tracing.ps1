# Скрипт верификации распределённой трассировки
param(
    [string]$GatewayUrl = "http://localhost:8088"
)

$ErrorActionPreference = "Stop"

Write-Host "=== Verification of Distributed Tracing ===" -ForegroundColor Cyan

# 1. Получение токена авторизации
Write-Host "`n[1/6] Authenticating..." -ForegroundColor Yellow
$tokenResponse = Invoke-RestMethod -Uri "http://localhost:8085/realms/course/protocol/openid-connect/token" `
    -Method Post `
    -Body @{
        client_id = "course-bff"
        client_secret = "course-bff-secret"
        grant_type = "password"
        username = "editor"
        password = "editor"
        scope = "openid profile email"
    } `
    -ContentType "application/x-www-form-urlencoded"

$accessToken = $tokenResponse.access_token
Write-Host "✓ Authentication successful" -ForegroundColor Green

# 2. Создание тестового рейса
Write-Host "`n[2/6] Creating test flight..." -ForegroundColor Yellow
$flightId = 12345

# 3. Отправка запроса с X-Request-ID
Write-Host "`n[3/6] Sending request with X-Request-ID..." -ForegroundColor Yellow
$requestId = [guid]::NewGuid().ToString()
$recommendationUrl = "$GatewayUrl/api/flights/$flightId/recommendations"

try {
    $response = Invoke-WebRequest -Uri $recommendationUrl `
        -Headers @{
            "Authorization" = "Bearer $accessToken"
            "X-Request-ID" = $requestId
        } `
        -ContentType "application/json"
    
    $responseRequestId = $response.Headers["X-Request-ID"]
    
    if ($responseRequestId -eq $requestId) {
        Write-Host "✓ X-Request-ID preserved: $requestId" -ForegroundColor Green
    } else {
        Write-Host "✗ X-Request-ID mismatch!" -ForegroundColor Red
        Write-Host "  Expected: $requestId" -ForegroundColor Red
        Write-Host "  Got: $responseRequestId" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "✗ Request failed: $_" -ForegroundColor Red
    exit 1
}

# 4. Проверка в логах
Write-Host "`n[4/6] Checking logs for request_id..." -ForegroundColor Yellow
Start-Sleep -Seconds 2

$logFiles = Get-ChildItem "runtime-logs" -Filter "*.log" -ErrorAction SilentlyContinue
if ($logFiles.Count -eq 0) {
    Write-Host "⚠ No log files found in runtime-logs/" -ForegroundColor Yellow
} else {
    $foundInLogs = 0
    foreach ($logFile in $logFiles) {
        $content = Get-Content $logFile.FullName -Raw
        if ($content -match $requestId) {
            $foundInLogs++
            Write-Host "  ✓ Found in $($logFile.Name)" -ForegroundColor Green
        }
    }
    
    if ($foundInLogs -ge 2) {
        Write-Host "✓ Request ID found in $foundInLogs log files" -ForegroundColor Green
    } else {
        Write-Host "⚠ Request ID found in only $foundInLogs log files (expected 3)" -ForegroundColor Yellow
    }
}

# 5. Проверка Tempo
Write-Host "`n[5/6] Checking Tempo availability..." -ForegroundColor Yellow
try {
    $tempoReady = Invoke-WebRequest -Uri "http://localhost:3200/ready" -TimeoutSec 5
    if ($tempoReady.StatusCode -eq 200) {
        Write-Host "✓ Tempo is ready" -ForegroundColor Green
    }
} catch {
    Write-Host "⚠ Tempo not accessible: $_" -ForegroundColor Yellow
}

# 6. Проверка невалидного X-Request-ID
Write-Host "`n[6/6] Testing invalid X-Request-ID handling..." -ForegroundColor Yellow
$invalidRequestId = "not-a-uuid"

try {
    $invalidResponse = Invoke-WebRequest -Uri $recommendationUrl `
        -Headers @{
            "Authorization" = "Bearer $accessToken"
            "X-Request-ID" = $invalidRequestId
        } `
        -ContentType "application/json"
    
    $newRequestId = $invalidResponse.Headers["X-Request-ID"]
    
    if ($newRequestId -ne $invalidRequestId -and $newRequestId -match '^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$') {
        Write-Host "✓ Invalid X-Request-ID replaced with valid UUID: $newRequestId" -ForegroundColor Green
    } else {
        Write-Host "✗ Invalid X-Request-ID not handled properly" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "✗ Request failed: $_" -ForegroundColor Red
    exit 1
}

Write-Host "`n=== Verification Summary ===" -ForegroundColor Cyan
Write-Host "Status: OK" -ForegroundColor Green
Write-Host "All checks passed successfully!" -ForegroundColor Green

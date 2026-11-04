# Скрипт получения service token для M2M взаимодействия
$response = Invoke-RestMethod -Uri "http://localhost:8085/realms/course/protocol/openid-connect/token" `
    -Method Post `
    -Body @{
        client_id = "library-service"
        client_secret = "library-service-secret"
        grant_type = "client_credentials"
        scope = "openid"
    } `
    -ContentType "application/x-www-form-urlencoded"

Write-Output $response.access_token

# Практическая работа № 3 — готовый вариант

В архив добавлен отдельный `recommendation-service` поверх исходного `RatingPunctuality`.

Основной стенд практической работы:

- Keycloak: `http://localhost:8085`
- Realm: `course`
- Resource Server: `http://localhost:8083`
- OAuth flow: `Client Credentials`
- Роли: `service`, `operator`
- Аудитория Resource Server: `recommendation-service`

## Запуск

1. Запустить Keycloak:

```powershell
docker compose up -d keycloak
```

2. Проверить Discovery:

```powershell
Invoke-RestMethod http://localhost:8085/realms/course/.well-known/openid-configuration |
  Select-Object issuer,authorization_endpoint,token_endpoint,jwks_uri
```

3. Запустить сервис рекомендаций:

```powershell
.\mvnw.cmd -f recommendation-service/pom.xml spring-boot:run
```

4. Выполнить все позитивные и негативные проверки:

```powershell
.\scripts\verify-oauth.ps1
```

### Учебные клиенты

`course-service-client / course-service-secret` → роль `service` → `aud=recommendation-service`.

`course-ops-client / course-ops-secret` → роль `operator` → `aud=recommendation-service`.

`course-other-client / course-other-secret` → роль `service` → `aud=other-api` (негативный сценарий).

Токены не выводятся скриптом и не сохраняются в репозитории.

## Что проверяется

`/internal/recommendations/{bookId}` доступен только роли `ROLE_SERVICE` и возвращает `bookId`, `Clean Code`, `Refactoring`.

`/internal/admin/info` доступен только роли `ROLE_OPERATOR` и возвращает `status=ok`.

Без токена ожидается `401`, с неверной ролью — `403`, с корректно подписанным токеном для другой аудитории — `401`.

> Исходный архив был односоставным Maven-проектом, поэтому новый модуль запускается через `-f recommendation-service/pom.xml`; его код и конфигурация соответствуют заданию Resource Server.

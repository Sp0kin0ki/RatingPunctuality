# Практическая работа № 3 — OAuth 2.0, OpenID Connect, JWT и сервисные учётные записи

В исходный проект добавлен отдельный `recommendation-service`, который работает как stateless OAuth 2.0 Resource Server и принимает только access token, выпущенные Keycloak для аудитории `recommendation-service`.

## Что реализовано

- Keycloak realm `course` с ролями `service` и `operator`.
- Клиенты `course-service-client`, `course-ops-client`, `course-other-client` для позитивных и негативных сценариев.
- Поток Client Credentials для сервисных учётных записей.
- JWT Resource Server с `issuer-uri` и проверкой `aud`.
- Конвертация `realm_access.roles` в `ROLE_SERVICE` / `ROLE_OPERATOR`.
- Защищённые эндпоинты:
  - `GET /internal/recommendations/{bookId}` → `ROLE_SERVICE`;
  - `GET /internal/admin/info` → `ROLE_OPERATOR`.
- Тесты для конвертера ролей и разграничения доступа.
- PowerShell-скрипт `scripts/verify-oauth.ps1` для полного сценария из задания.

## Запуск

Из корня проекта:

```powershell
docker compose up -d keycloak
```

Проверка OpenID Discovery:

```powershell
Invoke-RestMethod http://localhost:8085/realms/course/.well-known/openid-configuration |
  Select-Object issuer,authorization_endpoint,token_endpoint,jwks_uri
```

Запуск сервиса:

```powershell
.\mvnw.cmd -f recommendation-service/pom.xml spring-boot:run
```

API: `http://localhost:8083`.
Keycloak: `http://localhost:8085`.

Полная автоматическая проверка:

```powershell
.\scripts\verify-oauth.ps1
```

## Ожидаемые проверки

| Сценарий | Ожидаемый результат |
|---|---:|
| Запрос без Bearer token | 401 |
| `course-service-client` → recommendations | 200 |
| `course-ops-client` → recommendations | 403 |
| `course-ops-client` → admin/info | 200 |
| `course-service-client` → admin/info | 403 |
| `course-other-client` → recommendations | 401 |

В учебном стенде секреты клиентов хранятся в `course-realm.json` только для локальной демонстрации. Токены доступа не записываются в репозиторий, логи или URL.

> В предоставленном исходном архиве Maven Wrapper не содержал `.mvn/wrapper/maven-wrapper.properties`, поэтому для переносимого запуска в архив добавлен этот файл.

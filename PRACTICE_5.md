# Практическая работа № 5
## API Gateway, Backend for Frontend (BFF) и безопасная передача токенов

## Описание

Проект демонстрирует реализацию паттерна Backend for Frontend (BFF) с использованием Spring Cloud Gateway в качестве API Gateway. Токены доступа хранятся только на сервере BFF, а браузер получает лишь сессионную cookie, что минимизирует риск кражи токенов через XSS-атаки.

## Архитектура

```
Browser → Gateway Service (BFF) → Library Service (Resource Server)
          ↓                        ↓
          Redis (Sessions)         PostgreSQL (Data)
          ↓
          Keycloak (OIDC Provider)
```

### Компоненты:

1. **gateway-service** (порт 8088) - API Gateway / BFF
   - Spring Cloud Gateway (Reactive)
   - OAuth 2.0 Client (Authorization Code Flow)
   - Token Relay Filter
   - Spring Session с Redis
   - CSRF Protection

2. **library-service** (порт 8080) - Resource Server
   - OAuth 2.0 Resource Server (JWT)
   - Spring Security с роле-based авторизацией
   - REST API для управления книгами и авторами
   - PostgreSQL + Flyway миграции

3. **Keycloak** (порт 8085) - Identity Provider
   - Realm: `course`
   - BFF Client: `course-bff / course-bff-secret`
   - Пользователи: `student/student` (reader), `editor/editor` (editor)

4. **PostgreSQL** (порт 5432) - База данных
5. **Redis** (порт 6379) - Хранилище сессий

## Требования

- Java 21+
- Maven 3.8+
- Docker & Docker Compose

## Запуск

### 1. Запуск инфраструктуры

```powershell
# Запустить PostgreSQL, Redis и Keycloak
docker compose up -d postgres redis keycloak

# Проверить статус контейнеров
docker compose ps

# Дождаться готовности Keycloak (проверка OIDC discovery)
Invoke-RestMethod http://localhost:8085/realms/course/.well-known/openid-configuration | Select-Object issuer
```

### 2. Сборка проекта

```powershell
.\mvnw.cmd -DskipTests clean install
```

### 3. Запуск сервисов

В двух отдельных терминалах:

```powershell
# Терминал 1: Library Service
.\mvnw.cmd -pl library-service spring-boot:run

# Терминал 2: Gateway Service
.\mvnw.cmd -pl gateway-service spring-boot:run
```

**Важно:** Перед запуском `gateway-service` убедитесь, что Keycloak полностью запустился. Проверьте доступность `http://localhost:8085/realms/course/.well-known/openid-configuration` (должен вернуть 200 OK).

### 4. Открытие приложения

Откройте в браузере: http://localhost:8088

## Проверка функциональности

### Шаг 1: Аутентификация

1. На странице http://localhost:8088 нажмите кнопку **"Войти через Keycloak"**
2. Введите учетные данные:
   - **editor / editor** (роль editor - полный доступ)
   - **student / student** (роль reader - только чтение)

3. После успешной авторизации проверьте:
   - В DevTools → Application → Cookies есть cookie `SESSION`
   - В LocalStorage и SessionStorage токены **отсутствуют**

### Шаг 2: Проверка сессий в Redis

```powershell
# Просмотр сессий в Redis
docker compose exec redis redis-cli --scan --pattern 'course:gateway:sessions:*'

# Проверка ключей конкретной сессии (замените на реальный ID)
docker compose exec redis redis-cli HKEYS "course:gateway:sessions:sessions:<session-id>"
```

### Шаг 3: Token Relay

1. На веб-странице нажмите **"Загрузить список книг"**
2. Должен вернуться статус 200 OK с данными
3. Шлюз автоматически:
   - Извлек access token из серверной сессии
   - Добавил заголовок `Authorization: Bearer <token>`
   - Проксировал запрос к `library-service`

### Шаг 4: Проверка защиты backend

Откройте новое окно в режиме инкогнито и попробуйте:

```powershell
# Прямой запрос к library-service без токена
Invoke-RestMethod http://localhost:8080/api/books
# Ожидается: 401 Unauthorized
```

### Шаг 5: CSRF Protection

1. Нажмите **"Получить CSRF токен"**
2. Нажмите **"Создать автора"** (для пользователя `editor`)
3. Должен вернуться статус 201 Created

Если попытаться выполнить POST без CSRF токена:

```javascript
await fetch('/api/authors', {
  method: 'POST',
  credentials: 'same-origin',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({name: 'Test'})
});
// Ожидается: 403 Forbidden
```

### Шаг 6: Проверка авторизации по ролям

Войдите под пользователем `student / student` и попробуйте создать автора:

```javascript
const csrf = await fetch('/bff/csrf').then(r => r.json());
await fetch('/api/authors', {
  method: 'POST',
  credentials: 'same-origin',
  headers: {
    'Content-Type': 'application/json',
    [csrf.headerName]: csrf.token
  },
  body: JSON.stringify({name: 'Test'})
});
// Ожидается: 403 Forbidden (недостаточно прав)
```

### Шаг 7: Диагностика сбоев

1. Остановите `library-service`
2. Попробуйте загрузить список книг через Gateway
3. Ожидается: 500 Internal Server Error (или 502/503 в production)
4. Проверьте `/bff/me` - должен продолжать работать

## Критерии успешного выполнения

✅ Наличие активной сессии в Redis  
✅ Отсутствие токенов в LocalStorage и SessionStorage браузера  
✅ Корректная работа TokenRelay при проксировании запросов  
✅ Ответ 401 Unauthorized при прямом обращении к backend без токена  
✅ Ответ 403 Forbidden при модификации данных без валидного CSRF-токена  
✅ Ответ 403 Forbidden при недостаточных правах доступа (роль reader)  

## Конфигурация

### Gateway Service (application.yml)

- **Порт**: 8088
- **OAuth Client ID**: course-bff
- **Redirect URI**: http://localhost:8088/login/oauth2/code/keycloak
- **Session Store**: Redis (namespace: `course:gateway:sessions`)
- **Routes**: `/api/authors/**`, `/api/books/**`
- **Filters**: TokenRelay
- **Timeouts**: connect=500ms, response=3s

### Library Service (application.yml)

- **Порт**: 8080
- **Issuer URI**: http://localhost:8085/realms/course
- **Audience**: library-service
- **Роли**: ROLE_READER, ROLE_EDITOR
- **Database**: PostgreSQL (librarydb)

## Troubleshooting

### Проблема: Бесконечный redirect на login

**Причина**: Keycloak не запустился или неверный issuer-uri  
**Решение**: 
```powershell
# Проверить логи Keycloak
docker compose logs keycloak

# Проверить доступность OIDC discovery
Invoke-RestMethod http://localhost:8085/realms/course/.well-known/openid-configuration
```

### Проблема: 401 от library-service

**Причина**: Неверная аудитория или роли  
**Решение**:
```powershell
# Проверить логи library-service
.\mvnw.cmd -pl library-service spring-boot:run

# Включен DEBUG уровень для Spring Security
# Ищите строки с "JWT" и "audience"
```

### Проблема: 403 на POST-запросах

**Причина 1**: Отсутствует CSRF токен  
**Решение**: Сначала получить токен через `/bff/csrf`

**Причина 2**: Недостаточно прав (роль reader вместо editor)  
**Решение**: Войти под пользователем `editor/editor`

### Проблема: Сессия не сохраняется после перезапуска Gateway

**Причина**: Redis не запущен или неверная конфигурация  
**Решение**:
```powershell
# Проверить Redis
docker compose exec redis redis-cli ping
# Должен ответить: PONG

# Проверить namespace сессий
docker compose exec redis redis-cli --scan --pattern 'course:gateway:sessions:*'
```

## Остановка

```powershell
# Остановить Java-процессы (Ctrl+C в терминалах)

# Остановить инфраструктуру
docker compose down

# Удалить volumes (данные PostgreSQL)
docker compose down -v
```

## Структура проекта

```
rating_punctuality/
├── library-service/
│   ├── src/main/java/edu/rutmiit/enterprise/library/
│   │   ├── config/
│   │   │   ├── SecurityConfig.java
│   │   │   └── JwtRoleConverter.java
│   │   ├── controller/
│   │   │   ├── AuthorController.java
│   │   │   └── BookController.java
│   │   ├── entity/
│   │   │   ├── Author.java
│   │   │   └── Book.java
│   │   ├── repository/
│   │   │   ├── AuthorRepository.java
│   │   │   └── BookRepository.java
│   │   └── LibraryServiceApplication.java
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/
│   │       └── V1__create_authors_and_books.sql
│   └── pom.xml
├── gateway-service/
│   ├── src/main/java/edu/rutmiit/enterprise/gateway/
│   │   ├── BffController.java
│   │   ├── SecurityConfig.java
│   │   └── GatewayServiceApplication.java
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── static/
│   │       └── index.html
│   └── pom.xml
├── infra/keycloak/
│   └── course-realm.json
├── docker-compose.yml
├── pom.xml
└── PRACTICE_5.md
```

## Ссылки

- Keycloak Admin Console: http://localhost:8085/admin (admin / admin-dev-only)
- Gateway Service: http://localhost:8088
- Library Service: http://localhost:8080 (прямой доступ запрещён)
- OIDC Discovery: http://localhost:8085/realms/course/.well-known/openid-configuration

## Дополнительные материалы

### Диагностические команды

```powershell
# Проверка здоровья сервисов
Invoke-RestMethod http://localhost:8080/actuator/health
Invoke-RestMethod http://localhost:8088/actuator/health

# Просмотр текущего пользователя
Invoke-RestMethod http://localhost:8088/bff/me

# Получение CSRF токена
Invoke-RestMethod http://localhost:8088/bff/csrf

# Просмотр логов контейнеров
docker compose logs -f postgres
docker compose logs -f redis
docker compose logs -f keycloak
```

### Архитектурные паттерны

- **Backend for Frontend (BFF)**: Специализированный backend для каждого типа клиента
- **Token Relay**: Прокси передаёт токен downstream-сервисам
- **Gateway Routing**: Централизованная маршрутизация запросов
- **Gateway Offloading**: Вынос сквозной функциональности на границу
- **Session-based OAuth Client**: Хранение токенов в серверной сессии

### Механизмы безопасности

1. **Хранение токенов только на сервере**: Браузер не имеет доступа к access/refresh токенам
2. **CSRF Protection**: Все модифицирующие операции требуют CSRF токен
3. **Audience Validation**: library-service проверяет поле `aud` в JWT
4. **Role-based Authorization**: `@PreAuthorize("hasRole('EDITOR')")`
5. **Path Whitelisting**: Только явно разрешённые пути проксируются
6. **HttpOnly Cookies**: Cookie `SESSION` недоступна из JavaScript
7. **SameSite Cookie**: Защита от CSRF на уровне браузера (в production + Secure flag)

---

**Автор**: Практическая работа №5  
**Дата**: Ноябрь 2026

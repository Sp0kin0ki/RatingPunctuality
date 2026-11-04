# Практическая работа № 5 - Быстрый старт
## API Gateway, BFF и безопасная передача токенов для RatingPunctuality

## 🚀 Что нужно сделать для демонстрации

### 1. Запуск инфраструктуры (3 минуты)

```powershell
# Запустить PostgreSQL, Redis и Keycloak
docker compose up -d postgres redis keycloak

# Подождать запуска Keycloak (30-60 секунд)
Start-Sleep -Seconds 60

# Проверить готовность
Invoke-RestMethod http://localhost:8085/realms/course/.well-known/openid-configuration | Select-Object issuer
```

### 2. Сборка и запуск сервисов (2 минуты)

```powershell
# Собрать проект
.\mvnw.cmd -DskipTests clean install

# В ПЕРВОМ терминале запустить Flight Service (Resource Server)
.\mvnw.cmd -pl library-service spring-boot:run

# Подождать запуска (10-15 секунд)

# Во ВТОРОМ терминале запустить Gateway Service (BFF)
.\mvnw.cmd -pl gateway-service spring-boot:run
```

### 3. Демонстрация в браузере (5 минут)

#### Откройте: http://localhost:8088

#### Сценарий 1: Вход и проверка сессии
1. Нажмите **"Войти через Keycloak"**
2. Введите: **editor / editor** 
3. Нажмите **"Проверить сессию"** → видим authenticated: true
4. Откройте DevTools → Application → Cookies → видим только **SESSION** cookie
5. Проверьте LocalStorage и SessionStorage → **токенов нет** ✅

#### Сценарий 2: Token Relay работает
1. Нажмите **"Загрузить список рейсов"** → 200 OK, видим рейсы ✅
2. Откройте инкогнито-окно
3. Перейдите напрямую к http://localhost:8080/api/flights
4. Получите **401 Unauthorized** → backend защищён ✅

#### Сценарий 3: CSRF защита работает
1. В основном окне нажмите **"Получить CSRF токен"**
2. Нажмите **"Создать аэропорт"** → 201 Created ✅
3. Откройте консоль DevTools и выполните:
```javascript
await fetch('/api/airports', {
    method: 'POST',
    credentials: 'same-origin',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({code: 'TST', name: 'Test', city: 'Test'})
});
```
4. Получите **403 Forbidden** → CSRF защита работает ✅

#### Сценарий 4: Role-based авторизация
1. Нажмите Logout
2. Войдите как **student / student** (роль reader)
3. Нажмите "Загрузить список рейсов" → 200 OK (чтение разрешено)
4. Получите CSRF токен
5. Попробуйте создать аэропорт → **403 Forbidden** (недостаточно прав) ✅

#### Сценарий 5: Сессии в Redis
```powershell
# Проверить сессии
docker compose exec redis redis-cli --scan --pattern 'course:gateway:sessions:*'

# Перезапустить Gateway
# Ctrl+C в терминале Gateway, затем снова:
.\mvnw.cmd -pl gateway-service spring-boot:run

# Обновить страницу в браузере
# Сессия сохранилась! ✅
```

## 📊 Архитектура (для объяснения)

```
Браузер (только SESSION cookie)
   ↓ OAuth Authorization Code Flow
Gateway Service (BFF) :8088
   ├── Хранит access_token в Redis
   ├── CSRF Protection
   └── Token Relay → Flight Service :8080
                         ↓
                    JWT Validation + Audience Check
                    Role: READER или EDITOR
```

## 🎯 Что демонстрирует проект

### Безопасность
- ✅ Токены НЕ хранятся в браузере (защита от XSS)
- ✅ CSRF защита для POST/PUT/DELETE
- ✅ Audience validation (токен только для flight-service)
- ✅ Role-based авторизация (reader vs editor)
- ✅ Прямой доступ к backend запрещён

### Инфраструктура
- ✅ Multi-module Maven проект
- ✅ Spring Cloud Gateway (reactive)
- ✅ OAuth 2.0 + OIDC через Keycloak
- ✅ Redis для распределённых сессий
- ✅ PostgreSQL с Flyway миграциями

### Предметная область
- **Flight Service** - управление авиарейсами
  - Аэропорты (SVO, DME, LED)
  - Рейсы (номер, время вылета/прилёта, задержки)
  - REST API с JWT защитой

## 🔑 Учётные записи

| Логин | Пароль | Роль | Права |
|-------|--------|------|-------|
| editor | editor | editor + reader | Полный доступ (GET + POST/PUT/DELETE) |
| student | student | reader | Только чтение (GET) |

## 📁 Структура проекта

```
rating_punctuality/
├── gateway-service/        # BFF (порт 8088)
│   ├── SecurityConfig      # OAuth Client + Token Relay
│   ├── BffController       # /bff/me, /bff/csrf
│   └── index.html          # Веб-интерфейс для демо
│
├── library-service/        # Flight Service (порт 8080)
│   ├── config/
│   │   ├── SecurityConfig      # JWT Resource Server
│   │   └── JwtRoleConverter    # Keycloak roles → Spring
│   ├── entity/
│   │   ├── Airport             # Аэропорты
│   │   └── Flight              # Рейсы
│   └── controller/
│       ├── AirportController   # REST API аэропортов
│       └── FlightController    # REST API рейсов
│
├── infra/keycloak/
│   └── course-realm.json   # Keycloak: клиенты + пользователи
│
└── docker-compose.yml      # PostgreSQL, Redis, Keycloak
```

## 🐛 Troubleshooting

### Gateway не запускается
```powershell
# Проверить, что Keycloak запустился
docker compose logs keycloak | Select-Object -Last 20
```

### Бесконечный redirect
- Подождите 60 секунд после `docker compose up`
- Проверьте: `Invoke-RestMethod http://localhost:8085/realms/course/.well-known/openid-configuration`

### 401 от Flight Service
```powershell
# Проверить, что audience = flight-service
# Посмотреть логи
.\mvnw.cmd -pl library-service spring-boot:run | Select-String "audience"
```

### Сессия не сохраняется
```powershell
# Проверить Redis
docker compose exec redis redis-cli ping
# Должно вернуть: PONG
```

## 📝 Краткое резюме для защиты

**Задача:** Реализовать безопасную передачу токенов через BFF для системы управления авиарейсами.

**Решение:**
1. **Gateway Service (BFF)** - OAuth 2.0 Client, хранит токены в Redis, выдаёт браузеру только SESSION cookie
2. **Flight Service** - Resource Server, проверяет JWT, audience и роли
3. **Token Relay** - Gateway автоматически добавляет Bearer токен при проксировании

**Результат:** Токены защищены от XSS, CSRF защита работает, роли разграничены, прямой доступ к backend запрещён.

**Технологии:** Spring Boot 3.5, Spring Cloud Gateway, Spring Security OAuth2, Keycloak, Redis, PostgreSQL, Flyway.

---

**Время демонстрации:** ~5-7 минут  
**Сложность:** Средняя  
**Статус:** ✅ Готово к демонстрации

# Практическая работа № 5 - Итоговая сводка

## ✅ Выполненные задачи

Проект полностью подготовлен для выполнения практической работы № 5 по теме "API Gateway, Backend for Frontend (BFF) и безопасная передача токенов".

### 1. Создана архитектура проекта

```
rating_punctuality/
├── gateway-service/          # API Gateway / BFF (порт 8088)
│   ├── src/main/java/
│   │   └── edu/rutmiit/enterprise/gateway/
│   │       ├── GatewayServiceApplication.java
│   │       ├── SecurityConfig.java
│   │       └── BffController.java
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── static/
│   │       └── index.html      # Веб-интерфейс для демонстрации
│   └── pom.xml
│
├── library-service/          # Resource Server (порт 8080)
│   ├── src/main/java/
│   │   └── edu/rutmiit/enterprise/library/
│   │       ├── LibraryServiceApplication.java
│   │       ├── config/
│   │       │   ├── SecurityConfig.java
│   │       │   └── JwtRoleConverter.java
│   │       ├── controller/
│   │       │   ├── AuthorController.java
│   │       │   └── BookController.java
│   │       ├── entity/
│   │       │   ├── Author.java
│   │       │   └── Book.java
│   │       └── repository/
│   │           ├── AuthorRepository.java
│   │           └── BookRepository.java
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/
│   │       └── V1__create_authors_and_books.sql
│   └── pom.xml
│
├── infra/
│   └── keycloak/
│       └── course-realm.json  # Конфигурация Keycloak
│
├── docker-compose.yml         # PostgreSQL, Redis, Keycloak
├── pom.xml                   # Parent POM (multi-module)
├── PRACTICE_5.md             # Подробная инструкция
└── PRACTICE_5_SUMMARY.md     # Эта сводка
```

### 2. Реализованные компоненты

#### Gateway Service (BFF)
- ✅ Spring Cloud Gateway (Reactive)
- ✅ OAuth 2.0 Client (Authorization Code Flow)
- ✅ Token Relay Filter для проксирования токенов
- ✅ Spring Session с Redis для хранения сессий
- ✅ CSRF Protection
- ✅ BFF Controller для получения данных пользователя и CSRF токена
- ✅ Веб-интерфейс с современным дизайном для демонстрации

#### Library Service (Resource Server)
- ✅ OAuth 2.0 Resource Server с JWT валидацией
- ✅ Проверка audience (library-service)
- ✅ Конвертер ролей Keycloak в Spring Security authorities
- ✅ Role-based авторизация (@PreAuthorize)
- ✅ REST API для управления авторами и книгами
- ✅ PostgreSQL с Flyway миграциями
- ✅ Тестовые данные (3 автора, 3 книги)

#### Инфраструктура
- ✅ PostgreSQL 16 (база данных librarydb)
- ✅ Redis 7 (хранилище сессий)
- ✅ Keycloak 26 (OIDC Provider)
- ✅ Health checks для всех контейнеров

#### Keycloak конфигурация
- ✅ Realm: course
- ✅ OAuth Client: course-bff (Authorization Code)
- ✅ Пользователи:
  - student/student (роль reader - чтение)
  - editor/editor (роли reader + editor - полный доступ)
- ✅ Роли: reader, editor
- ✅ Audience mapper для library-service

### 3. Реализованные механизмы безопасности

1. **Безопасное хранение токенов**
   - Access и Refresh токены хранятся только на сервере BFF
   - Браузер получает только HttpOnly cookie SESSION
   - Токены не доступны из JavaScript (защита от XSS)

2. **CSRF Protection**
   - Все модифицирующие операции требуют CSRF токен
   - Токен выдается через эндпоинт /bff/csrf
   - SameSite cookie для дополнительной защиты

3. **Token Relay**
   - Gateway автоматически добавляет Bearer токен в запросы
   - Backend проверяет JWT подпись и аудиторию
   - Роли пользователя проверяются на уровне методов

4. **Audience Validation**
   - Library Service принимает только токены с aud=library-service
   - Токены для других API отклоняются (401)

5. **Role-based Authorization**
   - GET-запросы доступны ролям reader и editor
   - POST/PUT/DELETE доступны только роли editor
   - Недостаточные права возвращают 403

### 4. Инструкции по запуску

#### Шаг 1: Запуск инфраструктуры
```powershell
docker compose up -d postgres redis keycloak
docker compose ps
```

#### Шаг 2: Проверка готовности Keycloak
```powershell
Invoke-RestMethod http://localhost:8085/realms/course/.well-known/openid-configuration | Select-Object issuer
```

#### Шаг 3: Сборка проекта
```powershell
.\mvnw.cmd -DskipTests clean install
```

#### Шаг 4: Запуск сервисов (в двух терминалах)
```powershell
# Терминал 1
.\mvnw.cmd -pl library-service spring-boot:run

# Терминал 2 (после запуска library-service)
.\mvnw.cmd -pl gateway-service spring-boot:run
```

#### Шаг 5: Открытие приложения
Откройте браузер: http://localhost:8088

### 5. Сценарии проверки

#### ✅ Сценарий 1: Аутентификация через Keycloak
1. Открыть http://localhost:8088
2. Нажать "Войти через Keycloak"
3. Ввести editor/editor
4. Проверить наличие cookie SESSION в DevTools
5. Убедиться, что токены НЕ хранятся в LocalStorage/SessionStorage

#### ✅ Сценарий 2: Проверка Token Relay
1. После входа нажать "Загрузить список книг"
2. Получить 200 OK с данными
3. Открыть инкогнито-окно
4. Выполнить прямой запрос к http://localhost:8080/api/books
5. Получить 401 Unauthorized

#### ✅ Сценарий 3: CSRF Protection
1. Нажать "Получить CSRF токен"
2. Нажать "Создать автора" (для editor)
3. Получить 201 Created
4. В консоли выполнить POST без CSRF токена
5. Получить 403 Forbidden

#### ✅ Сценарий 4: Role-based Authorization
1. Войти как student/student
2. Нажать "Загрузить список книг" - успех (200)
3. Получить CSRF токен
4. Попытаться создать автора - отказ (403)
5. Выйти и войти как editor/editor
6. Создать автора - успех (201)

#### ✅ Сценарий 5: Проверка сессий в Redis
```powershell
docker compose exec redis redis-cli --scan --pattern 'course:gateway:sessions:*'
```

#### ✅ Сценарий 6: Диагностика сбоев
1. Остановить library-service (Ctrl+C)
2. Попытаться загрузить книги - ошибка (500/502)
3. Проверить /bff/me - продолжает работать
4. Запустить library-service снова - восстановление

### 6. Критерии успешного выполнения

- ✅ Наличие активной сессии в Redis
- ✅ Отсутствие токенов в браузере (LocalStorage, SessionStorage)
- ✅ Корректная работа TokenRelay
- ✅ Ответ 401 при прямом доступе к backend без токена
- ✅ Ответ 403 при POST без CSRF токена
- ✅ Ответ 403 при недостаточных правах (student пытается создать)

### 7. Ключевые эндпоинты

#### Gateway Service (8088)
- `GET /` - Главная страница с интерфейсом
- `GET /bff/me` - Информация о текущем пользователе
- `GET /bff/csrf` - Получение CSRF токена
- `GET /api/books` - Проксирование к library-service
- `GET /api/authors` - Проксирование к library-service
- `POST /api/authors` - Создание автора (требует CSRF + роль editor)

#### Library Service (8080)
- `GET /api/books` - Список книг (требует JWT)
- `GET /api/authors` - Список авторов (требует JWT)
- `POST /api/authors` - Создание автора (требует JWT + роль EDITOR)

#### Keycloak (8085)
- `/realms/course/.well-known/openid-configuration` - OIDC Discovery
- `/admin` - Admin Console (admin/admin-dev-only)

### 8. Технологический стек

**Backend:**
- Spring Boot 3.5.6
- Spring Cloud Gateway 2024.0.1
- Spring Security OAuth2 Client/Resource Server
- Spring Session Data Redis
- Spring Data JPA
- Flyway
- PostgreSQL Driver
- Lombok

**Infrastructure:**
- PostgreSQL 16.7
- Redis 7.4.2
- Keycloak 26.0.8

**Frontend:**
- Vanilla JavaScript
- Современный темный дизайн
- Fetch API для взаимодействия с BFF

### 9. Архитектурные паттерны

1. **Backend for Frontend (BFF)** - специализированный backend для веб-клиентов
2. **Token Relay** - проксирование токенов на downstream-сервисы
3. **Gateway Routing** - централизованная маршрутизация запросов
4. **Gateway Offloading** - вынос сквозной функциональности (auth, CSRF) на границу
5. **Session-based OAuth Client** - хранение токенов в серверной сессии
6. **Resource Server** - защита API через JWT валидацию
7. **Audience Restriction** - ограничение использования токенов по целевому сервису

### 10. Отличия от production

**В учебном проекте:**
- `sslRequired: NONE` в Keycloak
- Пароли в открытом виде в конфигурации
- HTTP вместо HTTPS
- SESSION cookie без флага Secure
- Отсутствует rate limiting
- Нет мониторинга и алертинга
- Отсутствует резервное копирование
- Один инстанс каждого сервиса

**В production должно быть:**
- HTTPS для всех соединений
- Секреты в vault (HashiCorp Vault, AWS Secrets Manager)
- SESSION cookie с флагами Secure + SameSite=Strict
- Rate limiting на API Gateway
- Множество реплик Gateway и Library Service
- Load Balancer перед Gateway
- Распределенная трассировка (Jaeger, Zipkin)
- Мониторинг (Prometheus, Grafana)
- Централизованное логирование (ELK, Loki)
- Регулярные бэкапы PostgreSQL и Redis

### 11. Troubleshooting

| Проблема | Решение |
|----------|---------|
| Gateway не запускается | Проверить доступность Keycloak OIDC discovery |
| 401 от library-service | Проверить audience в токене и конфигурацию |
| 403 на POST | Получить CSRF токен или проверить роль пользователя |
| Сессия не сохраняется | Проверить Redis и настройки spring.session |
| Бесконечный redirect | Проверить redirect URIs в Keycloak |

### 12. Дополнительные материалы

Подробная документация находится в файле **PRACTICE_5.md**, включая:
- Полное описание архитектуры
- Пошаговые инструкции по запуску
- Детальные сценарии проверки
- Диагностические команды
- Объяснение механизмов безопасности
- Таблица troubleshooting

## Заключение

Проект полностью готов к выполнению практической работы № 5. Все компоненты реализованы, документация написана, конфигурация подготовлена.

**Следующие шаги:**
1. Запустить инфраструктуру (`docker compose up -d`)
2. Собрать проект (`.\mvnw.cmd -DskipTests install`)
3. Запустить сервисы
4. Открыть http://localhost:8088 и следовать инструкциям

**Время выполнения:** ~15-20 минут (первый запуск с загрузкой образов)

---

**Дата создания:** 3 ноября 2026  
**Версия Spring Boot:** 3.5.6  
**Версия Spring Cloud:** 2024.0.1  
**Java версия:** 21

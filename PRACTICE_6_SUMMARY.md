# Практическая работа № 6 - Итоговая сводка

## ✅ Выполненные задачи

Проект полностью подготовлен для выполнения практической работы № 6 по теме "Корреляция запросов и распределённая трассировка" в предметной области учёта пунктуальности рейсов.

### 1. Архитектура проекта

```
rating_punctuality/
├── gateway-service/              # API Gateway / BFF (8088)
│   ├── filter/
│   │   └── RequestIdFilter.java (реактивный)
│   └── resources/
│       ├── application.yml (tracing + reactor context propagation)
│       └── logback-spring.xml
│
├── library-service/              # Flight Service (8080)
│   ├── config/
│   │   ├── RestClientConfig.java
│   │   └── OAuth2ClientHttpRequestInterceptor.java
│   ├── controller/
│   │   └── FlightController.java (с вызовом recommendation)
│   ├── filter/
│   │   └── RequestIdFilter.java
│   └── resources/
│       ├── application.yml (OAuth2 client + tracing)
│       └── logback-spring.xml
│
├── recommendation-service/       # Recommendation Service (8083)
│   ├── config/
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   └── RecommendationController.java
│   ├── filter/
│   │   └── RequestIdFilter.java
│   └── resources/
│       ├── application.yml (resource server + tracing)
│       └── logback-spring.xml
│
├── infra/
│   ├── otel-collector/
│   │   └── otel-collector-config.yaml
│   ├── tempo/
│   │   └── tempo.yaml
│   ├── grafana/
│   │   └── datasources.yaml
│   └── keycloak/
│       └── course-realm.json (+ recommendation-service клиент)
│
├── scripts/
│   ├── verify-tracing.ps1
│   └── get-service-token.ps1
│
├── runtime-logs/                 # Логи всех сервисов
├── docker-compose.yml            # + OTLP Collector, Tempo, Grafana
├── pom.xml                       # Multi-module с recommendation-service
├── PRACTICE_6.md                 # Полная документация
└── PRACTICE_6_SUMMARY.md         # Эта сводка
```

### 2. Реализованные компоненты

#### A. Корреляция через X-Request-ID

**Gateway Service (Reactive):**
- ✅ `RequestIdFilter` (GlobalFilter) для реактивной валидации
- ✅ Генерация UUID при отсутствии или невалидном формате
- ✅ Нормализация к lowercase
- ✅ Проброс через mutated request
- ✅ Установка в response headers
- ✅ Реактивная пропагация через contextWrite

**Library Service & Recommendation Service (Servlet):**
- ✅ `RequestIdFilter` (servlet Filter) с приоритетом HIGHEST_PRECEDENCE
- ✅ Валидация по regex паттерну UUID
- ✅ Запись в MDC (`request_id`)
- ✅ Установка в response headers
- ✅ Очистка MDC в finally блоке

#### B. Распределённая трассировка (OpenTelemetry)

**Все три сервиса:**
- ✅ `micrometer-tracing-bridge-otel`
- ✅ `opentelemetry-exporter-otlp`
- ✅ W3C Trace Context propagation
- ✅ Sampling probability: 1.0 (100%)
- ✅ OTLP endpoint: http://localhost:4318/v1/traces

**Логирование:**
- ✅ `logstash-logback-encoder` для структурированных логов
- ✅ MDC поля: `request_id`, `traceId`, `spanId`
- ✅ Вывод в console и файл (`runtime-logs/`)
- ✅ Rolling policy с 7-дневным хранением

#### C. Межсервисное взаимодействие

**Flight Service → Recommendation Service:**
- ✅ `RestClient.Builder` с базовым URL
- ✅ Интерцептор для проброса `X-Request-ID` из MDC
- ✅ `OAuth2ClientHttpRequestInterceptor` для M2M токенов
- ✅ `ObservationRegistry` для автоматической трассировки
- ✅ Client Credentials flow
- ✅ Audience validation: `recommendation-service`

**Эндпоинт:**
```
GET /api/flights/{id}/recommendations
```
- Возвращает рейс + рекомендации от Recommendation Service
- Формирует единую распределённую трассу из 5 HTTP-спанов

#### D. Инфраструктура

**OpenTelemetry Collector (порты 4317/4318):**
- ✅ OTLP HTTP receiver на :4318
- ✅ OTLP gRPC receiver на :4317
- ✅ Batch processor (1s timeout, 1024 batch size)
- ✅ Экспорт в Tempo
- ✅ Logging exporter для отладки

**Tempo (порт 3200):**
- ✅ OTLP ingestion
- ✅ Local storage backend
- ✅ Query frontend для поиска трасс
- ✅ Health endpoint: `/ready`
- ✅ 1h block retention

**Grafana (порт 3000):**
- ✅ Tempo datasource (преднастроен)
- ✅ Anonymous auth для dev (admin role)
- ✅ Admin password: `admin-dev-only`

**Keycloak:**
- ✅ Realm: `course`
- ✅ OAuth Client: `recommendation-service` (Resource Server)
- ✅ OAuth Client: `library-service` (M2M Client)
- ✅ Service Account enabled для M2M
- ✅ Audience mapper: `recommendation-service`

### 3. Сценарии проверки

#### ✅ Сценарий 1: Валидный X-Request-ID
```javascript
const requestId = crypto.randomUUID();
const response = await fetch('/api/flights/1/recommendations', {
  headers: { 'X-Request-ID': requestId },
  credentials: 'same-origin'
});
// Response headers содержит тот же requestId
```

#### ✅ Сценарий 2: Невалидный X-Request-ID
```javascript
const response = await fetch('/api/flights/1/recommendations', {
  headers: { 'X-Request-ID': 'not-a-uuid' },
  credentials: 'same-origin'
});
// Gateway генерирует новый валидный UUID
```

#### ✅ Сценарий 3: Корреляция в логах
```powershell
$requestId = '...'
rg --fixed-strings $requestId runtime-logs
# Находит requestId в логах gateway, library, recommendation
```

#### ✅ Сценарий 4: Визуализация трассы
1. Извлечь `trace_id` из логов (32 hex символа)
2. Открыть Grafana → Explore → Tempo
3. Вставить Trace ID и найти трассу
4. Увидеть дерево из 5 HTTP-спанов:
   - gateway SERVER
   - gateway→library CLIENT
   - library SERVER
   - library→recommendation CLIENT
   - recommendation SERVER

#### ✅ Сценарий 5: Автоматическая верификация
```powershell
.\scripts\verify-tracing.ps1
# ✓ Authentication successful
# ✓ X-Request-ID preserved
# ✓ Request ID found in N log files
# ✓ Tempo is ready
# ✓ Invalid X-Request-ID replaced
# Status: OK
```

### 4. Ключевые отличия от учебного примера

**Адаптация к предметной области:**
- Вместо `books` и `authors` → `flights` и `airports`
- Вместо `library-service` → семантически остался, но обрабатывает рейсы
- Рекомендации выдаются по рейсам, а не по книгам

**Технические особенности:**
- Моковые данные в Flight Controller (нет реальной БД для демонстрации)
- Flyway отключён для упрощения запуска
- Сохранены все механизмы безопасности из работы №5
- RestClient вместо WebClient для синхронного взаимодействия

### 5. Конфигурация портов

| Сервис | Порт | Описание |
|--------|------|----------|
| gateway-service | 8088 | API Gateway / BFF |
| library-service | 8080 | Flight Service |
| recommendation-service | 8083 | Recommendation Service |
| keycloak | 8085 | Identity Provider |
| postgres | 5432 | База данных |
| redis | 6379 | Сессии |
| otel-collector | 4318 | OTLP HTTP |
| otel-collector | 4317 | OTLP gRPC |
| tempo | 3200 | Tempo API |
| grafana | 3000 | UI для визуализации |

### 6. Учётные записи

**Keycloak Users:**
- `editor / editor` - роли: reader, editor (полный доступ)
- `student / student` - роль: reader (только чтение)

**Keycloak Clients:**
- `course-bff` / `course-bff-secret` - Authorization Code для BFF
- `library-service` / `library-service-secret` - Client Credentials для M2M
- `recommendation-service` - Resource Server (audience validation)

**Grafana:**
- `admin / admin-dev-only` (или anonymous auth)

### 7. Проверка работоспособности

```powershell
# 1. Запуск инфраструктуры
docker compose up -d
docker compose ps

# 2. Проверка готовности
Invoke-RestMethod http://localhost:8085/realms/course/.well-known/openid-configuration
Invoke-WebRequest http://localhost:3200/ready
Invoke-RestMethod http://localhost:3000/api/health

# 3. Сборка проекта
.\mvnw.cmd -DskipTests clean install

# 4. Запуск сервисов (в 3 терминалах)
.\mvnw.cmd -pl recommendation-service spring-boot:run
.\mvnw.cmd -pl library-service spring-boot:run
.\mvnw.cmd -pl gateway-service spring-boot:run

# 5. Автоматическая проверка
.\scripts\verify-tracing.ps1
```

### 8. Критерии успешного выполнения

- ✅ Валидация и нормализация X-Request-ID
- ✅ Генерация нового UUID при невалидном формате
- ✅ Единственный заголовок X-Request-ID в ответе
- ✅ Общий request_id в логах всех трёх сервисов
- ✅ Общий trace_id в логах всех сервисов
- ✅ Уникальные span_id для каждого сервиса
- ✅ Дерево из 5 HTTP-спанов в Grafana Tempo
- ✅ Корректные parent-child связи между спанами
- ✅ W3C traceparent в межсервисных вызовах
- ✅ Корреляция работает при ранних отказах (403, 401)

### 9. Файлы для демонстрации

**Конфигурация:**
- `docker-compose.yml` - добавлены OTLP Collector, Tempo, Grafana
- `infra/otel-collector/otel-collector-config.yaml`
- `infra/tempo/tempo.yaml`
- `infra/grafana/datasources.yaml`
- `infra/keycloak/course-realm.json` - добавлены клиенты для M2M

**Код:**
- `gateway-service/.../filter/RequestIdFilter.java` (реактивный)
- `library-service/.../filter/RequestIdFilter.java` (servlet)
- `library-service/.../config/RestClientConfig.java`
- `library-service/.../config/OAuth2ClientHttpRequestInterceptor.java`
- `library-service/.../controller/FlightController.java`
- `recommendation-service/.../controller/RecommendationController.java`

**Логирование:**
- `*/logback-spring.xml` - структурированное логирование с MDC
- `runtime-logs/*.log` - корреляция через request_id, trace_id, span_id

**Скрипты:**
- `scripts/verify-tracing.ps1` - автоматическая проверка всех критериев
- `scripts/get-service-token.ps1` - получение M2M токена для ручных тестов

**Документация:**
- `PRACTICE_6.md` - полная инструкция (на русском)
- `PRACTICE_6_SUMMARY.md` - эта сводка

### 10. Особенности реализации

**Реактивный Gateway:**
- `contextWrite()` для пропагации request_id в Reactor context
- `GlobalFilter` с порядком `HIGHEST_PRECEDENCE`
- Мутация request/response для добавления заголовков

**Синхронный Flight Service:**
- Servlet Filter с классическим MDC
- `RestClient` вместо WebClient
- OAuth2AuthorizedClientManager для автоматического получения M2M токенов
- Интерцепторы для проброса X-Request-ID и Authorization

**Автоматическая трассировка:**
- Spring Boot Auto-configuration для Micrometer Tracing
- ObservationRegistry регистрирует HTTP-вызовы автоматически
- W3C Trace Context propagation через HTTP headers
- Не требуется ручное создание спанов

### 11. Troubleshooting

**Проблема: Разные Trace ID в сервисах**
- Проверить наличие `ObservationRegistry` в RestClient
- Убедиться, что `traceparent` пробрасывается через HTTP

**Проблема: Request ID не в логах**
- Проверить `logback-spring.xml` - должны быть `includeMdcKeyName`
- Убедиться, что RequestIdFilter выполняется (HIGHEST_PRECEDENCE)

**Проблема: 401 от recommendation-service**
- Проверить M2M токен: `.\scripts\get-service-token.ps1`
- Проверить audience в Keycloak realm
- Убедиться, что `serviceAccountsEnabled: true` для library-service

**Проблема: Трасса не появляется в Tempo**
- Подождать 10-15 секунд (batch processor)
- Проверить логи OTLP Collector: `docker compose logs otel-collector`
- Проверить sampling probability: должна быть 1.0

### 12. Отличия от production

**В учебном проекте:**
- Sampling: 100% (в prod: 1-10%)
- Все сервисы на localhost
- Моковые данные (Flyway отключён)
- Anonymous auth в Grafana
- Логи в локальные файлы
- 1 hour retention в Tempo

**В production должно быть:**
- Adaptive sampling (1-10%)
- Service mesh для автоматической трассировки
- Centralized logging (ELK, Loki)
- Long-term storage в Tempo
- Alerting на основе трасс
- Trace-based testing
- Distributed context propagation через message queues

### 13. Следующие шаги

После успешного выполнения работы №6:
1. Интегрировать трассировку в собственный проект
2. Добавить custom spans для бизнес-логики
3. Настроить алерты на основе trace metrics
4. Изучить Trace-based testing
5. Подготовиться к работе №7: Промышленная наблюдаемость

## Заключение

Проект полностью готов к выполнению практической работы № 6. Реализованы все критические компоненты:
- ✅ Корреляция через X-Request-ID
- ✅ Распределённая трассировка через OpenTelemetry
- ✅ Межсервисное взаимодействие с OAuth2 M2M
- ✅ Визуализация трасс в Grafana Tempo
- ✅ Автоматическая верификация через PowerShell скрипт

**Время выполнения:** ~20-25 минут (первый запуск с загрузкой образов)

---

**Дата создания:** 3 ноября 2026  
**Версия Spring Boot:** 3.5.6  
**OpenTelemetry SDK:** Micrometer Tracing Bridge  
**Tempo версия:** 2.6.1  
**Grafana версия:** 11.3.1

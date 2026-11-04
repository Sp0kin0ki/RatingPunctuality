# Практическая работа № 6
## Корреляция запросов и распределённая трассировка

## Описание

Проект демонстрирует реализацию корреляции запросов через заголовок X-Request-ID и распределённую трассировку с использованием OpenTelemetry, Tempo и Grafana для системы учёта пунктуальности рейсов.

## Архитектура

```
Browser → Gateway (BFF) → Flight Service → Recommendation Service
          ↓                 ↓                ↓
          Redis            PostgreSQL      (моковые данные)
          ↓                 ↓                ↓
          OpenTelemetry Collector → Tempo → Grafana
```

### Компоненты:

1. **gateway-service** (порт 8088) - API Gateway / BFF
   - RequestIdFilter для валидации и нормализации X-Request-ID
   - OpenTelemetry tracing с W3C Trace Context
   - Реактивная пропагация контекста

2. **library-service (Flight Service)** (порт 8080) - Resource Server
   - RequestIdFilter для входящих запросов
   - RestClient с интерцепторами для проброса X-Request-ID и трассировки
   - OAuth 2.0 Client Credentials для M2M взаимодействия
   - Вызов recommendation-service

3. **recommendation-service** (порт 8083) - Resource Server
   - OAuth 2.0 Resource Server
   - RequestIdFilter
   - Моковые рекомендации рейсов

4. **OpenTelemetry Collector** (порты 4317/4318) - Сбор телеметрии
5. **Tempo** (порт 3200) - Хранилище трасс
6. **Grafana** (порт 3000) - Визуализация трасс

## Требования

- Java 21+
- Maven 3.8+
- Docker & Docker Compose
- PowerShell (для скриптов верификации)

## Запуск

### 1. Запуск инфраструктуры

```powershell
# Запустить все сервисы инфраструктуры
docker compose up -d

# Проверить статус контейнеров
docker compose ps

# Проверить готовность Keycloak
Invoke-RestMethod http://localhost:8085/realms/course/.well-known/openid-configuration | Select-Object issuer

# Проверить готовность Tempo
Invoke-WebRequest http://localhost:3200/ready

# Проверить Grafana
Invoke-RestMethod http://localhost:3000/api/health
```

### 2. Сборка проекта

```powershell
.\mvnw.cmd -DskipTests clean install
```

### 3. Запуск микросервисов

В трёх отдельных терминалах:

```powershell
# Терминал 1: Recommendation Service
.\mvnw.cmd -pl recommendation-service spring-boot:run

# Терминал 2: Flight Service (Library Service)
.\mvnw.cmd -pl library-service spring-boot:run

# Терминал 3: Gateway Service
.\mvnw.cmd -pl gateway-service spring-boot:run
```

**Важно:** Запускайте в указанном порядке и дождитесь полной инициализации каждого сервиса перед запуском следующего.

### 4. Открытие приложения

Откройте в браузере: http://localhost:8088

## Проверка функциональности

### Шаг 1: Аутентификация

1. Откройте http://localhost:8088
2. Нажмите "Войти через Keycloak"
3. Введите: **editor / editor**

### Шаг 2: Отправка запроса с трассировкой

В консоли браузера (DevTools → Console) выполните:

```javascript
{
  const requestId = crypto.randomUUID();
  const response = await fetch('/api/flights/1/recommendations', {
    headers: {
      'X-Request-ID': requestId
    },
    credentials: 'same-origin'
  });
  
  console.log('Request ID:', requestId);
  console.log('Response ID:', response.headers.get('X-Request-ID'));
  console.log('Status:', response.status);
  console.log('Data:', await response.json());
}
```

### Шаг 3: Проверка корреляции в логах

Скопируйте Request ID из консоли и найдите его в логах:

```powershell
$requestId = 'ПОДСТАВЬТЕ-UUID-ИЗ-БРАУЗЕРА'

# Используя ripgrep
rg --fixed-strings $requestId runtime-logs

# Или PowerShell
Get-ChildItem runtime-logs -Filter '*.log' | Select-String -SimpleMatch $requestId
```

Вы должны найти этот Request ID в логах всех трёх сервисов:
- gateway-service.log
- library-service.log
- recommendation-service.log

### Шаг 4: Визуализация трассы в Grafana

1. Откройте Grafana: http://localhost:3000
2. Перейдите в **Explore**
3. Выберите источник данных **Tempo**
4. Найдите trace_id из логов (32 hex-символа)
5. Вставьте Trace ID и выполните поиск

В Grafana вы увидите дерево из 5 HTTP-спанов:
```
gateway: входящий HTTP-запрос (SERVER)
└─ gateway: HTTP-вызов flight service (CLIENT)
   └─ library: входящий HTTP-запрос (SERVER)
      └─ library: HTTP-вызов recommendation (CLIENT)
         └─ recommendation: входящий запрос (SERVER)
```

### Шаг 5: Проверка валидации X-Request-ID

Отправьте запрос с невалидным X-Request-ID:

```javascript
{
  const response = await fetch('/api/flights/1/recommendations', {
    headers: {
      'X-Request-ID': 'not-a-uuid'
    },
    credentials: 'same-origin'
  });
  
  const newRequestId = response.headers.get('X-Request-ID');
  console.log('New Request ID:', newRequestId);
  console.log('Is valid UUID:', /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/.test(newRequestId));
}
```

Gateway должен сгенерировать новый валидный UUID.

### Шаг 6: Автоматическая верификация

Запустите скрипт полной проверки:

```powershell
.\scripts\verify-tracing.ps1
```

Скрипт выполнит:
- ✓ Аутентификацию через Keycloak
- ✓ Отправку запроса с X-Request-ID
- ✓ Проверку сохранения Request ID в ответе
- ✓ Поиск Request ID в логах всех сервисов
- ✓ Проверку доступности Tempo
- ✓ Тестирование обработки невалидного X-Request-ID

## Критерии успешного выполнения

✅ Сохранение валидного X-Request-ID из запроса клиента  
✅ Генерация нового UUID при невалидном формате  
✅ Единственный заголовок X-Request-ID в ответе  
✅ Общий request_id в логах всех трёх сервисов  
✅ Общий trace_id в логах всех сервисов  
✅ Дерево из 5 HTTP-спанов в Grafana Tempo  
✅ Корректные parent-child связи между спанами  
✅ Корреляция работает при ранних отказах (403, 401)  

## Конфигурация

### Flight Service (Library Service)

```yaml
server:
  port: 8080

spring:
  application:
    name: library-service
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8085/realms/course
          audiences: library-service
      client:
        registration:
          recommendation-client:
            provider: keycloak
            client-id: library-service
            client-secret: library-service-secret
            authorization-grant-type: client_credentials

recommendation:
  service:
    url: http://localhost:8083

management:
  tracing:
    sampling:
      probability: 1.0
  otlp:
    tracing:
      endpoint: http://localhost:4318/v1/traces
```

### Recommendation Service

```yaml
server:
  port: 8083

spring:
  application:
    name: recommendation-service
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8085/realms/course
          audiences: recommendation-service

management:
  tracing:
    sampling:
      probability: 1.0
  otlp:
    tracing:
      endpoint: http://localhost:4318/v1/traces
```

### Gateway Service

```yaml
server:
  port: 8088

spring:
  application:
    name: gateway-service
  reactor:
    context-propagation: auto
  cloud:
    gateway:
      routes:
        - id: library
          uri: http://localhost:8080
          predicates:
            - Path=/api/airports,/api/airports/**,/api/flights,/api/flights/**
          filters:
            - TokenRelay=

management:
  tracing:
    sampling:
      probability: 1.0
  otlp:
    tracing:
      endpoint: http://localhost:4318/v1/traces
```

## Ключевые компоненты

### 1. RequestIdFilter (Servlet-based)

Для library-service и recommendation-service:

```java
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter implements Filter {
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String MDC_REQUEST_ID_KEY = "request_id";
    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        String requestId = extractAndValidateRequestId(httpRequest);
        MDC.put(MDC_REQUEST_ID_KEY, requestId);
        httpResponse.setHeader(REQUEST_ID_HEADER, requestId);
        chain.doFilter(request, response);
        MDC.remove(MDC_REQUEST_ID_KEY);
    }
}
```

### 2. RequestIdFilter (Reactive Gateway)

Для gateway-service:

```java
@Component
public class RequestIdFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestId = extractAndValidateRequestId(headerValues);
        MDC.put(MDC_REQUEST_ID_KEY, requestId);
        
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(REQUEST_ID_HEADER, requestId)
                .build();
        
        exchange.getResponse().getHeaders().set(REQUEST_ID_HEADER, requestId);
        
        return chain.filter(mutatedExchange)
                .contextWrite(ctx -> ctx.put(MDC_REQUEST_ID_KEY, requestId));
    }
}
```

### 3. RestClient с интерцепторами

Flight Service вызывает Recommendation Service через RestClient:

```java
@Bean
public RestClient recommendationRestClient(
        OAuth2AuthorizedClientManager authorizedClientManager,
        ObservationRegistry observationRegistry) {
    
    return RestClient.builder()
            .baseUrl(recommendationServiceUrl)
            .requestInterceptor((request, body, execution) -> {
                // Проброс X-Request-ID из MDC
                String requestId = MDC.get("request_id");
                if (requestId != null) {
                    request.getHeaders().set("X-Request-ID", requestId);
                }
                return execution.execute(request, body);
            })
            .requestInterceptor(new OAuth2ClientHttpRequestInterceptor(authorizedClientManager))
            .observationRegistry(observationRegistry)
            .build();
}
```

## Troubleshooting

| Проблема | Решение |
|----------|---------|
| Разные Trace ID в сервисах | Проверить пропагацию traceparent в RestClient |
| Request ID не найден в логах | Проверить MDC и logback-spring.xml |
| Трасса не появляется в Tempo | Подождать 10-15 секунд, проверить OTLP Collector |
| 401 от recommendation-service | Проверить M2M токен и audience |
| Gateway возвращает 500 | Проверить доступность downstream сервисов |

## Структура проекта

```
rating_punctuality/
├── library-service/              # Flight Service (порт 8080)
│   ├── src/main/java/.../
│   │   ├── config/
│   │   │   ├── RestClientConfig.java
│   │   │   └── OAuth2ClientHttpRequestInterceptor.java
│   │   ├── controller/
│   │   │   └── FlightController.java
│   │   └── filter/
│   │       └── RequestIdFilter.java
│   └── src/main/resources/
│       ├── application.yml
│       └── logback-spring.xml
│
├── recommendation-service/       # Recommendation Service (порт 8083)
│   ├── src/main/java/.../
│   │   ├── config/
│   │   │   └── SecurityConfig.java
│   │   ├── controller/
│   │   │   └── RecommendationController.java
│   │   └── filter/
│   │       └── RequestIdFilter.java
│   └── src/main/resources/
│       ├── application.yml
│       └── logback-spring.xml
│
├── gateway-service/              # API Gateway / BFF (порт 8088)
│   ├── src/main/java/.../
│   │   └── filter/
│   │       └── RequestIdFilter.java
│   └── src/main/resources/
│       ├── application.yml
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
│       └── course-realm.json
│
├── scripts/
│   ├── verify-tracing.ps1
│   └── get-service-token.ps1
│
├── runtime-logs/                 # Логи всех сервисов
│   ├── gateway-service.log
│   ├── library-service.log
│   └── recommendation-service.log
│
├── docker-compose.yml
├── pom.xml
└── PRACTICE_6.md
```

## Адреса сервисов

- **Gateway (BFF)**: http://localhost:8088
- **Flight Service**: http://localhost:8080 (прямой доступ закрыт)
- **Recommendation Service**: http://localhost:8083 (прямой доступ закрыт)
- **Keycloak**: http://localhost:8085
- **Grafana**: http://localhost:3000 (admin / admin-dev-only)
- **Tempo API**: http://localhost:3200
- **OTLP Collector**: http://localhost:4318 (HTTP), :4317 (gRPC)

## Ключевые концепции

### X-Request-ID vs Trace ID

- **X-Request-ID**: Прикладной идентификатор для корреляции логов, передаётся явно в заголовке HTTP
- **Trace ID**: Идентификатор распределённой трассы, управляется OpenTelemetry SDK
- **Span ID**: Идентификатор конкретного участка работы внутри трассы

### W3C Trace Context

Заголовки стандарта W3C:
- `traceparent`: `00-{trace-id}-{parent-span-id}-{trace-flags}`
- `tracestate`: Vendor-specific data

### Логирование с MDC

Logback автоматически подхватывает:
- `traceId` - из OpenTelemetry
- `spanId` - из OpenTelemetry
- `request_id` - из MDC (наш прикладной контекст)

## Остановка

```powershell
# Остановить Java-процессы (Ctrl+C в каждом терминале)

# Остановить инфраструктуру
docker compose down

# Удалить volumes (включая историю трасс)
docker compose down -v
```

## Дополнительные материалы

- [W3C Trace Context](https://www.w3.org/TR/trace-context/)
- [OpenTelemetry Context Propagation](https://opentelemetry.io/docs/concepts/context-propagation/)
- [Spring Boot Tracing](https://docs.spring.io/spring-boot/reference/actuator/tracing.html)
- [Micrometer Tracing](https://micrometer.io/docs/tracing)

---

**Дата создания:** 3 ноября 2026  
**Версия Spring Boot:** 3.5.6  
**Версия OpenTelemetry:** Latest  
**Java версия:** 21

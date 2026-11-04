# ✅ Проект готов к работе - Итоговая сводка

## Статус выполнения

**Практическая работа № 6** полностью реализована и готова к демонстрации после обновления Java.

---

## 🚨 ВАЖНО: Требование Java 21

### Текущая проблема

В системе установлена **Java 8 (1.8.0_504)**, но проект требует **Java 21+**.

### ✅ Решение уже есть в системе!

Обнаружено, что **Java 21 УЖЕ УСТАНОВЛЕНА** в системе:
```
C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot
```

**Нужно только переключить систему на использование Java 21 вместо Java 8.**

### Быстрое переключение на Java 21

Выполните в PowerShell:

```powershell
# 1. Установить JAVA_HOME на Java 21
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot"

# 2. Добавить Java 21 в начало PATH
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# 3. Проверить версию
java -version
# Должно показать: openjdk version "21.0.13"

# 4. Проверить Maven
.\mvnw.cmd -version
# Должно показать: Java version: 21.0.13
```

### Сделать изменения постоянными

**Вариант 1: Через GUI (рекомендуется)**
1. Нажмите `Win + X` → **Система** → **Дополнительные параметры системы**
2. **Переменные среды**
3. В **Системные переменные**:
   - Найдите `JAVA_HOME` → Изменить → Установите:
     ```
     C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot
     ```
   - Найдите `Path` → Изменить → Переместите строку с `jdk-21` выше строки с `jdk-8`

4. Нажмите **OK** → Закройте все окна PowerShell → Откройте новое

**Вариант 2: Через PowerShell (администратор)**
```powershell
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot", "Machine")
```

---

## 📋 После переключения на Java 21

### 1. Сборка проекта

```powershell
cd D:\Projects\RatingPunctuality

# Очистить и пересобрать
.\mvnw.cmd -DskipTests clean install

# Ожидается: [INFO] BUILD SUCCESS
```

### 2. Запуск инфраструктуры

```powershell
# Запустить Docker Compose
docker compose up -d

# Проверить статус
docker compose ps

# Проверить готовность Keycloak (подождать ~60 секунд)
Invoke-RestMethod http://localhost:8085/realms/course/.well-known/openid-configuration | Select-Object issuer

# Проверить Tempo
Invoke-WebRequest http://localhost:3200/ready

# Проверить Grafana
Start-Process http://localhost:3000
```

### 3. Запуск микросервисов

Откройте **3 отдельных терминала PowerShell**:

**Терминал 1 - Recommendation Service:**
```powershell
cd D:\Projects\RatingPunctuality
.\mvnw.cmd -pl recommendation-service spring-boot:run
```

**Терминал 2 - Flight Service (Library Service):**
```powershell
cd D:\Projects\RatingPunctuality
.\mvnw.cmd -pl library-service spring-boot:run
```

**Терминал 3 - Gateway Service:**
```powershell
cd D:\Projects\RatingPunctuality
.\mvnw.cmd -pl gateway-service spring-boot:run
```

### 4. Открыть приложение

Откройте браузер: **http://localhost:8088**

### 5. Автоматическая проверка

После того как все сервисы запущены:

```powershell
.\scripts\verify-tracing.ps1
```

Скрипт проверит:
- ✓ Аутентификацию через Keycloak
- ✓ Сохранение X-Request-ID
- ✓ Корреляцию в логах
- ✓ Доступность Tempo
- ✓ Обработку невалидного X-Request-ID

---

## 📚 Документация

Проект включает следующую документацию:

### Основные документы

1. **PRACTICE_6.md** - Полная инструкция по практической работе №6
   - Описание архитектуры
   - Пошаговые инструкции
   - Сценарии проверки
   - Troubleshooting

2. **PRACTICE_6_SUMMARY.md** - Краткая итоговая сводка
   - Выполненные задачи
   - Структура проекта
   - Критерии успешного выполнения

3. **JAVA21_SETUP_GUIDE.md** - Руководство по установке/переключению Java 21
   - Пошаговая инструкция
   - Решение типовых проблем
   - FAQ

### Предыдущие работы

4. **PRACTICE_5.md** - Практическая работа №5 (API Gateway & BFF)
5. **PRACTICE_5_SUMMARY.md** - Сводка по работе №5

### Быстрый старт

6. **QUICK_DEMO.md** - Быстрый запуск для демонстрации фронтенда
7. **START_HERE.md** - Общая точка входа в проект

---

## 🏗️ Реализованные компоненты

### Практическая работа №6: Корреляция и трассировка

#### ✅ Корреляция через X-Request-ID

**Gateway Service (Reactive):**
- `RequestIdFilter` (GlobalFilter) для реактивной валидации UUID
- Генерация нового UUID при невалидном формате
- Пропагация через Reactor context

**Flight Service & Recommendation Service (Servlet):**
- `RequestIdFilter` (Servlet Filter) с HIGHEST_PRECEDENCE
- Валидация по regex паттерну
- Запись в MDC и response headers

#### ✅ Распределённая трассировка

**Все сервисы:**
- OpenTelemetry + Micrometer Tracing
- W3C Trace Context propagation
- OTLP экспорт в Tempo
- Структурированное логирование с trace_id, span_id, request_id

**Межсервисное взаимодействие:**
- RestClient с OAuth2 интерцепторами
- Client Credentials flow для M2M
- Автоматическая трассировка через ObservationRegistry

#### ✅ Инфраструктура

- **OpenTelemetry Collector** (порты 4317, 4318)
- **Tempo** для хранения трасс (порт 3200)
- **Grafana** для визуализации (порт 3000)
- **Keycloak** с M2M клиентами (порт 8085)
- **Redis** для сессий (порт 6379)

---

## 📊 Архитектура

```
Browser → Gateway Service (8088)
          ↓ [OAuth2 + Token Relay + X-Request-ID]
          → Flight Service (8080)
            ↓ [OAuth2 M2M + RestClient + X-Request-ID]
            → Recommendation Service (8083)

Все сервисы → OpenTelemetry Collector → Tempo → Grafana
Все сервисы → Structured Logs (runtime-logs/) с request_id, trace_id
```

---

## 🎯 Критерии успешного выполнения

После запуска можно проверить:

- ✅ Валидация и нормализация X-Request-ID
- ✅ Генерация UUID при невалидном формате
- ✅ Единственный заголовок X-Request-ID в ответе
- ✅ Общий request_id в логах всех трёх сервисов
- ✅ Общий trace_id в логах всех сервисов
- ✅ Дерево из 5 HTTP-спанов в Grafana Tempo
- ✅ Корректные parent-child связи между спанами
- ✅ W3C traceparent в межсервисных вызовах

---

## 🔍 Сценарии проверки

### Сценарий 1: Корреляция через X-Request-ID

В браузере (DevTools Console):
```javascript
const requestId = crypto.randomUUID();
const response = await fetch('/api/flights/1/recommendations', {
  headers: { 'X-Request-ID': requestId },
  credentials: 'same-origin'
});
console.log('Request ID:', requestId);
console.log('Response ID:', response.headers.get('X-Request-ID'));
// Должны совпадать!
```

### Сценарий 2: Поиск в логах

```powershell
$requestId = 'ВСТАВЬТЕ-UUID-ИЗ-БРАУЗЕРА'
Get-ChildItem runtime-logs -Filter '*.log' | Select-String -SimpleMatch $requestId
```

Должны найтись записи в логах:
- `gateway-service.log`
- `library-service.log`
- `recommendation-service.log`

### Сценарий 3: Визуализация в Grafana

1. Откройте http://localhost:3000
2. Перейдите в **Explore**
3. Выберите **Tempo**
4. Извлеките `trace_id` из логов (32 hex-символа)
5. Вставьте Trace ID и найдите трассу

Вы увидите дерево из 5 спанов:
- gateway: SERVER
- gateway→library: CLIENT
- library: SERVER
- library→recommendation: CLIENT
- recommendation: SERVER

---

## 🛠️ Полезные команды

### Проверка портов

```powershell
# Проверить занятые порты
netstat -ano | findstr ":8088 :8080 :8083 :8085 :3000 :3200"
```

### Логи Docker

```powershell
# Логи всех контейнеров
docker compose logs -f

# Логи конкретного сервиса
docker compose logs -f tempo
docker compose logs -f otel-collector
```

### Просмотр логов приложений

```powershell
# Показать последние 50 строк из всех логов
Get-ChildItem runtime-logs -Filter '*.log' | ForEach-Object {
    Write-Host "`n=== $($_.Name) ===" -ForegroundColor Cyan
    Get-Content $_.FullName -Tail 50
}
```

### Остановка всего

```powershell
# Остановить Java-процессы (Ctrl+C в каждом терминале)

# Остановить Docker
docker compose down

# Удалить volumes (если нужна полная очистка)
docker compose down -v
```

---

## 📞 Troubleshooting

### Проблема: Maven показывает Java 8

**Решение:** Установите переменные окружения (см. раздел "Быстрое переключение на Java 21")

### Проблема: Не компилируется

**Ошибка:** `class file has wrong version 61.0, should be 52.0`

**Решение:** Переключитесь на Java 21 и выполните:
```powershell
.\mvnw.cmd clean install -DskipTests
```

### Проблема: Keycloak не запускается

**Решение:** Подождите 60-90 секунд после `docker compose up -d`, проверьте:
```powershell
docker compose logs keycloak | Select-Object -Last 20
```

### Проблема: Трасса не появляется в Tempo

**Решение:** 
- Подождите 10-15 секунд (batch processor)
- Проверьте OTLP Collector: `docker compose logs otel-collector`

---

## ✨ Итог

Проект **полностью готов** к выполнению практической работы №6.

**Осталось только:**
1. Переключить систему на Java 21 (она уже установлена!)
2. Собрать проект: `.\mvnw.cmd -DskipTests clean install`
3. Запустить инфраструктуру: `docker compose up -d`
4. Запустить 3 микросервиса
5. Открыть http://localhost:8088
6. Запустить проверку: `.\scripts\verify-tracing.ps1`

**Время выполнения:** ~20-25 минут

**Успехов в выполнении практической работы! 🚀**

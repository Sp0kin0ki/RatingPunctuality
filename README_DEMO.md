# ✅ ПРОЕКТ ГОТОВ К ДЕМОНСТРАЦИИ

## Что сделано

Создан полноценный проект для практической работы № 5 **"API Gateway, Backend for Frontend (BFF) и безопасная передача токенов"** для предметной области **управления рейсами и аэропортами**.

### Компоненты:

1. **Flight Service** (порт 8080) - Resource Server
   - OAuth 2.0 Resource Server с JWT валидацией
   - REST API для рейсов и аэропортов
   - Role-based авторизация (READER, EDITOR)
   - **Моковые данные** (база отключена)

2. **Gateway Service** (порт 8088) - API Gateway / BFF
   - Spring Cloud Gateway (Reactive)
   - OAuth 2.0 Client (Authorization Code Flow)
   - Token Relay Filter
   - Spring Session с Redis
   - CSRF Protection
   - Веб-интерфейс с современным дизайном

3. **Keycloak** (порт 8085) - Identity Provider
   - Realm: `course`
   - Client: `course-bff`
   - Пользователи: `editor/editor`, `student/student`

---

## 🚀 ЗАПУСК (3 шага)

### Шаг 1: Установить Java 21

**В PowerShell выполните:**

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Проверка
java -version
```

**Должно показать:** `openjdk version "21.0.13"`

---

### Шаг 2: Собрать проект

```powershell
.\mvnw.cmd clean compile -DskipTests
```

**Ожидаемый результат:** `BUILD SUCCESS`

---

### Шаг 3: Запустить сервисы

**Терминал 1 - Flight Service:**

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

.\mvnw.cmd -pl library-service spring-boot:run
```

**Дождитесь:** `Started FlightServiceApplication`

---

**Терминал 2 - Gateway Service:**

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

.\mvnw.cmd -pl gateway-service spring-boot:run
```

**Дождитесь:** `Started GatewayServiceApplication`

---

**Откройте браузер:**

```
http://localhost:8088
```

---

## 🎯 ДЕМОНСТРАЦИЯ (5 минут)

### 1. Главная страница
- Откройте http://localhost:8088
- Видите темный интерфейс с кнопками

### 2. Вход через Keycloak
- Нажмите **"Войти через Keycloak"**
- Логин: **editor**
- Пароль: **editor**
- Перенаправит обратно на Gateway

### 3. Проверка сессии
- Нажмите **"Проверить сессию"**
- Видите:
  ```json
  {
    "authenticated": true,
    "username": "editor",
    "email": "editor@example.com",
    "roles": ["ROLE_READER", "ROLE_EDITOR"]
  }
  ```

### 4. Token Relay (передача токенов)
- Нажмите **"Загрузить рейсы"**
- Видите 3 моковых рейса:
  - **SU123**: SVO → LED (SCHEDULED)
  - **SU456**: LED → SVO (DEPARTED)
  - **SU789**: DME → LED (SCHEDULED)
- Gateway автоматически передал токен на Flight Service

### 5. CSRF Protection
- Нажмите **"Получить CSRF токен"**
- Видите токен:
  ```json
  {
    "headerName": "X-CSRF-TOKEN",
    "token": "..."
  }
  ```
- Нажмите **"Создать аэропорт"**
- Видите: **201 Created** + данные созданного аэропорта

### 6. Безопасность (главное!)
- Откройте **DevTools** (F12)
- Перейдите на вкладку **Application** → **Cookies**
- ✅ Видите cookie `SESSION`
- Перейдите на **Local Storage**
- ✅ Пусто! Токенов НЕТ в браузере
- Перейдите на **Session Storage**
- ✅ Пусто! Токенов НЕТ в браузере

**Вывод:** Токены хранятся только на сервере BFF, защищены от XSS-атак.

---

## 📋 Критерии успешной демонстрации

- ✅ OAuth 2.0 вход через Keycloak работает
- ✅ Токены НЕ доступны в браузере (только SESSION cookie)
- ✅ Token Relay работает (Gateway → Flight Service)
- ✅ CSRF Protection работает (POST требует токен)
- ✅ Role-based авторизация работает (READER vs EDITOR)
- ✅ Моковые данные показываются

---

## 🔧 Troubleshooting

### Проблема: Java 8 вместо Java 21

**Решение:**

```powershell
# Закрыть ВСЕ терминалы
# Открыть НОВЫЙ PowerShell

$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

where.exe java
# Должно показать: C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot\bin\java.exe

java -version
# openjdk version "21.0.13"
```

### Проблема: Keycloak не запущен

**Решение:**

```powershell
# Если Docker установлен
docker-compose up -d keycloak redis

# Подождать 60 секунд
Start-Sleep -Seconds 60

# Проверить
Invoke-RestMethod http://localhost:8085/realms/course/.well-known/openid-configuration
```

**Если Docker НЕ установлен:** Gateway покажет ошибку при старте, но фронтенд откроется.

### Проблема: Порт 8080 занят

**Решение:**

```powershell
# Найти процесс
Get-NetTCPConnection -LocalPort 8080 | Select-Object OwningProcess
Get-Process -Id <PID> | Select-Object Name

# Остановить
Stop-Process -Id <PID>
```

---

## 📊 Моковые данные

### Аэропорты:
- **SVO** - Sheremetyevo (Moscow)
- **DME** - Domodedovo (Moscow)
- **LED** - Pulkovo (Saint Petersburg)

### Рейсы:
- **SU123**: SVO → LED, статус SCHEDULED
- **SU456**: LED → SVO, статус DEPARTED
- **SU789**: DME → LED, статус SCHEDULED

### Пользователи:
- **editor / editor** - роли READER + EDITOR (полный доступ)
- **student / student** - роль READER (только чтение)

---

## 🎬 Сценарий для преподавателя

**Время:** 5 минут

1. **Показать главную страницу** (10 сек)
2. **Войти как editor/editor** (20 сек)
3. **Проверить сессию** → показать JSON (15 сек)
4. **Загрузить рейсы** → показать 3 рейса (15 сек)
5. **Получить CSRF** → показать токен (10 сек)
6. **Создать аэропорт** → показать 201 Created (20 сек)
7. **Открыть DevTools**:
   - Показать cookie SESSION ✅ (30 сек)
   - Показать LocalStorage пустой ✅ (15 сек)
   - Показать SessionStorage пустой ✅ (15 сек)
8. **Резюме:** "Токены защищены, хранятся только на сервере BFF" (30 сек)

**Итого:** 3 минуты демонстрация + 2 минуты вопросы

---

## 📁 Структура проекта

```
rating_punctuality/
├── library-service/           # Flight Service (порт 8080)
│   ├── src/main/java/
│   │   └── edu/rutmiit/enterprise/library/
│   │       ├── FlightServiceApplication.java
│   │       ├── config/
│   │       │   ├── SecurityConfig.java
│   │       │   └── JwtRoleConverter.java
│   │       ├── controller/
│   │       │   ├── FlightController.java
│   │       │   └── AirportController.java
│   │       └── entity/
│   │           ├── Flight.java
│   │           └── Airport.java
│   └── pom.xml
│
├── gateway-service/           # Gateway / BFF (порт 8088)
│   ├── src/main/java/
│   │   └── edu/rutmiit/enterprise/gateway/
│   │       ├── GatewayServiceApplication.java
│   │       ├── SecurityConfig.java
│   │       └── BffController.java
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── static/
│   │       └── index.html    # Веб-интерфейс
│   └── pom.xml
│
├── infra/keycloak/
│   └── course-realm.json     # Конфигурация Keycloak
│
├── docker-compose.yml
├── pom.xml                   # Parent POM
├── START_DEMO.md             # Эта инструкция
├── PRACTICE_5.md             # Подробная документация
└── INSTALL_JAVA_21.md        # Инструкция по установке Java
```

---

## 🔒 Архитектурные паттерны

1. **Backend for Frontend (BFF)** - Gateway как прокси для веб-клиента
2. **Token Relay** - автоматическая передача токенов на backend
3. **Session-based OAuth Client** - токены в серверной сессии, не в браузере
4. **CSRF Protection** - защита модифицирующих операций
5. **Audience Validation** - токены только для нужного API
6. **Role-based Authorization** - права на уровне методов

---

## ✅ Что демонстрирует проект

- ✅ **OAuth 2.0 Authorization Code Flow** через Keycloak
- ✅ **Безопасное хранение токенов** (только на сервере BFF)
- ✅ **Token Relay** (Gateway → Resource Server)
- ✅ **CSRF Protection** (POST/PUT/DELETE требуют токен)
- ✅ **JWT валидация** (подпись, audience, роли)
- ✅ **Role-based авторизация** (@PreAuthorize)
- ✅ **Сессии в Redis** (stateless Gateway)
- ✅ **Современный UI** (темный дизайн, реактивность)

---

## 📚 Документация

- **START_DEMO.md** - эта инструкция (быстрый старт)
- **PRACTICE_5.md** - полная документация (349 строк)
- **INSTALL_JAVA_21.md** - инструкция по установке Java 21
- **PRACTICE_5_SUMMARY.md** - итоговая сводка

---

## 🎓 Для защиты работы

**Вопросы, на которые нужно ответить:**

1. **Где хранятся access токены?**
   - На сервере BFF, в Redis-сессии

2. **Что видит браузер?**
   - Только HttpOnly cookie SESSION

3. **Как токен попадает на backend?**
   - Gateway извлекает из сессии и добавляет заголовок Authorization: Bearer

4. **Зачем нужен CSRF токен?**
   - Защита от CSRF-атак при использовании cookie

5. **Что проверяет Flight Service?**
   - Подпись JWT, issuer, audience, роли пользователя

6. **Чем BFF отличается от обычного API Gateway?**
   - BFF специализирован под конкретный тип клиента и управляет сессиями

---

**Дата создания:** 3 ноября 2026  
**Версия Spring Boot:** 3.5.6  
**Версия Java:** 21  
**Статус:** ✅ Готово к демонстрации

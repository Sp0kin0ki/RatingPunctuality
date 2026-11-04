# 🚀 ФИНАЛЬНАЯ ИНСТРУКЦИЯ ДЛЯ ЗАПУСКА

## ✅ Проект готов к демонстрации!

### Что создано:

1. **Flight Service** (порт 8080) - Resource Server с JWT
2. **Gateway Service** (порт 8088) - BFF с Token Relay
3. **Веб-интерфейс** - современный темный дизайн
4. **Keycloak** - OAuth 2.0 провайдер
5. **Моковые данные** - 3 рейса, 3 аэропорта (без базы)

---

## 📋 Шаг 1: Установить Java 21

**В PowerShell выполните:**

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Проверка
java -version
```

**Должно показать:** `openjdk version "21.0.13"`

---

## 📋 Шаг 2: Запустить Keycloak (опционально)

Если у вас установлен Docker:

```powershell
docker compose up -d keycloak redis

# Подождать 60 секунд
Start-Sleep -Seconds 60

# Проверить готовность
Invoke-RestMethod http://localhost:8085/realms/course/.well-known/openid-configuration
```

**Если Docker НЕ установлен** - пропустите, Gateway покажет ошибку при старте, но фронтенд откроется.

---

## 📋 Шаг 3: Запустить Flight Service

**Терминал 1:**

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

.\mvnw.cmd -pl library-service spring-boot:run
```

**Дождитесь:** `Started FlightServiceApplication in X.XXX seconds`

---

## 📋 Шаг 4: Запустить Gateway Service

**Терминал 2:**

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

.\mvnw.cmd -pl gateway-service spring-boot:run
```

**Дождитесь:** `Started GatewayServiceApplication in X.XXX seconds`

---

## 📋 Шаг 5: Открыть браузер

```
http://localhost:8088
```

---

## 🎯 ДЕМОНСТРАЦИЯ (5 минут)

### 1. Главная страница
- Видите темный интерфейс с кнопками

### 2. Вход через Keycloak
- Нажмите **"Войти через Keycloak"**
- Логин: **editor**
- Пароль: **editor**

### 3. Проверка сессии
- Нажмите **"Проверить сессию"**
- Видите данные пользователя + роли

### 4. Загрузка данных (Token Relay)
- Нажмите **"Загрузить рейсы"**
- Видите 3 рейса: SU123, SU456, SU789
- Gateway автоматически передал токен

### 5. CSRF Protection
- Нажмите **"Получить CSRF токен"**
- Нажмите **"Создать аэропорт"**
- Видите: **201 Created**

### 6. Безопасность (ГЛАВНОЕ!)
- Откройте **DevTools** (F12)
- **Application → Cookies** ✅ Есть SESSION
- **Local Storage** ✅ ПУСТО (токенов НЕТ!)
- **Session Storage** ✅ ПУСТО (токенов НЕТ!)

**Вывод:** Токены защищены, хранятся только на сервере BFF.

---

## ✅ Критерии успешной демонстрации

- ✅ OAuth 2.0 вход работает
- ✅ Токены НЕ в браузере (только SESSION cookie)
- ✅ Token Relay работает (Gateway → Flight Service)
- ✅ CSRF Protection работает
- ✅ Role-based авторизация работает
- ✅ Моковые данные показываются

---

## 🔧 Troubleshooting

### Проблема: "Java 8 вместо Java 21"

**Решение:** Закройте ВСЕ терминалы, откройте новый и выполните:

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

where.exe java
# Должно: C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot\bin\java.exe
```

### Проблема: "Keycloak not ready"

**Решение:** Подождите 60-90 секунд после `docker compose up`, затем проверьте:

```powershell
Invoke-RestMethod http://localhost:8085/realms/course/.well-known/openid-configuration
```

### Проблема: "Порт 8080 занят"

**Решение:**

```powershell
Get-NetTCPConnection -LocalPort 8080 | Select-Object OwningProcess
Stop-Process -Id <PID>
```

---

## 📊 Моковые данные

### Аэропорты:
- **SVO** - Sheremetyevo (Moscow)
- **DME** - Domodedovo (Moscow)
- **LED** - Pulkovo (Saint Petersburg)

### Рейсы:
- **SU123**: SVO → LED (SCHEDULED)
- **SU456**: LED → SVO (DEPARTED)
- **SU789**: DME → LED (SCHEDULED)

### Пользователи:
- **editor / editor** - READER + EDITOR (полный доступ)
- **student / student** - READER (только чтение)

---

## 🎬 Сценарий для защиты (3 минуты)

1. **Показать главную страницу** (10 сек)
2. **Войти как editor** (20 сек)
3. **Проверить сессию** (15 сек)
4. **Загрузить рейсы** → показать Token Relay (20 сек)
5. **Получить CSRF** (10 сек)
6. **Создать аэропорт** (20 сек)
7. **DevTools:**
   - Cookie SESSION ✅ (20 сек)
   - LocalStorage пустой ✅ (10 сек)
   - SessionStorage пустой ✅ (10 сек)
8. **Резюме** (20 сек)

**Итого:** 2:35 минуты

---

## 🔒 Вопросы для защиты

**1. Где хранятся токены?**
- На сервере BFF, в Redis-сессии

**2. Что видит браузер?**
- Только HttpOnly cookie SESSION

**3. Как токен попадает на backend?**
- Gateway извлекает из сессии и добавляет заголовок `Authorization: Bearer`

**4. Зачем CSRF токен?**
- Защита от CSRF-атак при использовании cookie

**5. Что проверяет Flight Service?**
- Подпись JWT, issuer, роли пользователя

**6. Чем BFF отличается от обычного Gateway?**
- BFF специализирован под веб-клиентов и управляет сессиями

---

## 📁 Структура проекта

```
rating_punctuality/
├── library-service/          # Flight Service (8080)
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
├── gateway-service/          # Gateway / BFF (8088)
│   ├── src/main/java/
│   │   └── edu/rutmiit/enterprise/gateway/
│   │       ├── GatewayServiceApplication.java
│   │       ├── SecurityConfig.java
│   │       └── BffController.java
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── static/
│   │       └── index.html
│   └── pom.xml
│
├── infra/keycloak/
│   └── course-realm.json
│
├── docker-compose.yml
├── pom.xml
└── README_FINAL.md          # Эта инструкция
```

---

## 🎓 Архитектурные паттерны

1. **Backend for Frontend (BFF)** - Gateway как прокси для веб-клиента
2. **Token Relay** - автоматическая передача токенов
3. **Session-based OAuth Client** - токены в серверной сессии
4. **CSRF Protection** - защита модифицирующих операций
5. **Role-based Authorization** - права на уровне методов

---

## ✅ Что демонстрирует проект

- ✅ OAuth 2.0 Authorization Code Flow
- ✅ Безопасное хранение токенов (только на сервере)
- ✅ Token Relay (Gateway → Resource Server)
- ✅ CSRF Protection
- ✅ JWT валидация (подпись, issuer, роли)
- ✅ Role-based авторизация (@PreAuthorize)
- ✅ Современный UI (темный дизайн)

---

**Время запуска:** 2-3 минуты  
**Время демонстрации:** 3-5 минут  
**Статус:** ✅ Готово к защите

---

*Дата создания: 3 ноября 2026*  
*Версия Spring Boot: 3.5.6*  
*Версия Java: 21*

# ✅ ПРОЕКТ ЗАВЕРШЕН!

## Что создано для практической работы № 5

Полноценный проект **"API Gateway, Backend for Frontend (BFF) и безопасная передача токенов"** для предметной области управления рейсами и аэропортами.

---

## 📦 Компоненты системы

### 1. Flight Service (порт 8080)
- OAuth 2.0 Resource Server с JWT валидацией
- REST API для рейсов (`/api/flights`) и аэропортов (`/api/airports`)
- Role-based авторизация (`@PreAuthorize`)
- Конвертер ролей Keycloak → Spring Security
- Моковые данные (без PostgreSQL)

### 2. Gateway Service (порт 8088)
- Spring Cloud Gateway (Reactive)
- OAuth 2.0 Client (Authorization Code Flow)
- Token Relay Filter (автоматическая передача токенов)
- Spring Session с Redis (сессии на сервере)
- CSRF Protection
- BFF Controller (`/bff/me`, `/bff/csrf`)
- Веб-интерфейс с темным дизайном

### 3. Keycloak (порт 8085)
- Realm: `course`
- OAuth Client: `course-bff / course-bff-secret`
- Пользователи: `editor/editor` (EDITOR), `student/student` (READER)
- Роли: `reader`, `editor`

### 4. Redis (порт 6379)
- Хранилище сессий Gateway
- Namespace: `course:gateway:sessions`

---

## 🎯 Реализованные механизмы безопасности

1. ✅ **Безопасное хранение токенов** - только на сервере BFF, не в браузере
2. ✅ **Token Relay** - автоматическая передача токенов от Gateway к Backend
3. ✅ **CSRF Protection** - защита модифицирующих операций
4. ✅ **JWT валидация** - проверка подписи, issuer, ролей
5. ✅ **Role-based авторизация** - разделение прав READER/EDITOR
6. ✅ **HttpOnly Cookie** - SESSION cookie недоступна из JavaScript
7. ✅ **Session-based OAuth Client** - токены привязаны к серверной сессии

---

## 📖 Документация

| Файл | Назначение |
|------|-----------|
| **README_FINAL.md** | Главная инструкция по запуску и демонстрации |
| **README_DEMO.md** | Подробная документация (165 строк) |
| **PRACTICE_5.md** | Теоретические материалы (349 строк) |
| **START_DEMO.md** | Быстрый старт без базы данных |
| **INSTALL_JAVA_21.md** | Инструкция по установке Java 21 |

---

## 🚀 Как запустить (3 шага)

### Шаг 1: Установить Java 21

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
java -version  # Должно показать: openjdk version "21.0.13"
```

### Шаг 2: Запустить Flight Service (Терминал 1)

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
.\mvnw.cmd -pl library-service spring-boot:run
```

### Шаг 3: Запустить Gateway Service (Терминал 2)

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
.\mvnw.cmd -pl gateway-service spring-boot:run
```

### Открыть браузер

```
http://localhost:8088
```

Логин: **editor** / Пароль: **editor**

---

## 🎬 Сценарий демонстрации (5 минут)

1. **Вход через Keycloak** (30 сек)
2. **Проверка сессии** → показать данные пользователя (20 сек)
3. **Загрузка рейсов** → показать Token Relay (30 сек)
4. **Получить CSRF токен** (15 сек)
5. **Создать аэропорт** → показать 201 Created (30 сек)
6. **DevTools:**
   - Cookie SESSION есть ✅ (30 сек)
   - LocalStorage пустой ✅ (20 сек)
   - SessionStorage пустой ✅ (20 сек)
7. **Резюме:** токены защищены (30 сек)

**Итого:** 3:45 минуты

---

## ✅ Критерии успешной демонстрации

- ✅ OAuth 2.0 вход работает
- ✅ Токены НЕ в браузере (только SESSION cookie)
- ✅ Token Relay работает (Gateway → Flight Service)
- ✅ CSRF Protection работает
- ✅ Role-based авторизация работает (READER vs EDITOR)
- ✅ Моковые данные показываются (3 рейса, 3 аэропорта)

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

## 🔒 Вопросы для защиты

**1. Где хранятся access токены?**
→ На сервере BFF, в Redis-сессии

**2. Что видит браузер?**
→ Только HttpOnly cookie SESSION

**3. Как токен попадает на backend?**
→ Gateway извлекает из сессии и добавляет заголовок `Authorization: Bearer`

**4. Зачем CSRF токен?**
→ Защита от CSRF-атак при использовании cookie

**5. Что проверяет Flight Service?**
→ Подпись JWT, issuer, роли пользователя

**6. Чем BFF отличается от обычного Gateway?**
→ BFF специализирован под веб-клиентов и управляет сессиями

---

## 📁 Структура проекта

```
rating_punctuality/
├── library-service/              # Flight Service
│   ├── controller/               # FlightController, AirportController
│   ├── entity/                   # Flight, Airport
│   ├── config/                   # SecurityConfig, JwtRoleConverter
│   └── FlightServiceApplication.java
│
├── gateway-service/              # Gateway / BFF
│   ├── GatewayServiceApplication.java
│   ├── SecurityConfig.java
│   ├── BffController.java
│   └── static/index.html         # Веб-интерфейс
│
├── infra/keycloak/
│   └── course-realm.json         # Конфигурация Keycloak
│
├── docker-compose.yml            # Keycloak + Redis
├── pom.xml                       # Parent POM
├── README_FINAL.md               # Главная инструкция
└── README.md                     # Эта сводка
```

---

## 🎓 Архитектурные паттерны

1. **Backend for Frontend (BFF)** - Gateway для веб-клиентов
2. **Token Relay** - автоматическая передача токенов
3. **Session-based OAuth Client** - токены в серверной сессии
4. **CSRF Protection** - защита модифицирующих операций
5. **Role-based Authorization** - права на уровне методов
6. **Gateway Routing** - маршрутизация через предикаты Path
7. **Gateway Offloading** - централизация аутентификации

---

## ⚙️ Технологии

- **Java 21**
- **Spring Boot 3.5.6**
- **Spring Cloud Gateway 2024.0.1**
- **Spring Security OAuth2 Client/Resource Server**
- **Spring Session Data Redis**
- **Keycloak 26**
- **Redis 7**

---

## ⏱ Время

- **Сборка:** 30 секунд
- **Запуск:** 2-3 минуты
- **Демонстрация:** 3-5 минут

---

## ✅ Статус: ГОТОВО К ЗАЩИТЕ

Проект полностью готов к демонстрации и защите практической работы № 5.

Для запуска откройте **README_FINAL.md** и следуйте инструкциям.

---

*Дата создания: 3 ноября 2026*  
*Версия Spring Boot: 3.5.6*  
*Версия Java: 21*  
*Статус: ✅ Готово*

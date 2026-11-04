# 🚀 БЫСТРЫЙ ЗАПУСК (БЕЗ БАЗЫ ДАННЫХ)

## ✅ Что сделано

- База данных PostgreSQL ОТКЛЮЧЕНА
- Используются МОКОВЫЕ данные
- Java 21 настроена
- Все зависимости исправлены

## Шаг 1: Установить переменные окружения Java 21

**ВАЖНО:** Выполните в PowerShell:

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Проверка
java -version
# Должно показать: openjdk version "21.0.13"
```

## Шаг 2: Запустить Keycloak и Redis

```powershell
# Если Docker установлен
docker-compose up -d redis keycloak

# Подождать 60 секунд
Start-Sleep -Seconds 60

# Проверить Keycloak
Invoke-RestMethod http://localhost:8085/realms/course/.well-known/openid-configuration
```

**Если Docker НЕ установлен** - пропустите этот шаг, запустим только Gateway на моках.

## Шаг 3: Собрать проект

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

.\mvnw.cmd clean compile -DskipTests
```

## Шаг 4: Запустить Flight Service

**Терминал 1:**
```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

.\mvnw.cmd -pl library-service spring-boot:run
```

**Ожидаемый вывод:**
```
Started FlightServiceApplication in X.XXX seconds
Tomcat started on port 8080 (http)
```

## Шаг 5: Запустить Gateway Service

**Терминал 2:**
```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

.\mvnw.cmd -pl gateway-service spring-boot:run
```

## Шаг 6: Открыть браузер

```
http://localhost:8088
```

## 🎯 Что показать на демонстрации

### 1. Вход через Keycloak
- Нажать **"Войти через Keycloak"**
- Логин: **editor** / Пароль: **editor**

### 2. Проверка сессии
- Нажать **"Проверить сессию"**
- Увидите: `authenticated: true`, email, роли

### 3. Token Relay
- Нажать **"Загрузить рейсы"**
- Увидите 3 моковых рейса:
  - SU123 (SVO → LED, SCHEDULED)
  - SU456 (LED → SVO, DEPARTED)
  - SU789 (DME → LED, SCHEDULED)

### 4. CSRF Protection
- Нажать **"Получить CSRF токен"**
- Нажать **"Создать аэропорт"**
- Успех: `201 Created`

### 5. Безопасность
- В DevTools проверить:
  - ✅ Cookie `SESSION` есть
  - ✅ LocalStorage пустой (токенов НЕТ)
  - ✅ SessionStorage пустой (токенов НЕТ)

---

## ⚠️ Если ошибка "Java 8"

Выполните **ДО запуска**:

```powershell
# Закрыть ВСЕ терминалы PowerShell
# Открыть НОВЫЙ PowerShell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Проверить
java -version
# openjdk version "21.0.13"

where.exe java
# C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot\bin\java.exe
```

---

## 📋 Моковые данные

### Аэропорты:
- SVO - Sheremetyevo (Moscow)
- DME - Domodedovo (Moscow)  
- LED - Pulkovo (Saint Petersburg)

### Рейсы:
- SU123: SVO → LED (SCHEDULED)
- SU456: LED → SVO (DEPARTED)
- SU789: DME → LED (SCHEDULED)

### Пользователи:
- **editor / editor** - полный доступ (READER + EDITOR)
- **student / student** - только чтение (READER)

---

## 🎬 Сценарий демонстрации

1. **Показать главную страницу** (http://localhost:8088)
2. **Войти как editor**
3. **Проверить сессию** → видим данные пользователя
4. **Загрузить рейсы** → видим 3 рейса
5. **Получить CSRF** → видим токен
6. **Создать аэропорт** → успех (201)
7. **Открыть DevTools** → показать cookie SESSION
8. **Показать LocalStorage** → пустой (токенов нет!)

**Итого: 5 минут демонстрации**

---

## ✅ Критерии успешной демонстрации

- ✅ OAuth 2.0 вход работает
- ✅ Токены НЕ в браузере (только SESSION cookie)
- ✅ Token Relay работает (Gateway → Flight Service)
- ✅ CSRF Protection работает
- ✅ Role-based авторизация работает
- ✅ Моковые данные показываются

---

**Время подготовки:** 2 минуты  
**Время демонстрации:** 5 минут  
**Без Docker:** работает (только фронт + моки)  
**Без PostgreSQL:** работает ✅

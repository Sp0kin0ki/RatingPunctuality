# 🎯 ГЛАВНЫЙ ФАЙЛ - НАЧНИТЕ ОТСЮДА!

## ⚠️ Критически важно: Java 21

**Хорошая новость:** Java 21 УЖЕ УСТАНОВЛЕНА в вашей системе!

Путь: `C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot`

**Нужно только переключить систему на её использование (30 секунд).**

---

## 🚀 Быстрый старт для демонстрации

### ⚡ Шаг 1: Переключение на Java 21 (30 сек)

Скопируйте и выполните в PowerShell:

```powershell
# Установить JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot"

# Обновить PATH
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Проверить версию
java -version
```

**Ожидается:** `openjdk version "21.0.13"`

Если показывает Java 8 → закройте PowerShell, откройте новое окно, повторите команды.

---

### 📦 Шаг 2: Сборка проекта (2 мин)

```powershell
cd D:\Projects\RatingPunctuality

.\mvnw.cmd -DskipTests clean install
```

**Ожидается:** `[INFO] BUILD SUCCESS`

---

### 🐳 Шаг 3: Запуск инфраструктуры (1 мин + 60 сек ожидание)

```powershell
# Запустить Redis и Keycloak
docker compose up -d redis keycloak

# Подождать 60 секунд для инициализации Keycloak
Start-Sleep -Seconds 60

# Проверить готовность Keycloak
Invoke-RestMethod http://localhost:8085/realms/course/.well-known/openid-configuration | Select-Object issuer
```

**Ожидается:** `http://localhost:8085/realms/course`

---

### 🚀 Шаг 4: Запуск сервисов (2 мин)

Откройте **3 отдельных терминала PowerShell**:

**Терминал 1 - Recommendation Service:**
```powershell
cd D:\Projects\RatingPunctuality
.\mvnw.cmd -pl recommendation-service spring-boot:run
```

**Терминал 2 - Flight Service:**
```powershell
cd D:\Projects\RatingPunctuality
.\mvnw.cmd -pl library-service spring-boot:run
```

**Терминал 3 - Gateway Service:**
```powershell
cd D:\Projects\RatingPunctuality
.\mvnw.cmd -pl gateway-service spring-boot:run
```

Дождитесь сообщений о старте всех сервисов (~30-60 сек каждый).

---

### 🌐 Шаг 5: Открыть в браузере

```
http://localhost:8088
```

**Войти:** `editor` / `editor`

---

## ✅ Что готово показать

### 1️⃣ OAuth 2.0 через Keycloak
- ✓ Редирект на форму входа Keycloak
- ✓ Токены хранятся только на сервере (BFF)
- ✓ Браузер получает только HttpOnly cookie SESSION
- ✓ LocalStorage и SessionStorage пусты (безопасность от XSS)

**Демонстрация:**
1. Открыть DevTools → Application → Cookies → видна SESSION cookie
2. LocalStorage и SessionStorage → пусто

### 2️⃣ API Gateway с Token Relay
- ✓ Единая точка входа для всех API
- ✓ Автоматическая передача токенов на backend
- ✓ Защита от прямого доступа

**Демонстрация:**
1. Нажать "Загрузить рейсы" → 200 OK (токен передан)
2. Открыть Incognito → `http://localhost:8080/api/flights` → 401 Unauthorized

### 3️⃣ CSRF Protection
- ✓ Модифицирующие операции требуют CSRF токен
- ✓ POST без токена → 403 Forbidden
- ✓ POST с токеном → 201 Created

**Демонстрация:**
1. Нажать "Получить CSRF токен" → видим токен
2. Нажать "Создать аэропорт" → 201 Created
3. В DevTools Console:
```javascript
await fetch('/api/airports', {
  method: 'POST',
  headers: {'Content-Type': 'application/json'},
  body: JSON.stringify({code: 'TST', name: 'Test', city: 'Test'})
})
// Результат: 403 Forbidden (нет CSRF токена)
```

### 4️⃣ Role-based авторизация
- ✓ editor: полный доступ (GET + POST/PUT/DELETE)
- ✓ student: только чтение (GET)
- ✓ Проверка прав на уровне методов

**Демонстрация:**
1. Выход из системы
2. Войти как `student` / `student`
3. Нажать "Загрузить рейсы" → 200 OK (чтение доступно)
4. Получить CSRF токен
5. Попытка "Создать аэропорт" → 403 Forbidden (нет роли editor)
6. Выход, вход как `editor` / `editor`
7. Повторить создание → 201 Created (роль позволяет)

### 5️⃣ Корреляция запросов (X-Request-ID)
- ✓ Единый ID прослеживается через все сервисы
- ✓ Автоматическая генерация UUID
- ✓ Валидация формата UUID
- ✓ Логи всех сервисов содержат один request_id

**Демонстрация:**
1. В DevTools Console:
```javascript
const requestId = crypto.randomUUID();
const response = await fetch('/api/flights/1/recommendations', {
  headers: { 'X-Request-ID': requestId }
});
console.log('Sent:', requestId);
console.log('Received:', response.headers.get('X-Request-ID'));
// Должны совпадать!
```

2. В PowerShell:
```powershell
$requestId = 'ВСТАВИТЬ-UUID-ИЗ-БРАУЗЕРА'
Get-ChildItem runtime-logs -Filter '*.log' | Select-String -SimpleMatch $requestId
```

3. Показать, что один ID встречается в логах всех 3 сервисов

### 6️⃣ Распределённая трассировка (опционально)
- ✓ OpenTelemetry + Micrometer Tracing
- ✓ W3C Trace Context propagation
- ✓ Визуализация в Grafana Tempo

**Для демонстрации трассировки** (если есть время):

1. Запустить полную инфраструктуру:
```powershell
docker compose up -d
```

2. Выполнить запрос через браузер

3. Открыть http://localhost:3000 (Grafana)
   - Логин: `admin` / `admin`

4. Explore → Tempo → вставить trace_id из логов

5. Показать дерево из 5 HTTP-спанов с timing

---

## 📋 Сценарии демонстрации

### Сценарий А: Полная демонстрация безопасности (5 мин)

1. Показать OAuth вход через Keycloak
2. Показать cookie SESSION, отсутствие токенов
3. Загрузить данные через Gateway → успех
4. Прямой запрос к backend → отказ (401)
5. CSRF защита: с токеном работает, без токена → 403
6. Роли: student не может создавать, editor может

### Сценарий Б: Корреляция и логи (3 мин)

1. Сгенерировать UUID в браузере
2. Отправить запрос с X-Request-ID
3. Проверить, что тот же ID вернулся в ответе
4. Найти ID в логах всех 3 сервисов
5. Показать, что все логи связаны одним request_id

### Сценарий В: Быстрая демонстрация (2 мин)

1. Войти через Keycloak
2. Загрузить данные → работает
3. Показать CSRF токен
4. Создать ресурс → успех

---

## 🆘 Быстрые решения проблем

### Проблема: "Maven показывает Java 8"

```powershell
# 1. Закройте ВСЕ окна PowerShell
# 2. Откройте новое
# 3. Повторите:
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
.\mvnw.cmd -version
```

### Проблема: "Keycloak не отвечает"

```powershell
# Посмотреть логи
docker compose logs keycloak | Select-Object -Last 20

# Перезапустить
docker compose restart keycloak

# Подождать ещё 30 секунд
```

### Проблема: "Порт уже занят"

```powershell
# Найти процесс на порту
netstat -ano | findstr ":8088"

# Убить процесс (замените PID)
taskkill /PID <номер> /F
```

### Проблема: "BUILD FAILURE"

```powershell
# 1. Проверить Java версию
java -version
# Должна быть 21.x

# 2. Очистить и пересобрать
.\mvnw.cmd clean
.\mvnw.cmd -DskipTests install
```

---

## 📚 Документация

Если нужны подробности:

- **QUICK_START_DEMO.md** - Детальная инструкция по запуску
- **JAVA21_SETUP_GUIDE.md** - Полное руководство по Java 21
- **PRACTICE_6.md** - Подробная инструкция по практической работе №6
- **PRACTICE_5.md** - Документация по API Gateway и BFF
- **README_START_PRACTICE_6.md** - Итоговая сводка проекта

---

## 🎓 Учётные записи

| Пользователь | Пароль | Роли | Доступ |
|-------------|--------|------|--------|
| editor | editor | reader, editor | Полный доступ (GET + POST/PUT/DELETE) |
| student | student | reader | Только чтение (GET) |

---

## ⏱️ Тайминг

- **Переключение Java:** 30 сек
- **Сборка проекта:** 2 мин
- **Запуск инфраструктуры:** 1 мин + 60 сек ожидание
- **Запуск сервисов:** 2 мин
- **Демонстрация:** 5-10 мин

**Итого:** ~10-15 минут от начала до полной демонстрации

---

## 🎯 Самое важное

1. ✅ **Java 21 УЖЕ УСТАНОВЛЕНА** - нужно только переключиться
2. ✅ Выполните команды переключения Java (Шаг 1)
3. ✅ Соберите проект: `.\mvnw.cmd -DskipTests clean install`
4. ✅ Запустите Redis и Keycloak: `docker compose up -d redis keycloak`
5. ✅ Запустите 3 микросервиса в отдельных терминалах
6. ✅ Откройте http://localhost:8088 и войдите

---

## ✨ Что реализовано

### Практическая работа №6: Корреляция и трассировка
- ✅ X-Request-ID через все сервисы
- ✅ Валидация UUID формата
- ✅ Автоматическая генерация при отсутствии
- ✅ OpenTelemetry + Micrometer Tracing
- ✅ W3C Trace Context propagation
- ✅ Структурированное логирование с trace_id, span_id, request_id
- ✅ Tempo для хранения трасс
- ✅ Grafana для визуализации

### Практическая работа №5: API Gateway и BFF
- ✅ Spring Cloud Gateway (Reactive)
- ✅ OAuth 2.0 Client (Authorization Code Flow)
- ✅ Token Relay Filter
- ✅ Session хранение в Redis
- ✅ CSRF Protection
- ✅ JWT Resource Server с audience validation
- ✅ Role-based авторизация

---

## 🚀 Проект полностью готов!

**Успешной демонстрации!**

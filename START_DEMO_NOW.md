# 🎯 НАЧНИТЕ ЗДЕСЬ - Практическая работа готова!

## ⚡ Критически важно

**У вас УЖЕ УСТАНОВЛЕНА Java 21!**

Найдена по пути:
```
C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot
```

**Нужно только переключить систему на её использование** (30 секунд)

---

## 🚀 3 шага до запуска

### Шаг 1: Переключиться на Java 21 (30 сек)

Скопируйте и выполните в PowerShell:

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
java -version
```

**Ожидается:** `openjdk version "21.0.13"`

Если показывает Java 8 - **закройте PowerShell и откройте новое окно**, повторите команды.

---

### Шаг 2: Собрать проект (2 мин)

```powershell
cd D:\Projects\RatingPunctuality

.\mvnw.cmd -DskipTests clean install
```

**Ожидается:** `[INFO] BUILD SUCCESS`

---

### Шаг 3: Запустить для демонстрации (3 мин)

**Запустить инфраструктуру:**
```powershell
docker compose up -d redis keycloak
```

**Подождать 60 секунд**, затем проверить:
```powershell
Invoke-RestMethod http://localhost:8085/realms/course/.well-known/openid-configuration | Select-Object issuer
```

**Запустить сервисы** (в трёх отдельных терминалах):

```powershell
# Терминал 1
.\mvnw.cmd -pl recommendation-service spring-boot:run

# Терминал 2
.\mvnw.cmd -pl library-service spring-boot:run

# Терминал 3
.\mvnw.cmd -pl gateway-service spring-boot:run
```

**Открыть браузер:**
```
http://localhost:8088
```

**Войти:** `editor` / `editor`

---

## ✅ Что готово показать

### 1️⃣ OAuth 2.0 аутентификация через Keycloak
- Вход через внешний Identity Provider
- Токены хранятся только на сервере (BFF)
- Браузер получает только HttpOnly cookie

### 2️⃣ API Gateway с Token Relay
- Единая точка входа
- Автоматическая передача токенов на backend
- Защита от прямого доступа (прямой запрос к backend → 401)

### 3️⃣ CSRF Protection
- Модифицирующие операции требуют CSRF токен
- Демонстрация: POST без токена → 403 Forbidden
- С токеном → 201 Created

### 4️⃣ Role-based авторизация
- **editor/editor** - полный доступ (GET + POST)
- **student/student** - только чтение (GET)
- Демонстрация: student пытается создать → 403

### 5️⃣ Корреляция запросов (X-Request-ID)
- Единый ID прослеживается через все сервисы
- Логи всех 3 микросервисов содержат один request_id
- Автоматическая генерация UUID при отсутствии

### 6️⃣ Распределённая трассировка (OpenTelemetry)
- Визуализация цепочки вызовов в Grafana
- Дерево из 5 HTTP-спанов
- W3C Trace Context propagation

---

## 📋 Сценарии демонстрации

### Сценарий А: Базовая безопасность

1. Открыть http://localhost:8088
2. Показать редирект на Keycloak
3. Войти как **editor/editor**
4. DevTools → Application → показать cookie SESSION
5. DevTools → Application → показать пустой LocalStorage (токенов нет!)
6. Нажать "Загрузить рейсы" → 200 OK
7. Открыть Incognito окно → http://localhost:8080/api/flights → 401 Unauthorized

### Сценарий Б: CSRF защита

1. Нажать "Получить CSRF токен"
2. Показать токен в ответе
3. Нажать "Создать аэропорт" → 201 Created
4. DevTools Console:
   ```javascript
   await fetch('/api/airports', {
     method: 'POST',
     headers: {'Content-Type': 'application/json'},
     body: JSON.stringify({code: 'TST', name: 'Test', city: 'Test'})
   })
   // Результат: 403 Forbidden (нет CSRF токена)
   ```

### Сценарий В: Роли и авторизация

1. Выход (если залогинены)
2. Войти как **student/student**
3. Нажать "Загрузить рейсы" → 200 OK (чтение разрешено)
4. Получить CSRF токен
5. Попытка "Создать аэропорт" → 403 Forbidden (нет роли editor)
6. Выход, вход как **editor/editor**
7. Повторить создание → 201 Created (роль позволяет)

### Сценарий Г: Корреляция

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

### Сценарий Д: Трассировка (опционально, если успеете)

1. Запустить полную инфраструктуру:
   ```powershell
   docker compose up -d
   ```

2. Выполнить запрос через браузер

3. Открыть http://localhost:3000 (Grafana)
   - Логин: admin / admin

4. Explore → Tempo → вставить trace_id из логов

5. Показать дерево спанов с timing

---

## 🆘 Быстрые решения проблем

### "Maven показывает Java 8"

```powershell
# 1. Закройте ВСЕ окна PowerShell
# 2. Откройте новое
# 3. Повторите:
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
.\mvnw.cmd -version
```

### "Keycloak не запускается"

```powershell
docker compose logs keycloak | Select-Object -Last 20
# Если ошибка - перезапустить:
docker compose restart keycloak
# Подождать ещё 30 сек
```

### "Порт уже занят"

```powershell
# Найти процесс
netstat -ano | findstr ":8088"
# Убить (заменить PID)
taskkill /PID <номер> /F
```

### "BUILD FAILURE"

1. Убедитесь, что `java -version` показывает 21.x
2. Очистите кэш Maven:
   ```powershell
   .\mvnw.cmd clean
   ```
3. Пересоберите:
   ```powershell
   .\mvnw.cmd -DskipTests install
   ```

---

## 📚 Документация

- **QUICK_START_DEMO.md** - Этот файл (самое главное)
- **JAVA21_SETUP_GUIDE.md** - Подробно про Java 21
- **PRACTICE_6.md** - Полная инструкция по работе №6
- **PRACTICE_5.md** - Документация по Gateway & BFF
- **README_START_PRACTICE_6.md** - Итоговая сводка

---

## 🎓 Учётные записи

| Логин | Пароль | Роли | Возможности |
|-------|--------|------|-------------|
| editor | editor | reader, editor | GET + POST/PUT/DELETE |
| student | student | reader | Только GET |

---

## ⏱️ Тайминг

- **Переключение Java:** 30 сек
- **Сборка проекта:** 2 мин
- **Запуск инфраструктуры:** 1 мин (+ 60 сек ожидание Keycloak)
- **Запуск сервисов:** 1-2 мин
- **Демонстрация:** 5-10 мин

**Итого:** ~10-15 минут от начала до полной демонстрации

---

## ✨ Итог

Всё готово! Нужно только:

1. ✅ Переключиться на Java 21 (она уже установлена)
2. ✅ Собрать проект
3. ✅ Запустить сервисы
4. ✅ Показать OAuth, Token Relay, CSRF, роли, корреляцию

**Проект полностью реализован и документирован.**

**Успешной демонстрации! 🚀**

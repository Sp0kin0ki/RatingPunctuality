# 🎯 Финальная инструкция для демонстрации

## Критическая информация

**Java 21 УЖЕ УСТАНОВЛЕНА** в вашей системе по пути:
```
C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot
```

Нужно только переключить систему на её использование.

---

## 🚀 Быстрый старт (5 минут)

### Шаг 1: Переключиться на Java 21

Выполните в PowerShell:

```powershell
# Установить JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot"

# Обновить PATH
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Проверить версию
java -version
# Должно показать: openjdk version "21.0.13"
```

### Шаг 2: Собрать проект

```powershell
cd D:\Projects\RatingPunctuality

.\mvnw.cmd -DskipTests clean install
```

Ожидается: `[INFO] BUILD SUCCESS`

### Шаг 3: Запустить инфраструктуру

```powershell
docker compose up -d redis keycloak
```

Подождите 60 секунд, затем проверьте:

```powershell
Invoke-RestMethod http://localhost:8085/realms/course/.well-known/openid-configuration | Select-Object issuer
```

### Шаг 4: Запустить сервисы

Откройте **3 терминала**:

**Терминал 1:**
```powershell
.\mvnw.cmd -pl recommendation-service spring-boot:run
```

**Терминал 2:**
```powershell
.\mvnw.cmd -pl library-service spring-boot:run
```

**Терминал 3:**
```powershell
.\mvnw.cmd -pl gateway-service spring-boot:run
```

### Шаг 5: Открыть в браузере

```
http://localhost:8088
```

**Войти:** editor / editor

---

## ✅ Что показать для демонстрации

### 1. OAuth 2.0 аутентификация
- Войти через Keycloak (editor/editor)
- Показать cookie SESSION в DevTools
- Показать отсутствие токенов в LocalStorage

### 2. Token Relay
- Нажать "Загрузить рейсы" → статус 200 OK
- Показать, что токен передаётся автоматически

### 3. CSRF Protection
- Нажать "Получить CSRF токен"
- Нажать "Создать аэропорт" → статус 201 Created
- Показать защиту (без токена = 403)

### 4. Role-based авторизация
- Войти как student/student
- Попытка создать → 403 Forbidden
- Выход и вход как editor → успех

---

## 📋 Сделать изменения постоянными (опционально)

Чтобы не вводить команды каждый раз:

### Через GUI (проще)

1. `Win + X` → **Система** → **Дополнительные параметры системы**
2. **Переменные среды**
3. В **Системные переменные**:
   - **JAVA_HOME** → Изменить:
     ```
     C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot
     ```
   - **Path** → Изменить → Найти строку с `jdk-21` и переместить её **выше** строки с `jdk-8`

4. **OK** → Перезапустить PowerShell

### Через PowerShell (администратор)

```powershell
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot", "Machine")
```

Перезапустить PowerShell.

---

## 🆘 Если что-то не работает

### Проблема: Maven всё ещё показывает Java 8

**Проверка:**
```powershell
.\mvnw.cmd -version
```

**Решение:**
1. Закройте **все** окна PowerShell
2. Откройте новое окно
3. Повторите команды из Шага 1
4. Проверьте снова

### Проблема: Keycloak не запускается

**Решение:**
```powershell
# Посмотреть логи
docker compose logs keycloak

# Если ошибка - перезапустить
docker compose restart keycloak

# Подождать ещё 30 секунд
```

### Проблема: Порт уже занят

**Решение:**
```powershell
# Найти процесс на порту 8088
netstat -ano | findstr ":8088"

# Убить процесс (замените PID)
taskkill /PID <номер_процесса> /F
```

---

## 📚 Документация

Если нужна подробная информация:

- **JAVA21_SETUP_GUIDE.md** - Полное руководство по Java 21
- **PRACTICE_6.md** - Полная инструкция по практической работе
- **PRACTICE_6_SUMMARY.md** - Краткая сводка
- **PRACTICE_5.md** - Документация по API Gateway и BFF

---

## 🎓 Учётные записи для демонстрации

| Пользователь | Пароль | Роли | Доступ |
|-------------|--------|------|--------|
| editor | editor | reader, editor | Полный доступ (GET + POST) |
| student | student | reader | Только чтение (GET) |

---

## ⚡ Самое важное

1. **Java 21 УЖЕ УСТАНОВЛЕНА** - нужно только переключиться
2. Выполните команды из **Шага 1**
3. Соберите проект: `.\mvnw.cmd -DskipTests clean install`
4. Запустите сервисы
5. Откройте http://localhost:8088

**Время:** 5-10 минут от начала до демонстрации

**Удачи! 🚀**

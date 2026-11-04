# ⚠️ КРИТИЧЕСКАЯ ПРОБЛЕМА: Несовместимая версия Java

## Текущая ситуация

**У вас установлена Java 8**, а проект требует **Java 21**

```
Ваша версия: openjdk version "1.8.0_504" (Java 8)
Требуется:   Java 21+
```

## Ошибка

```
class file has wrong version 61.0, should be 52.0
```

- **61.0** = Java 17+ (требуется проектом)
- **52.0** = Java 8 (у вас сейчас)

Spring Boot 3.5.6 требует минимум Java 17, рекомендуется Java 21.

---

## 🔧 РЕШЕНИЕ: Установить Java 21

### Шаг 1: Скачать Java 21

Перейдите по ссылке и скачайте **Windows x64 MSI** установщик:

🔗 **https://adoptium.net/temurin/releases/?version=21**

*(Eclipse Temurin - это бесплатная OpenJDK сборка)*

### Шаг 2: Установить

1. Запустите скачанный `.msi` файл
2. Установите в стандартную папку (например, `C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot`)
3. При установке отметьте:
   - ☑️ Add to PATH
   - ☑️ Set JAVA_HOME variable

### Шаг 3: Настроить переменные среды (если не настроилось автоматически)

Откройте **новый** PowerShell и выполните:

```powershell
# Укажите путь к установленной Java 21
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Проверить версию
java -version
```

**Ожидаемый результат:**
```
openjdk version "21.0.5" 2024-10-15 LTS
```

### Шаг 4: Собрать и запустить проект

```powershell
# Очистить и собрать проект
.\mvnw.cmd clean install -DskipTests

# Запустить library-service
.\mvnw.cmd -pl library-service spring-boot:run
```

---

## 🎯 Быстрый старт после установки Java 21

```powershell
# 1. Проверить Java версию
java -version

# 2. Собрать проект
.\mvnw.cmd clean install -DskipTests

# 3. Запустить сервисы (в двух терминалах)

# Терминал 1: Flight Service
.\mvnw.cmd -pl library-service spring-boot:run

# Терминал 2: Gateway Service  
.\mvnw.cmd -pl gateway-service spring-boot:run

# 4. Открыть браузер
# http://localhost:8088

# 5. Войти через Keycloak
# editor / editor
```

---

## 📋 Что будет работать

- ✅ OAuth 2.0 вход через Keycloak
- ✅ Сессии в Redis (токены не в браузере)
- ✅ Token Relay от Gateway к Flight Service
- ✅ CSRF Protection
- ✅ Role-based авторизация
- ✅ Моковые данные (без PostgreSQL)

---

## 🚫 Альтернативы (НЕ рекомендуется)

### ❌ Вариант 1: Downgrade до Spring Boot 2.7

Можно понизить версию до Spring Boot 2.7 (поддерживает Java 8), но:
- Потеряете все современные функции
- Spring Cloud Gateway требует изменений
- Практическая работа разработана под Java 21

### ❌ Вариант 2: Использовать Docker

Можно запустить всё через Docker, но потребуется:
- Создать Dockerfile для каждого сервиса
- Настроить docker-compose
- Больше времени на настройку

---

## ❓ Проверка после установки

```powershell
# 1. Версия Java
java -version
# Должно быть: openjdk version "21.x.x"

# 2. JAVA_HOME
$env:JAVA_HOME
# Должно быть: C:\Program Files\Eclipse Adoptium\jdk-21...

# 3. Компиляция проекта
.\mvnw.cmd clean compile -DskipTests
# Должно быть: BUILD SUCCESS
```

---

## 📞 Если проблема остается

1. **Полностью закройте PowerShell** (не просто новая вкладка)
2. Откройте **новый** PowerShell
3. Проверьте: `java -version`
4. Если всё ещё Java 8:
   ```powershell
   # Найти все установленные Java
   Get-ChildItem "C:\Program Files" -Filter "*jdk*" -Recurse -Depth 2 -ErrorAction SilentlyContinue | Select-Object FullName
   
   # Установить вручную
   $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot"
   $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
   ```

---

## 💡 Итого

**Для продолжения работы ОБЯЗАТЕЛЬНО нужна Java 21.**

После установки проект соберётся за 30 секунд и запустится без проблем.

---

*Дата создания: 3 ноября 2026*  
*Проект: Практическая работа №5 - API Gateway & BFF*

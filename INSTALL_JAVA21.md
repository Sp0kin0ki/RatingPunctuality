# Краткая инструкция по обновлению Java

## ❌ Текущая ситуация

Установлена: **Java 8 (1.8.0_504)** из Eclipse Adoptium  
Требуется: **Java 21+**  
Путь: `C:\Program Files\Eclipse Adoptium\jdk-8.0.504.1-hotspot`

## ✅ Решение

### Шаг 1: Скачать Java 21

Скачайте Eclipse Temurin 21 (LTS):
- **Прямая ссылка**: https://adoptium.net/temurin/releases/?version=21
- Выберите: **Windows x64 MSI** (для установщика)
- Или: **Windows x64 ZIP** (для portable версии)

### Шаг 2: Установить Java 21

**Вариант A: MSI-установщик (рекомендуется)**
1. Запустить загруженный `.msi` файл
2. При установке поставить галочку **"Set JAVA_HOME variable"**
3. Поставить галочку **"Add to PATH"**

**Вариант B: ZIP-архив**
1. Распаковать в `C:\Program Files\Eclipse Adoptium\jdk-21.x.x`
2. Настроить переменные окружения вручную (см. ниже)

### Шаг 3: Настроить переменные окружения

Если установщик не настроил автоматически:

**В PowerShell (только для текущей сессии):**
```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.5+11"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```

**Или в Windows (постоянно):**
1. Открыть **Панель управления** → **Система** → **Дополнительные параметры системы**
2. **Переменные среды**
3. **Системные переменные** → Создать/Изменить:
   - `JAVA_HOME` = `C:\Program Files\Eclipse Adoptium\jdk-21.0.5+11`
4. **Path** → Изменить → Добавить в начало:
   - `%JAVA_HOME%\bin`

### Шаг 4: Проверить установку

Откройте **новый** PowerShell и выполните:

```powershell
java -version
# Должно показать: openjdk version "21.0.x"

.\mvnw.cmd -version
# Должно показать: Java version: 21.0.x
```

### Шаг 5: Собрать проект

```powershell
cd D:\Projects\RatingPunctuality

# Очистить и пересобрать
.\mvnw.cmd -DskipTests clean install

# Если успешно, запустить сервисы
.\mvnw.cmd -pl recommendation-service spring-boot:run
```

## 🚀 Быстрая команда для установки (через winget)

Если у вас установлен Windows Package Manager:

```powershell
winget install EclipseAdoptium.Temurin.21.JDK
```

## 📝 Альтернативные варианты Java 21

Если по какой-то причине Eclipse Adoptium недоступен:

- **Oracle JDK 21**: https://www.oracle.com/java/technologies/downloads/#java21
- **Microsoft OpenJDK 21**: https://learn.microsoft.com/en-us/java/openjdk/download
- **Amazon Corretto 21**: https://aws.amazon.com/corretto/

## ⚠️ Важно

- После установки **обязательно откройте новое окно PowerShell**
- Старые терминалы не подхватят новые переменные окружения
- Убедитесь, что `JAVA_HOME` указывает на JDK 21, а не на старую версию

## 🔍 Диагностика проблем

Если после установки всё ещё показывает Java 8:

```powershell
# Проверить, какая Java используется
Get-Command java | Select-Object Source

# Проверить JAVA_HOME
$env:JAVA_HOME

# Проверить PATH (должна быть JDK 21 в начале)
$env:PATH -split ';' | Select-String -Pattern "java|jdk"

# Если найдена старая версия выше в PATH, нужно переместить JDK 21 выше
```

---

**После установки вернитесь к проекту и выполните:**
```powershell
.\mvnw.cmd -DskipTests clean install
```

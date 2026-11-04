# 🚨 КРИТИЧНО: Требуется обновление Java

## Текущая ситуация

**Установлено:** Java 8 (1.8.0_504) - Eclipse Adoptium  
**Требуется:** Java 21+ для Spring Boot 3.5.6  
**Статус:** ❌ Проект не компилируется

## Путь к быстрому запуску

### 1️⃣ Скачать Java 21

**Ссылка для скачивания:**
https://adoptium.net/temurin/releases/?version=21

Выберите:
- **Operating System:** Windows
- **Architecture:** x64
- **Package Type:** JDK
- **Archive:** `.msi` (установщик)

Или используйте прямую ссылку:
https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.5%2B11/OpenJDK21U-jdk_x64_windows_hotspot_21.0.5_11.msi

### 2️⃣ Установить Java 21

1. Запустите скачанный `.msi` файл
2. **ВАЖНО:** При установке обязательно поставьте галочки:
   - ✅ **Set JAVA_HOME variable**
   - ✅ **Add to PATH**
   - ✅ **JavaSoft registry keys**

3. Установка по умолчанию произойдёт в:
   ```
   C:\Program Files\Eclipse Adoptium\jdk-21.0.5+11
   ```

### 3️⃣ Перезапустить PowerShell

**ОБЯЗАТЕЛЬНО** закройте все окна PowerShell и откройте новое!

### 4️⃣ Проверить установку

```powershell
# Проверка версии Java
java -version
# Ожидается: openjdk version "21.0.5" 2024-10-15 LTS

# Проверка переменной окружения
$env:JAVA_HOME
# Ожидается: C:\Program Files\Eclipse Adoptium\jdk-21.0.5+11

# Проверка Maven
.\mvnw.cmd -version
# Ожидается: Java version: 21.0.5
```

### 5️⃣ Собрать проект

```powershell
cd D:\Projects\RatingPunctuality

# Очистить старые артефакты и пересобрать
.\mvnw.cmd -DskipTests clean install

# Если сборка успешна, увидите:
# [INFO] BUILD SUCCESS
```

### 6️⃣ Запустить сервисы

```powershell
# Терминал 1: Recommendation Service
.\mvnw.cmd -pl recommendation-service spring-boot:run

# Терминал 2: Flight Service  
.\mvnw.cmd -pl library-service spring-boot:run

# Терминал 3: Gateway Service
.\mvnw.cmd -pl gateway-service spring-boot:run
```

## Альтернатива: Установка через winget

Если у вас установлен Windows Package Manager:

```powershell
# Установить Java 21
winget install EclipseAdoptium.Temurin.21.JDK

# Перезапустить PowerShell, затем проверить
java -version
```

## Проблема: Java 21 установлена, но Maven использует Java 8

Если после установки Maven всё ещё видит Java 8:

### Решение 1: Обновить системную переменную PATH

1. Нажмите **Win + X** → **Система**
2. **Дополнительные параметры системы**
3. **Переменные среды**
4. В **Системные переменные** найдите **Path**
5. **Изменить** → найдите строку с Java 8:
   ```
   C:\Program Files\Eclipse Adoptium\jdk-8.0.504.1-hotspot\bin
   ```
6. **Переместите вверх** или **замените** на:
   ```
   C:\Program Files\Eclipse Adoptium\jdk-21.0.5+11\bin
   ```
7. Нажмите **ОК** во всех окнах

### Решение 2: Настроить JAVA_HOME вручную

В **Системные переменные**:
- Найдите или создайте переменную **JAVA_HOME**
- Установите значение:
  ```
  C:\Program Files\Eclipse Adoptium\jdk-21.0.5+11
  ```

**Затем обязательно перезапустите PowerShell!**

## Проверка правильности настройки

```powershell
# Какой java.exe используется
Get-Command java | Select-Object Source

# Должно показать:
# C:\Program Files\Eclipse Adoptium\jdk-21.0.5+11\bin\java.exe

# Проверить JAVA_HOME
$env:JAVA_HOME

# Должно показать:
# C:\Program Files\Eclipse Adoptium\jdk-21.0.5+11
```

## Часто задаваемые вопросы

### Q: Нужно ли удалять Java 8?
**A:** Нет, можно оставить обе версии. Главное - чтобы Java 21 была выше в PATH.

### Q: Можно ли использовать другой дистрибутив Java?
**A:** Да, подойдёт любой:
- Oracle JDK 21
- Microsoft Build of OpenJDK 21
- Amazon Corretto 21
- Azul Zulu 21

### Q: Что делать, если установщик не настроил JAVA_HOME?
**A:** Настройте вручную через **Переменные среды** (инструкция выше).

### Q: Maven всё ещё видит Java 8 после установки
**A:** 
1. Закройте **ВСЕ** окна PowerShell
2. Откройте новое окно
3. Проверьте `java -version`
4. Если всё ещё Java 8 - проверьте порядок в PATH (см. "Решение 1" выше)

## После успешной установки

Вернитесь к основной документации:
- **PRACTICE_6.md** - полная инструкция по практической работе
- **PRACTICE_6_SUMMARY.md** - краткая сводка

Запустите автоматическую проверку:
```powershell
.\scripts\verify-tracing.ps1
```

---

## Поддержка

Если возникли проблемы:
1. Убедитесь, что скачали **JDK**, а не **JRE**
2. Проверьте, что выбрали **x64** архитектуру
3. Перезагрузите компьютер (если ничего не помогло)

**Ссылки:**
- Eclipse Adoptium: https://adoptium.net/
- Документация Spring Boot: https://docs.spring.io/spring-boot/system-requirements.html

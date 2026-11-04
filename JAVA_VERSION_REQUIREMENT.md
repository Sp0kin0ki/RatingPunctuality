# ⚠️ Требование: Java 21

## Проблема

Проект требует **Java 21**, но в системе установлена **Java 8 (1.8.0_504)**.

Spring Boot 3.5.6 требует минимум Java 17, а в проекте явно указана версия Java 21.

## Ошибка компиляции

```
class file has wrong version 61.0, should be 52.0
```

- **61.0** = Java 17 (class file version)
- **52.0** = Java 8 (текущая версия компилятора)

## Решение

### Вариант 1: Установить Java 21 (Рекомендуется)

1. Скачать **Eclipse Temurin 21**:
   - https://adoptium.net/temurin/releases/?version=21

2. Установить и настроить `JAVA_HOME`:
   ```powershell
   $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.x.x"
   $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
   ```

3. Проверить версию:
   ```powershell
   java -version
   # Должно показать: openjdk version "21.x.x"
   ```

4. Пересобрать проект:
   ```powershell
   .\mvnw.cmd -DskipTests clean install
   ```

### Вариант 2: Использовать Maven Toolchains (если Java 21 уже установлена)

Если Java 21 установлена, но Maven использует неправильную версию:

1. Найти путь к Java 21:
   ```powershell
   Get-ChildItem "C:\Program Files" -Recurse -Filter "java.exe" -ErrorAction SilentlyContinue | 
       Where-Object { $_.FullName -match "jdk-21" } | 
       Select-Object FullName
   ```

2. Создать файл `%USERPROFILE%\.m2\toolchains.xml`:
   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <toolchains>
     <toolchain>
       <type>jdk</type>
       <provides>
         <version>21</version>
       </provides>
       <configuration>
         <jdkHome>C:\Program Files\Eclipse Adoptium\jdk-21.0.5+11</jdkHome>
       </configuration>
     </toolchain>
   </toolchains>
   ```

3. Пересобрать проект

### Вариант 3: Откатить проект на Spring Boot 2.7.x + Java 8 (Не рекомендуется)

Это потребует масштабных изменений и потери функциональности:
- Нет поддержки Jakarta EE (javax -> jakarta)
- Старые версии Spring Security
- Устаревшие зависимости

## Рекомендация

✅ **Установите Java 21** - это единственный правильный путь для работы с современными версиями Spring Boot 3.x.

## Быстрая проверка после установки

```powershell
# 1. Проверить версию Java
java -version

# 2. Проверить Maven использует правильную версию
.\mvnw.cmd -version

# 3. Собрать проект
.\mvnw.cmd -DskipTests clean install

# 4. Запустить сервис
.\mvnw.cmd -pl recommendation-service spring-boot:run
```

## Ссылки

- Eclipse Temurin (Adoptium): https://adoptium.net/
- Oracle JDK 21: https://www.oracle.com/java/technologies/downloads/#java21
- Microsoft Build of OpenJDK 21: https://learn.microsoft.com/en-us/java/openjdk/download

---

**Текущая Java:** 1.8.0_504 ❌  
**Требуется:** 21+ ✅  
**Spring Boot:** 3.5.6 (требует Java 17+)

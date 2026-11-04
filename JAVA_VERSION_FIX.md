# ⚠️ ПРОБЛЕМА: Java 8 вместо Java 21

## Проблема
У вас установлена **Java 8** (версия 1.8.0_504), а проект требует **Java 21**.

Ошибка: `class file has wrong version 61.0, should be 52.0`
- 61.0 = Java 17+
- 52.0 = Java 8

## Решение

### Вариант 1: Установить Java 21 (Рекомендуется)

1. Скачайте **Eclipse Temurin JDK 21**:
   https://adoptium.net/temurin/releases/?version=21

2. Установите и добавьте в переменные среды:
   ```powershell
   # После установки
   $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.x.x-hotspot"
   $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
   
   # Проверка
   java -version
   # Должно показать: openjdk version "21.x.x"
   ```

3. Перезапустите PowerShell и выполните:
   ```powershell
   .\mvnw.cmd -pl library-service spring-boot:run
   ```

### Вариант 2: Понизить версию Spring Boot до 2.7.x (Для Java 8)

Если нельзя обновить Java, измените проект на Spring Boot 2.7:

1. В `pom.xml` (корневой):
   ```xml
   <parent>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-parent</artifactId>
       <version>2.7.18</version>
   </parent>
   ```

2. Удалите Spring Cloud Gateway (не совместим с Java 8):
   - Используйте Spring Cloud Gateway MVC или Zuul

**НО**: Практическая работа № 5 разработана под Java 21 и Spring Boot 3.x

### Вариант 3: Использовать Maven Wrapper с нужной Java

Если у вас есть Java 21, но она не в PATH:

```powershell
$env:JAVA_HOME = "C:\путь\к\java-21"
.\mvnw.cmd -pl library-service spring-boot:run
```

## Текущая ситуация

- ✅ Keycloak запущен и работает
- ✅ Код написан и готов
- ❌ Java 8 не может скомпилировать Spring Boot 3.5.6
- ❌ Нужна Java 21

## Быстрый старт после установки Java 21

```powershell
# 1. Проверить версию
java -version
# openjdk version "21.x.x"

# 2. Собрать проект
.\mvnw.cmd clean install -DskipTests

# 3. Запустить сервисы
.\mvnw.cmd -pl library-service spring-boot:run
# В другом терминале:
.\mvnw.cmd -pl gateway-service spring-boot:run

# 4. Открыть браузер
# http://localhost:8088
```

## Альтернатива: Использовать Docker для сервисов

Если не хотите устанавливать Java 21, можно запустить через Docker:

```powershell
# Создать Dockerfile для library-service
# и запустить всё в контейнерах
```

---

**Вывод:** Для продолжения работы необходимо установить Java 21.

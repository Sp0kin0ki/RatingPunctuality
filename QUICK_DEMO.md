# 🚀 Быстрый запуск для демонстрации фронтенда

## Миграции отключены, используются моковые данные

### 1. Запустить инфраструктуру (только Redis и Keycloak)
```powershell
docker compose up -d redis keycloak

# Подождать 60 секунд
Start-Sleep -Seconds 60

# Проверить Keycloak
Invoke-RestMethod http://localhost:8085/realms/course/.well-known/openid-configuration | Select-Object issuer
```

### 2. Запустить сервисы

**Терминал 1 - Flight Service:**
```powershell
.\mvnw.cmd -pl library-service spring-boot:run
```

**Терминал 2 - Gateway Service:**
```powershell
.\mvnw.cmd -pl gateway-service spring-boot:run
```

### 3. Открыть браузер
```
http://localhost:8088
```

### 4. Демонстрация

1. **Войти:** editor / editor
2. **Проверить сессию** - видим authenticated: true
3. **Загрузить рейсы** - видим 3 моковых рейса (SU123, SU456, SU789)
4. **Получить CSRF токен**
5. **Создать аэропорт** - успех (моковое создание)

## Что работает

✅ OAuth 2.0 вход через Keycloak  
✅ Сессии в Redis  
✅ Token Relay от Gateway к Flight Service  
✅ CSRF Protection  
✅ Role-based авторизация  
✅ Моковые данные (без базы PostgreSQL)

## Учётные записи

- **editor / editor** - полный доступ
- **student / student** - только чтение

---

**Время запуска:** 2-3 минуты  
**Без базы данных:** ✅  
**Для демонстрации фронтенда:** ✅

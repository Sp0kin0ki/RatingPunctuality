# Практическая работа №1 для RatingPunctuality

Задание из PDF адаптировано под сущность `User` и существующий API проекта.

## Что изменено

- `V1`–`V3` фиксируют базовую историю таблиц `users`, `roles`, `users_roles`.
- `V4__add_user_country_code.sql` добавляет nullable `users.country_code VARCHAR(2)` с `CHECK` для `RU`, `BY`, `KZ`, `ZZ`.
- `V5__require_user_country_code.sql` задаёт `DEFAULT 'ZZ'`, заполняет старые `NULL` и включает `NOT NULL`.
- Поле `countryCode` добавлено в `User` и `UserRegistrationDto`.
- Старое тело регистрации без `countryCode` по-прежнему допустимо; сервис преобразует отсутствие значения в `ZZ` до сохранения.
- Ответы регистрации и `/api/auth/me` содержат `countryCode`.
- Добавлен Testcontainers-тест миграций, который применяет Flyway до V4, вставляет пользователя с `NULL`, затем применяет V5 и проверяет сохранность имени, `ZZ` и 5 успешных миграций.
- Добавлен API-тест для старого тела без кода страны и нового тела с `countryCode: RU`.

## Примеры API

Старое тело (без нового поля):

```json
{
  "username": "legacy_api_user",
  "email": "legacy-api@example.com",
  "password": "Password1",
  "confirm_password": "Password1",
  "description": "Legacy API payload"
}
```

Новое тело:

```json
{
  "username": "ru_api_user",
  "email": "ru-api@example.com",
  "password": "Password1",
  "confirm_password": "Password1",
  "description": "New API payload",
  "countryCode": "RU"
}
```

Эндпоинт: `POST /api/auth/add-user`.

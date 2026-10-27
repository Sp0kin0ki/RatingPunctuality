ALTER TABLE users
    ADD COLUMN IF NOT EXISTS country_code VARCHAR(2);

ALTER TABLE users
    DROP CONSTRAINT IF EXISTS ck_users_country_code;

ALTER TABLE users
    ADD CONSTRAINT ck_users_country_code
        CHECK (country_code IN ('RU', 'BY', 'KZ', 'ZZ'));

ALTER TABLE users
    ALTER COLUMN country_code SET DEFAULT 'ZZ';

UPDATE users
SET country_code = 'ZZ'
WHERE country_code IS NULL;

ALTER TABLE users
    ALTER COLUMN country_code SET NOT NULL;

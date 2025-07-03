-- liquibase formatted sql

-- changeset sbakhareva:1
ALTER TABLE users
ALTER COLUMN email SET NOT NULL,
ALTER COLUMN first_name SET NOT NULL,
ALTER COLUMN last_name SET NOT NULL,
ALTER COLUMN password SET NOT NULL,
ALTER COLUMN role SET NOT NULL,
ALTER COLUMN phone SET NOT NULL;

ALTER TABLE users
ADD CONSTRAINT uk_users_email UNIQUE (email),
ADD CONSTRAINT uk_users_phone UNIQUE (phone);

ALTER TABLE users
ADD CONSTRAINT chk_email_min_length CHECK (char_length(email) >= 4);

ALTER TABLE users
ADD CONSTRAINT chk_email_max_length CHECK (char_length(email) <= 32);

ALTER TABLE users
ADD CONSTRAINT chk_password_min_length CHECK (char_length(password) >= 8);

ALTER TABLE users
ADD CONSTRAINT chk_password_max_length CHECK (char_length(password) <= 16);

-- changeset sbakhareva:2

ALTER TABLE ads
ALTER COLUMN title SET NOT NULL,
ALTER COLUMN price SET NOT NULL,
ALTER COLUMN description SET NOT NULL,
ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE ads
ADD CONSTRAINT chk_title_min_length CHECK (char_length(title) >= 4),
ADD CONSTRAINT chk_title_max_length CHECK (char_length(title) <= 32),
ADD CONSTRAINT chk_description_min_length CHECK (char_length(description) >= 8),
ADD CONSTRAINT chk_description_max_length CHECK (char_length(description) <= 64);

ALTER TABLE ads
ADD CONSTRAINT chk_price CHECK (price >= 0 AND price <= 10000000);
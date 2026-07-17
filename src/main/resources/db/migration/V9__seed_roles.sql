ALTER TABLE roles
ADD CONSTRAINT roles_role_name_key UNIQUE (role_name);
INSERT INTO roles (role_name)
VALUES ('MANAGER'),
    ('STAFF'),
    ('CUSTOMER') ON CONFLICT (role_name) DO NOTHING;
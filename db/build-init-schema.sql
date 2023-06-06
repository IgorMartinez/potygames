CREATE TABLE users (
    id SERIAL,
    user_name TEXT UNIQUE NOT NULL,
    full_name TEXT NOT NULL,
    email TEXT NOT NULL,
    password TEXT NOT NULL,
    account_non_expired BOOLEAN NOT NULL,
    account_non_locked BOOLEAN NOT NULL,
    credentials_non_expired BOOLEAN NOT NULL,
    enabled BOOLEAN NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE permissions (
    id SERIAL,
    description TEXT,
    PRIMARY KEY (id)
);

CREATE TABLE user_permissions (
    id_user INTEGER,
    id_permission INTEGER,
    PRIMARY KEY (id_user, id_permission),
    FOREIGN KEY (id_user) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (id_permission) REFERENCES permissions (id) ON DELETE CASCADE
);

CREATE TABLE user_addresses (
    id SERIAL,
    id_user INTEGER NOT NULL,
    favorite BOOLEAN DEFAULT FALSE,
    description TEXT,
    street TEXT,
    number TEXT,
    complement TEXT,
    neighborhood TEXT,
    city TEXT,
    state TEXT,
    country TEXT,
    zip_code VARCHAR(8),
    PRIMARY KEY (id),
    FOREIGN KEY (id_user) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE user_contacts (
    id SERIAL,
    id_user INTEGER NOT NULL,
    favorite BOOLEAN DEFAULT FALSE,
    description TEXT,
    phone_number TEXT,
    PRIMARY KEY (id),
    FOREIGN KEY (id_user) REFERENCES users(id) ON DELETE CASCADE
);
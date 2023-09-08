CREATE TABLE user_addresses (
    id SERIAL,
    id_user INTEGER NOT NULL,
    favorite BOOLEAN DEFAULT FALSE,
    billing_address BOOLEAN DEFAULT FALSE,
    description TEXT,
    street TEXT,
    number TEXT,
    complement TEXT,
    neighborhood TEXT,
    city TEXT,
    state TEXT,
    country TEXT,
    zip_code TEXT,
    PRIMARY KEY (id),
    FOREIGN KEY (id_user) REFERENCES users(id) ON DELETE CASCADE
);
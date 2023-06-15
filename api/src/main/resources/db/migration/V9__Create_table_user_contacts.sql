CREATE TABLE user_contacts (
    id SERIAL,
    id_user INTEGER NOT NULL,
    favorite BOOLEAN DEFAULT FALSE,
    description TEXT,
    phone_number TEXT,
    PRIMARY KEY (id),
    FOREIGN KEY (id_user) REFERENCES users(id) ON DELETE CASCADE
);
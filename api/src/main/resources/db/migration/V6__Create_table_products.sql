CREATE TABLE product_types (
    id SERIAL,
    key_word TEXT,
    description TEXT,
    PRIMARY KEY(id)
);

CREATE TABLE products (
    id SERIAL,
    type INTEGER NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    PRIMARY KEY(id),
    FOREIGN KEY (type) REFERENCES product_types(id)
);

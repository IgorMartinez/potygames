CREATE TABLE product_types (
    id SERIAL,
    description TEXT,
    PRIMARY KEY(id)
);

CREATE TABLE products (
    id SERIAL,
    type INTEGER NOT NULL,
    name TEXT NOT NULL,
    alt_name TEXT,
    price MONEY NOT NULL,
    quantity INT NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY (type) REFERENCES product_types(id)
);

CREATE TABLE inventory_items (
    id SERIAL,
    id_product INTEGER,
    version TEXT,
    condition TEXT,
    price MONEY,
    quantity INTEGER,
    PRIMARY KEY(id),
    FOREIGN KEY (id_product) REFERENCES products(id) ON DELETE CASCADE
);
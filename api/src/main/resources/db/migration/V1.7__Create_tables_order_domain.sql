CREATE TABLE orders (
    id SERIAL,
    id_user INTEGER NOT NULL,
    total_price NUMERIC(12,2),
    status TEXT,
    PRIMARY KEY (id),
    FOREIGN KEY (id_user) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE order_addresses (
    id SERIAL,
    id_order INTEGER NOT NULL,
    delivery_address BOOLEAN DEFAULT FALSE,
    billing_address BOOLEAN DEFAULT FALSE,
    street TEXT,
    number TEXT,
    complement TEXT,
    neighborhood TEXT,
    city TEXT,
    state TEXT,
    country TEXT,
    zip_code TEXT,
    PRIMARY KEY (id),
    FOREIGN KEY (id_order) REFERENCES orders(id) ON DELETE CASCADE
);

CREATE TABLE order_items (
    id SERIAL,
    id_order INTEGER NOT NULL,
    id_inventory_item INTEGER NOT NULL,
    quantity INTEGER,
    unit_price NUMERIC(12,2),
    PRIMARY KEY (id),
    FOREIGN KEY (id_order) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (id_inventory_item) REFERENCES inventory_items(id) ON DELETE CASCADE
);

CREATE TABLE shopping_cart_items (
    id SERIAL,
    id_user INTEGER NOT NULL,
    id_inventory_item INTEGER NOT NULL,
    quantity INTEGER,
    PRIMARY KEY (id),
    FOREIGN KEY (id_user) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (id_inventory_item) REFERENCES inventory_items(id) ON DELETE CASCADE
);

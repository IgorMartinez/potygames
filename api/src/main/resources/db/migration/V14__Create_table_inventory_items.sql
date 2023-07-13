CREATE TABLE inventory_items (
    id SERIAL,
    id_product INTEGER,
    id_yugioh_card INTEGER,
    version TEXT,
    condition TEXT,
    price MONEY,
    quantity INTEGER,
    PRIMARY KEY(id),
    FOREIGN KEY (id_product) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (id_yugioh_card) REFERENCES yugioh_cards(id) ON DELETE CASCADE
);
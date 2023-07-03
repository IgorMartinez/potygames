CREATE TYPE YUGIOH_CARD_ATTRIBUTE AS ENUM('DARK', 'DIVINE', 'EARTH', 'FIRE', 'LIGHT', 'WATER', 'WIND');

CREATE TABLE yugioh_card_categories (
    id SERIAL,
    category TEXT NOT NULL, 
    sub_category TEXT NOT NULL, 
    main_deck boolean NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO yugioh_card_categories (category, sub_category, main_deck)
VALUES 
('Monster', 'Effect', true),
('Monster', 'Flip Effect', true),
('Monster', 'Flip Tuner Effect', true),
('Monster', 'Normal', true),
('Monster', 'Gemini', true),
('Monster', 'Normal Tuner', true),
('Monster', 'Pendulum Effect', true),
('Monster', 'Pendulum Effect Ritual', true),
('Monster', 'Pendulum Flip Effect', true),
('Monster', 'Pendulum Normal', true),
('Monster', 'Pendulum Tuner Effect', true),
('Monster', 'Ritual Effect', true),
('Monster', 'Ritual', true),
('Monster', 'Spirit', true),
('Monster', 'Toon', true),
('Monster', 'Tuner', true),
('Monster', 'Union Effect', true),
('Monster', 'Fusion', false),
('Monster', 'Link', false),
('Monster', 'Pendulum Effect Fusion', false),
('Monster', 'Synchro', false),
('Monster', 'Synchro Pendulum Effect', false),
('Monster', 'Synchro Tuner', false),
('Monster', 'XYZ', false),
('Monster', 'XYZ Pendulum Effect', false),
('Spell', 'Normal', true),
('Spell', 'Field', true),
('Spell', 'Equip', true), 
('Spell', 'Continuous', true), 
('Spell', 'Quick-Play', true), 
('Spell', 'Ritual', true),
('Trap', 'Normal', true), 
('Trap', 'Continuous', true), 
('Trap', 'Counter', true),
('Others', 'Skill Card', false),
('Others', 'Token', false);

CREATE TABLE yugioh_card_types (
    id SERIAL,
    description TEXT NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO yugioh_card_types (description)
VALUES 
('Aqua'),
('Beast'),
('Beast-Warrior'),
('Creator-God'),
('Cyberse'),
('Dinosaur'),
('Divine-Beast'),
('Dragon'),
('Fairy'),
('Fiend'),
('Fish'),
('Insect'),
('Illusionist'),
('Machine'),
('Plant'),
('Psychic'),
('Pyro'),
('Reptile'),
('Rock'),
('Sea Serpent'),
('Spellcaster'),
('Thunder'),
('Warrior'),
('Winged Beast'),
('Wyrm'),
('Zombie');

CREATE TABLE yugioh_cards (
    id SERIAL,
    id_ygoprodeck INTEGER,
    name TEXT,
    category INTEGER NOT NULL, 
    type INTEGER, 
    attribute YUGIOH_CARD_ATTRIBUTE, 
    level_rank_link INTEGER, 
    effect_lore_text TEXT,
    pendulum_scale INTEGER,
    link_arrows varchar(2)[],
    atk INTEGER,
    def INTEGER,
    PRIMARY KEY (id),
    FOREIGN KEY (category) REFERENCES yugioh_card_categories(id) ON DELETE CASCADE,
    FOREIGN KEY (type) REFERENCES yugioh_card_types(id) ON DELETE CASCADE
);
COMMENT ON COLUMN yugioh_cards.link_arrows IS '[N,NE,E,SE,S,SW,W,NW]';

-- Drop all tables of Poty Games Database
DROP TABLE flyway_schema_history;

DROP TABLE yugioh_cards;
DROP TABLE yugioh_card_types;
DROP TABLE yugioh_card_categories;
DROP CAST(varchar AS yugioh_card_attribute);
DROP TYPE YUGIOH_CARD_ATTRIBUTE;

DROP TABLE products;
DROP TABLE product_types;

DROP TABLE user_addresses;
DROP TABLE user_permissions;
DROP TABLE users;
DROP TABLE permissions;
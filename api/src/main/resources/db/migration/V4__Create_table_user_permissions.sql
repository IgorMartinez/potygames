CREATE TABLE user_permissions (
    id_user INTEGER,
    id_permission INTEGER,
    PRIMARY KEY (id_user, id_permission),
    FOREIGN KEY (id_user) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (id_permission) REFERENCES permissions (id) ON DELETE CASCADE
);
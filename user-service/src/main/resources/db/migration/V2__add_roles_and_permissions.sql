-- ROLES
CREATE TABLE roles
(
    id   BINARY(16) NOT NULL,
    code VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,

    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT ux_roles_code UNIQUE (code),
    CONSTRAINT ux_roles_name UNIQUE (name)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- PERMISSIONS
CREATE TABLE permissions
(
    id   BINARY(16) NOT NULL,
    name VARCHAR(100) NOT NULL,

    CONSTRAINT pk_permissions PRIMARY KEY (id),
    CONSTRAINT ux_permissions_name UNIQUE (name)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- USER ROLES
CREATE TABLE user_roles
(
    user_id BINARY(16) NOT NULL,
    role_id BINARY(16) NOT NULL,

    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users_profile (user_id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- ROLE PERMISSIONS
CREATE TABLE role_permissions
(
    role_id       BINARY(16) NOT NULL,
    permission_id BINARY(16) NOT NULL,

    CONSTRAINT pk_role_permissions PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles (id),
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions (id)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

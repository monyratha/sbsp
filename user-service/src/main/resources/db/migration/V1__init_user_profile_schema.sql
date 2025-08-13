-- USER PROFILES
CREATE TABLE users_profile
(
    user_id    BINARY(16) NOT NULL,
    username   VARCHAR(100) NOT NULL,
    email      VARCHAR(190) NULL,
    phone      VARCHAR(32) NULL,
    status     TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '0=OFF, 1=ON',
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_users_profile PRIMARY KEY (user_id),
    CONSTRAINT ux_users_profile_username UNIQUE (username),
    CONSTRAINT ux_users_profile_email UNIQUE (email),
    CONSTRAINT ck_users_profile_status CHECK (status IN (0, 1))
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;


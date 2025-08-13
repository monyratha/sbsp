-- USERS
CREATE TABLE users
(
    id                      BINARY(16) NOT NULL,
    username                VARCHAR(100) NOT NULL,
    password_hash           VARCHAR(255) NOT NULL,
    failed_attempts         INT UNSIGNED NOT NULL DEFAULT 0,
    lock_until              TIMESTAMP(6) NULL,

    -- enriched profile & security
    email                   VARCHAR(190) NULL,
    email_verified          TINYINT(1) NOT NULL DEFAULT 0,
    enabled                 TINYINT(1) NOT NULL DEFAULT 1,
    created_at              TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at              TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    last_login_at           TIMESTAMP(6) NULL,
    last_password_change_at TIMESTAMP(6) NULL,
    locale                  VARCHAR(16) NULL,
    time_zone               VARCHAR(64) NULL,
    mfa_enabled             TINYINT(1) NOT NULL DEFAULT 0,
    totp_secret             VARCHAR(128) NULL,

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT ux_users_username UNIQUE (username),
    CONSTRAINT ux_users_email UNIQUE (email)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE INDEX ix_users_lock_until ON users (lock_until);
CREATE INDEX ix_users_enabled ON users (enabled);
CREATE INDEX ix_users_last_login ON users (last_login_at);

-- REFRESH TOKENS (hashed; revoke via revoked_at)
CREATE TABLE refresh_tokens
(
    id      BINARY(16) NOT NULL,
    user_id    BINARY(16) NOT NULL,
    token_hash VARCHAR(128) NOT NULL,
    expires_at TIMESTAMP(6) NOT NULL,
    revoked_at TIMESTAMP(6) NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_ip VARCHAR(45) NULL,
    user_agent VARCHAR(255) NULL,

    PRIMARY KEY (id),
    INDEX      ix_refresh_user_id (user_id),
    INDEX      ix_refresh_expires (expires_at),
    CONSTRAINT fk_refresh_tokens_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

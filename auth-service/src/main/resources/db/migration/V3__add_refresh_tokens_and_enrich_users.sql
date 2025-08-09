ALTER TABLE users
    ADD COLUMN email varchar(190) NULL,
    ADD COLUMN email_verified bit NOT NULL DEFAULT 0,
    ADD COLUMN enabled bit NOT NULL DEFAULT 1,
    ADD COLUMN created_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    ADD COLUMN updated_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    ADD COLUMN last_login_at timestamp(6) NULL,
    ADD COLUMN last_password_change_at timestamp(6) NULL,
    ADD COLUMN locale varchar(16) NULL,
    ADD COLUMN time_zone varchar(64) NULL,
    ADD COLUMN mfa_enabled bit NOT NULL DEFAULT 0,
    ADD COLUMN totp_secret varchar(128) NULL,
    ADD UNIQUE INDEX ux_users_email (email),
    ADD INDEX ix_users_lock_until (lock_until),
    ADD INDEX ix_users_enabled (enabled),
    ADD INDEX ix_users_last_login_at (last_login_at);

CREATE TABLE user_roles (
    user_id varchar(36) NOT NULL,
    role varchar(255) NOT NULL,
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE refresh_tokens (
    id varchar(36) NOT NULL,
    user_id varchar(36) NOT NULL,
    token_hash varchar(128) NOT NULL,
    expires_at timestamp(6) NOT NULL,
    revoked_at timestamp(6) NULL,
    created_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_ip varchar(45) NULL,
    user_agent varchar(255) NULL,
    PRIMARY KEY (id),
    INDEX ix_refresh_user_id (user_id),
    INDEX ix_refresh_expires_at (expires_at),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

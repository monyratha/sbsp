-- Users table
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) NOT NULL,
    username VARCHAR(64) NOT NULL,
    email VARCHAR(190),
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    password_hash VARCHAR(100) NOT NULL,
    failed_attempts INT NOT NULL DEFAULT 0,
    lock_until TIMESTAMP NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP NULL,
    last_password_change_at TIMESTAMP NULL,
    locale VARCHAR(16),
    time_zone VARCHAR(64),
    mfa_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    totp_secret VARCHAR(128),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Ensure new columns exist when upgrading from older schema
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS email VARCHAR(190),
    ADD COLUMN IF NOT EXISTS email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS password_hash VARCHAR(100) NOT NULL,
    ADD COLUMN IF NOT EXISTS failed_attempts INT NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS lock_until TIMESTAMP NULL,
    ADD COLUMN IF NOT EXISTS enabled BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    ADD COLUMN IF NOT EXISTS last_login_at TIMESTAMP NULL,
    ADD COLUMN IF NOT EXISTS last_password_change_at TIMESTAMP NULL,
    ADD COLUMN IF NOT EXISTS locale VARCHAR(16),
    ADD COLUMN IF NOT EXISTS time_zone VARCHAR(64),
    ADD COLUMN IF NOT EXISTS mfa_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS totp_secret VARCHAR(128);

CREATE UNIQUE INDEX IF NOT EXISTS ux_users_username ON users(username);
CREATE UNIQUE INDEX IF NOT EXISTS ux_users_email ON users(email);
CREATE INDEX IF NOT EXISTS ix_users_lock_until ON users(lock_until);
CREATE INDEX IF NOT EXISTS ix_users_enabled ON users(enabled);
CREATE INDEX IF NOT EXISTS ix_users_last_login_at ON users(last_login_at);

-- User roles table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id VARCHAR(36) NOT NULL,
    role VARCHAR(32) NOT NULL,
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Refresh tokens table
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    token_hash VARCHAR(128) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_ip VARCHAR(45),
    user_agent VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_refresh_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IF NOT EXISTS ix_refresh_user ON refresh_tokens(user_id);
CREATE INDEX IF NOT EXISTS ix_refresh_expires ON refresh_tokens(expires_at);

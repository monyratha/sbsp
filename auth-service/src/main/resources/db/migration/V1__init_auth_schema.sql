-- USERS
CREATE TABLE users (
                       id                       VARCHAR(36)  NOT NULL,
                       username                 VARCHAR(100) NOT NULL,
                       password_hash            VARCHAR(255) NOT NULL,
                       failed_attempts          INT NOT NULL DEFAULT 0,
                       lock_until               TIMESTAMP(6) NULL,

    -- enriched profile & security
                       email                    VARCHAR(190) NULL,
                       email_verified           BIT NOT NULL DEFAULT 0,
                       enabled                  BIT NOT NULL DEFAULT 1,
                       created_at               TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                       updated_at               TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                       last_login_at            TIMESTAMP(6) NULL,
                       last_password_change_at  TIMESTAMP(6) NULL,
                       locale                   VARCHAR(16)  NULL,
                       time_zone                VARCHAR(64)  NULL,
                       mfa_enabled              BIT NOT NULL DEFAULT 0,
                       totp_secret              VARCHAR(128) NULL,

                       CONSTRAINT pk_users PRIMARY KEY (id),
                       CONSTRAINT ux_users_username UNIQUE (username),
                       CONSTRAINT ux_users_email    UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX ix_users_lock_until  ON users(lock_until);
CREATE INDEX ix_users_enabled     ON users(enabled);
CREATE INDEX ix_users_last_login  ON users(last_login_at);

-- USER ROLES (simple role list, many-to-one)
CREATE TABLE user_roles (
                            user_id  VARCHAR(36)  NOT NULL,
                            role     VARCHAR(255) NOT NULL,
                            PRIMARY KEY (user_id, role),
                            CONSTRAINT fk_user_roles_user
                                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- REFRESH TOKENS (hashed; revoke via revoked_at)
CREATE TABLE refresh_tokens (
                                id          VARCHAR(36)  NOT NULL,
                                user_id     VARCHAR(36)  NOT NULL,
                                token_hash  VARCHAR(128) NOT NULL,
                                expires_at  TIMESTAMP(6) NOT NULL,
                                revoked_at  TIMESTAMP(6) NULL,
                                created_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                created_ip  VARCHAR(45)  NULL,
                                user_agent  VARCHAR(255) NULL,
                                PRIMARY KEY (id),
                                INDEX ix_refresh_user_id (user_id),
                                INDEX ix_refresh_expires (expires_at),
                                CONSTRAINT fk_refresh_tokens_user
                                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

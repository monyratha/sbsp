-- USER PROFILES
CREATE TABLE users_profile (
    id VARCHAR(36) NOT NULL,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(190) NULL,
    phone VARCHAR(32) NULL,
    status VARCHAR(32) NOT NULL,
    tenant_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_users_profile PRIMARY KEY (id),
    CONSTRAINT ux_users_profile_username UNIQUE (username),
    CONSTRAINT ux_users_profile_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX ix_users_profile_tenant_id ON users_profile (tenant_id);
CREATE INDEX ix_users_profile_status ON users_profile (status);
CREATE INDEX ix_users_profile_phone ON users_profile (phone);

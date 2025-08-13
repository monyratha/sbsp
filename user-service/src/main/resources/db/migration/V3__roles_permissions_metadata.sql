-- Add metadata columns to roles
ALTER TABLE roles
    ADD COLUMN description VARCHAR(255) NULL,
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Update permissions structure
ALTER TABLE permissions
    DROP INDEX ux_permissions_name,
    CHANGE COLUMN name code VARCHAR(100) NOT NULL,
    ADD COLUMN section VARCHAR(100) NOT NULL,
    ADD COLUMN label VARCHAR(100) NOT NULL,
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    ADD CONSTRAINT ux_permissions_code UNIQUE (code);

-- Ensure role_permissions cascades on delete
ALTER TABLE role_permissions
    DROP FOREIGN KEY fk_role_permissions_role,
    DROP FOREIGN KEY fk_role_permissions_permission;

ALTER TABLE role_permissions
    ADD CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE;

-- Seed default roles
INSERT INTO roles (id, name, description) VALUES
  (UUID_TO_BIN('c3908054-dc02-43c6-8eca-969b7da54ea4'), 'Super Admin', 'Full platform access'),
  (UUID_TO_BIN('7e6c7b80-46bd-48c4-bc05-6e8e78f06116'), 'Admin', 'Manage subscriptions and customers'),
  (UUID_TO_BIN('4d50cf00-f990-4172-bd30-92595e2f4a02'), 'Merchant', 'Owns and manages sites, products, services'),
  (UUID_TO_BIN('d7317200-5f5c-47a9-aa5f-023dd9fccd3a'), 'Marketing', 'Promote via referral/invite codes'),
  (UUID_TO_BIN('09b2b833-3303-4b45-848a-883b5e66faa0'), 'Assists', 'Assists Merchant in site/product/category management');

-- Seed permissions
INSERT INTO permissions (id, code, section, label) VALUES
  (UUID_TO_BIN('58597d50-b246-4c6c-aa95-565ed656b368'), 'subscription.manage', 'Subscription', 'Manage subscriptions'),
  (UUID_TO_BIN('9baa1cac-cd7b-4250-a55a-3b74aa10bb8d'), 'customer.manage', 'Customer', 'Manage customers'),
  (UUID_TO_BIN('8aaf60ec-ca7e-4dd2-b9b5-a60e315d65ff'), 'site.manage', 'Site', 'Manage sites'),
  (UUID_TO_BIN('e728a4e8-19fc-4664-813c-2a6a6b23263f'), 'product.manage', 'Product', 'Manage products'),
  (UUID_TO_BIN('bad07f7f-49b5-429e-9f21-2d1ad7a3ca2f'), 'service.manage', 'Service', 'Manage services'),
  (UUID_TO_BIN('cf29ff1d-0b88-4fa0-8339-70f704461537'), 'referral.manage', 'Referral', 'Manage referral codes'),
  (UUID_TO_BIN('1e61a03f-496c-4d8b-851f-98ab23deee8a'), 'invite.manage', 'Invite', 'Manage invite codes'),
  (UUID_TO_BIN('38f32558-0254-41a7-b93e-67ccb53038fb'), 'category.manage', 'Category', 'Manage categories');

-- Super Admin gets all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT UUID_TO_BIN('c3908054-dc02-43c6-8eca-969b7da54ea4'), id FROM permissions;

-- Admin permissions
INSERT INTO role_permissions (role_id, permission_id) VALUES
  (UUID_TO_BIN('7e6c7b80-46bd-48c4-bc05-6e8e78f06116'), UUID_TO_BIN('58597d50-b246-4c6c-aa95-565ed656b368')),
  (UUID_TO_BIN('7e6c7b80-46bd-48c4-bc05-6e8e78f06116'), UUID_TO_BIN('9baa1cac-cd7b-4250-a55a-3b74aa10bb8d'));

-- Merchant permissions
INSERT INTO role_permissions (role_id, permission_id) VALUES
  (UUID_TO_BIN('4d50cf00-f990-4172-bd30-92595e2f4a02'), UUID_TO_BIN('8aaf60ec-ca7e-4dd2-b9b5-a60e315d65ff')),
  (UUID_TO_BIN('4d50cf00-f990-4172-bd30-92595e2f4a02'), UUID_TO_BIN('e728a4e8-19fc-4664-813c-2a6a6b23263f')),
  (UUID_TO_BIN('4d50cf00-f990-4172-bd30-92595e2f4a02'), UUID_TO_BIN('bad07f7f-49b5-429e-9f21-2d1ad7a3ca2f')),
  (UUID_TO_BIN('4d50cf00-f990-4172-bd30-92595e2f4a02'), UUID_TO_BIN('38f32558-0254-41a7-b93e-67ccb53038fb'));

-- Marketing permissions
INSERT INTO role_permissions (role_id, permission_id) VALUES
  (UUID_TO_BIN('d7317200-5f5c-47a9-aa5f-023dd9fccd3a'), UUID_TO_BIN('cf29ff1d-0b88-4fa0-8339-70f704461537')),
  (UUID_TO_BIN('d7317200-5f5c-47a9-aa5f-023dd9fccd3a'), UUID_TO_BIN('1e61a03f-496c-4d8b-851f-98ab23deee8a'));

-- Assists permissions
INSERT INTO role_permissions (role_id, permission_id) VALUES
  (UUID_TO_BIN('09b2b833-3303-4b45-848a-883b5e66faa0'), UUID_TO_BIN('8aaf60ec-ca7e-4dd2-b9b5-a60e315d65ff')),
  (UUID_TO_BIN('09b2b833-3303-4b45-848a-883b5e66faa0'), UUID_TO_BIN('e728a4e8-19fc-4664-813c-2a6a6b23263f')),
  (UUID_TO_BIN('09b2b833-3303-4b45-848a-883b5e66faa0'), UUID_TO_BIN('38f32558-0254-41a7-b93e-67ccb53038fb'));

-- Seed a default super user with Super Admin role
INSERT INTO users_profile (user_id, username, email) VALUES
  (UUID_TO_BIN('11111111-1111-1111-1111-111111111111'), 'superuser', 'super@sbsp.local');

INSERT INTO user_roles (user_id, role_id) VALUES
  (UUID_TO_BIN('11111111-1111-1111-1111-111111111111'), UUID_TO_BIN('c3908054-dc02-43c6-8eca-969b7da54ea4'));

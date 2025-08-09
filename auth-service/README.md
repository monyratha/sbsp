# Auth Service

Authentication microservice for the SBSP platform.

## Finished Endpoints

| Method | Path | Description |
| ------ | ---- | ----------- |
| `POST` | `/auth/register` | Register a new user account. |
| `POST` | `/auth/login` | Authenticate and return access and refresh tokens. |
| `GET`  | `/auth/me` | Retrieve information about the current user. |
| `POST` | `/auth/refresh` | Rotate a refresh token and issue a new access token. |
| `POST` | `/auth/logout` | Revoke the supplied refresh token. |
| `POST` | `/auth/change-password` | Change the current user's password. |

## Planned Endpoints

### Sessions / Devices

- `GET /auth/sessions` – List active logins.
- `POST /auth/sessions/revoke` – Revoke a specific session.
- `POST /auth/sessions/revoke-others` – Revoke all other sessions.
- `POST /auth/sessions/revoke-all` – Revoke every session.

### Password Recovery

- `POST /auth/password/forgot` – Request a password reset.
- `POST /auth/password/reset` – Complete the reset with a token.

### MFA (TOTP)

- `POST /auth/mfa/totp/setup` – Get secret, QR, and recovery codes.
- `POST /auth/mfa/totp/verify` – Activate TOTP.
- `POST /auth/mfa/totp/disable`
- `GET /auth/mfa/recovery-codes` – Rotate or download new codes.
- `POST /auth/login` – Will accept optional `{ otp }` when MFA is enabled.

### Keys / Discovery

- `GET /.well-known/jwks.json` – JWKS for token verification.
- `GET /.well-known/openid-configuration` *(optional)* – OIDC discovery.
- `POST /auth/introspect` *(optional)* – Online token introspection.

### Account State / Verification

- Existing `POST /auth/register` triggers verification when configured.
- `POST /auth/verify`
- `POST /auth/verify/resend`
- **Admin only:**
  - `POST /auth/account/lock`
  - `POST /auth/account/unlock`
  - `POST /auth/account/disable`
  - `POST /auth/account/enable`

### Token UX Variants

- `POST /auth/login-cookie` – Set `HttpOnly` cookies with access and refresh tokens.
- `POST /auth/refresh-cookie` – Rotate the refresh cookie and set new tokens.
- `POST /auth/logout-cookie` – Clear cookies and revoke the session.

### Auditing / Compliance

- `GET /auth/audit/my-events` – User-facing audit log.

### Health / Ops

- `GET /auth/health`
- `GET /auth/readiness`
- `GET /auth/version` – Returns commit and build time.


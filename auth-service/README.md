# Auth Service – Planned Endpoints

## 1) Sessions / Devices

* `GET /auth/sessions`
  **Auth:** Bearer
  **Returns:** `[{sessionId, createdAt, expiresAt, ip, userAgent, current}]`
  **Why:** Let users see active logins.

* `POST /auth/sessions/revoke`
  **Body:** `{ sessionId }`
  **Auth:** Bearer
  **Why:** Kill a single device.

* `POST /auth/sessions/revoke-others`
  **Auth:** Bearer
  **Why:** Keep current, log out everywhere else.

* `POST /auth/sessions/revoke-all`
  **Auth:** Bearer
  **Why:** Panic button (lost phone).

## 2) Password Recovery

* `POST /auth/password/forgot`
  **Body:** `{ usernameOrEmail }`
  **Notes:** rate-limited, generic 200 (don’t leak existence).
  **Why:** UX + support.

* `POST /auth/password/reset`
  **Body:** `{ resetToken, newPassword }`
  **Why:** Completes the flow.

## 3) MFA (TOTP) – Phase 1

* `POST /auth/mfa/totp/setup`
  **Auth:** Bearer
  **Returns:** `{ secret, qrSvg, recoveryCodes }` (secret pending until verify)

* `POST /auth/mfa/totp/verify`
  **Body:** `{ code }`
  **Auth:** Bearer
  **Why:** Activates TOTP.

* `POST /auth/mfa/totp/disable`
  **Auth:** Bearer

* `GET /auth/mfa/recovery-codes`
  **Auth:** Bearer
  **Why:** Rotate/download new codes.

* **Login extension:** `POST /auth/login` accepts optional `{ otp }` when MFA is enabled.

## 4) Keys / Discovery (make other services happy)

* `GET /.well-known/jwks.json`
  **Why:** Switch to RSA/EC signing; other services validate via JWKS.

* `GET /.well-known/openid-configuration` (optional)
  **Why:** OIDC-lite discovery for clients.

* `POST /auth/introspect` (optional)
  **Body:** `{ token }` → `{ active, sub, exp, scope }`
  **Why:** Gateways that prefer online checks.

## 5) Account State / Verification

* `POST /auth/register` → (already) trigger verification when configured
* `POST /auth/verify`
  **Body:** `{ code }`
* `POST /auth/verify/resend`
  **Body:** `{ destination }`
* **Admin-only:**

    * `POST /auth/account/lock` `{ userId }`
    * `POST /auth/account/unlock` `{ userId }`
    * `POST /auth/account/disable` / `enable` `{ userId }`
      **Why:** Abuse handling + ops.

## 6) Token UX Variants (web-friendly)

* `POST /auth/login-cookie`
  **Sets:** `HttpOnly` cookies (`access`, `refresh`) with `SameSite=Strict|Lax`.
  **Why:** Safer for browsers (no JS access).

* `POST /auth/refresh-cookie`
  **Reads:** refresh cookie, rotates, sets new cookies.

* `POST /auth/logout-cookie`
  **Clears:** cookies + revokes current session.

## 7) Auditing / Compliance

* `GET /auth/audit/my-events`
  **Auth:** Bearer
  **Query:** `type`, `from`, `to`
  **Why:** User-facing audit (logins, refreshes, revokes, password changes).

## 8) Health / Ops (if not via Actuator already)

* `GET /auth/health` (liveness)
* `GET /auth/readiness`
* `GET /auth/version` → `{ commit, buildTime }`
  **Why:** SRE needs.

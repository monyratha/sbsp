# Auth Service

The auth service returns message keys instead of translated text in API responses. Consumers should translate these keys on the client side using their preferred i18n library.

## Response format

```json
{
  "code": 401,
  "messageKey": "auth.invalid.credentials",
  "data": null
}
```

### Validation error example

```json
{
  "code": 400,
  "messageKey": "validation.error",
  "data": null
}
```

### Translating message keys on the frontend

```javascript
import { t } from 'i18next';

fetch('/auth/login', options)
  .then(res => res.json())
  .then(body => {
    const message = t(body.messageKey);
    console.log(message);
  });
```

Define the message keys in your translation files:

```json
{
  "auth.invalid.credentials": "Invalid username or password",
  "auth.username.exists": "Username already exists",
  "auth.register.success": "Registration successful",
  "validation.error": "Please correct the highlighted fields."
}
```

This keeps the backend language-agnostic while allowing the frontend to localize messages for each user.

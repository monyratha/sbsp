# Repository Guidelines

This is a Java 21, Spring Boot 3 multi‑module Maven project. Services live under module folders (e.g., `auth-service`, `user-service`, `gateway-service`, `config-service`, `discovery-service`). Use these guidelines to contribute consistently and efficiently.

## Project Structure & Modules
- Root POM (`pom.xml`): parent, dependency management, shared test deps.
- Modules: each service has `src/main/java`, `src/main/resources`, and `src/test/java`.
- Config: `docker-compose.yml` for local stack (Zipkin, MySQL, services).
- Docs: `readme.md`, `dev-helper.md` for additional tips.

## Build, Test, and Run
- Build all: `mvn clean verify` (runs unit tests; JaCoCo reports in `target/site/jacoco`).
- Build one module: `mvn -pl auth-service -am clean verify`.
- Run a service locally: `mvn -pl gateway-service spring-boot:run`.
- Build container image (when profile present): `mvn -pl auth-service -Pbuild-image spring-boot:build-image`.
- Start stack with images: `docker-compose up -d` (uses `SPRING_PROFILES_ACTIVE=docker`).

## Coding Style & Naming
- Java conventions, 4‑space indentation, UTF‑8.
- Packages: `morning.com.services.<service>...`.
- Types: `PascalCase` for classes, `camelCase` for fields/methods, `UPPER_SNAKE_CASE` for constants.
- Spring stereotypes: `*Controller`, `*Service`, `*Repository`; DTOs and requests in `dto` packages.
- Prefer Lombok for boilerplate (e.g., `@Getter`, `@Builder`).

## Testing Guidelines
- Frameworks: JUnit 5 and Mockito (with `@ExtendWith(MockitoExtension.class)`).
- Naming: mirror class under test, e.g., `UserServiceTest` in the same package under `src/test/java`.
- Run tests: `mvn test` (root) or `mvn -pl user-service test` (module).
- Coverage: keep or improve existing coverage; check HTML report under `<module>/target/site/jacoco`.

## Commit & Pull Request Guidelines
- Commits: follow Conventional Commits, optionally scoped by module, e.g., `feat(auth-service): add refresh token rotation` or `refactor: simplify page response`.
- PRs: concise description, rationale, and linked issue; include commands to reproduce and any API changes (paths, payloads). Add screenshots or `curl` samples when useful.
- CI: CircleCI runs `mvn verify sonar:sonar`; ensure tests pass locally before opening PRs.

## Security & Configuration
- Do not commit secrets; use environment variables and `application.yml` overrides per profile.
- For Docker runs, verify local DB ports in `docker-compose.yml` and profiles in `application.yml`.


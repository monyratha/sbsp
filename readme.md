# Spring Boot Subscription Platform (SBSP)

A modular, microservices-based SaaS platform built with Spring Boot 3.5.4 and Spring Cloud 2023.x. This system allows users to register, manage their own subdomain-based sites, choose subscription plans, and access features based on role-based permissions.

---

## Project Modules

| Module             | Description | Status |
|--------------------|-------------|--------|
| `zipkin`     | Distributed tracing system that collects and visualizes timing spans across microservices | Available |
| `config-service`   | Centralized Spring Cloud Config Server (Git-backed) | Available |
| `discovery-service`| Eureka server for service discovery | Available |
| `auth-service`     | Handles user authentication (login, register, JWT issuance, password hashing) | Planned |
| `user-service`     | Manages users, profiles, roles, permissions, and referral/invite codes | Planned |
| `site-service`     | Manages customer sites (subdomains), ownership, and multi-tenant logic | Planned |
| `subscription-service` | Handles subscription plans, active plans, history, billing, etc. | Planned |
| `gateway-service`  | API Gateway using Spring Cloud Gateway + route-based authentication | Planned |
| `audit-log-service`| Tracks and stores all important user/system actions | Planned |
| `settings-service` | Platform-wide configuration and environment flags | Planned |

---

## Development Plan

### Phase 1: Infrastructure Setup
- [x] Create parent Maven project (`spring-boot-subscription-platform`)
- [x] Create zipkin
- [x] Create and register `config-service`
- [x] Add `discovery-service` (Eureka)

### Phase 2: Core Features
- [ ] Generate `auth-service` via Spring Initializr
- [ ] Configure Config Server and connect `auth-service`
- [ ] Implement login, register, password hashing, JWT generation
- [ ] Add role & permission model
- [ ] Implement global error handling, validation, Swagger

### Phase 3: User & Tenant Services
- [ ] Create `user-service` and link with `auth-service`
- [ ] Build `site-service` to manage subdomains & ownership
- [ ] Add user-site assignment logic
- [ ] Start `subscription-service` with plan models and history tracking

### Phase 4: Advanced Features
- [ ] Implement role-based access control with permissions
- [ ] Add audit logging and user actions
- [ ] Connect Prometheus/Grafana for observability
- [ ] Add centralized logging with ELK or Loki

---

## Tech Stack

- **Java 17**
- **Spring Boot 3.5.4**
- **Spring Cloud 2023.0.1**
- **Spring Security + JWT**
- **Spring Data JPA**
- **Eureka Service Discovery**
- **Spring Cloud Gateway**
- **Spring Cloud Config Server**
- **Docker / Docker Compose**
- **MySQL / PostgreSQL / Redis**
- **Prometheus + Grafana**
- **OpenAPI / Swagger**

---

## Folder Structure (Monorepo)

```azure
spring-boot-subscription-platform/
├── pom.xml                # Parent POM
├── auth-service/          # First service (login/register/jwt)
├── user-service/          # Planned: user management & RBAC
├── site-service/          # Planned: subdomain management
├── subscription-service/  # Planned: plan logic
├── config-service/        # Spring Cloud Config Server
├── discovery-service/     # Eureka server for service discovery
├── docker/                # Planned: Docker Compose setup
└── config-repo/           # Planned: Git-backed external config files
```


---

## Roles & Permissions Overview

| Role         | Description |
|--------------|-------------|
| `Admin`      | Full platform access |
| `Sub-admin`  | Manage subscriptions and customers |
| `Marketing`  | Promote via referral/invite codes |
| `Customer`   | Owns and manages sites, products, services |
| `Sub-user`   | Assists customer in site/product/category management |

---

## Goals

- Clean microservices separation
- Multi-tenant architecture using subdomains
- Secure, scalable, and extensible
- Production-ready with observability, logging, and CI/CD

---

## Author

Built and maintained by **Lucas** — a backend developer passionate about scalable SaaS, clean code, and great architecture.
### Installation

1. Clone the repository:
```bash
git clone https://github.com/monyratha/sbsp.git
cd sbsp
```

2. Build all services:
```bash
mvn clean package -Pbuild-image
```

3. Start the services using Docker Compose:
```bash
docker compose up -d
```

### Quick Start
1. Access the Discovery Service dashboard:
```
http://localhost:8061
```

2. Access the API Gateway:
```
http://localhost:8060
```

3. View API documentation:
```
http://localhost:8060/swagger-ui.html
```

### Deployment steps:
```bash
# Build all services
mvn clean package -Pbuild-image

# Start infrastructure
docker compose up -d config-service discovery-service

# Start business services
docker compose up -d employee-service department-service organization-service

# Start gateway
docker compose up -d gateway-service
```
# 🏦 Banking Microservices Platform

A production-ready microservices architecture for banking operations built with Spring Boot, Apache Kafka, and Netflix Eureka. This system provides a scalable, event-driven platform for handling user management, accounts, transactions, and notifications.

[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-latest-black.svg)](https://kafka.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)

## 📋 Table of Contents

- [Architecture](#-architecture)
- [Features](#-features)
- [Microservices](#-microservices)
- [Technologies](#-technologies)
- [Prerequisites](#-prerequisites)
- [Getting Started](#-getting-started)
- [API Documentation](#-api-documentation)
- [Configuration](#-configuration)
- [Monitoring](#-monitoring)
- [Contributing](#-contributing)

## 🏗 Architecture


## ✨ Features

### Core Banking Operations
- 💰 **Account Management**: Create, view, and manage bank accounts
- 💸 **Money Transfers**: Real-time money transfers between accounts
- 📊 **Transaction History**: Complete audit trail of all transactions
- 🔐 **JWT Authentication**: Secure API access with JWT tokens
- 📧 **Notifications**: Email and SMS notifications for transactions

### Technical Features
- ⚡ **Event-Driven Architecture**: Asynchronous processing with Kafka
- 🔄 **Service Discovery**: Dynamic service registration with Eureka
- 🛡️ **API Gateway**: Centralized routing and security
- 🔒 **Database Isolation**: Separate PostgreSQL databases per service
- 📝 **Database Migrations**: Flyway for version-controlled schema changes
- 🔁 **Load Balancing**: Client-side load balancing via Spring Cloud LoadBalancer
- 🎯 **Circuit Breaker**: Resilience with Spring Cloud Circuit Breaker (optional)

## 🎯 Microservices

### 1. **API Gateway** (Port 8080)
- **Purpose**: Entry point for all client requests
- **Features**:
  - JWT token validation
  - Request routing to microservices
  - Load balancing
  - CORS handling
- **Public Endpoints**:
  - `POST /api/users/auth/token` - User login
  - `POST /api/users/user` - User registration

### 2. **User Service** (Dynamic Port)
- **Purpose**: User authentication and management
- **Database**: PostgreSQL (Port 5433)
- **Endpoints**:
  - `POST /user/token` - Generate JWT token
  - `POST /user/user` - Create new user
  - `GET /user/{userId}` - Get user details

### 3. **Accounts Service** (Dynamic Port)
- **Purpose**: Bank account management and balance operations
- **Database**: PostgreSQL (Port 5434)
- **Endpoints**:
  - `POST /account` - Create account
  - `GET /account/{accountId}` - Get account details
  - `PUT /account/credit/{accountId}` - Credit account
  - `PUT /account/debit/{accountId}` - Debit account
- **Features**:
  - Pessimistic locking for concurrent transactions
  - Kafka event publishing for transaction results

### 4. **Transaction Service** (Dynamic Port)
- **Purpose**: Process money transfers between accounts
- **Database**: PostgreSQL (Port 5435)
- **Endpoints**:
  - `POST /transaction/transfer` - Initiate transfer
  - `GET /transaction/history/{userId}` - Transaction history
- **Features**:
  - Asynchronous transaction processing
  - Transaction status tracking (PENDING, COMPLETED, FAILED)
  - Kafka integration for event-driven updates

### 5. **Notification Service** (Dynamic Port)
- **Purpose**: Send notifications for transaction events
- **Features**:
  - Kafka consumer for transaction events
  - Email/SMS notification delivery
  - User and account detail fetching via Feign clients

### 6. **Eureka Server** (Port 8761)
- **Purpose**: Service discovery and registration
- **Features**:
  - Dynamic service registration
  - Health monitoring
  - Service instance tracking
- **Dashboard**: http://localhost:8761

## 🛠 Technologies

### Backend
- **Java 25**
- **Spring Boot 3.2.0**
- **Spring Cloud 2023.0.0**
- **Spring Security 6.x**
- **Spring Data JPA**
- **Spring Cloud Gateway MVC**

### Messaging & Discovery
- **Apache Kafka** (3 broker cluster)
- **Netflix Eureka** (Service Discovery)
- **OpenFeign** (Declarative REST Clients)

### Databases
- **PostgreSQL 15**
- **Flyway** (Database Migrations)

### Security
- **JWT (JSON Web Tokens)**
- **JJWT 0.12.5** (JWT Library)

### Build & Tools
- **Maven 3.x**
- **Docker & Docker Compose**

## 📦 Prerequisites

- **Java 25** (or Java 17+)
- **Maven 3.6+**
- **Docker & Docker Compose**
- **Git**

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/Woun-dex/banking-microservices.git
cd banking-microservices
```

### 2. Start Infrastructure Services

Start Kafka cluster and PostgreSQL databases:

```bash
docker-compose up -d
```

This will start:
- 3 Kafka brokers (ports 9092, 9093, 9094)
- 3 PostgreSQL databases (ports 5433, 5434, 5435)

### 3. Build All Services

```bash
mvn clean install
```

### 4. Start Services in Order

**Step 1**: Start Eureka Server
```bash
cd eureka-server
mvn spring-boot:run
```
Wait for Eureka dashboard: http://localhost:8761

**Step 2**: Start Core Services (in separate terminals)
```bash
# Terminal 1: User Service
cd user-service
mvn spring-boot:run

# Terminal 2: Accounts Service
cd accounts-service
mvn spring-boot:run

# Terminal 3: Transaction Service
cd transaction-service
mvn spring-boot:run

# Terminal 4: Notification Service
cd notification-service
mvn spring-boot:run
```

**Step 3**: Start API Gateway
```bash
cd api-gateway
mvn spring-boot:run
```

### 5. Verify Services

Check Eureka Dashboard: http://localhost:8761

You should see all services registered:
- `USER-SERVICE`
- `ACCOUNTS-SERVICE`
- `TRANSACTION-SERVICE`
- `NOTIFICATION-SERVICE`
- `API-GATEWAY`

## 📖 API Documentation

### Authentication

#### Register User
```http
POST http://localhost:8080/api/users/user
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securePassword123"
}
```

#### Login (Get JWT Token)
```http
POST http://localhost:8080/api/users/auth/token
Content-Type: application/json

{
  "username": "john_doe",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "userId": "123"
}
```

### Account Operations

#### Create Account
```http
POST http://localhost:8080/api/accounts/account
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "userId": "123",
  "initialBalance": 1000.00
}
```

#### Get Account Details
```http
GET http://localhost:8080/api/accounts/account/{accountId}
Authorization: Bearer YOUR_JWT_TOKEN
```

### Transaction Operations

#### Initiate Transfer
```http
POST http://localhost:8080/api/transactions/transaction/transfer
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "fromAccountId": "acc-123",
  "toAccountId": "acc-456",
  "amount": 250.00,
  "description": "Payment for services"
}
```

**Response (202 ACCEPTED):**
```json
{
  "transactionId": "txn-789",
  "status": "PENDING",
  "message": "Transaction is being processed"
}
```

#### Get Transaction History
```http
GET http://localhost:8080/api/transactions/transaction/history/{userId}
Authorization: Bearer YOUR_JWT_TOKEN
```

## ⚙️ Configuration

### JWT Secret Key
The JWT secret is configured in:
- `user-service/src/main/resources/application.properties`
- `api-gateway/src/main/resources/application.properties`

```properties
jwt.secret = a8f5e2b1c9d4a7e3f6b2c8d5e9f1a4b7c3d6e8f2a5b9c1d4e7f3a6b8c2d5e9f1
```

⚠️ **Security Note**: Change this to a unique secret in production!

### Database Configuration

Each service has its own PostgreSQL database:

**User Service:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/usersdb
spring.datasource.username=postgres
spring.datasource.password=woundex123
```

**Accounts Service:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5434/accountsdb
spring.datasource.username=postgres
spring.datasource.password=woundex123
```

**Transaction Service:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5435/txdb
spring.datasource.username=postgres
spring.datasource.password=woundex123
```

### Kafka Configuration

All services connect to Kafka on `localhost:9092` (primary broker).

```properties
spring.kafka.bootstrap-servers=localhost:9092
```

**Topics:**
- `transaction.created` - New transaction events
- `transactions.completed` - Successful transactions
- `transactions.failed` - Failed transactions

### Eureka Configuration

Services register with Eureka on `http://localhost:8761/eureka`.

```properties
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
```

## 📊 Monitoring

### Eureka Dashboard
Monitor service instances: http://localhost:8761

### Kafka Topics
View Kafka topics:
```bash
docker exec -it kafka-1 kafka-topics.sh --bootstrap-server localhost:9092 --list
```

View messages:
```bash
docker exec -it kafka-1 kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic transaction.created \
  --from-beginning
```

### Database Access
Connect to databases:
```bash
# User Database
psql -h localhost -p 5433 -U postgres -d usersdb

# Accounts Database
psql -h localhost -p 5434 -U postgres -d accountsdb

# Transactions Database
psql -h localhost -p 5435 -U postgres -d txdb
```

## 🔧 Troubleshooting

### Service Not Registering with Eureka
- Check Eureka Server is running on port 8761
- Verify `eureka.client.service-url.defaultZone` configuration
- Check for port conflicts

### Kafka Connection Issues
- Verify Kafka cluster is running: `docker ps | grep kafka`
- Check bootstrap servers configuration
- View Kafka logs: `docker logs kafka-1`

### JWT Authentication Fails
- Ensure JWT secret matches in both `user-service` and `api-gateway`
- Check token is passed in `Authorization: Bearer TOKEN` header
- Verify token hasn't expired (default: 1 hour)

### Database Connection Errors
- Check PostgreSQL containers: `docker ps | grep postgres`
- Verify database ports (5433, 5434, 5435) aren't in use
- Check credentials in `application.properties`

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👤 Author

**Woundex**
- GitHub: [@Woun-dex](https://github.com/Woun-dex)

## 🙏 Acknowledgments

- Spring Boot Team for the excellent framework
- Apache Kafka for reliable event streaming
- Netflix OSS for Eureka service discovery
- PostgreSQL community

---

**⭐ If you find this project useful, please consider giving it a star!**
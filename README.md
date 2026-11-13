# 💰 Expense & Budget Tracker

A full-stack microservices application for tracking personal income and expenses in Indian Rupees (₹).  
Built with **Spring Boot 3 + React** as a college project.

---

## 🏗 Architecture

```
Browser (React :5173)
       │
       ▼  HTTP /api/*
API Gateway (:8080)   ◄── validates JWT, adds X-User-Id header
       │
       ├──► user-service        (:8081) — register, login, JWT
       ├──► transaction-service (:8082) — CRUD, search, balance
       └──► budget-analytics-service (:8083) — budgets, analytics
                │
                └──► Feign → transaction-service (spending data)

All services register with Eureka (:8761)
```

---

## 📋 Prerequisites

| Tool | Version | Download |
|------|---------|----------|
| Java JDK | 17+ | https://adoptium.net |
| Apache Maven | 3.9+ | https://maven.apache.org |
| Node.js | 18+ | https://nodejs.org |
| Git | any | https://git-scm.com |

> **Note:** Tested on JDK 25.0.4. H2 in-memory database is used by default — no MySQL setup needed.

---

## 🚀 How to Start (Development Mode)

### Step 1 — Build all backend services

```powershell
# From the project root (expense/)
mvn clean package -DskipTests
```

### Step 2 — Start services IN ORDER (wait ~8 seconds between each)

Open **5 separate terminals** or run as background processes:

```powershell
# Terminal 1 — Eureka Service Registry (start this FIRST)
java -jar backend/discovery-server/target/discovery-server-1.0.0.jar

# Terminal 2 — API Gateway (wait for Eureka to be UP)
java -jar backend/api-gateway/target/api-gateway-1.0.0.jar

# Terminal 3 — User Service
java -jar backend/user-service/target/user-service-1.0.0.jar

# Terminal 4 — Transaction Service
java -jar backend/transaction-service/target/transaction-service-1.0.0.jar

# Terminal 5 — Budget Analytics Service
java -jar backend/budget-analytics-service/target/budget-analytics-service-1.0.0.jar
```

### Step 3 — Start the React frontend

```powershell
cd frontend
npm install       # first time only
npm run dev
```

Open **http://localhost:5173** in your browser.

---

## 🐳 Docker Compose (One-Command Start)

```powershell
# From project root
docker-compose up --build

# Frontend will be at http://localhost:3000
# API Gateway at    http://localhost:8080
```

---

## 🌐 Service Ports

| Service | Port | URL |
|---------|------|-----|
| Eureka Dashboard | 8761 | http://localhost:8761 |
| API Gateway | 8080 | http://localhost:8080 |
| User Service | 8081 | http://localhost:8081 |
| Transaction Service | 8082 | http://localhost:8082 |
| Budget Analytics | 8083 | http://localhost:8083 |
| React Frontend | 5173 | http://localhost:5173 |

---

## 📖 Swagger UI (API Documentation)

| Service | Swagger URL |
|---------|------------|
| User Service | http://localhost:8081/swagger-ui.html |
| Transaction Service | http://localhost:8082/swagger-ui.html |
| Budget Analytics | http://localhost:8083/swagger-ui.html |

---

## 🧪 Sample API Requests (curl)

### 1. Register a new user
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Adhiraj Sharma","email":"adhiraj@example.com","password":"password123"}'
```
Response includes a `token` — copy it for all subsequent requests.

### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"adhiraj@example.com","password":"password123"}'
```

### 3. Add an expense transaction
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{"type":"EXPENSE","amount":15000,"category":"Housing","description":"Monthly rent","date":"2026-11-01"}'
```

### 4. Add an income transaction
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{"type":"INCOME","amount":55000,"category":"Salary","description":"November salary","date":"2026-11-01"}'
```

### 5. Get transactions with filters
```bash
# All transactions
curl http://localhost:8080/api/transactions \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"

# Filter by type and month
curl "http://localhost:8080/api/transactions?type=EXPENSE&month=2026-11" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 6. Set a monthly budget
```bash
curl -X POST http://localhost:8080/api/budgets \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{"category":"Housing","budgetLimit":15000,"month":11,"year":2026}'
```

### 7. Get budgets with spending alerts
```bash
curl "http://localhost:8080/api/budgets?month=11&year=2026" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 8. Get dashboard summary (income, expense, balance)
```bash
curl http://localhost:8080/api/analytics/summary \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 9. Get yearly report
```bash
curl "http://localhost:8080/api/analytics/report?year=2026" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 10. Get spending prediction (3-month moving average)
```bash
curl http://localhost:8080/api/analytics/predict \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

## 🔐 Security

- Passwords are hashed using **BCrypt** (never stored in plain text)
- JWT tokens expire after **24 hours**
- The API Gateway validates the JWT on every request to protected routes
- `/api/auth/register` and `/api/auth/login` are public (no token needed)
- Each service only sees its own database; no cross-database queries

---

## 📁 Project Structure

```
expense/
├── backend/
│   ├── discovery-server/      # Eureka service registry
│   ├── api-gateway/           # Spring Cloud Gateway + JWT filter
│   ├── user-service/          # Register, login, JWT generation
│   ├── transaction-service/   # Transaction CRUD + search
│   └── budget-analytics-service/ # Budgets, alerts, analytics
├── frontend/                  # React + Vite + Tailwind app
├── sql/
│   └── seed-data.sql          # Demo data (MySQL format)
├── postman/
│   └── Expense_Tracker_API.postman_collection.json
├── docs/
│   └── project_report.pdf
├── docker-compose.yml
└── README.md
```

---

## 🛠 Troubleshooting

| Problem | Solution |
|---------|----------|
| "No servers available for USER-SERVICE" | Wait 30s after starting a service for Eureka to register it |
| 401 Unauthorized | Token expired or missing — login again |
| H2 data lost after restart | H2 is in-memory — re-register user and add data again |
| Port already in use | Kill the process: `netstat -ano | findstr :8081` then `taskkill /PID <id> /F` |
| Frontend 404 on API call | Check Vite proxy in `vite.config.js` and ensure gateway is running |
| Budget shows 0% spent | Add expense transactions matching the same category, month, year |

---

## 📚 Tech Stack Reference

| Component | Technology |
|-----------|-----------|
| Backend | Java 17, Spring Boot 3.3, Spring Cloud 2023 |
| Auth | Spring Security, JWT (io.jsonwebtoken) |
| Database | H2 (dev), MySQL 8 (prod) |
| Service Discovery | Netflix Eureka |
| API Gateway | Spring Cloud Gateway |
| Service-to-Service | OpenFeign |
| API Docs | springdoc-openapi (Swagger UI) |
| Testing | JUnit 5, Mockito |
| Frontend | React 18, Vite 6, Tailwind CSS v4 |
| Charts | Recharts |
| HTTP Client | Axios |
| Routing | React Router v7 |


# 🏢 Multi-Tenant Organization Management Service

**GitHub Repository:** REPLACE_WITH_YOUR_GITHUB_REPO_URL  

A modular Spring Boot application implementing a multi-tenant architecture using MongoDB.  
Each organization receives its own dynamic MongoDB collection (`org_<organization_name>`), and admins authenticate securely via JWT.

This project demonstrates:
- Multi-tenant backend architecture  
- JWT-based authentication  
- Dynamic collection creation  
- Clean service-layer-driven design  
- Modular, class-based implementation  

---

## 🚀 Features

### 🔹 Organization Management
- Create new organizations dynamically  
- Each org gets its own collection: `org_<organization_name>`  
- Retrieve organization metadata from master DB  
- Update organization metadata + migrate documents  
- Delete organization + remove its collection  

### 🔹 Admin Authentication
- Secure admin login  
- JWT token generation  
- Password hashing using BCrypt  
- Token embeds:
  - `adminId`
  - `orgId`
  - `email`

### 🔹 Master Database (MongoDB)
Stores:
- Organization metadata  
- Admin user credentials  
- Dynamic collection names  
- Admin–Org mapping  

---

## 🧱 High-Level Architecture Diagram


           ┌───────────────────────────────────┐
           │            Client / Postman        │
           └──────────────────────┬────────────┘
                                  │ REST API
                                  ▼
                       ┌─────────────────────────────┐
                       │      Spring Boot Backend     │
                       │                             │
                       │  Controllers  ──→  Services  │
                       │     │                 │      │
                       │     ▼                 ▼      │
                       │  Repositories (MongoTemplate)│
                       │                             │
                       │  Security Layer (JWT Filter) │
                       └──────────────┬──────────────┘
                                      │
                                      ▼
                     ┌────────────────────────────────┐
                     │ MongoDB Master DB              │
                     │ - organizations                │
                     │ - admins                       │
                     │ - org_<name> (dynamic per org) │
                     └────────────────────────────────┘


## 🛠️ How to Run the Application

1. Clone the Repository
git clone https://github.com/Chiru582004/Backend-intern-Assignment
cd <Backend-intern-assignment>


2. Configure MongoDB Connection

spring.data.mongodb.uri=mongodb://localhost:27017/multi_tenant_master

app.jwt.secret=ChangeThisToA32+CharacterSecureSecretKey
app.jwt.expirationMs=3600000
server.port=8080


3. Start MongoDB

brew tap mongodb/brew
brew install mongodb-community@6.0
brew services start mongodb-community@6.0


4. Build & Start the Application

mvn clean install
mvn spring-boot:run

Server runs on:
http://localhost:8080

📡 API Endpoints (Postman Guide)
1️⃣ Create Organization (Public)
POST /org/create

{
  "organization_name": "acme",
  "email": "admin@acme.com",
  "password": "Pass123"
}

2️⃣ Get Organization by Name (Public)
GET /org/get?organization_name=acme

3️⃣ Admin Login (Public)
POST /admin/login

{
  "email": "admin@acme.com",
  "password": "Pass123"
}


Sample Response:

{
  "token": "<jwt-token>",
  "orgId": "<mongo-id>"
}

4️⃣ Update Organization (Protected)

Header:

Authorization: Bearer <token>

PUT /org/update

{
  "organization_name": "acme",
  "new_organization_name": "acme_new",
  "email": "new_admin@acme.com",
  "password": "NewPass123"
}

5️⃣ Delete Organization (Protected)
DELETE /org/delete
Authorization: Bearer <token>

{
  "organization_name": "acme_new"
}

🧪 Testing Flow Summary

Create Organization → admin created, collection created

Login → receive JWT token

Use token for Update/Delete

Validate collection changes in MongoDB

🧠 Design Choices (Brief Notes)
1. Multi-Tenant Strategy
One master DB for metadata
One dynamic collection per org for isolation
Easiest model for low–medium scale multi-tenancy
Avoids cross-tenant data leakage

2. JWT Security
Stateless, scalable
Encodes tenant and admin identity inside token
Works well with microservices and load-balanced environments

4. Modular Class-Based Architecture
Controllers → input/output mapping
Service → business logic
Repo → persistence
Security → filtering & authentication
Promotes clean separation of concerns and testing

4. Trade-offs
Per-tenant collection model becomes expensive if tenants → thousands
MongoDB has namespace limits, but manageable for moderate tenancy
For large deployments → Prefer shared schema with tenantId field or database-per-tenant




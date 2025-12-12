# 🏢 Multi-Tenant Organization Management Service

GitHub Repository: https://github.com/Chiru582004/Backend-intern-Assignment

A modular Spring Boot application implementing a multi-tenant architecture using MongoDB.  
Each organization receives its own dynamic MongoDB collection, and admins authenticate securely via JWT.

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
- Each organizatioon gets its own collection: `org_<organization_name>`  
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



---

# 🛠️ How to Run the Application

### **1. Clone the Repository**
```sh
git clone https://github.com/Chiru582004/Backend-intern-Assignment
cd Backend-intern-Assignment
```

### **2. Configure MongoDB Connection**
```sh
 

spring.data.mongodb.uri=mongodb://localhost:27017/multi_tenant_master
app.jwt.secret=ChangeThisToA32+CharacterSecureSecretKey
app.jwt.expirationMs=3600000
server.port=8080
```


### **3. Start MongoDB**
```sh

brew tap mongodb/brew
brew install mongodb-community@6.0
brew services start mongodb-community@6.0
```

### **4. Build & Start the Application**
```sh

mvn clean install
mvn spring-boot:run
Server runs at:
http://localhost:8080
```


# 📡 API Endpoints (Postman Guide)

### **1️⃣ Create Organization (Public)**

POST /org/create
Body:
json
Copy code

```sh
{
  "organization_name": "acme",
  "email": "admin@acme.com",
  "password": "Pass123"
}
```
### **2️⃣ Get Organization by Name (Public)**
```sh
GET /org/get?organization_name=acme
```

### **3️⃣ Admin Login (Public)**
POST /admin/login

json
Copy code
```sh
{
  "email": "admin@acme.com",
  "password": "Pass123"
}
```
**Response:**

json
Copy code
```sh
{
  "token": "<jwt-token>",
  "orgId": "<mongo-id>"
}
```

### **4️⃣ Update Organization (Protected)**
PUT /org/update
Header:

makefile
Copy code
Authorization: Bearer <token>
Body:

json
Copy code
```sh
{
  "organization_name": "acme",
  "new_organization_name": "acme_new",
  "email": "new_admin@acme.com",
  "password": "NewPass123"
}
```
### **5️⃣ Delete Organization (Protected)**
DELETE /org/delete
Header:

makefile
Copy code
Authorization: Bearer <token>
Body:

json
Copy code
```sh
{
  "organization_name": "acme_new"
}
```
# 🧪 Testing Flow Summary
Create Organization → admin created + dynamic collection created

Login → receive JWT token

Use token for:

Update Organization

Delete Organization

Verify new collections in MongoDB

## 🧠 Design Choices (Brief Notes)
### **1. Multi-Tenant Strategy**

One master database storing metadata

One collection per organization

Clean separation of tenant data

Perfect for low–medium scale deployments

### **2. JWT Security**
Stateless, scalable authentication

Token carries tenant + admin identity

No server-side session storage needed

### **3. Modular Architecture**
Controller layer: request mapping

Service layer: core business logic

Repository layer: MongoDB interactions

Security layer: JWT authentication filter + config

Promotes maintainability and testability

### **4. Trade-offs**
Per-organization collections may become heavy if tenants → thousands

MongoDB namespace limits require planning

Alternatives for large scale:

Shared schema with tenantId

Database-per-tenant

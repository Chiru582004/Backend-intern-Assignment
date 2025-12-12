# 🏢 Multi-Tenant Organization Management Service  
A modular Spring Boot application that provides multi-tenant organization creation, admin authentication via JWT, and dynamic MongoDB collection management.

This project was built as part of an assignment demonstrating:
- Multi-tenant architecture  
- Secure JWT-based authentication  
- Dynamic MongoDB collection creation  
- Clean service-layer-driven design  
- Class-based modular structure  

---

## 🚀 Features

### **Organization Management**
- Create new organizations dynamically
- Automatically create MongoDB collections using pattern: `org_<organization_name>`
- Retrieve organization metadata from master DB
- Update organization metadata & migrate data
- Delete organization & remove corresponding collections

### **Admin Authentication**
- JWT-based login
- Admin credentials stored securely (BCrypt)
- Token carries:
  - `adminId`
  - `orgId`
  - `email`

### **Master Database**
Stores:
- Organization metadata
- Admin user details (hashed password)
- Dynamic collection names
- Created timestamps

---

## 🧱 Project Architecture (High-Level)



             ┌──────────────────────────────┐
             │        Client / Postman       │
             └──────────────┬───────────────┘
                            │ REST API
                            ▼
               ┌───────────────────────────┐
               │     Spring Boot App       │
               │                           │
               │  ┌─────────────────────┐  │
               │  │   Controllers       │  │
               │  └─────────┬──────────┘  │
               │            │              │
               │  ┌─────────▼──────────┐  │
               │  │  Services Layer     │  │
               │  └─────────┬──────────┘  │
               │            │              │
               │  ┌─────────▼──────────┐  │
               │  │ Repositories/Mongo │  │
               │  └─────────┬──────────┘  │
               │            │              │
               │  ┌─────────▼──────────┐  │
               │  │ MongoDB Master DB  │  │
               │  │ + Dynamic Collections│ │
               │  └─────────────────────┘  │
               │                           │
               │  Security Layer (JWT + Filters)
               └───────────────────────────┘





---

## 📦 Tech Stack

- **Java 17**
- **Spring Boot 3**
- **Spring Web**
- **Spring Security**
- **MongoDB**
- **JWT (jjwt)**
- **Lombok**
- **Maven**

---

## 🛠️ How to Run the Application

### **1. Clone the Repository**
```bash
git clone https://github.com/<your-username>/<your-repo>.git
cd <your-repo>


src/main/java/com/example/multitenant/
│
├── controller/
│   ├── OrgController.java
│   └── AuthController.java
│
├── service/
│   └── OrganizationService.java
│
├── repo/
│   ├── OrganizationRepository.java
│   └── AdminRepository.java
│
├── model/
│   ├── Organization.java
│   └── AdminUser.java
│
└── security/
    ├── SecurityConfig.java
    ├── JwtUtil.java
    └── JwtFilter.java

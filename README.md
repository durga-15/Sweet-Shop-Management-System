# Sweet Shop Management System - Incubyte Assessment

## Objectives

This project implements a full-stack Sweet Shop Management System that allows users to perform essential operations such as adding sweets to inventory, purchasing products, managing stock levels, and viewing available items. It is developed using **Java 17 with Spring Boot**, **React with TypeScript**, **PostgreSQL**, and follows **Test-Driven Development (TDD)** principles with **SOLID principles** and clean architecture patterns.

## Features

- **User Authentication**: Users can register and login with secure JWT-based authentication
- **Add Sweets**: Admins can add new sweets to the inventory with name, category, price, and quantity
- **Purchase Sweets**: Users can browse and purchase sweets from the available inventory
- **Manage Inventory**: Admins can update sweet details, delete sweets, and restock inventory
- **Search & Filter**: Users can search sweets by name, category, and price range
- **Role-Based Access Control**: Different permissions for regular users and admin users
- **Real-Time Updates**: Inventory updates in real-time across all users
- **Responsive UI**: Modern and intuitive user interface built with React and TypeScript

## Project Structure

```
Incubyte/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/incubyte/sweetshop/
│   │   │   │   ├── controller/        (API endpoints)
│   │   │   │   ├── service/           (Business logic - TDD)
│   │   │   │   ├── repository/        (Database operations)
│   │   │   │   ├── entity/            (JPA entities)
│   │   │   │   ├── dto/               (Data transfer objects)
│   │   │   │   ├── security/          (JWT & security - TDD)
│   │   │   │   └── util/              (Utility classes)
│   │   │   └── resources/             (Configuration files)
│   │   └── test/java/com/incubyte/sweetshop/
│   │       ├── service/               (Service layer unit tests)
│   │       ├── controller/            (Controller integration tests)
│   │       ├── repository/            (Repository tests)
│   │       └── security/              (Security tests)
│   └── pom.xml                        (Maven configuration)
├── frontend/
│   ├── src/
│   │   ├── components/                (React components)
│   │   ├── context/                   (Authentication context - TDD)
│   │   ├── services/                  (API communication - TDD)
│   │   ├── App.tsx                    (Main app component)
│   │   └── main.tsx                   (Entry point)
│   ├── src/tests/                     (Frontend test files)
│   ├── package.json                   (NPM dependencies)
│   ├── vite.config.ts                 (Vite configuration)
│   └── tsconfig.json                  (TypeScript configuration)
├── database-setup.sql                 (Database initialization script)
└── README.md                          (
```

## Requirements

- **Java 17** or higher
- **Maven 3.8+**
- **PostgreSQL 12+** (or Docker for containerized setup)
- **Node.js 18+**
- **npm 9+**
- **Git**

## Setup Instructions

### 1. Clone the Repository

Clone the repository to your local machine:

```bash
git clone https://github.com/durga-15/Sweet-Shop-Management-System.git
```

### 2. Database Setup

#### Using PostgreSQL Directly

1. Install PostgreSQL from https://www.postgresql.org/download/
2. Create the database:
   ```bash
   psql -U postgres
   CREATE DATABASE sweet_shop;
   ```
3. Run the SQL setup script:
   ```bash
   psql -U postgres -d sweet_shop -f database-setup.sql
   ```

### 3. Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Ensure Java 17 is Configured:
   - Go to `File → Project Structure → Project` (in IntelliJ IDEA)
   - Set the Project SDK to Java 21
   - Ensure the Project language level is set to 21 (Preview)

3. Configure Database Connection:
   - Open `backend/src/main/resources/application.properties`
   - Update the database credentials:
     ```properties
     spring.datasource.url=jdbc:postgresql://localhost:5432/sweet_shop
     spring.datasource.username=postgres
     spring.datasource.password=your_password
     ```

4. Build and Run the Backend:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
   - The backend API will be available at `http://localhost:8081`

### 4. Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd ../frontend
   ```

2. Install Dependencies:
   ```bash
   npm install
   ```

3. Verify Backend URL:
   - Check `frontend/src/services/api.ts`
   - Ensure `API_BASE_URL = 'http://localhost:8081/api'`

4. Run the Frontend:
   ```bash
   npm run dev
   ```

## API Endpoints

### Authentication Endpoints
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/register-admin` - Register a new admin (admin only)
- `POST /api/auth/login` - Login user

### Sweets Endpoints
- `GET /api/sweets` - Get all available sweets
- `GET /api/sweets/search?name=&category=&minPrice=&maxPrice=` - Search sweets
- `POST /api/sweets` - Add new sweet (admin only)
- `PUT /api/sweets/:id` - Update sweet details (admin only)
- `DELETE /api/sweets/:id` - Delete sweet (admin only)
- `POST /api/sweets/:id/purchase` - Purchase sweet
- `POST /api/sweets/:id/restock` - Restock sweet (admin only)

## Technology Stack

### Backend
- **Framework**: Spring Boot 3
- **Language**: Java 17
- **Build Tool**: Maven
- **Database**: PostgreSQL
- **ORM**: Hibernate/JPA
- **Security**: Spring Security with JWT
- **Password Encryption**: BCrypt

### Frontend
- **Framework**: React 18
- **Language**: TypeScript
- **Build Tool**: Vite
- **State Management**: React Context API
- **HTTP Client**: Axios
- **Routing**: React Router v6
- **Styling**: CSS

## Architecture

The project follows a **3-tier architecture pattern**:

1. **Presentation Tier**: React frontend with components, routing, and user interface
2. **Application Tier**: Spring Boot backend with controllers, services, and business logic
3. **Data Tier**: PostgreSQL database with JPA repositories

## Authentication & Security

- **JWT Tokens**: Secure token-based authentication with 24-hour expiration
- **Password Encryption**: BCrypt algorithm for password hashing
- **Role-Based Access Control**: USER and ADMIN roles with different permissions
- **CORS Configuration**: Configured for frontend domain
- **Interceptors**: Automatic JWT token injection in all API requests

## My AI Usage

This section documents my comprehensive use of AI tools throughout the development process, ensuring transparency and responsible AI usage.


### How I Used AI Tools

#### 1. Project Initialization and Architecture Planning
- **ChatGPT**: Brainstormed the 3-tier architecture design, API endpoint structures, and database schema design. Discussed best practices for JWT authentication, authorization patterns, and TDD methodology
- **Claude**: Reviewed architectural decisions, provided suggestions for improving code organization, SOLID principles implementation, and TDD test structure

#### 2. Test-Driven Development Setup (TDD)
- **ChatGPT**: Explained TDD principles (Red-Green-Refactor cycle), discussed test structure, and suggested testing frameworks (JUnit 5, Mockito, Spring Boot Test)
- **Claude**: Reviewed test cases for completeness, suggested improvements to test coverage, and validated TDD implementation

#### 3. Backend Development (Java/Spring Boot)

**Controller Layer**:
- **ChatGPT**: Helped design RESTful API endpoints following REST conventions, discussed proper HTTP status codes and error responses

**Service Layer**: 
- **ChatGPT**:Generated service method implementations based on repository interfaces, suggested business logic patterns for CRUD operations. Provided guidance on service layer design patterns, transaction management, and separation of concerns
- **Claude**: Reviewed service implementations for code quality, suggested improvements to error handling and business logic flow

**Security Implementation**:
- **ChatGPT**: Explained JWT best practices, token expiration strategies, and role-based access control implementation
- **Claude**: Reviewed security code for vulnerabilities, suggested enhancements to password hashing and token management

#### 4. Frontend Development (React/TypeScript)

**API Integration**:

- **ChatGPT**: Generated Axios configuration, request/response interceptors, and API service layer. Provided guidance on error handling, request/response transformation, and token injection patterns
- **Claude**: Suggested improvements to API service architecture and error boundary implementation

#### 5. Bug Fixing and Debugging

- **ChatGPT**: Explained error causes, suggested debugging approaches, and alternative solutions
- **Claude**: Performed code review for identified issues, suggested refactoring to prevent similar bugs

### AI Impact on Workflow

AI tools significantly enhanced development velocity while maintaining code quality through careful review and customization. The key to responsible AI usage was treating suggestions as starting points rather than final solutions, maintaining critical evaluation, and ensuring thorough testing. AI was particularly valuable for accelerating boilerplate generation and test setup, allowing more focus on business logic and architecture design. This project demonstrates how AI can be a valuable development partner when used thoughtfully and responsibly, especially in a TDD-driven environment.


## Clarification

This project implements a complete Sweet Shop Management System with a running server and API endpoints. The system includes:
- Full-stack implementation with backend API and frontend UI
- User authentication and authorization
- Complete CRUD operations for sweets inventory
- Purchase and inventory management features
- Database persistence with PostgreSQL

## License

This project is created as an Incubyte Assessment.

---

**Happy Coding! 🎉**


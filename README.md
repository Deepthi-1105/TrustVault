# TrustVault — Secure Document Management

TrustVault is a web-based document management application built with Java Spring Boot and PostgreSQL. It aims to provide secure document storage, controlled sharing, and an audit trail of document-related activities.

## Project Overview

Managing sensitive documents requires secure storage, appropriate access permissions, and accountability. TrustVault is designed to address these needs through a centralized document management platform.

## Technologies Used

- **Backend:** Java, Spring Boot
- **Frontend:** HTML, CSS, Thymeleaf
- **Database:** PostgreSQL
- **Data Access:** Spring Data JPA, Hibernate
- **Build Tool:** Maven
- **Version Control:** Git and GitHub

## Current Features

- Web-based homepage and document dashboard
- Document metadata management
- Document titles and descriptions
- User and document entity structure
- PostgreSQL database integration
- Initial audit logging for document-related actions

## Planned Security Features

- Secure PDF and TXT file uploads
- Private file storage
- Authentication and authorization
- Permission-based document sharing
- Document access revocation
- Secure document downloads
- Comprehensive audit trails
- Automated security testing

## Project Structure

```text
trustvault/
├── src/
│   └── main/
│       ├── java/com/trustvault/trustvault/
│       │   ├── entity/
│       │   ├── repository/
│       │   ├── controller/
│       │   └── TrustvaultApplication.java
│       └── resources/
│           ├── templates/
│           └── application.properties
├── pom.xml
└── README.md
```

*Note: The structure above illustrates the intended organization. Some folders and features may still be under development.*

## Prerequisites

- Java 17 or a compatible JDK
- PostgreSQL
- Git

## How to Run

1. Clone the repository:

   ```bash
   git clone https://github.com/Deepthi-1105/TrustVault.git
   ```

2. Open the project directory:

   ```bash
   cd TrustVault
   ```

3. Configure your PostgreSQL database and application settings locally. Do not commit passwords or other secrets to GitHub.

4. Start the application on Windows using the Maven wrapper:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

5. Open your browser at:

   `http://localhost:8080`

## Project Goals

TrustVault aims to demonstrate backend development, database integration, secure document handling, access control, and security-focused software engineering.

## Author

**Deepthi Pagolu**

GitHub: [Deepthi-1105](https://github.com/Deepthi-1105)

---

This project is being developed as a secure document management and sharing application.

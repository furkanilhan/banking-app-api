# Banking Project

This project is a Spring Boot application that includes the basic functionalities of a banking application. Below are detailed instructions for setting up and configuring the project.

## Table of Contents

- [Getting Started](#getting-started)
- [Setup and Running](#setup-and-running)
- [Configuration](#configuration)
- [Dependencies](#dependencies)
- [Database Schema](#database-schema)
- [API Usage](#api-usage)

## Getting Started

This project is developed using Spring Boot 3.3.2, Spring Security 6.3.1, and Java 17. PostgreSQL is used as the database, and Liquibase is used for configuration management.

### Requirements

- Java 17
- Maven
- Docker
- Docker Compose

## Setup and Running

1. Make sure **Docker and Docker Compose** are installed.

2. Open a terminal in the project directory and run the following command:
   ```sh
   docker-compose up --build -d
   ```

3. Then run the following command:

   ```sh
   mvn clean install
   ```
   
4. Then you can start the project with the following command:
    ```sh
   mvn spring-boot:run
   ```

5. **Once the application is running, you can access it at http://localhost:8080/api.**


## Configuration

The following configurations are made in the src/main/resources/application.properties file:

- **Database Connection:**

   ```sh
   spring.datasource.url=jdbc:postgresql://localhost:5432/banking
   spring.datasource.username=postgres
   spring.datasource.password=postgres
   spring.datasource.driver-class-name=org.postgresql.Driver
  ```

- **JPA and Liquibase Settings:**

   ```sh
   spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.PostgreSQLDialect
   spring.jpa.hibernate.ddl-auto = none
   spring.liquibase.change-log=classpath:db/changelog/changelog.xml
   ```

- **JWT Settings:**

  ```sh
  banking.app.jwtSecret=furkanSecret
  banking.app.jwtExpirationMs=900000
  ```

## Dependencies

The project includes the following dependencies:

- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter Web
- Spring Boot Starter Validation
- PostgreSQL Driver
- Liquibase Core
- Lombok
- JJWT
- MapStruct
- H2 Database (for test)

## Database Schema

The project database schema is managed with Liquibase. Changes to the schema are defined in the src/main/resources/db/changelog/changelog.xml file.

## API Usage

You can use the app by importing the JSON file shared in `postman/banking.postman_collection.json` into your Postman app.

First, sign up for the Banking app. After signing in, get the token. Copy the token and go to the 
`Environments > Globals` section in the Postman app. Create a global variable with the name `furkanToken` and paste 
the token as the value. After that, you can use the other methods.

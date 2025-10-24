# java21-spring3-maven-reference

## Prerequisites

- Java 21
- Maven 3.8 or higher
- Spring Boot 3.x

## Run tests

### Start Redis server

```bash
docker run --name redis-session -p 6379:6379 redis
```

### Start PostgreSQL Server in Docker

```bash
docker run -d \
  --name spring-session-postgres \
  -e POSTGRES_DB=session_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  postgres:16
```

### Connect to PostgreSQL with `psql`

```bash
docker run -it --rm \
  --network host \
  postgres:16 \
  psql -h localhost -U postgres -d session_db
```

### Create `session_db` database

```sql
CREATE DATABASE session_db
    WITH
    OWNER = postgres
    TEMPLATE = postgres
    ENCODING = 'UTF-8'
    LC_COLLATE = 'C'
    LC_CTYPE = 'C'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;
```

### Create Spring Session tables

```sql
DROP TABLE IF EXISTS SPRING_SESSION_ATTRIBUTES;
DROP TABLE IF EXISTS SPRING_SESSION;

CREATE TABLE IF NOT EXISTS SPRING_SESSION
(
    PRIMARY_ID            CHAR(36) NOT NULL,
    SESSION_ID            CHAR(36) NOT NULL,
    CREATION_TIME         BIGINT   NOT NULL,
    LAST_ACCESS_TIME      BIGINT   NOT NULL,
    MAX_INACTIVE_INTERVAL INT      NOT NULL,
    EXPIRY_TIME           BIGINT   NOT NULL,
    PRINCIPAL_NAME        VARCHAR(100),
    CONSTRAINT SPRING_SESSION_PK PRIMARY KEY (PRIMARY_ID)
);

CREATE UNIQUE INDEX IF NOT EXISTS SPRING_SESSION_IX1 ON SPRING_SESSION (SESSION_ID);
CREATE INDEX IF NOT EXISTS SPRING_SESSION_IX2 ON SPRING_SESSION (EXPIRY_TIME);
CREATE INDEX IF NOT EXISTS SPRING_SESSION_IX3 ON SPRING_SESSION (PRINCIPAL_NAME);

CREATE TABLE IF NOT EXISTS SPRING_SESSION_ATTRIBUTES (
                                           SESSION_PRIMARY_ID CHAR(36) NOT NULL,
                                           ATTRIBUTE_NAME VARCHAR(200) NOT NULL,
                                           ATTRIBUTE_BYTES BYTEA NOT NULL,
                                           CONSTRAINT SPRING_SESSION_ATTRIBUTES_PK PRIMARY KEY (SESSION_PRIMARY_ID, ATTRIBUTE_NAME),
                                           CONSTRAINT SPRING_SESSION_ATTRIBUTES_FK FOREIGN KEY (SESSION_PRIMARY_ID) REFERENCES SPRING_SESSION(PRIMARY_ID) ON DELETE CASCADE
);
```

### Verify Spring Session via logs

```bash
docker logs spring-session-postgres
```

### Run tests with Maven

```bash
mvn clean install -Dspring.profiles.active=test
```

---

## Common Maven Commands

### Compile the project

```
mvn compile
```

### Install the project to the local repository

```
mvn clean install
```

### Run tests

```
mvn test
```

### Run all tests in a specific class

```
mvn -Dtest=TestClassName test
```
  
### Run multiple tests (comma-separated)

```
mvn -Dtest=TestClassName1,TestClassName2,TestClassName3#testMethodName test
```

### Run a specific test method

```
mvn -Dtest=TestClassName#testMethodName test
```

### Optional: to skip compiling first if already built, use

```
mvn -DskipCompile=true -Dtest=TestClassName#testMethodName test
```

or

```
mvn -Dtest=TestClassName#testMethodName surefire:test
```

### Package the application into a JAR file

```
mvn package
```

### Run application

```
mvn spring-boot:run
```

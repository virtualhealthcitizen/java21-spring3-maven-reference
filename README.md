# java21-spring3-maven-reference

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

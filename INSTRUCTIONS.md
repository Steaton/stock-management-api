# Stock Management API

I've setup the microservice to be production like. The following services are
included by default.

## Swagger UI
The documentation for the API can be found at:
- http://localhost:8080/swagger-ui.html

Cedentials: user/password

## Health Check
Actuator is installedand the health endpoint is exposed. at:
- http://localhost:8080/actuator/health

Credentials: user/password 

## H2 Database Console 
There is a user interface for the database installed.
This can be found at:
 - http://localhost:8080/h2-console

Credentials: sa/<blank>
 
## Technology Stack Used
- JDK8
- Junit5
- Spring 5
- Spring Testing
- Spring MVC
- Spring Data
- JPA
- Spring Security
 
## Project Structure
We are using a maven conventional structure, with 
integration tests located separately from the tests in
the <code>test-intergration</code> folder.
 
## Build Instructions
<code>mvn clean install spring-boot:run</code>

## Users Configured
All endpoint are accessible using either of the following 
pre-configured user credentials:
- user/password
- admin/admin

## Concurrency Approach
No static or shared data except the database. I have used
a unique constraint on the Stock->Exchange relationship. 
There is also a unique constraint on the names of both the
the Stock and the Exchange.
 
 
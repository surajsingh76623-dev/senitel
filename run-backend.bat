@echo off
echo ==============================================================================
echo Starting SENTINEL Backend (Spring Boot 3.3 / Java 21+)...
echo API will be available at http://localhost:8080/api
echo Swagger UI will be available at http://localhost:8080/swagger-ui.html
echo ==============================================================================

set "JAVA_HOME=C:\Program Files\Java\jdk-25"
set "PATH=C:\Program Files\Java\jdk-25\bin;C:\Users\Asus\AppData\Local\Programs\maven\apache-maven-3.9.9\bin;%PATH%"

cd /d "%~dp0backend"
mvn spring-boot:run

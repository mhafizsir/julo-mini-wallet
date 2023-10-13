# Julo Mini Wallet Application

This is a Spring Boot application written in Java 17. This README will guide you through setting up and running the
application, including reconfiguring properties based on the database you're using.

## Prerequisites

- Java 17
- Maven

## Steps to Run the Application

1. **Clone the Repository**

   ```
   git clone https://github.com/mhafizsir/julo-mini-wallet.git
   ```

2. **Navigate to the Project Directory**

   ```
   cd /path/to/project
   ```

3. **Database Configuration**

   Open the `application.properties` file located in `src/main/resources`.

    - **For PostgreSQL**

      ```properties
      spring.datasource.url=jdbc:postgresql://localhost:5432/yourdatabase
      spring.datasource.username=yourusername
      spring.datasource.password=yourpassword
      spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
      ```

    - **For MySQL**

      ```properties
      spring.datasource.url=jdbc:mysql://localhost:3306/yourdatabase
      spring.datasource.username=yourusername
      spring.datasource.password=yourpassword
      spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
      ```

   Update these properties based on your database setup.

4. **Build the Project**

   ```
   mvn clean install
   ```

5. **Run the Application**

   ```
   mvn spring-boot:run
   ```

   The application will start and will be accessible at `http://localhost:8080`.

## Support

For further assistance or questions, you can contact me at `mhafizsir@gmail.com`.

# Small Business Invoices

A Spring Boot application for managing invoices and payments for small businesses. This application provides RESTful APIs to handle invoice creation, management, and payment processing.

## Features

- Create, read, update, and delete invoices
- Track payment status of invoices
- Generate invoice reports
- RESTful API endpoints for integration
- Built with Spring Boot and JPA/Hibernate

## Prerequisites

- Java 17 or higher
- Maven 3.6.3 or higher
- MySQL 8.0 or higher (or your preferred database)

## Getting Started

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/small-business-invoices.git
   cd small-business-invoices
   ```

2. **Configure the database**
   - Create a MySQL database
   - Update `application.properties` with your database credentials

3. **Build the application**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

   The application will start on `http://localhost:8080`

## API Endpoints

### Invoices
- `GET /api/invoices` - Get all invoices
- `GET /api/invoices/{id}` - Get invoice by ID
- `POST /api/invoices` - Create a new invoice
- `PUT /api/invoices/{id}` - Update an existing invoice
- `DELETE /api/invoices/{id}` - Delete an invoice

### Payments
- `GET /api/payments` - Get all payments
- `POST /api/payments` - Process a payment
- `GET /api/payments/invoice/{invoiceId}` - Get payments for a specific invoice

## Project Structure

```
src/main/java/com/sazimtandabuzo/smallbusinessinvoices/
├── controller/       # REST controllers
├── model/            # Entity classes
├── repository/       # Data access layer
├── service/          # Business logic
└── SmallBusinessInvoicesApplication.java  # Application entry point
```

## Configuration

Configure your database and other settings in `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

# Server Configuration
server.port=8080
```

## Built With

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Maven](https://maven.apache.org/)
- [MySQL](https://www.mysql.com/)

## Contributing

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Spring Framework
- Open Source Community

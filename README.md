# Small Business Invoices

A Spring Boot application for managing invoices and payments for small businesses. This application provides RESTful APIs to handle invoice creation, management, and payment processing with JWT-based authentication.

## Features

- **User Authentication & Authorization**
  - JWT-based authentication
  - Role-based access control (Admin, User)
  - Secure password storage with BCrypt
  - Refresh token mechanism

- **Invoice Management**
  - Create, read, update, and delete invoices
  - Track payment status (PAID, UNPAID, OVERDUE)
  - Generate invoice reports
  - Calculate totals, taxes, and discounts

- **Technical Features**
  - RESTful API endpoints
  - Built with Spring Boot 3.x, JPA/Hibernate, and MySQL 8.0
  - Containerized with Docker
  - Deployable to AWS ECS with Fargate
  - CI/CD with GitHub Actions
  - API documentation with Swagger/OpenAPI
  - CORS configuration for web clients

## 🚀 Prerequisites

- Java 17 or higher
- Maven 3.6.3 or higher
- MySQL 8.0 or higher
- Lombok plugin for your IDE
- Docker 20.10+ and Docker Compose (for local development)
- AWS CLI (for deployment)
- An AWS account with ECS, ECR, and RDS access

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

## 🐳 Docker Setup

### Development with Docker Compose
```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/invoice_db?createDatabaseIfNotExist=true
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=password
      - JWT_SECRET=your-secret-key
    depends_on:
      - db
    networks:
      - app-network

  db:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: invoice_db
    volumes:
      - mysql_data:/var/lib/mysql
    ports:
      - "3306:3306"
    networks:
      - app-network

networks:
  app-network:
    driver: bridge

volumes:
  mysql_data:
```

### Production Build
```bash
# Build the application
mvn clean package -DskipTests

# Build Docker image
docker build -t small-business-invoices .

# Run with environment variables
docker run -d \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://your-rds-endpoint:3306/your_database \
  -e SPRING_DATASOURCE_USERNAME=your_username \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  -e JWT_SECRET=your-secure-jwt-secret \
  small-business-invoices
```

## ⚙️ Configuration

### Environment Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|:--------:|
| `SPRING_PROFILES_ACTIVE` | Active profiles (dev, prod) | `prod` | No |
| `SPRING_DATASOURCE_URL` | Database JDBC URL | - | Yes |
| `SPRING_DATASOURCE_USERNAME` | Database username | - | Yes |
| `SPRING_DATASOURCE_PASSWORD` | Database password | - | Yes |
| `JWT_SECRET` | Secret key for JWT token generation | - | Yes |
| `JWT_EXPIRATION_MS` | JWT token expiration time in ms | `86400000` (24h) | No |
| `CORS_ALLOWED_ORIGINS` | Comma-separated list of allowed origins | `http://localhost:4200` | No |
| `AWS_REGION` | AWS Region for services | `eu-north-1` | No |
| `AWS_ACCESS_KEY_ID` | AWS Access Key | - | For AWS services |
| `AWS_SECRET_ACCESS_KEY` | AWS Secret Key | - | For AWS services |

### Security Headers
By default, the application includes the following security headers:
- `X-Content-Type-Options: nosniff`
- `X-Frame-Options: DENY`
- `X-XSS-Protection: 1; mode=block`
- `Strict-Transport-Security: max-age=31536000 ; includeSubDomains`
- `Content-Security-Policy: default-src 'self'`

### CORS Configuration
CORS is configured to allow requests from specified origins. Update `CORS_ALLOWED_ORIGINS` to add your frontend URLs:
```properties
CORS_ALLOWED_ORIGINS=http://localhost:4200,https://your-production-domain.com
```

## Configuration

The application supports multiple configuration sources in the following order of precedence:
1. Environment variables
2. JVM system properties
3. Config files in `/app/config/` (when running in container)
4. Packaged application properties

### Security Headers
By default, the application includes the following security headers:
- `X-Content-Type-Options: nosniff`
- `X-Frame-Options: DENY`
- `X-XSS-Protection: 1; mode=block`
- `Strict-Transport-Security: max-age=31536000 ; includeSubDomains`

### Rate Limiting
API endpoints are rate limited to prevent abuse:
- 100 requests per minute per IP for public endpoints
- 1000 requests per minute per authenticated user

## 🔐 Authentication

### Authentication Endpoints
- `POST /api/auth/signin` - Authenticate user
  ```json
  {
    "username": "user",
    "password": "password"
  }
  ```

- `POST /api/auth/signup` - Register new user
  ```json
  {
    "username": "newuser",
    "email": "user@example.com",
    "password": "password123",
    "roles": ["user"]
  }
  ```

## 📡 API Endpoints

### Invoices
- `GET /api/invoices` - Get all invoices (requires authentication)
- `GET /api/invoices/{id}` - Get invoice by ID
- `POST /api/invoices` - Create a new invoice
- `PUT /api/invoices/{id}` - Update an existing invoice
- `DELETE /api/invoices/{id}` - Delete an invoice

### Users
- `GET /api/users` - Get all users (Admin only)
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user (Admin only)

### Payments
- `POST /payments` - Record a payment
- `GET /invoices/{invoiceId}/payments` - Get payments for an invoice
- `DELETE /payments/{id}` - Delete a payment

## Testing the Application

### 1. Using cURL

#### Create a New Invoice
```bash
curl -X POST http://localhost:8080/api/invoices \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "John Doe",
    "customerEmail": "john@example.com",
    "amount": 999.99,
    "dueDate": "2025-12-31",
    "description": "Website Development Services"
  }'
```

#### Record a Payment
```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "invoiceId": 1,
    "amount": 500.00,
    "paymentDate": "2025-09-29T10:00:00",
    "paymentMethod": "CREDIT_CARD",
    "notes": "First installment"
  }'
```

### 2. Using Postman

#### Create Invoice Request
- **Method**: POST
- **URL**: `http://localhost:8080/api/invoices`
- **Headers**: 
  - `Content-Type: application/json`
- **Body** (raw JSON):
  ```json
  {
    "customerName": "Acme Corp",
    "customerEmail": "billing@acmecorp.com",
    "amount": 2500.00,
    "dueDate": "2025-12-31",
    "description": "Monthly Retainer - October 2025"
  }
  ```

#### Record Payment Request
- **Method**: POST
- **URL**: `http://localhost:8080/api/payments`
- **Headers**: 
  - `Content-Type: application/json`
- **Body** (raw JSON):
  ```json
  {
    "invoiceId": 1,
    "amount": 1250.00,
    "paymentDate": "2025-09-29T14:30:00",
    "paymentMethod": "BANK_TRANSFER",
    "notes": "50% advance payment"
  }
  ```

### 3. Using Web Browser

You can use the Swagger UI for interactive testing:
1. Start the application
2. Open `http://localhost:8080/swagger-ui.html` in your browser
3. Explore the API documentation and test endpoints directly from the UI

### 4. Frontend Integration

If you have the frontend application running:
1. Ensure the backend is running on `http://localhost:8080`
2. Open the frontend application in your browser
3. Use the UI to create invoices and record payments
4. View payment history and invoice status in real-time

### Expected Responses

#### Successful Invoice Creation (HTTP 201)
```json
{
  "id": 1,
  "invoiceNumber": "INV-2025-0001",
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "amount": 999.99,
  "status": "PENDING",
  "dueDate": "2025-12-31",
  "createdAt": "2025-09-29T10:00:00"
}
```

#### Successful Payment (HTTP 200)
```json
{
  "id": 1,
  "invoiceId": 1,
  "amount": 500.00,
  "paymentDate": "2025-09-29T10:00:00",
  "paymentMethod": "CREDIT_CARD",
  "notes": "First installment"
}
```

#### Validation Error (HTTP 400)
```json
{
  "success": false,
  "message": "Validation failed",
  "status": 400,
  "errors": {
    "customerEmail": ["must be a well-formed email address"],
    "amount": ["must be greater than 0"]
  }
}
```

#### Not Found Error (HTTP 404)
```json
{
  "timestamp": "2025-09-29T10:05:00",
  "status": 404,
  "error": "Not Found",
  "message": "Invoice not found with id: 999"
}
```

## Testing Payment Status Updates

### Test Cases for Payment Status Transitions

1. **Initial Invoice Creation**
   - **Action**: Create a new invoice
   - **Expected Status**: `PENDING` (if due date is in future) or `OVERDUE` (if due date is in past)
   - **Verification**: Check invoice status after creation

2. **First Payment (Partial)**
   ```http
   POST /api/payments
   Content-Type: application/json
   
   {
     "invoiceId": 1,
     "amount": 50.00,
     "paymentDate": "2025-09-29T10:00:00",
     "paymentMethod": "CREDIT_CARD"
   }
   ```
   - **Expected Status**: `PARTIALLY_PAID` or `PARTIALLY_PAID_OVERDUE`

3. **Full Payment**
   ```http
   POST /api/payments
   Content-Type: application/json
   
   {
     "invoiceId": 1,
     "amount": 150.00,
     "paymentDate": "2025-09-29T11:00:00",
     "paymentMethod": "BANK_TRANSFER"
   }
   ```
   - **Expected Status**: `PAID`
   - **Verification**: No further payments should change the status from `PAID`

4. **Overdue Invoice**
   - Create an invoice with a past due date
   - **Expected Status**: `OVERDUE`
   - Make a partial payment
   - **Expected Status**: `PARTIALLY_PAID_OVERDUE`

5. **Payment Deletion**
   ```http
   DELETE /api/payments/1
   ```
   - **Expected**: Status should revert based on remaining payments
   - If no payments remain, status should go back to `PENDING` or `OVERDUE`

### Automated Testing

You can use the following cURL commands to test the payment status flow:

```bash
# 1. Create a test invoice
INVOCE_ID=$(curl -X POST http://localhost:8080/api/invoices \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Test Customer","amount":200.00,"dueDate":"2025-12-31"}' | jq -r '.id')

echo "Created invoice ID: $INVOCE_ID"

# 2. Make partial payment
PAYMENT_ID=$(curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d "{\"invoiceId\":$INVOCE_ID,\"amount\":50.00,\"paymentDate\":\"2025-09-29T10:00:00\",\"paymentMethod\":\"CREDIT_CARD\"}" | jq -r '.id')

echo "Created payment ID: $PAYMENT_ID"

# 3. Check invoice status
curl -X GET "http://localhost:8080/api/invoices/$INVOCE_ID"

# 4. Delete payment (cleanup)
curl -X DELETE "http://localhost:8080/api/payments/$PAYMENT_ID"

# 5. Delete invoice (cleanup)
curl -X DELETE "http://localhost:8080/api/invoices/$INVOCE_ID"
```

### Testing Tips

1. **Test Validation**: 
   - Submit forms with missing or invalid data to see proper error messages
   - Try negative amounts, future payment dates, etc.

2. **Check Status Updates**: 
   - Verify status changes after each payment operation
   - Check that status is updated in real-time

3. **Test Edge Cases**: 
   - **Overpayment**: Paying more than the invoice amount is allowed and will mark the invoice as `PAID` (the system will track the full payment amount but won't process refunds for overpayments)
   - **Future/Past Dates**: Test with payment dates in the future and past
   - **Concurrent Payments**: Verify proper handling when multiple payments are processed simultaneously
   - **Partial Payments**: Test with multiple partial payments that sum to exactly the invoice amount
   - **Zero Amount Payments**: Ensure the system rejects payments with zero or negative amounts

4. **Check Database**: 
   - Verify payment records are correctly linked to invoices
   - Check that status changes are properly logged
   - Verify transaction consistency
- `POST /invoices` - Create a new invoice
- `PUT /invoices/{id}` - Update an existing invoice
- `DELETE /invoices/{id}` - Delete an invoice
- `GET /invoices/status/{status}` - Get invoices by status (PAID, UNPAID, OVERDUE)
- `GET /invoices/overdue` - Get all overdue invoices
- `PATCH /invoices/{id}/status` - Update invoice status
- `GET /invoices/total-outstanding` - Get total outstanding amount

### Health Check
- `GET /actuator/health` - Application health status
- `GET /actuator/info` - Application information
- `GET /actuator/metrics` - Application metrics

### Example Request (Create Invoice)
```http
POST /invoices
Content-Type: application/json

{
  "customerName": "John Doe",
  "customerEmail": "john.doe@example.com",
  "issueDate": "2025-09-26",
  "dueDate": "2025-10-26",
  "items": [
    {
      "description": "Web Development Services",
      "quantity": 10,
      "unitPrice": 100.00
    }
  ],
  "taxRate": 15.0,
  "discount": 0.0,
  "notes": "Thank you for your business!"
}
```

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

### Application Properties
Configure your settings in `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# Actuator Endpoints
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always

# Logging
logging.level.org.springframework=INFO
logging.level.com.sazimtandabuzo=DEBUG
```

### Production Properties (`application-prod.properties`)
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://your-rds-endpoint:3306/your_database?createDatabaseIfNotExist=true&autoReconnect=true&useSSL=false
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Hikari Connection Pool
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=30000
spring.datasource.hikari.max-lifetime=2000000

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# Transaction Management
spring.transaction.default-timeout=30s
spring.transaction.rollback-on-commit-failure=true

# Actuator Endpoints
management.endpoints.web.base-path=/actuator
management.endpoint.health.show-details=always
management.health.db.enabled=true
management.health.defaults.enabled=true

# Logging
logging.level.org.springframework=INFO
logging.level.com.sazimtandabuzo=DEBUG
logging.file.name=app.log
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
```

## Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | Database connection URL | `jdbc:mysql://localhost:3306/your_db` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `dbuser` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `dbpassword` |
| `SERVER_PORT` | Application server port | `8080` |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `prod` |

## Monitoring and Maintenance

### Health Checks
- The application exposes health check endpoints at `/actuator/health`
- Configure your load balancer to use this endpoint for health checks

### Logs
- Application logs are written to `app.log` in the working directory
- In ECS, logs are streamed to CloudWatch Logs

### Metrics
- Application metrics are available at `/actuator/metrics`
- Integrate with Prometheus or AWS CloudWatch for monitoring

## Troubleshooting

### Common Issues
1. **Database Connection Issues**
   - Verify database is running and accessible
   - Check database credentials and permissions
   - Ensure the database user has proper privileges

2. **Health Check Failures**
   - Verify the application is running on the correct port
   - Check application logs for errors
   - Ensure the health check path is correct in the load balancer

3. **Deployment Issues**
   - Check ECS service events for errors
   - Verify task definition and container configuration
   - Ensure the container has proper permissions to access ECR

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Deployment

### Prerequisites
- Java 17 or higher
- Maven 3.6.3 or higher
- Docker (for containerization)
- AWS Account (for cloud deployment)

### Local Development
1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/small-business-invoices.git
   cd small-business-invoices
   ```

2. **Configure the database**
   - Create a MySQL database
   - Update `src/main/resources/application.properties` with your database credentials

3. **Build and run**
   ```bash
   mvn spring-boot:run
   ```
   The application will be available at `http://localhost:8080`

### Docker
1. **Build the Docker image**
   ```bash
   ./docker-build.sh
   ```

2. **Run the container**
   ```bash
   docker run -p 8080:8080 small-business-invoices:latest
   ```

## AWS ECS Deployment Guide

### Prerequisites
- AWS CLI configured with appropriate permissions
- Docker installed and running
- ECR repository created for the application
- VPC with public subnets and internet gateway
- Security group allowing inbound traffic on port 80
- RDS MySQL database (or other compatible database)

### Prerequisites
- AWS Free Tier account (sign up at [AWS Free Tier](https://aws.amazon.com/free/))
- AWS CLI installed and configured with your credentials
- Git installed on your local machine
- Basic knowledge of AWS services

### 1. Set up ECR Repository
```bash
aws ecr create-repository --repository-name small-business-invoices --region eu-north-1
```

### 2. Build and Push Docker Image
1. Authenticate Docker to your ECR registry:
   ```bash
   aws ecr get-login-password --region eu-north-1 | docker login --username AWS --password-stdin 810772959397.dkr.ecr.eu-north-1.amazonaws.com
   ```

2. Build and tag the image:
   ```bash
   ./docker-build.sh
   docker tag small-business-invoices:latest 810772959397.dkr.ecr.eu-north-1.amazonaws.com/small-business-invoices:latest
   ```

3. Push the image to ECR:
   ```bash
   docker push 810772959397.dkr.ecr.eu-north-1.amazonaws.com/small-business-invoices:latest
   ```

### 3. Set up ECS Cluster and Service
1. Create an ECS cluster:
   ```bash
   aws ecs create-cluster --cluster-name small-business-cluster --region eu-north-1
   ```

2. Create a task definition (see `ecs/task-definition.json` for reference)

3. Update the task definition with your ECR image URI and database configuration

4. Create the ECS service:
   ```bash
   aws ecs create-service \
     --cluster small-business-cluster \
     --service-name small-business-invoices-service \
     --task-definition small-business-invoices:1 \
     --desired-count 1 \
     --launch-type FARGATE \
     --network-configuration "awsvpcConfiguration={subnets=[subnet-007a8c2be665983f2],securityGroups=[sg-035e28f552080156a],assignPublicIp=ENABLED}" \
     --region eu-north-1
   ```

### 4. Update and Deploy
1. After making changes, rebuild and push the Docker image
2. Create a new task definition revision
3. Update the service with the new task definition:
   ```bash
   ./ecs-deploy.sh
   ```
1. Log in to AWS Management Console
2. Navigate to EC2 service
3. Click "Launch Instance"
4. Choose "Amazon Linux 2 AMI (HVM), SSD Volume Type" (Free Tier eligible)
5. Select t2.micro instance type (Free Tier eligible)
6. Configure instance details (use defaults for Free Tier)
7. Add storage (8GB gp2 is Free Tier eligible)
8. Add tags (optional)
9. Configure Security Group:
   - Add Rule: SSH (port 22)
   - Add Rule: HTTP (port 80)
   - Add Rule: Custom TCP (port 8080)
10. Review and launch
11. Create a new key pair, download it, and keep it secure

### Step 2: Set up RDS Database
1. Navigate to RDS service
2. Click "Create database"
3. Choose "Standard Create" and "MySQL"
4. Select "Free tier" template
5. Configure settings:
   - DB instance identifier: `small-business-db`
   - Master username: `admin`
   - Master password: [create a strong password]
6. Choose db.t3.micro instance class (Free Tier eligible)
7. Configure storage (20GB is Free Tier eligible)
8. Enable public access (for demo purposes)
9. Create a new VPC security group
10. Set initial database name: `small_business`
11. Click "Create database"
12. Note the endpoint (hostname) of your RDS instance

### Step 3: Update Application Configuration
1. Update `src/main/resources/application-prod.properties`:
   ```properties
   # Database Configuration
   spring.datasource.url=jdbc:mysql://[RDS_ENDPOINT]:3306/small_business?createDatabaseIfNotExist=true
   spring.datasource.username=admin
   spring.datasource.password=[YOUR_DB_PASSWORD]
   spring.jpa.hibernate.ddl-auto=update
   
   # Server Configuration
   server.port=8080
   server.servlet.context-path=/api
   
   # Production settings
   spring.profiles.active=prod
   ```

### Step 4: Package the Application
```bash
mvn clean package -DskipTests
```

### Step 5: Deploy to EC2
1. Connect to your EC2 instance:
   ```bash
   chmod 400 your-key-pair.pem
   ssh -i "your-key-pair.pem" ec2-user@your-ec2-public-dns
   ```

2. Install Java 17 and Maven:
   ```bash
   sudo amazon-linux-extras install java-openjdk17 -y
   sudo wget https://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
   sudo sed -i 's/\$releasever/7/g' /etc/yum.repos.d/epel-apache-maven.repo
   sudo yum install -y apache-maven
   ```

3. Install and configure Nginx:
   ```bash
   sudo amazon-linux-extras install nginx1 -y
   sudo systemctl start nginx
   sudo systemctl enable nginx
   ```

4. Configure Nginx as reverse proxy:
   ```bash
   sudo nano /etc/nginx/conf.d/springboot.conf
   ```
   Add:
   ```nginx
   server {
       listen 80;
       server_name your-domain.com www.your-domain.com;
       
       location /api {
           proxy_pass http://localhost:8080;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_set_header X-Forwarded-Proto $scheme;
           proxy_set_header X-Forwarded-Port $server_port;
       }
   }
   ```
   Test and reload Nginx:
   ```bash
   sudo nginx -t
   sudo systemctl restart nginx
   ```

### Step 6: Set up SSL with Let's Encrypt (Optional but Recommended)
```bash
sudo yum install -y certbot python3-certbot-nginx
sudo certbot --nginx -d your-domain.com -d www.your-domain.com
```

### Step 7: Run the Application
1. Copy your JAR file to EC2 (from your local machine):
   ```bash
   scp -i "your-key-pair.pem" target/your-application.jar ec2-user@your-ec2-public-dns:/home/ec2-user/
   ```

2. On EC2, create a systemd service:
   ```bash
   sudo nano /etc/systemd/system/small-business.service
   ```
   Add:
   ```ini
   [Unit]
   Description=Small Business Invoices
   After=syslog.target
   
   [Service]
   User=ec2-user
   ExecStart=/usr/bin/java -jar /home/ec2-user/your-application.jar --spring.profiles.active=prod
   SuccessExitStatus=143
   
   [Install]
   WantedBy=multi-user.target
   ```

3. Start and enable the service:
   ```bash
   sudo systemctl daemon-reload
   sudo systemctl enable small-business
   sudo systemctl start small-business
   ```

### Step 8: Verify Deployment
- Check application logs: `journalctl -u small-business -f`
- Access your API at: `http://your-ec2-public-dns/api/`

## Built With

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Maven](https://maven.apache.org/)
- [MySQL](https://www.mysql.com/)
- [AWS EC2](https://aws.amazon.com/ec2/)
- [AWS RDS](https://aws.amazon.com/rds/)

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

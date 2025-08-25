# SwiftLogistics Setup Guide

## Overview
This guide will help you set up the complete SwiftLogistics middleware solution for development and testing.

## Prerequisites

### Required Software
1. **Java 17 or higher**
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
   - Verify: `java -version`

2. **Node.js 18 or higher**
   - Download from [nodejs.org](https://nodejs.org/)
   - Verify: `node --version` and `npm --version`

3. **Docker & Docker Compose**
   - Download from [docker.com](https://www.docker.com/products/docker-desktop/)
   - Verify: `docker --version` and `docker-compose --version`

4. **Git**
   - Download from [git-scm.com](https://git-scm.com/)
   - Verify: `git --version`

5. **Maven 3.6+ (Optional)**
   - Download from [maven.apache.org](https://maven.apache.org/)
   - Verify: `mvn --version`

## Quick Start with Docker

### 1. Clone and Setup
```bash
# Clone the repository
git clone <your-repo-url>
cd MiddleWare

# Make sure Docker is running
docker --version
```

### 2. Start All Services
```bash
# Navigate to docker directory
cd docker

# Start all services
docker-compose up -d

# Check if all services are running
docker-compose ps
```

### 3. Access Applications
- **Client Portal**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)
- **Mock Services**: 
  - CMS: http://localhost:8081
  - ROS: http://localhost:8082
  - WMS: http://localhost:8083

### 4. Test the System
1. Open the Client Portal: http://localhost:3000
2. Navigate to "New Order"
3. Fill in the order details and submit
4. Track the order using the generated tracking number

## Manual Development Setup

### 1. Database Setup
```bash
# Start PostgreSQL (using Docker)
docker run -d --name postgres \
  -e POSTGRES_DB=swiftlogistics \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  postgres:15
```

### 2. Message Broker Setup
```bash
# Start RabbitMQ (using Docker)
docker run -d --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3.12-management
```

### 3. Backend Setup
```bash
# Navigate to backend directory
cd backend

# Install dependencies and run
./mvnw clean install
./mvnw spring-boot:run

# Or if you have Maven installed
mvn clean install
mvn spring-boot:run
```

### 4. Client Portal Setup
```bash
# Navigate to client portal directory
cd client-portal

# Install dependencies
npm install

# Start development server
npm start
```

## Project Structure

```
MiddleWare/
├── backend/                    # Spring Boot middleware
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/swiftlogistics/middleware/
│   │       │       ├── config/           # Configuration classes
│   │       │       ├── controller/       # REST controllers
│   │       │       ├── dto/              # Data transfer objects
│   │       │       ├── integration/      # External system adapters
│   │       │       ├── model/            # Domain models
│   │       │       ├── repository/       # Data repositories
│   │       │       └── service/          # Business logic
│   │       └── resources/
│   │           └── application.properties
│   └── pom.xml
├── client-portal/              # React web application
│   ├── public/
│   ├── src/
│   │   ├── components/         # React components
│   │   ├── App.js
│   │   └── index.js
│   └── package.json
├── driver-app/                 # React Native mobile app
├── docs/                       # Documentation
│   └── Architecture-Documentation.md
├── docker/                     # Docker configuration
│   ├── docker-compose.yml
│   └── mock-services/          # Mock external systems
└── README.md
```

## Configuration

### Environment Variables

#### Backend Configuration
```properties
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/swiftlogistics
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password

# RabbitMQ
SPRING_RABBITMQ_HOST=localhost
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=guest
SPRING_RABBITMQ_PASSWORD=guest

# External Systems
EXTERNAL_CMS_URL=http://localhost:8081/cms
EXTERNAL_ROS_URL=http://localhost:8082/ros
EXTERNAL_WMS_HOST=localhost
EXTERNAL_WMS_PORT=8083
```

#### Frontend Configuration
```javascript
// In client-portal/.env
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_WS_URL=http://localhost:8080
```

## API Documentation

### Key Endpoints

#### Orders API
- `POST /api/orders` - Create new order
- `GET /api/orders/{id}` - Get order by ID
- `GET /api/orders/client/{clientId}` - Get orders by client
- `GET /api/orders/tracking/{trackingNumber}` - Track order
- `PUT /api/orders/{id}/status` - Update order status

#### WebSocket Endpoints
- `/ws` - WebSocket connection endpoint
- `/topic/orders/{orderId}` - Order status updates
- `/topic/notifications/{clientId}` - Client notifications

## Testing

### Running Tests
```bash
# Backend tests
cd backend
./mvnw test

# Frontend tests
cd client-portal
npm test
```

### Manual Testing Scenarios

#### 1. Order Creation Flow
1. Open Client Portal
2. Create new order with sample data:
   - Recipient: John Doe
   - Address: 123 Main St, Colombo
   - Description: Test package
3. Verify order appears in "My Orders"
4. Check backend logs for integration calls

#### 2. Real-time Tracking
1. Create an order and note the tracking number
2. Open tracking page in another browser tab
3. Manually update order status via backend
4. Verify real-time updates appear

#### 3. Integration Testing
1. Check RabbitMQ management UI for message flow
2. Verify database entries in PostgreSQL
3. Monitor integration adapter logs

## Troubleshooting

### Common Issues

#### 1. Port Conflicts
```bash
# Check what's running on ports
netstat -tulpn | grep :8080
netstat -tulpn | grep :3000
netstat -tulpn | grep :5432

# Kill processes if needed
sudo kill -9 <PID>
```

#### 2. Database Connection Issues
```bash
# Check PostgreSQL is running
docker ps | grep postgres

# Connect to database
docker exec -it postgres psql -U postgres -d swiftlogistics
```

#### 3. RabbitMQ Issues
```bash
# Check RabbitMQ status
docker exec -it rabbitmq rabbitmqctl status

# Access management UI
open http://localhost:15672
```

#### 4. Frontend Build Issues
```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and reinstall
rm -rf node_modules
npm install
```

### Log Locations
- **Backend Logs**: Check console output or `logs/` directory
- **Frontend Logs**: Browser console (F12)
- **Docker Logs**: `docker-compose logs <service-name>`

## Development Workflow

### 1. Making Changes
```bash
# For backend changes
cd backend
# Make changes to Java files
./mvnw spring-boot:run  # Restart if needed

# For frontend changes
cd client-portal
# Make changes to React files
# Hot reload will automatically update
```

### 2. Adding New Features
1. Create feature branch: `git checkout -b feature/new-feature`
2. Implement changes in appropriate layers
3. Add tests for new functionality
4. Update documentation
5. Create pull request

### 3. Database Changes
```bash
# Backend will auto-create tables due to hibernate.ddl-auto=update
# For production, use migration scripts
```

## Production Deployment Notes

### 1. Environment Configuration
- Use environment-specific configuration files
- Secure database credentials
- Configure proper logging levels
- Set up monitoring and health checks

### 2. Security Considerations
- Enable HTTPS/TLS
- Configure proper CORS settings
- Implement authentication/authorization
- Regular security updates

### 3. Scalability
- Use load balancers for multiple backend instances
- Implement database connection pooling
- Configure message queue clustering
- Monitor performance metrics

## Support

### Getting Help
1. Check this documentation first
2. Look at error logs for specific issues
3. Consult the architecture documentation in `docs/`
4. Check common troubleshooting scenarios above

### Useful Commands
```bash
# View all running containers
docker ps

# View logs for specific service
docker-compose logs backend

# Restart specific service
docker-compose restart backend

# Stop all services
docker-compose down

# Clean up volumes (WARNING: destroys data)
docker-compose down -v
```

This completes the setup guide for the SwiftLogistics middleware solution!

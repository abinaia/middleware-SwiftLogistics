# SwiftLogistics Middleware Architecture Project

## Project Overview
This is a middleware architecture solution for SwiftLogistics - a delivery company that needs to integrate 3 separate systems:
- CMS (Client Management System) - SOAP/XML
- ROS (Route Optimization System) - REST/JSON  
- WMS (Warehouse Management System) - TCP/IP messaging

## Architecture Components
- **Backend**: Spring Boot middleware with message brokers
- **Client Portal**: React web application
- **Driver App**: React Native mobile application
- **Integration Layer**: Protocol adapters for SOAP, REST, and TCP/IP

## Project Structure
- `/backend` - Spring Boot middleware services
- `/client-portal` - React web application for clients
- `/driver-app` - React Native mobile application
- `/docs` - Architecture documentation and diagrams
- `/docker` - Containerization files

## Development Guidelines
- Use open-source technologies only
- Implement asynchronous message processing
- Ensure transaction consistency across systems
- Include security measures for all communications
- Design for scalability and resilience

## Checklist Progress
- [x] Project structure created
- [ ] Backend middleware implementation
- [ ] Client portal development  
- [ ] Driver app development
- [ ] Integration layer implementation
- [ ] Documentation and testing

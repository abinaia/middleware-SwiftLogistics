# SwiftLogistics Interface Access Guide

## 🌐 Client Portal - Web Interface

The Client Portal is a React.js web application where clients can submit orders and track deliveries.

### 🚀 Quick Start

#### Option 1: Using Docker (Recommended)
```bash
# Navigate to the docker directory
cd C:\Users\ACER\Downloads\MiddleWare\docker

# Start all services including the client portal
docker-compose up -d

# Wait for services to start (about 2-3 minutes)
# Check if services are running
docker-compose ps
```

**Access the Client Portal:**
- **URL**: http://localhost:3000
- **Status**: Available immediately after Docker starts

#### Option 2: Manual Setup
```bash
# Navigate to client portal directory
cd C:\Users\ACER\Downloads\MiddleWare\client-portal

# Install dependencies (first time only)
npm install

# Start the development server
npm start

# The application will automatically open at http://localhost:3000
```

### 📋 Client Portal Features

1. **Dashboard**
   - Overview of order statistics
   - Recent orders summary
   - Quick action buttons
   - System status indicators

2. **Create New Order**
   - Order form with recipient details
   - Package information
   - Delivery address
   - Real-time validation

3. **Order Management**
   - View all orders
   - Filter by status
   - Order details and timeline

4. **Real-time Tracking**
   - Track by order number or tracking ID
   - Live status updates
   - Delivery timeline
   - Estimated delivery times

### 🎯 How to Use Client Portal

1. **Access the Portal**: http://localhost:3000
2. **Navigate to "New Order"** from the top menu
3. **Fill out the order form**:
   - Recipient Name: John Doe
   - Phone: +94771234567
   - Address: 123 Main Street, Colombo 03, Sri Lanka
   - Package Description: Documents
   - Weight: 0.5 kg
4. **Submit the order** - you'll get an order number and tracking ID
5. **Track your order** using the tracking number

---

## 📱 Driver Mobile App - React Native

The Driver Mobile App is for delivery drivers to manage routes and mark deliveries.

### 🚀 Setup Options

#### Option 1: Web Preview (Easiest)
```bash
# Navigate to driver app directory
cd C:\Users\ACER\Downloads\MiddleWare\driver-app

# Install dependencies
npm install

# Install Expo CLI globally (if not already installed)
npm install -g @expo/cli

# Start the web version
npm run web
# or
expo start --web
```

**Access via Web Browser:**
- **URL**: http://localhost:19006
- **Note**: This runs the mobile app in a web browser for easy testing

#### Option 2: Mobile Device (Android/iOS)

**For Android:**
```bash
# Install Android Studio and set up Android SDK
# Enable Developer Options and USB Debugging on your Android device

# Connect your Android device via USB
# Navigate to driver app directory
cd C:\Users\ACER\Downloads\MiddleWare\driver-app

# Install dependencies
npm install

# Start Metro bundler
npm start

# In another terminal, run on Android
npm run android
```

**For iOS (Mac only):**
```bash
# Install Xcode from Mac App Store
# Navigate to driver app directory
cd C:\Users\ACER\Downloads\MiddleWare\driver-app

# Install dependencies
npm install

# Install iOS dependencies
cd ios && pod install && cd ..

# Run on iOS simulator
npm run ios
```

#### Option 3: Expo Go App (Recommended for Testing)
```bash
# Install Expo CLI
npm install -g @expo/cli

# Navigate to driver app
cd C:\Users\ACER\Downloads\MiddleWare\driver-app

# Start Expo development server
expo start

# Scan QR code with Expo Go app on your phone
# Download Expo Go from:
# - Google Play Store (Android)
# - App Store (iOS)
```

### 📱 Driver App Features

1. **Dashboard**
   - Driver information
   - Daily delivery statistics
   - Quick actions (Start Route, Emergency Alert)
   - Today's route summary

2. **Route Management**
   - Optimized delivery route
   - Map view with stops
   - Navigation integration
   - Route progress tracking

3. **Delivery Management**
   - List of assigned deliveries
   - Mark as delivered/failed
   - Capture signatures
   - Take delivery photos
   - Add delivery notes

4. **Real-time Updates**
   - Receive new delivery assignments
   - Route changes and updates
   - Emergency notifications

### 🎯 How to Use Driver App

1. **Access the app** via one of the setup options above
2. **Dashboard Tab**: View your daily statistics and driver info
3. **Route Tab**: See your optimized delivery route on map
4. **Deliveries Tab**: 
   - View list of packages to deliver
   - Tap on a delivery to see details
   - Mark as "Delivered" or "Failed"
   - Add notes and capture proof of delivery
5. **Profile Tab**: Manage driver settings and preferences

---

## 🔧 Troubleshooting

### Client Portal Issues

**Port 3000 already in use:**
```bash
# Kill process using port 3000
netstat -ano | findstr :3000
taskkill /PID <PID> /F

# Or start on different port
set PORT=3001 && npm start
```

**Dependencies not installing:**
```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and reinstall
rmdir /s node_modules
npm install
```

### Driver App Issues

**Metro bundler not starting:**
```bash
# Reset Metro cache
npx react-native start --reset-cache
```

**Android build failing:**
```bash
# Clean and rebuild
cd android
./gradlew clean
cd ..
npm run android
```

**Expo not working:**
```bash
# Update Expo CLI
npm install -g @expo/cli@latest

# Clear Expo cache
expo start -c
```

### Network Issues

**Backend not accessible:**
1. Make sure backend is running on port 8080
2. Check if Docker services are up: `docker-compose ps`
3. Verify API endpoint: http://localhost:8080/api/orders

**Real-time updates not working:**
1. Check WebSocket connection at: ws://localhost:8080/ws
2. Ensure RabbitMQ is running: http://localhost:15672

---

## 🎥 Demo Scenarios

### Complete Order Flow Demo

1. **Client Portal** (http://localhost:3000):
   - Create new order
   - Note the tracking number
   - Track the order status

2. **Backend Processing**:
   - Check logs for integration with CMS, ROS, WMS
   - Monitor RabbitMQ queues: http://localhost:15672

3. **Driver App**:
   - View the new delivery in deliveries list
   - Mark package as picked up
   - Complete the delivery

4. **Real-time Updates**:
   - Watch status changes in client portal
   - See notifications in driver app

### Access URLs Summary

| Interface | URL | Purpose |
|-----------|-----|---------|
| **Client Portal** | http://localhost:3000 | Web interface for clients |
| **Driver App (Web)** | http://localhost:19006 | Mobile app in browser |
| **Backend API** | http://localhost:8080/api | REST API endpoints |
| **RabbitMQ UI** | http://localhost:15672 | Message queue management |
| **API Documentation** | http://localhost:8080/swagger-ui.html | API docs (if enabled) |

Both interfaces are now ready to use! The Client Portal provides a full web experience for order management, while the Driver App offers a mobile-optimized interface for delivery operations.

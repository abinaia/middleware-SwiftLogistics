// API service for the client portal
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

class ApiService {
  // Orders API
  async createOrder(orderData) {
    try {
      const response = await fetch(`${API_BASE_URL}/orders`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(orderData)
      });
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      return await response.json();
    } catch (error) {
      // For demo, return mock data if API fails
      console.warn('API call failed, using mock data:', error);
      return this.createMockOrder(orderData);
    }
  }

  async getOrders(clientId) {
    try {
      console.log(`Attempting to fetch orders from: ${API_BASE_URL}/orders/client/${clientId}`);
      const response = await fetch(`${API_BASE_URL}/orders/client/${clientId}`);
      
      console.log(`Response status: ${response.status}`);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const data = await response.json();
      console.log('Successfully fetched orders from API:', data);
      return data;
    } catch (error) {
      // Return empty array instead of mock data to see real behavior
      console.error('API call failed:', error);
      return [];
    }
  }

  async trackOrder(trackingNumber) {
    try {
      const response = await fetch(`${API_BASE_URL}/orders/tracking/${trackingNumber}`);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      return await response.json();
    } catch (error) {
      // For demo, return mock data if API fails
      console.warn('API call failed, using mock data:', error);
      return this.getMockTrackingData(trackingNumber);
    }
  }

  async getDashboardStats(clientId) {
    try {
      const response = await fetch(`${API_BASE_URL}/dashboard/stats/${clientId}`);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      return await response.json();
    } catch (error) {
      // For demo, return mock data if API fails
      console.warn('API call failed, using mock data:', error);
      return this.getMockDashboardStats();
    }
  }

  // Mock data methods for demo purposes
  createMockOrder(orderData) {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          id: Math.floor(Math.random() * 1000),
          orderNumber: 'ORD-' + Date.now(),
          trackingNumber: 'TRK-' + Math.random().toString(36).substr(2, 8).toUpperCase(),
          status: 'SUBMITTED',
          ...orderData,
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString()
        });
      }, 1000);
    });
  }

  getMockOrders() {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve([
          {
            id: 1,
            orderNumber: 'ORD-1234567890',
            trackingNumber: 'TRK-ABC12345',
            recipientName: 'John Doe',
            deliveryAddress: '123 Main St, Colombo 03',
            status: 'OUT_FOR_DELIVERY',
            createdAt: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),
            updatedAt: new Date().toISOString()
          },
          {
            id: 2,
            orderNumber: 'ORD-1234567891',
            trackingNumber: 'TRK-DEF67890',
            recipientName: 'Jane Smith',
            deliveryAddress: '456 Oak Ave, Kandy',
            status: 'DELIVERED',
            createdAt: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000).toISOString(),
            updatedAt: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000).toISOString(),
            deliveredAt: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000).toISOString()
          },
          {
            id: 3,
            orderNumber: 'ORD-1234567892',
            trackingNumber: 'TRK-GHI11111',
            recipientName: 'Bob Johnson',
            deliveryAddress: '789 Pine Rd, Galle',
            status: 'IN_WAREHOUSE',
            createdAt: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000).toISOString(),
            updatedAt: new Date(Date.now() - 12 * 60 * 60 * 1000).toISOString()
          },
          {
            id: 4,
            orderNumber: 'ORD-1234567893',
            trackingNumber: 'TRK-JKL22222',
            recipientName: 'Alice Brown',
            deliveryAddress: '321 Cedar St, Negombo',
            status: 'PROCESSING',
            createdAt: new Date(Date.now() - 6 * 60 * 60 * 1000).toISOString(),
            updatedAt: new Date(Date.now() - 3 * 60 * 60 * 1000).toISOString()
          }
        ]);
      }, 500);
    });
  }

  getMockTrackingData(trackingNumber) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        // Simulate some tracking numbers not being found
        if (trackingNumber.includes('NOTFOUND')) {
          reject(new Error('Order not found'));
          return;
        }

        resolve({
          id: 1,
          orderNumber: 'ORD-1234567890',
          trackingNumber: trackingNumber,
          clientName: 'Demo Client',
          recipientName: 'John Doe',
          recipientPhone: '+94771234567',
          deliveryAddress: '123 Main Street, Colombo 03, Sri Lanka',
          status: 'OUT_FOR_DELIVERY',
          createdAt: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),
          updatedAt: new Date().toISOString(),
          deliveredAt: null,
          trackingHistory: [
            {
              status: 'SUBMITTED',
              timestamp: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),
              location: 'Online',
              description: 'Order submitted successfully'
            },
            {
              status: 'PROCESSING',
              timestamp: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000 + 60 * 60 * 1000).toISOString(),
              location: 'Warehouse',
              description: 'Order is being processed'
            },
            {
              status: 'IN_WAREHOUSE',
              timestamp: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000).toISOString(),
              location: 'Colombo Warehouse',
              description: 'Package is ready for dispatch'
            },
            {
              status: 'OUT_FOR_DELIVERY',
              timestamp: new Date(Date.now() - 4 * 60 * 60 * 1000).toISOString(),
              location: 'Colombo Hub',
              description: 'Package is out for delivery'
            }
          ]
        });
      }, 800);
    });
  }

  getMockDashboardStats() {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          totalOrders: 24,
          activeOrders: 8,
          deliveredOrders: 16,
          pendingOrders: 3,
          failedOrders: 1,
          recentOrders: [
            {
              id: 1,
              orderNumber: 'ORD-1234567890',
              recipientName: 'John Doe',
              deliveryAddress: '123 Main St, Colombo',
              status: 'OUT_FOR_DELIVERY',
              createdAt: new Date().toISOString()
            },
            {
              id: 2,
              orderNumber: 'ORD-1234567891',
              recipientName: 'Jane Smith',
              deliveryAddress: '456 Oak Ave, Kandy',
              status: 'DELIVERED',
              createdAt: new Date(Date.now() - 86400000).toISOString()
            },
            {
              id: 3,
              orderNumber: 'ORD-1234567892',
              recipientName: 'Bob Wilson',
              deliveryAddress: '789 Pine Rd, Galle',
              status: 'PROCESSING',
              createdAt: new Date(Date.now() - 3600000).toISOString()
            }
          ]
        });
      }, 600);
    });
  }

  // Utility methods
  getStatusColor(status) {
    const statusColors = {
      'SUBMITTED': 'secondary',
      'PROCESSING': 'warning',
      'IN_WAREHOUSE': 'info',
      'ROUTE_PLANNED': 'primary',
      'OUT_FOR_DELIVERY': 'warning',
      'DELIVERED': 'success',
      'FAILED': 'danger'
    };
    return statusColors[status] || 'secondary';
  }

  formatDateTime(dateString) {
    return new Date(dateString).toLocaleString();
  }

  formatDate(dateString) {
    return new Date(dateString).toLocaleDateString();
  }
}

const apiService = new ApiService();
export default apiService;

// WebSocket service for real-time updates
class WebSocketService {
  constructor() {
    this.socket = null;
    this.listeners = [];
    this.reconnectAttempts = 0;
    this.maxReconnectAttempts = 5;
  }

  connect(url = 'ws://localhost:8080/ws') {
    try {
      this.socket = new WebSocket(url);
      
      this.socket.onopen = (event) => {
        console.log('WebSocket connected');
        this.reconnectAttempts = 0;
        this.notifyListeners('connected', event);
      };

      this.socket.onmessage = (event) => {
        const data = JSON.parse(event.data);
        console.log('WebSocket message received:', data);
        this.notifyListeners('message', data);
      };

      this.socket.onclose = (event) => {
        console.log('WebSocket disconnected');
        this.notifyListeners('disconnected', event);
        this.attemptReconnect(url);
      };

      this.socket.onerror = (error) => {
        console.error('WebSocket error:', error);
        this.notifyListeners('error', error);
      };

    } catch (error) {
      console.warn('WebSocket connection failed, using mock data');
      this.startMockUpdates();
    }
  }

  disconnect() {
    if (this.socket) {
      this.socket.close();
      this.socket = null;
    }
  }

  send(data) {
    if (this.socket && this.socket.readyState === WebSocket.OPEN) {
      this.socket.send(JSON.stringify(data));
    }
  }

  addListener(callback) {
    this.listeners.push(callback);
  }

  removeListener(callback) {
    this.listeners = this.listeners.filter(listener => listener !== callback);
  }

  notifyListeners(type, data) {
    this.listeners.forEach(listener => {
      try {
        listener({ type, data });
      } catch (error) {
        console.error('Listener error:', error);
      }
    });
  }

  attemptReconnect(url) {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      const delay = Math.pow(2, this.reconnectAttempts) * 1000;
      
      setTimeout(() => {
        console.log(`Attempting to reconnect... (${this.reconnectAttempts}/${this.maxReconnectAttempts})`);
        this.connect(url);
      }, delay);
    } else {
      console.log('Max reconnection attempts reached, using mock data');
      this.startMockUpdates();
    }
  }

  // Mock real-time updates for demo
  startMockUpdates() {
    setInterval(() => {
      const mockUpdates = [
        {
          type: 'ORDER_STATUS_UPDATE',
          orderId: 'ORD-1234567890',
          status: 'OUT_FOR_DELIVERY',
          location: 'Colombo Hub',
          timestamp: new Date().toISOString()
        },
        {
          type: 'DELIVERY_COMPLETED',
          orderId: 'ORD-1234567891',
          status: 'DELIVERED',
          deliveredAt: new Date().toISOString(),
          signature: 'Customer signature received'
        },
        {
          type: 'NEW_ORDER',
          orderId: 'ORD-' + Date.now(),
          status: 'SUBMITTED',
          customerName: 'Demo Customer'
        }
      ];

      const randomUpdate = mockUpdates[Math.floor(Math.random() * mockUpdates.length)];
      this.notifyListeners('message', randomUpdate);
    }, 15000); // Send update every 15 seconds
  }
}

export default new WebSocketService();

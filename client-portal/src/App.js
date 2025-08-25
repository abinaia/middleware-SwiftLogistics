import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { Container } from 'react-bootstrap';
import { AppProvider } from './context/AppContext';
import Navigation from './components/Navigation';
import Dashboard from './components/Dashboard';
import OrderForm from './components/OrderForm';
import OrderTracking from './components/OrderTracking';
import OrderList from './components/OrderList';
import NotificationSystem from './components/NotificationSystem';

function App() {
  return (
    <AppProvider>
      <div className="App">
        <Navigation />
        <Container className="mt-4">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/orders" element={<OrderList />} />
            <Route path="/orders/new" element={<OrderForm />} />
            <Route path="/tracking" element={<OrderTracking />} />
            <Route path="/tracking/:trackingNumber" element={<OrderTracking />} />
          </Routes>
        </Container>
        <NotificationSystem />
      </div>
    </AppProvider>
  );
}

export default App;

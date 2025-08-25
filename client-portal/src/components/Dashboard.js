import React, { useState, useEffect } from 'react';
import { Row, Col, Card, Button, Alert } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { useApp } from '../context/AppContext';
import ApiService from '../services/apiService';
import LoadingSpinner from './LoadingSpinner';

const Dashboard = () => {
  const { state, actions } = useApp();
  const [stats, setStats] = useState({
    totalOrders: 0,
    activeOrders: 0,
    deliveredOrders: 0
  });
  const [recentOrders, setRecentOrders] = useState([]);

  useEffect(() => {
    fetchDashboardData();
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  const fetchDashboardData = async () => {
    try {
      actions.setLoading(true);
      actions.clearError();
      
      const data = await ApiService.getDashboardStats(state.user.id);
      
      setStats({
        totalOrders: data.totalOrders,
        activeOrders: data.activeOrders,
        deliveredOrders: data.deliveredOrders
      });
      setRecentOrders(data.recentOrders || []);
      
      // Add welcome notification
      actions.addNotification({
        type: 'success',
        title: 'Welcome!',
        message: `Dashboard loaded successfully. You have ${data.activeOrders} active orders.`
      });
      
    } catch (err) {
      actions.setError('Failed to load dashboard data');
      console.error('Dashboard error:', err);
    } finally {
      actions.setLoading(false);
    }
  };

  const getStatusBadge = (status) => {
    return ApiService.getStatusColor(status);
  };

  if (state.loading) {
    return <LoadingSpinner message="Loading dashboard data..." />;
  }

  return (
    <div>
      <h1 className="mb-4">Dashboard</h1>
      
      {state.error && <Alert variant="danger">{state.error}</Alert>}
      
      {/* Statistics Cards */}
      <Row className="mb-4">
        <Col md={4}>
          <Card className="text-center">
            <Card.Body>
              <Card.Title className="text-primary">{stats.totalOrders}</Card.Title>
              <Card.Text>Total Orders</Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="text-center">
            <Card.Body>
              <Card.Title className="text-warning">{stats.activeOrders}</Card.Title>
              <Card.Text>Active Orders</Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="text-center">
            <Card.Body>
              <Card.Title className="text-success">{stats.deliveredOrders}</Card.Title>
              <Card.Text>Delivered Orders</Card.Text>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Quick Actions */}
      <Row className="mb-4">
        <Col>
          <Card>
            <Card.Header>
              <h5>Quick Actions</h5>
            </Card.Header>
            <Card.Body>
              <Row>
                <Col md={3}>
                  <Link to="/orders/new">
                    <Button variant="primary" className="w-100 mb-2">
                      Create New Order
                    </Button>
                  </Link>
                </Col>
                <Col md={3}>
                  <Link to="/tracking">
                    <Button variant="outline-primary" className="w-100 mb-2">
                      Track Order
                    </Button>
                  </Link>
                </Col>
                <Col md={3}>
                  <Link to="/orders">
                    <Button variant="outline-secondary" className="w-100 mb-2">
                      View All Orders
                    </Button>
                  </Link>
                </Col>
              </Row>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Recent Orders */}
      <Row>
        <Col>
          <Card>
            <Card.Header>
              <h5>Recent Orders</h5>
            </Card.Header>
            <Card.Body>
              {recentOrders.length === 0 ? (
                <p className="text-muted">No recent orders found.</p>
              ) : (
                recentOrders.map(order => (
                  <Card key={order.id} className="mb-3 order-card">
                    <Card.Body>
                      <Row>
                        <Col md={3}>
                          <strong>{order.orderNumber}</strong>
                        </Col>
                        <Col md={3}>
                          {order.recipientName}
                        </Col>
                        <Col md={4}>
                          {order.deliveryAddress}
                        </Col>
                        <Col md={2}>
                          <span className={`badge bg-${getStatusBadge(order.status)} status-badge`}>
                            {order.status.replace('_', ' ')}
                          </span>
                        </Col>
                      </Row>
                    </Card.Body>
                  </Card>
                ))
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default Dashboard;

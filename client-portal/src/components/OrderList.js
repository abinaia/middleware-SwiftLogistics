import React, { useState, useEffect } from 'react';
import { Card, Badge, Row, Col, Button, Alert, Form } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import apiService from '../services/apiService';

const OrderList = () => {
  const [orders, setOrders] = useState([]);
  const [filteredOrders, setFilteredOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  useEffect(() => {
    fetchOrders();
  }, []);

  useEffect(() => {
    filterOrders();
  }, [orders, statusFilter]); // eslint-disable-line react-hooks/exhaustive-deps

  const fetchOrders = async () => {
    try {
      setLoading(true);
      console.log('OrderList: Starting to fetch orders...');
      
      // Use real API service to fetch orders for client 1
      const ordersData = await apiService.getOrders(1);
      console.log('OrderList: Received orders data:', ordersData);
      setOrders(ordersData);
      
    } catch (err) {
      console.error('OrderList: Error in fetchOrders:', err);
      setError('Failed to load orders');
      console.error('Orders fetch error:', err);
    } finally {
      setLoading(false);
    }
  };

  const filterOrders = () => {
    if (statusFilter === 'ALL') {
      setFilteredOrders(orders);
    } else {
      setFilteredOrders(orders.filter(order => order.status === statusFilter));
    }
  };

  const getStatusInfo = (status) => {
    const statusMap = {
      'SUBMITTED': { variant: 'secondary', text: 'Submitted' },
      'PROCESSING': { variant: 'warning', text: 'Processing' },
      'IN_WAREHOUSE': { variant: 'info', text: 'In Warehouse' },
      'ROUTE_PLANNED': { variant: 'primary', text: 'Route Planned' },
      'OUT_FOR_DELIVERY': { variant: 'warning', text: 'Out for Delivery' },
      'DELIVERED': { variant: 'success', text: 'Delivered' },
      'FAILED': { variant: 'danger', text: 'Failed' }
    };
    return statusMap[status] || { variant: 'secondary', text: status };
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString();
  };

  const formatDateTime = (dateString) => {
    return new Date(dateString).toLocaleString();
  };

  if (loading) {
    return (
      <div className="text-center">
        <div className="spinner-border" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1>My Orders</h1>
        <Link to="/orders/new">
          <Button variant="primary">Create New Order</Button>
        </Link>
      </div>

      {error && <Alert variant="danger">{error}</Alert>}

      {/* Filter Controls */}
      <Card className="mb-4">
        <Card.Body>
          <Row>
            <Col md={4}>
              <Form.Group>
                <Form.Label>Filter by Status</Form.Label>
                <Form.Select 
                  value={statusFilter} 
                  onChange={(e) => setStatusFilter(e.target.value)}
                >
                  <option value="ALL">All Orders</option>
                  <option value="SUBMITTED">Submitted</option>
                  <option value="PROCESSING">Processing</option>
                  <option value="IN_WAREHOUSE">In Warehouse</option>
                  <option value="ROUTE_PLANNED">Route Planned</option>
                  <option value="OUT_FOR_DELIVERY">Out for Delivery</option>
                  <option value="DELIVERED">Delivered</option>
                  <option value="FAILED">Failed</option>
                </Form.Select>
              </Form.Group>
            </Col>
            <Col md={8} className="d-flex align-items-end">
              <div className="text-muted">
                Showing {filteredOrders.length} of {orders.length} orders
              </div>
            </Col>
          </Row>
        </Card.Body>
      </Card>

      {/* Orders List */}
      {filteredOrders.length === 0 ? (
        <Card>
          <Card.Body className="text-center">
            <p className="text-muted mb-3">No orders found.</p>
            <Link to="/orders/new">
              <Button variant="primary">Create Your First Order</Button>
            </Link>
          </Card.Body>
        </Card>
      ) : (
        filteredOrders.map(order => (
          <Card key={order.id} className="mb-3 order-card">
            <Card.Body>
              <Row>
                <Col md={8}>
                  <div className="d-flex justify-content-between align-items-start mb-2">
                    <div>
                      <h6 className="mb-1">{order.orderNumber}</h6>
                      <p className="text-muted mb-1">
                        <small>Tracking: {order.trackingNumber}</small>
                      </p>
                    </div>
                    <Badge 
                      bg={getStatusInfo(order.status).variant}
                      className="status-badge"
                    >
                      {getStatusInfo(order.status).text}
                    </Badge>
                  </div>
                  
                  <div className="mb-2">
                    <strong>Recipient:</strong> {order.recipientName}
                  </div>
                  
                  <div className="mb-2">
                    <strong>Delivery Address:</strong> {order.deliveryAddress}
                  </div>
                  
                  <div className="text-muted">
                    <small>
                      Created: {formatDate(order.createdAt)} | 
                      Last Updated: {formatDateTime(order.updatedAt)}
                      {order.deliveredAt && (
                        <> | Delivered: {formatDateTime(order.deliveredAt)}</>
                      )}
                    </small>
                  </div>
                </Col>
                
                <Col md={4} className="d-flex flex-column justify-content-center">
                  <Link 
                    to={`/tracking/${order.trackingNumber}`}
                    className="mb-2"
                  >
                    <Button variant="outline-primary" size="sm" className="w-100">
                      Track Order
                    </Button>
                  </Link>
                  
                  <Button 
                    variant="outline-secondary" 
                    size="sm" 
                    className="w-100"
                    onClick={() => {
                      // In real app, this could show order details modal
                      alert('Order details feature would be implemented here');
                    }}
                  >
                    View Details
                  </Button>
                </Col>
              </Row>
            </Card.Body>
          </Card>
        ))
      )}

      {/* Summary */}
      {orders.length > 0 && (
        <Card className="mt-4">
          <Card.Body>
            <h6>Order Summary</h6>
            <Row>
              <Col md={3}>
                <div className="text-center">
                  <div className="h5 text-primary">
                    {orders.filter(o => ['SUBMITTED', 'PROCESSING', 'IN_WAREHOUSE', 'ROUTE_PLANNED', 'OUT_FOR_DELIVERY'].includes(o.status)).length}
                  </div>
                  <div className="text-muted">Active Orders</div>
                </div>
              </Col>
              <Col md={3}>
                <div className="text-center">
                  <div className="h5 text-success">
                    {orders.filter(o => o.status === 'DELIVERED').length}
                  </div>
                  <div className="text-muted">Delivered</div>
                </div>
              </Col>
              <Col md={3}>
                <div className="text-center">
                  <div className="h5 text-danger">
                    {orders.filter(o => o.status === 'FAILED').length}
                  </div>
                  <div className="text-muted">Failed</div>
                </div>
              </Col>
              <Col md={3}>
                <div className="text-center">
                  <div className="h5 text-info">{orders.length}</div>
                  <div className="text-muted">Total Orders</div>
                </div>
              </Col>
            </Row>
          </Card.Body>
        </Card>
      )}
    </div>
  );
};

export default OrderList;

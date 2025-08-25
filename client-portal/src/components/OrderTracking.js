import React, { useState, useEffect } from 'react';
import { Form, Button, Card, Alert, Row, Col, Badge } from 'react-bootstrap';
import { useParams } from 'react-router-dom';

const OrderTracking = () => {
  const { trackingNumber: urlTrackingNumber } = useParams();
  const [trackingNumber, setTrackingNumber] = useState(urlTrackingNumber || '');
  const [orderData, setOrderData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (urlTrackingNumber) {
      handleTrack();
    }
  }, [urlTrackingNumber]); // eslint-disable-line react-hooks/exhaustive-deps

  const handleTrack = async () => {
    if (!trackingNumber.trim()) {
      setError('Please enter a tracking number');
      return;
    }

    setLoading(true);
    setError('');
    setOrderData(null);

    try {
      // For demo purposes, using mock data
      // In real app, this would call: axios.get(`/api/orders/tracking/${trackingNumber}`)
      
      await new Promise(resolve => setTimeout(resolve, 1000)); // Simulate API delay
      
      const mockOrder = {
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
        deliveredAt: null
      };

      setOrderData(mockOrder);
      
    } catch (err) {
      setError('Order not found or failed to fetch tracking information');
      console.error('Tracking error:', err);
    } finally {
      setLoading(false);
    }
  };

  const getStatusInfo = (status) => {
    const statusMap = {
      'SUBMITTED': { variant: 'secondary', text: 'Order Submitted', progress: 20 },
      'PROCESSING': { variant: 'warning', text: 'Processing', progress: 40 },
      'IN_WAREHOUSE': { variant: 'info', text: 'In Warehouse', progress: 60 },
      'ROUTE_PLANNED': { variant: 'primary', text: 'Route Planned', progress: 70 },
      'OUT_FOR_DELIVERY': { variant: 'warning', text: 'Out for Delivery', progress: 90 },
      'DELIVERED': { variant: 'success', text: 'Delivered', progress: 100 },
      'FAILED': { variant: 'danger', text: 'Delivery Failed', progress: 0 }
    };
    return statusMap[status] || { variant: 'secondary', text: status, progress: 0 };
  };

  const formatDateTime = (dateString) => {
    return new Date(dateString).toLocaleString();
  };

  return (
    <div>
      <h1 className="mb-4">Track Your Order</h1>
      
      <Card className="tracking-container mb-4">
        <Card.Body>
          <Form onSubmit={(e) => { e.preventDefault(); handleTrack(); }}>
            <Row>
              <Col md={8}>
                <Form.Group>
                  <Form.Label>Tracking Number</Form.Label>
                  <Form.Control
                    type="text"
                    value={trackingNumber}
                    onChange={(e) => setTrackingNumber(e.target.value)}
                    placeholder="Enter your tracking number (e.g., TRK-ABC12345)"
                    required
                  />
                </Form.Group>
              </Col>
              <Col md={4} className="d-flex align-items-end">
                <Button
                  variant="primary"
                  onClick={handleTrack}
                  disabled={loading}
                  className="w-100"
                >
                  {loading ? (
                    <>
                      <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                      Tracking...
                    </>
                  ) : (
                    'Track Order'
                  )}
                </Button>
              </Col>
            </Row>
          </Form>
        </Card.Body>
      </Card>

      {error && <Alert variant="danger">{error}</Alert>}

      {orderData && (
        <Card className="tracking-container">
          <Card.Header>
            <h5>Order Tracking Information</h5>
          </Card.Header>
          <Card.Body>
            <Row className="mb-4">
              <Col md={6}>
                <h6>Order Details</h6>
                <p><strong>Order Number:</strong> {orderData.orderNumber}</p>
                <p><strong>Tracking Number:</strong> {orderData.trackingNumber}</p>
                <p><strong>Order Date:</strong> {formatDateTime(orderData.createdAt)}</p>
                <p><strong>Last Updated:</strong> {formatDateTime(orderData.updatedAt)}</p>
              </Col>
              <Col md={6}>
                <h6>Delivery Information</h6>
                <p><strong>Recipient:</strong> {orderData.recipientName}</p>
                <p><strong>Phone:</strong> {orderData.recipientPhone}</p>
                <p><strong>Address:</strong> {orderData.deliveryAddress}</p>
              </Col>
            </Row>

            <Row className="mb-4">
              <Col>
                <h6>Current Status</h6>
                <div className="d-flex align-items-center mb-3">
                  <Badge 
                    bg={getStatusInfo(orderData.status).variant} 
                    className="me-3 p-2"
                    style={{ fontSize: '1rem' }}
                  >
                    {getStatusInfo(orderData.status).text}
                  </Badge>
                  {orderData.deliveredAt && (
                    <span className="text-success">
                      Delivered on {formatDateTime(orderData.deliveredAt)}
                    </span>
                  )}
                </div>
                
                {/* Progress Bar */}
                <div className="progress mb-3" style={{ height: '10px' }}>
                  <div 
                    className={`progress-bar bg-${getStatusInfo(orderData.status).variant}`}
                    role="progressbar" 
                    style={{ width: `${getStatusInfo(orderData.status).progress}%` }}
                  ></div>
                </div>
              </Col>
            </Row>

            {/* Timeline */}
            <Row>
              <Col>
                <h6>Order Timeline</h6>
                <div className="timeline">
                  <div className="timeline-item">
                    <div className="timeline-marker bg-success"></div>
                    <div className="timeline-content">
                      <h6>Order Submitted</h6>
                      <p className="text-muted">{formatDateTime(orderData.createdAt)}</p>
                    </div>
                  </div>
                  
                  {['PROCESSING', 'IN_WAREHOUSE', 'ROUTE_PLANNED', 'OUT_FOR_DELIVERY', 'DELIVERED'].includes(orderData.status) && (
                    <div className="timeline-item">
                      <div className="timeline-marker bg-info"></div>
                      <div className="timeline-content">
                        <h6>Order Processing Started</h6>
                        <p className="text-muted">Order is being processed</p>
                      </div>
                    </div>
                  )}
                  
                  {['IN_WAREHOUSE', 'ROUTE_PLANNED', 'OUT_FOR_DELIVERY', 'DELIVERED'].includes(orderData.status) && (
                    <div className="timeline-item">
                      <div className="timeline-marker bg-primary"></div>
                      <div className="timeline-content">
                        <h6>Package in Warehouse</h6>
                        <p className="text-muted">Package received at distribution center</p>
                      </div>
                    </div>
                  )}
                  
                  {['ROUTE_PLANNED', 'OUT_FOR_DELIVERY', 'DELIVERED'].includes(orderData.status) && (
                    <div className="timeline-item">
                      <div className="timeline-marker bg-warning"></div>
                      <div className="timeline-content">
                        <h6>Route Planned</h6>
                        <p className="text-muted">Delivery route has been optimized</p>
                      </div>
                    </div>
                  )}
                  
                  {['OUT_FOR_DELIVERY', 'DELIVERED'].includes(orderData.status) && (
                    <div className="timeline-item">
                      <div className="timeline-marker bg-warning"></div>
                      <div className="timeline-content">
                        <h6>Out for Delivery</h6>
                        <p className="text-muted">Package is on the way to delivery address</p>
                      </div>
                    </div>
                  )}
                  
                  {orderData.status === 'DELIVERED' && (
                    <div className="timeline-item">
                      <div className="timeline-marker bg-success"></div>
                      <div className="timeline-content">
                        <h6>Delivered</h6>
                        <p className="text-muted">{formatDateTime(orderData.deliveredAt || orderData.updatedAt)}</p>
                      </div>
                    </div>
                  )}
                </div>
              </Col>
            </Row>
          </Card.Body>
        </Card>
      )}

      <style jsx>{`
        .timeline {
          position: relative;
          padding-left: 30px;
        }
        
        .timeline::before {
          content: '';
          position: absolute;
          left: 15px;
          top: 0;
          bottom: 0;
          width: 2px;
          background: #dee2e6;
        }
        
        .timeline-item {
          position: relative;
          margin-bottom: 20px;
        }
        
        .timeline-marker {
          position: absolute;
          left: -22px;
          top: 5px;
          width: 12px;
          height: 12px;
          border-radius: 50%;
          border: 2px solid white;
          box-shadow: 0 0 0 2px #dee2e6;
        }
        
        .timeline-content h6 {
          margin-bottom: 5px;
          font-size: 0.9rem;
        }
        
        .timeline-content p {
          margin-bottom: 0;
          font-size: 0.8rem;
        }
      `}</style>
    </div>
  );
};

export default OrderTracking;

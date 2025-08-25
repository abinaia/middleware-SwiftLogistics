import React, { useState } from 'react';
import { Form, Button, Card, Alert, Row, Col } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import apiService from '../services/apiService';

const OrderForm = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    deliveryAddress: '',
    recipientName: '',
    recipientPhone: '',
    packageDescription: '',
    packageWeight: '',
    packageDimensions: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      // For demo purposes, using mock client ID
      const orderData = {
        ...formData,
        clientId: 1, // In real app, this would come from authentication
        packageWeight: formData.packageWeight ? parseFloat(formData.packageWeight) : null
      };

      // Call real backend API
      console.log('Submitting order to backend:', orderData);
      const response = await apiService.createOrder(orderData);
      console.log('Order created successfully:', response);

      setSuccess(`Order created successfully! Order Number: ${response.orderNumber}, Tracking Number: ${response.trackingNumber}`);
      
      // Reset form
      setFormData({
        deliveryAddress: '',
        recipientName: '',
        recipientPhone: '',
        packageDescription: '',
        packageWeight: '',
        packageDimensions: ''
      });

      // Navigate to orders list after 3 seconds
      setTimeout(() => {
        navigate('/orders');
      }, 3000);

    } catch (err) {
      setError('Failed to create order. Please try again.');
      console.error('Order submission error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h1 className="mb-4">Create New Order</h1>
      
      <Card className="form-container">
        <Card.Body>
          {error && <Alert variant="danger">{error}</Alert>}
          {success && <Alert variant="success">{success}</Alert>}
          
          <Form onSubmit={handleSubmit}>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Recipient Name *</Form.Label>
                  <Form.Control
                    type="text"
                    name="recipientName"
                    value={formData.recipientName}
                    onChange={handleChange}
                    required
                    placeholder="Enter recipient's full name"
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Recipient Phone</Form.Label>
                  <Form.Control
                    type="tel"
                    name="recipientPhone"
                    value={formData.recipientPhone}
                    onChange={handleChange}
                    placeholder="Enter phone number"
                  />
                </Form.Group>
              </Col>
            </Row>

            <Form.Group className="mb-3">
              <Form.Label>Delivery Address *</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                name="deliveryAddress"
                value={formData.deliveryAddress}
                onChange={handleChange}
                required
                placeholder="Enter complete delivery address including city and postal code"
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Package Description *</Form.Label>
              <Form.Control
                type="text"
                name="packageDescription"
                value={formData.packageDescription}
                onChange={handleChange}
                required
                placeholder="Describe the contents of the package"
              />
            </Form.Group>

            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Package Weight (kg)</Form.Label>
                  <Form.Control
                    type="number"
                    step="0.1"
                    name="packageWeight"
                    value={formData.packageWeight}
                    onChange={handleChange}
                    placeholder="0.0"
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Package Dimensions (L x W x H)</Form.Label>
                  <Form.Control
                    type="text"
                    name="packageDimensions"
                    value={formData.packageDimensions}
                    onChange={handleChange}
                    placeholder="e.g., 30 x 20 x 15 cm"
                  />
                </Form.Group>
              </Col>
            </Row>

            <div className="d-grid gap-2 d-md-flex justify-content-md-end">
              <Button
                variant="secondary"
                onClick={() => navigate('/orders')}
                disabled={loading}
              >
                Cancel
              </Button>
              <Button
                variant="primary"
                type="submit"
                disabled={loading}
              >
                {loading ? (
                  <>
                    <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                    Creating Order...
                  </>
                ) : (
                  'Create Order'
                )}
              </Button>
            </div>
          </Form>
        </Card.Body>
      </Card>
    </div>
  );
};

export default OrderForm;

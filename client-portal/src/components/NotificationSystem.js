import React from 'react';
import { Toast, ToastContainer } from 'react-bootstrap';
import { useApp } from '../context/AppContext';

const NotificationSystem = () => {
  const { state, actions } = useApp();

  const handleClose = (notificationId) => {
    actions.removeNotification(notificationId);
  };

  if (state.notifications.length === 0) {
    return null;
  }

  return (
    <ToastContainer position="top-end" className="p-3" style={{ zIndex: 1050 }}>
      {state.notifications.map((notification) => (
        <Toast
          key={notification.id}
          show={true}
          onClose={() => handleClose(notification.id)}
          delay={notification.autoHide !== false ? 5000 : 0}
          autohide={notification.autoHide !== false}
          bg={notification.type || 'primary'}
        >
          <Toast.Header>
            <strong className="me-auto">
              {notification.title || 'SwiftLogistics'}
            </strong>
            <small>{new Date().toLocaleTimeString()}</small>
          </Toast.Header>
          <Toast.Body className={notification.type === 'dark' ? 'text-white' : ''}>
            {notification.message}
          </Toast.Body>
        </Toast>
      ))}
    </ToastContainer>
  );
};

export default NotificationSystem;

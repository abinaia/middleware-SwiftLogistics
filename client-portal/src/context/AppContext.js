import React, { createContext, useContext, useReducer } from 'react';

// Initial state
const initialState = {
  user: {
    id: 1, // Mock user ID for demo
    name: 'Demo Client',
    email: 'demo@swiftlogistics.com'
  },
  orders: [],
  loading: false,
  error: null,
  notifications: []
};

// Actions
export const ACTIONS = {
  SET_LOADING: 'SET_LOADING',
  SET_ERROR: 'SET_ERROR',
  CLEAR_ERROR: 'CLEAR_ERROR',
  SET_ORDERS: 'SET_ORDERS',
  ADD_ORDER: 'ADD_ORDER',
  UPDATE_ORDER: 'UPDATE_ORDER',
  ADD_NOTIFICATION: 'ADD_NOTIFICATION',
  REMOVE_NOTIFICATION: 'REMOVE_NOTIFICATION',
  SET_USER: 'SET_USER'
};

// Reducer
function appReducer(state, action) {
  switch (action.type) {
    case ACTIONS.SET_LOADING:
      return { ...state, loading: action.payload };
    
    case ACTIONS.SET_ERROR:
      return { ...state, error: action.payload, loading: false };
    
    case ACTIONS.CLEAR_ERROR:
      return { ...state, error: null };
    
    case ACTIONS.SET_ORDERS:
      return { ...state, orders: action.payload, loading: false };
    
    case ACTIONS.ADD_ORDER:
      return { 
        ...state, 
        orders: [action.payload, ...state.orders],
        loading: false 
      };
    
    case ACTIONS.UPDATE_ORDER:
      return {
        ...state,
        orders: state.orders.map(order =>
          order.id === action.payload.id ? { ...order, ...action.payload } : order
        ),
        loading: false
      };
    
    case ACTIONS.ADD_NOTIFICATION:
      return {
        ...state,
        notifications: [...state.notifications, {
          id: Date.now(),
          ...action.payload
        }]
      };
    
    case ACTIONS.REMOVE_NOTIFICATION:
      return {
        ...state,
        notifications: state.notifications.filter(notif => notif.id !== action.payload)
      };
    
    case ACTIONS.SET_USER:
      return { ...state, user: action.payload };
    
    default:
      return state;
  }
}

// Context
const AppContext = createContext();

// Provider component
export function AppProvider({ children }) {
  const [state, dispatch] = useReducer(appReducer, initialState);

  // Action creators
  const actions = {
    setLoading: (loading) => dispatch({ type: ACTIONS.SET_LOADING, payload: loading }),
    setError: (error) => dispatch({ type: ACTIONS.SET_ERROR, payload: error }),
    clearError: () => dispatch({ type: ACTIONS.CLEAR_ERROR }),
    setOrders: (orders) => dispatch({ type: ACTIONS.SET_ORDERS, payload: orders }),
    addOrder: (order) => dispatch({ type: ACTIONS.ADD_ORDER, payload: order }),
    updateOrder: (order) => dispatch({ type: ACTIONS.UPDATE_ORDER, payload: order }),
    addNotification: (notification) => dispatch({ type: ACTIONS.ADD_NOTIFICATION, payload: notification }),
    removeNotification: (id) => dispatch({ type: ACTIONS.REMOVE_NOTIFICATION, payload: id }),
    setUser: (user) => dispatch({ type: ACTIONS.SET_USER, payload: user })
  };

  const value = {
    state,
    actions
  };

  return (
    <AppContext.Provider value={value}>
      {children}
    </AppContext.Provider>
  );
}

// Custom hook to use the context
export function useApp() {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useApp must be used within an AppProvider');
  }
  return context;
}

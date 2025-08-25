import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { createStackNavigator } from '@react-navigation/stack';
import Icon from 'react-native-vector-icons/MaterialIcons';

// Import screens
import DashboardScreen from './screens/DashboardScreen';
import RouteScreen from './screens/RouteScreen';
import DeliveryScreen from './screens/DeliveryScreen';
import ProfileScreen from './screens/ProfileScreen';
import DeliveryDetailsScreen from './screens/DeliveryDetailsScreen';

const Tab = createBottomTabNavigator();
const Stack = createStackNavigator();

// Stack Navigator for Delivery screens
const DeliveryStack = () => {
  return (
    <Stack.Navigator>
      <Stack.Screen 
        name="DeliveryList" 
        component={DeliveryScreen}
        options={{ title: 'My Deliveries' }}
      />
      <Stack.Screen 
        name="DeliveryDetails" 
        component={DeliveryDetailsScreen}
        options={{ title: 'Delivery Details' }}
      />
    </Stack.Navigator>
  );
};

// Main Tab Navigator
const TabNavigator = () => {
  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        tabBarIcon: ({ focused, color, size }) => {
          let iconName;

          if (route.name === 'Dashboard') {
            iconName = 'dashboard';
          } else if (route.name === 'Route') {
            iconName = 'map';
          } else if (route.name === 'Deliveries') {
            iconName = 'local-shipping';
          } else if (route.name === 'Profile') {
            iconName = 'person';
          }

          return <Icon name={iconName} size={size} color={color} />;
        },
        tabBarActiveTintColor: '#007bff',
        tabBarInactiveTintColor: 'gray',
        headerStyle: {
          backgroundColor: '#007bff',
        },
        headerTintColor: '#fff',
        headerTitleStyle: {
          fontWeight: 'bold',
        },
      })}
    >
      <Tab.Screen 
        name="Dashboard" 
        component={DashboardScreen}
        options={{ title: 'Dashboard' }}
      />
      <Tab.Screen 
        name="Route" 
        component={RouteScreen}
        options={{ title: 'My Route' }}
      />
      <Tab.Screen 
        name="Deliveries" 
        component={DeliveryStack}
        options={{ title: 'Deliveries', headerShown: false }}
      />
      <Tab.Screen 
        name="Profile" 
        component={ProfileScreen}
        options={{ title: 'Profile' }}
      />
    </Tab.Navigator>
  );
};

// Main App Component
const App = () => {
  return (
    <NavigationContainer>
      <TabNavigator />
    </NavigationContainer>
  );
};

export default App;

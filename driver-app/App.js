import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { StyleSheet, StatusBar } from 'react-native';

// Import screens
import DashboardScreen from './src/screens/DashboardScreen';
import RoutesScreen from './src/screens/RoutesScreen';
import DeliveriesScreen from './src/screens/DeliveriesScreen';

const Tab = createBottomTabNavigator();

export default function App() {
  return (
    <NavigationContainer>
      <StatusBar barStyle="dark-content" backgroundColor="#fff" />
      <Tab.Navigator
        screenOptions={{
          tabBarActiveTintColor: '#007AFF',
          tabBarInactiveTintColor: '#999',
          tabBarStyle: styles.tabBar,
          headerStyle: styles.header,
          headerTintColor: '#fff',
          headerTitleStyle: styles.headerTitle,
        }}
      >
        <Tab.Screen 
          name="Dashboard" 
          component={DashboardScreen}
          options={{
            tabBarLabel: 'Dashboard',
            headerTitle: 'Driver Dashboard'
          }}
        />
        <Tab.Screen 
          name="Routes" 
          component={RoutesScreen}
          options={{
            tabBarLabel: 'Routes',
            headerTitle: 'My Routes'
          }}
        />
        <Tab.Screen 
          name="Deliveries" 
          component={DeliveriesScreen}
          options={{
            tabBarLabel: 'Deliveries',
            headerTitle: 'Deliveries'
          }}
        />
      </Tab.Navigator>
    </NavigationContainer>
  );
}

const styles = StyleSheet.create({
  tabBar: {
    backgroundColor: '#fff',
    borderTopWidth: 1,
    borderTopColor: '#e0e0e0',
    paddingBottom: 5,
    height: 60,
  },
  header: {
    backgroundColor: '#007AFF',
  },
  headerTitle: {
    fontWeight: 'bold',
  },
});

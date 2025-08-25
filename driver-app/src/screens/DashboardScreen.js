import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Alert,
  RefreshControl,
} from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';

const DashboardScreen = () => {
  const [driverStats, setDriverStats] = useState({
    todayDeliveries: 0,
    completedDeliveries: 0,
    pendingDeliveries: 0,
    totalDistance: 0,
  });
  const [loading, setLoading] = useState(false);
  const [driverInfo, setDriverInfo] = useState({
    name: 'John Driver',
    id: 'DRV001',
    vehicle: 'VAN-123',
    status: 'ACTIVE'
  });

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    setLoading(true);
    try {
      // Mock data - in real app, this would call your API
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      setDriverStats({
        todayDeliveries: 12,
        completedDeliveries: 8,
        pendingDeliveries: 4,
        totalDistance: 87.5,
      });
    } catch (error) {
      Alert.alert('Error', 'Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  const handleStartRoute = () => {
    Alert.alert(
      'Start Route',
      'Are you ready to start your delivery route for today?',
      [
        { text: 'Cancel', style: 'cancel' },
        { text: 'Start', onPress: () => Alert.alert('Route Started!') }
      ]
    );
  };

  const handleEmergency = () => {
    Alert.alert(
      'Emergency Alert',
      'This will notify dispatch immediately. Continue?',
      [
        { text: 'Cancel', style: 'cancel' },
        { text: 'Send Alert', style: 'destructive', onPress: () => Alert.alert('Emergency alert sent!') }
      ]
    );
  };

  return (
    <ScrollView
      style={styles.container}
      refreshControl={
        <RefreshControl refreshing={loading} onRefresh={fetchDashboardData} />
      }
    >
      {/* Driver Info Card */}
      <View style={styles.card}>
        <View style={styles.cardHeader}>
          <Icon name="person" size={24} color="#007bff" />
          <Text style={styles.cardTitle}>Driver Information</Text>
        </View>
        <View style={styles.driverInfo}>
          <Text style={styles.driverName}>{driverInfo.name}</Text>
          <Text style={styles.driverDetail}>ID: {driverInfo.id}</Text>
          <Text style={styles.driverDetail}>Vehicle: {driverInfo.vehicle}</Text>
          <View style={styles.statusContainer}>
            <View style={[styles.statusDot, { backgroundColor: '#28a745' }]} />
            <Text style={styles.statusText}>{driverInfo.status}</Text>
          </View>
        </View>
      </View>

      {/* Statistics Cards */}
      <View style={styles.statsContainer}>
        <View style={styles.statCard}>
          <Icon name="today" size={30} color="#007bff" />
          <Text style={styles.statNumber}>{driverStats.todayDeliveries}</Text>
          <Text style={styles.statLabel}>Today's Deliveries</Text>
        </View>
        <View style={styles.statCard}>
          <Icon name="check-circle" size={30} color="#28a745" />
          <Text style={styles.statNumber}>{driverStats.completedDeliveries}</Text>
          <Text style={styles.statLabel}>Completed</Text>
        </View>
      </View>

      <View style={styles.statsContainer}>
        <View style={styles.statCard}>
          <Icon name="pending" size={30} color="#ffc107" />
          <Text style={styles.statNumber}>{driverStats.pendingDeliveries}</Text>
          <Text style={styles.statLabel}>Pending</Text>
        </View>
        <View style={styles.statCard}>
          <Icon name="navigation" size={30} color="#17a2b8" />
          <Text style={styles.statNumber}>{driverStats.totalDistance} km</Text>
          <Text style={styles.statLabel}>Distance</Text>
        </View>
      </View>

      {/* Quick Actions */}
      <View style={styles.card}>
        <View style={styles.cardHeader}>
          <Icon name="flash-on" size={24} color="#007bff" />
          <Text style={styles.cardTitle}>Quick Actions</Text>
        </View>
        <View style={styles.actionsContainer}>
          <TouchableOpacity style={styles.actionButton} onPress={handleStartRoute}>
            <Icon name="play-arrow" size={24} color="#fff" />
            <Text style={styles.actionButtonText}>Start Route</Text>
          </TouchableOpacity>
          <TouchableOpacity style={[styles.actionButton, styles.emergencyButton]} onPress={handleEmergency}>
            <Icon name="warning" size={24} color="#fff" />
            <Text style={styles.actionButtonText}>Emergency</Text>
          </TouchableOpacity>
        </View>
      </View>

      {/* Today's Route Summary */}
      <View style={styles.card}>
        <View style={styles.cardHeader}>
          <Icon name="route" size={24} color="#007bff" />
          <Text style={styles.cardTitle}>Today's Route</Text>
        </View>
        <View style={styles.routeSummary}>
          <Text style={styles.routeText}>Route: Central Colombo Loop</Text>
          <Text style={styles.routeText}>Estimated Time: 6 hours</Text>
          <Text style={styles.routeText}>Total Stops: 12</Text>
          <Text style={styles.routeText}>Next Stop: 123 Main St, Colombo 03</Text>
        </View>
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f8f9fa',
    padding: 16,
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: 8,
    padding: 16,
    marginBottom: 16,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  cardHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 16,
  },
  cardTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginLeft: 8,
    color: '#333',
  },
  driverInfo: {
    alignItems: 'center',
  },
  driverName: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 4,
  },
  driverDetail: {
    fontSize: 14,
    color: '#666',
    marginBottom: 2,
  },
  statusContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 8,
  },
  statusDot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    marginRight: 6,
  },
  statusText: {
    fontSize: 14,
    fontWeight: '500',
    color: '#28a745',
  },
  statsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 16,
  },
  statCard: {
    flex: 1,
    backgroundColor: '#fff',
    borderRadius: 8,
    padding: 16,
    alignItems: 'center',
    marginHorizontal: 4,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  statNumber: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333',
    marginTop: 8,
  },
  statLabel: {
    fontSize: 12,
    color: '#666',
    textAlign: 'center',
    marginTop: 4,
  },
  actionsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  actionButton: {
    flex: 1,
    backgroundColor: '#007bff',
    borderRadius: 8,
    padding: 16,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    marginHorizontal: 4,
  },
  emergencyButton: {
    backgroundColor: '#dc3545',
  },
  actionButtonText: {
    color: '#fff',
    fontWeight: 'bold',
    marginLeft: 8,
  },
  routeSummary: {
    paddingVertical: 8,
  },
  routeText: {
    fontSize: 14,
    color: '#333',
    marginBottom: 4,
  },
});

export default DashboardScreen;

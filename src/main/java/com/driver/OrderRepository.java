package com.driver;

import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class OrderRepository {
    private HashMap<String, Order> orderMap = new HashMap<>();
    private HashMap<String, DeliveryPartner> partnerMap = new HashMap<>();
    private HashMap<String, HashSet<String>> partnerToOrderMap = new HashMap<>();
    private HashMap<String, String> orderToPartnerMap = new HashMap<>();

    public void saveOrder(Order order) {
        orderMap.put(order.getId(), order);
    }

    public void savePartner(String partnerId) {
        partnerMap.put(partnerId, new DeliveryPartner(partnerId));
    }

    public void saveOrderPartnerMap(String orderId, String partnerId) {
        if (orderMap.containsKey(orderId) && partnerMap.containsKey(partnerId)) {
            // Unassign from existing partner
            String existingPartnerId = orderToPartnerMap.get(orderId);
            if (existingPartnerId != null) {
                HashSet<String> existingOrders = partnerToOrderMap.get(existingPartnerId);
                existingOrders.remove(orderId);
                DeliveryPartner existingPartner = partnerMap.get(existingPartnerId);
                existingPartner.setNumberOfOrders(existingPartner.getNumberOfOrders() - 1);
            }

            // Assign to new partner
            HashSet<String> orders = partnerToOrderMap.getOrDefault(partnerId, new HashSet<>());
            orders.add(orderId);
            partnerToOrderMap.put(partnerId, orders);
            orderToPartnerMap.put(orderId, partnerId);
            DeliveryPartner partner = partnerMap.get(partnerId);
            partner.setNumberOfOrders(orders.size());
        }
    }

    public Order findOrderById(String orderId) {
        return orderMap.get(orderId);
    }

    public DeliveryPartner findPartnerById(String partnerId) {
        return partnerMap.get(partnerId);
    }

    public Integer findOrderCountByPartnerId(String partnerId) {
        return partnerToOrderMap.getOrDefault(partnerId, new HashSet<>()).size();
    }

    public List<String> findOrdersByPartnerId(String partnerId) {
        return new ArrayList<>(partnerToOrderMap.getOrDefault(partnerId, new HashSet<>()));
    }

    public List<Order> findAllOrders() {
        return new ArrayList<>(orderMap.values());
    }

    public void deletePartner(String partnerId) {
        partnerMap.remove(partnerId);
        HashSet<String> orders = partnerToOrderMap.remove(partnerId);
        if (orders != null) {
            for (String orderId : orders) {
                orderToPartnerMap.remove(orderId);
            }
        }
    }

    public void deleteOrder(String orderId) {
        orderMap.remove(orderId);
        String partnerId = orderToPartnerMap.remove(orderId);
        if (partnerId != null) {
            HashSet<String> orders = partnerToOrderMap.get(partnerId);
            orders.remove(orderId);
            if (orders.isEmpty()) partnerToOrderMap.remove(partnerId);
        }
    }

    public Integer findCountOfUnassignedOrders() {
        return orderMap.size() - orderToPartnerMap.size();
    }

    public Integer findOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
        int givenTime = convertTimeToMinutes(time);
        HashSet<String> orders = partnerToOrderMap.getOrDefault(partnerId, new HashSet<>());
        int count = 0;
        for (String orderId : orders) {
            int orderTime = convertTimeToMinutes(orderMap.get(orderId).getDeliveryTime());
            if (orderTime > givenTime) count++;
        }
        return count;
    }

    public String findLastDeliveryTimeByPartnerId(String partnerId) {
        HashSet<String> orders = partnerToOrderMap.getOrDefault(partnerId, new HashSet<>());
        int maxTime = 0;
        for (String orderId : orders) {
            int orderTime = convertTimeToMinutes(orderMap.get(orderId).getDeliveryTime());
            maxTime = Math.max(maxTime, orderTime);
        }
        return String.format("%02d:%02d", maxTime / 60, maxTime % 60);
    }

    private int convertTimeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }
}
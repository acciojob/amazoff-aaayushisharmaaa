package com.driver;

public class Order {

    private String id;
    private int deliveryTime;

    public Order(String id, String deliveryTime) {

        // The deliveryTime has to converted from string to int and then stored in the attribute
        //deliveryTime  = HH*60 + MM
        this.id=id;
        //this.deliveryTime-getDeliveryTime(deliveryTime);
        String[] parts = deliveryTime.split(":");
        this.deliveryTime = Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    public String getId() {
        return id;
    }

    //public String getDeliveryTime() {return deliveryTime;}
    public String getDeliveryTime() {
        return String.format("%02d:%02d", deliveryTime / 60, deliveryTime % 60);
    }
}

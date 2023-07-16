package com.example.audace;


import java.util.ArrayList;

public class Order {
    private String orderId;


    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    private String totalPrice;
    private ArrayList<Favorite> productList;

    public Order(String orderId, String totalPrice, ArrayList<Favorite> productList) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.productList = productList;
    }

    public String getOrderId() {
        return orderId;
    }


    public ArrayList<Favorite> getProductList() {
        return productList;
    }
}


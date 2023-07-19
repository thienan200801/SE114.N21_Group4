package com.example.audace.model;

public class NamePrice {
    private String nameOfCheckoutItem, priceOfCheckoutItem;

    private Integer quantity;

    public NamePrice(String nameOfCheckoutItem, String priceOfCheckoutItem, Integer quantity){
        this.nameOfCheckoutItem = nameOfCheckoutItem;
        this.priceOfCheckoutItem = priceOfCheckoutItem;
        this.quantity = quantity;
    }

    public String getNameOfCheckoutItem() {
        return nameOfCheckoutItem;
    }

    public void setNameOfCheckoutItem(String nameOfCheckoutItem) {
        this.nameOfCheckoutItem = nameOfCheckoutItem;
    }

    public String getPriceOfCheckoutItem() {
        return priceOfCheckoutItem;
    }

    public void setPriceOfCheckoutItem(String priceOfCheckoutItem) {
        this.priceOfCheckoutItem = priceOfCheckoutItem;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

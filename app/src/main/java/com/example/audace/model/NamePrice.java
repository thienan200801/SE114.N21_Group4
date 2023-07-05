package com.example.audace.model;

public class NamePrice {
    private String nameOfCheckoutItem, priceOfCheckoutItem;

    public NamePrice(String nameOfCheckoutItem, String priceOfCheckoutItem){
        this.nameOfCheckoutItem = nameOfCheckoutItem;
        this.priceOfCheckoutItem = priceOfCheckoutItem;
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
}

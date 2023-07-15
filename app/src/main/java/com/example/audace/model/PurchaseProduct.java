package com.example.audace.model;

import android.annotation.SuppressLint;

public class PurchaseProduct
{
    private String productId;
    private String colorId;

    private String sizeId;
    private int quantity;

    public PurchaseProduct(String productId, String colorId, String sizeId)
    {
        this.productId = productId;
        this.colorId = colorId;
        this.sizeId = sizeId;
        this.quantity = 1;
    }
    @SuppressLint("DefaultLocale")
    public String toJSON(){
        return String.format("{\r\n            \"product\": \"%s\",\r\n            \"size\": \"%s\",\r\n            \"color\": \"%s\",\r\n            \"quantity\": %d \r\n        }", productId, sizeId, colorId, quantity);
    }
}
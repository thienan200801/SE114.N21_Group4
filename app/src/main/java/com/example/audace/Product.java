package com.example.audace;

public class Product {
    String productID;

    String imgUrl;

    String name;

    String price;

    boolean favourite;

    public Product(String productID, String name, String price, boolean favourite, String img)
    {
        this.productID = productID;
        imgUrl = img;
        this.name = name;
        this.price = price;
        this.favourite = favourite;
    }
}

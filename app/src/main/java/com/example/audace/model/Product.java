package com.example.audace.model;

public class Product {
    String productID;

    String imgUrl;

    String name;

    String price;

    String stablePrice;

    boolean favourite;

    public Product(String productID, String name, String price, String stablePrice, boolean favourite, String img)
    {
        this.productID = productID;
        imgUrl = img;
        this.name = name;
        this.price = price;
        this.favourite = favourite;
        this.stablePrice = stablePrice;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getStablePrice() {
        return stablePrice;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getProductID() {
        return productID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public boolean isFavourite() {
        return favourite;
    }
}

package com.example.audace.model;

public class CheckoutItemDetails {
    private String name, imageURL;

    private ColorObject color;

    private SizeObject size;
    private  String price, quantity;


    public CheckoutItemDetails(String name, SizeObject size, ColorObject color, String price, String quantity, String imageURL) {
        this.name = name;
        this.size = size;
        this.color = color;
        this.price = price;
        this.quantity = quantity;
        this.imageURL = imageURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }

    public ColorObject getColor() {
        return color;
    }

    public void setColor(ColorObject color) {
        this.color = color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SizeObject getSize() {
        return size;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setSize(SizeObject size) {
        this.size = size;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}

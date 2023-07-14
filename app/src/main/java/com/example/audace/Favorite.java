package com.example.audace;

public class Favorite {
    String id;
    String name;
    String quantity;
    String image;
    String price;

    String color;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    String size;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Favorite(String id, String name, String image, String price) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public  String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }



}

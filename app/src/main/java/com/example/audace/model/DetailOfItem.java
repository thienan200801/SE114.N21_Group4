package com.example.audace.model;

public class DetailOfItem {
    private String id, name, description, imageURL, sold, currentPrice, stablePrice;
    private float rating;
    private String[] categories;
    private Colors[] colors;
    private boolean isFavourite;

    public DetailOfItem() {}
    public DetailOfItem(String id, String name, String description, String imageURL, String sold, String currentPrice, String stablePrice, float rating, String[] categories, Colors[] colors, boolean isFavourite) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageURL = imageURL;
        this.sold = sold;
        this.currentPrice = currentPrice;
        this.stablePrice = stablePrice;
        this.rating = rating;
        this.categories = categories;
        this.colors = colors;
        this.isFavourite = isFavourite;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Colors[] getColors() {
        return colors;
    }

    public float getRating() {
        return rating;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getSold() {
        return sold;
    }

    public String getStablePrice() {
        return stablePrice;
    }

    public String[] getCategories() {
        return categories;
    }
    public boolean getIsFavourite(){
        return isFavourite;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public void setColors(Colors[] colors) {
        this.colors = colors;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setSold(String sold) {
        this.sold = sold;
    }

    public void setStablePrice(String stablePrice) {
        this.stablePrice = stablePrice;
    }
}

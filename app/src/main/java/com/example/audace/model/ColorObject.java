package com.example.audace.model;

public class ColorObject {
    private String colorId;

    private String colorName;
    private String color;

    public ColorObject(String id, String name, String color) {
        colorId = id;
        colorName = name;
        this.color = color;
    }

    public String getColorId() {
        return colorId;
    }

    public void setColorId(String colorId) {
        this.colorId = colorId;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

}

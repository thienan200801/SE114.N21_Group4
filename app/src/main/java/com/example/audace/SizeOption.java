package com.example.audace;

public class SizeOption {
    public SizeOption(String width, String height) {
        this.width = width;
        this.height = height;
    }

    String width;
    String height;

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
}

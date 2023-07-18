package com.example.audace;

public class SizeOption {
    String id;
    boolean selected;
    public SizeOption(String id,String width, String height) {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    String width;
    String height;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

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

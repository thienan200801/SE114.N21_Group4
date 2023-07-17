package com.example.audace;

public class ColorOption {

    String id;
    boolean selected;
    String name;
    String hex;

    public ColorOption(String name, String hex) {
        this.name = name;
        this.hex = hex;
    }
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


    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}


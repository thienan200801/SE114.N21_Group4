package com.example.audace;

public class ColorOption {

    String id;
    String name;

    public ColorOption(String id, String name, String hex) {
        this.id = id;
        this.name = name;
        this.hex = hex;
    }

    String hex;
    boolean selected;



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


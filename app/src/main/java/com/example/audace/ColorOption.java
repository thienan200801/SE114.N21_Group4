package com.example.audace;

public class ColorOption {

    String name;
    String hex;

    public ColorOption(String name, String hex) {
        this.name = name;
        this.hex = hex;
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


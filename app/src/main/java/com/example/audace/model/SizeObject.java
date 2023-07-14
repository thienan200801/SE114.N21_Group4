package com.example.audace.model;

public class SizeObject {
    private String sizeId;
    private String sizeInCentimeter;

    public SizeObject(String id, String sizeInCentimeter) {
        this.sizeId = id;
        this.sizeInCentimeter = sizeInCentimeter;
    }

    public String getSizeInCentimeter() {
        return sizeInCentimeter;
    }

    public void setSizeInCentimeter(String sizeInCentimeter) {
        this.sizeInCentimeter = sizeInCentimeter;
    }

    public String getSizeId() {
        return sizeId;
    }

    public void setSizeId(String sizeId) {
        this.sizeId = sizeId;
    }
}

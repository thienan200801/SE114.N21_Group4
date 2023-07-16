package com.example.audace.model;

import java.util.ArrayList;

public class Catagory {
    String catagoryID;
    String CatagoryName;

    String ImgUrl;


    ArrayList<Catagory> subCatagories;

    public Catagory(String ID, String name, String Url){
        catagoryID = ID;
        CatagoryName = name;
        ImgUrl = Url;
        subCatagories = new ArrayList<>();
    }

    public ArrayList<Catagory> getSubCatagories() {
        return subCatagories;
    }

    public void setSubCatagories(ArrayList<Catagory> subCatagories) {
        this.subCatagories = subCatagories;
    }

    public String getCatagoryName() {
        return CatagoryName;
    }

    public String getCatagoryID() {
        return catagoryID;
    }

    public String getImgUrl() {
        return ImgUrl;
    }

    public void setCatagoryID(String catagoryID) {
        this.catagoryID = catagoryID;
    }

    public void setCatagoryName(String catagoryName) {
        CatagoryName = catagoryName;
    }

    public void setImgUrl(String imgUrl) {
        ImgUrl = imgUrl;
    }
}

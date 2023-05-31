package com.example.audace;

import java.util.ArrayList;

public class Catagory {
    String imgID;
    String CatagoryName;

    String ImgUrl;


    ArrayList<Catagory> subCatagories;

    public Catagory(String ID, String name, String Url){
        imgID = ID;
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
}

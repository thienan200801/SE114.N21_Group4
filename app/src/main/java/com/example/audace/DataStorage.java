package com.example.audace;

import com.example.audace.model.Catagory;

import java.util.ArrayList;

public class DataStorage {
    private ArrayList<Catagory> catagoryArrayList;

    private String AccessToken;

    private String productId;

    private String catagoryId;

    public ArrayList<Catagory> getCatagoryArrayList() {
        return catagoryArrayList;
    }

    public void setCatagoryArrayList(ArrayList<Catagory> catagoryArrayList) {
        this.catagoryArrayList = catagoryArrayList;
        if(catagoryArrayList.size() != 0)
            catagoryId = catagoryArrayList.get(0).imgID;
    }

    public String getAccessToken() {
        return AccessToken;
    }

    public void setAccessToken(String accessToken) {
        AccessToken = accessToken;
    }

    public DataStorage(){
        catagoryArrayList = new ArrayList<Catagory>();
        AccessToken = "";
    }

    private static final DataStorage storage = new DataStorage();

    public static DataStorage getInstance() {return storage;}

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setCatagoryId(String catagoryId) {
        this.catagoryId = catagoryId;
    }

    public String getCatagoryId() {
        return catagoryId;
    }
}

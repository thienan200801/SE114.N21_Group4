package com.example.audace.model;

public class Banner {
    String BannerID;
    String imgURL;
    public Banner(String BannerID, String imgURL)
    {
        this.BannerID = BannerID;
        this.imgURL = imgURL;
    }

    public String getBannerID() {
        return BannerID;
    }

    public void setBannerID(String bannerID) {
        BannerID = bannerID;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
}


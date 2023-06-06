package com.example.audace.model;

public class User {
    String userId;

    String imgUrl;

    String location;

    String userName;

    String phoneNum;

    String email;

    String dateOfBirth;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getUserId() {
        return userId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getLocation() {
        return location;
    }

    public String getUserName() {
        return userName;
    }

    public User(String userId) {
        this.userId = userId;
    }

    public String getPhoneNum() {
        return phoneNum;
    }


    public String getEmail() {
        return email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }
}

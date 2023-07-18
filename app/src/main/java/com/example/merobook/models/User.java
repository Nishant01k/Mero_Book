package com.example.merobook.models;

public class User {

    private String uid, name, email, profileImage, token, userType;

    private long coins;
    private int spins;
    public User() {

    }

    public User(String uid, String name, String email, String profileImage, String userType, long coins, int spins) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
        this.coins = coins;
        this.spins = spins;
    }


    public long getCoins() {
        return coins;
    }

    public void setCoins(long coins) {
        this.coins = coins;
    }

    public String getUserType() {
        return userType;
    }

    public User(String userType) {
        this.userType = userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setPhoneNumber(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getSpins() {
        return spins;
    }

    public void setSpins(int spins) {
        this.spins = spins;
    }
}

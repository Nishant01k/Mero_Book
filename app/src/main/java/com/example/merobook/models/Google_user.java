package com.example.merobook.models;

public class Google_user {
    private static String uid,DisplayName,PhotoUrl;
    private String uId, name, profile, city ;

    public Google_user(String uid, String displayName, String s, String unknown, int i) {

    }

    public Google_user(String uId, String name, String profile, String city, String uid, String DisplayName, String PhotoUrl) {
        this.uId = uId;
        this.name = name;
        this.profile = profile;
        this.city = city;
        Google_user.DisplayName = DisplayName;
        Google_user.uid =uid;
        Google_user.PhotoUrl = PhotoUrl;
    }

    public static String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        Google_user.uid = uid;
    }

    public static String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public static String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

}

package com.hasanin.hossam.techplanet;

public class Users {

    String id;
    String name;
    String profileImage;
    String email;

    public Users(String id, String name, String profileImage, String email) {
        this.id = id;
        this.name = name;
        this.profileImage = profileImage;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

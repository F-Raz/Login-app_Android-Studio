package com.example.appform;

public class User {
    private String id;
    private String name;
    private String email;
    private String gender;
    private String city;
    private String bio;

    public User() {
        // Required for Firebase
    }

    public User(String name, String email, String gender, String city, String bio) {
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.city = city;
        this.bio = bio;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}

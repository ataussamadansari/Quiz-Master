package com.example.quizmaster.Login_Signup;

public class UserModel {
    String uid;
    String name;
    String email;
    String phone;
    String password;
    String profile;
    int points = 0;
    int rank;

    public UserModel() {
    }

    public UserModel(String uid, String name, String profile, int points, int rank) {
        this.uid = uid;
        this.name = name;
        this.profile = profile;
        this.points = points;
        this.rank = rank;
    }

    public UserModel(String name, String email, String phone, String password, String profile, int points, int rank) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.profile = profile;
        this.points = points;
        this.rank = rank;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}

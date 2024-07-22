package com.example.quizmaster.Leaderboard;

public class LeaderModel {
    String uid;
    String userName;
    String userRank;
    String userPoint;
    int userImage;

    public LeaderModel(String uid, String userName, String userRank, String userPoint, int userImage) {
        this.uid = uid;
        this.userName = userName;
        this.userRank = userRank;
        this.userPoint = userPoint;
        this.userImage = userImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRank() {
        return userRank;
    }

    public void setUserRank(String userRank) {
        this.userRank = userRank;
    }

    public String getUserPoint() {
        return userPoint;
    }

    public void setUserPoint(String userPoint) {
        this.userPoint = userPoint;
    }

    public int getUserImage() {
        return userImage;
    }

    public void setUserImage(int userImage) {
        this.userImage = userImage;
    }
}

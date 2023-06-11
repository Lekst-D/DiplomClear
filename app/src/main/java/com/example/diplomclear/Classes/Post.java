package com.example.diplomclear.Classes;

public class Post {

    private String ImagePost;
    private String UserID;
    private String PostDate;
    private String PostTime;
    private String PostText;

    private String PostD;

    Post() {
    }

    public Post(String ImagePost, String UserID,String PostText,String PostDate,String PostTime,String PostD) {
        this.UserID = UserID;
        this.ImagePost = ImagePost;
        this.PostTime = PostTime;
        this.PostText = PostText;
        this.PostDate=PostDate;
        this.PostD=PostD;
    }

    public String getPostD() {
        return PostD;
    }

    public void setPostD(String postD) {
        PostD = postD;
    }

    public String getImagePost() {
        return ImagePost;
    }

    public void setImagePost(String imagePost) {
        ImagePost = imagePost;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getPostDate() {
        return PostDate;
    }

    public void setPostDate(String postDate) {
        PostDate = postDate;
    }

    public String getPostTime() {
        return PostTime;
    }

    public void setPostTime(String postTime) {
        PostTime = postTime;
    }

    public String getPostText() {
        return PostText;
    }

    public void setPostText(String postText) {
        PostText = postText;
    }
}

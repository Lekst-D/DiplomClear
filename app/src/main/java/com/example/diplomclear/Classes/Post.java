package com.example.diplomclear.Classes;

public class Post {

    private String ImagePost;
    private String UserID;
    private String PostDate;
    private String PostTime;
    private String PostText;

    Post() {
    }

    public Post(String ImagePost, String UserID,String PostText,String PostDate,String PostTime) {
        this.UserID = UserID;
        this.ImagePost = ImagePost;
        this.PostTime = PostTime;
        this.PostText = PostText;
        this.PostDate=PostDate;
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

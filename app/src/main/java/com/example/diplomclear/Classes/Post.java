package com.example.diplomclear.Classes;

public class Post {

    private String ImagePost;
    private String FIO_text;
    private String PostTime_text;
    private String PostText_text;

    Post() {
    }

    public Post(String ImagePost, String FIO_text,String PostText_text,String PostTime_text) {
        this.FIO_text = FIO_text;
        this.ImagePost = ImagePost;
        this.PostTime_text = PostTime_text;
        this.PostText_text = PostText_text;

    }

    public String getImagePost() {
        return ImagePost;
    }

    public void setImagePost(String imagePost) {
        ImagePost = imagePost;
    }

    public String getFIO_text() {
        return FIO_text;
    }

    public void setFIO_text(String FIO_text) {
        this.FIO_text = FIO_text;
    }

    public String getPostTime_text() {
        return PostTime_text;
    }

    public void setPostTime_text(String postTime_text) {
        PostTime_text = postTime_text;
    }

    public String getPostText_text() {
        return PostText_text;
    }

    public void setPostText_text(String postText_text) {
        PostText_text = postText_text;
    }

}

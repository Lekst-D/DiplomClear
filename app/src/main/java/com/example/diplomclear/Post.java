package com.example.diplomclear;

public class Post {

    private String TextPost;
    private String ImagePost;
    private String DatatimePost;



    Post() {
    }

    public Post(String TextPost, String ImagePost, String DatatimePost) {
        this.TextPost = TextPost;
        this.ImagePost = ImagePost;
        this.DatatimePost = DatatimePost;
    }

    public String getTextPost() {
        return TextPost;
    }

    public void setTextPost(String textPost) {
        TextPost = textPost;
    }

    public String getImagePost() {
        return ImagePost;
    }

    public void setImagePost(String imagePost) {
        ImagePost = imagePost;
    }

    public String getDatatimePost() {
        return DatatimePost;
    }

    public void setDatatimePost(String datatimePost) {
        DatatimePost = datatimePost;
    }

}

package com.example.diplomclear;

public class SearchList {

    private String ImageUser;
    private String FIO;

    SearchList() {
    }

    public SearchList(String ImageUser, String FIO) {
        this.ImageUser = ImageUser;
        this.FIO = FIO;
    }

    public String getImageUser() {
        return ImageUser;
    }

    public void setImageUser(String imageUser) {
        ImageUser = imageUser;
    }

    public String getFIO() {
        return FIO;
    }

    public void setFIO(String FIO) {
        this.FIO = FIO;
    }
}

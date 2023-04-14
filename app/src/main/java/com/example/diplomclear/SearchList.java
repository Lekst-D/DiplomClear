package com.example.diplomclear;

public class SearchList {

    private String ImageUser;
    private String FIO;


    private String IDUser;

    SearchList() {
    }

    public SearchList(String ImageUser, String FIO, String IDUser) {
        this.ImageUser = ImageUser;
        this.FIO = FIO;
        this.IDUser = IDUser;
    }

    public String getIDUser() {
        return IDUser;
    }

    public void setIDUser(String IDUser) {
        this.IDUser = IDUser;
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

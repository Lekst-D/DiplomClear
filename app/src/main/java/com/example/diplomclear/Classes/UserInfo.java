package com.example.diplomclear.Classes;

public class UserInfo {

    private String UserName;
    private String UserSurname;
    private String UserPhoto;
    private String UserPhone;
    private String BirthDay;
    private String IDUser;

    UserInfo() {
    }

    public UserInfo(String UserName, String UserSurname, String UserPhone,
                    String BirthDay, String IDUser, String UserPhoto) {
        this.UserName = UserName;
        this.UserPhone = UserPhone;
        this.UserSurname = UserSurname;
        this.BirthDay = BirthDay;
        this.IDUser = IDUser;
        this.UserPhoto = UserPhoto;

    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserSurname() {
        return UserSurname;
    }

    public void setUserSurname(String userSurname) {
        UserSurname = userSurname;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }

    public String getBirthDay() {
        return BirthDay;
    }

    public void setBirthDay(String birthDay) {
        BirthDay = birthDay;
    }

    public String getIDUser() { return IDUser; }

    public void setIDUser(String IDUser) { this.IDUser = IDUser; }

    public String getUserPhoto() { return UserPhoto; }

    public void setUserPhoto(String userPhoto) { UserPhoto = userPhoto; }
}

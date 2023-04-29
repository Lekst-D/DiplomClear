package com.example.diplomclear.Message;

public class Message {

    String IDU = "";
    String FIO = "";
    String DateMess = "";
    String TimeMess = "";
    String TextMess = "";
    String ImageMess = "";
    String AudioMess = "";

    public Message() {
        this.IDU = "";
        this.FIO = "";
        this.TimeMess = "";
        this.TextMess = "";
        this.ImageMess = "";
    }

    public Message(String IDU, String FIO, String DateMess, String TimeMess, String TextMess, String ImageMess) {
        this.IDU = IDU;
        this.FIO = FIO;
        this.TimeMess = TimeMess;
        this.TextMess = TextMess;
        this.ImageMess = ImageMess;
        this.AudioMess = "";
        this.DateMess = DateMess;
    }

    public Message(String IDU, String FIO, String DateMess, String TimeMess, String AudioMess) {
        this.IDU = IDU;
        this.FIO = FIO;
        this.TimeMess = TimeMess;
        this.TextMess = "";
        this.ImageMess = "";
        this.AudioMess = AudioMess;
        this.DateMess = DateMess;
    }


    public String getIDU() {
        return IDU;
    }

    public void setIDU(String IDU) {
        this.IDU = IDU;
    }

    public String getFIO() {
        return FIO;
    }

    public void setFIO(String FIO) {
        this.FIO = FIO;
    }

    public String getTimeMess() {
        return TimeMess;
    }

    public void setTimeMess(String timeMess) {
        TimeMess = timeMess;
    }

    public String getTextMess() {
        return TextMess;
    }

    public void setTextMess(String textMess) {
        TextMess = textMess;
    }

    public String getImageMess() {
        return ImageMess;
    }

    public void setImageMess(String imageMess) {
        ImageMess = imageMess;
    }

    public String getAudioMess() {
        return AudioMess;
    }

    public void setAudioMess(String audioMess) {
        AudioMess = audioMess;
    }

    public String getDateMess() {
        return DateMess;
    }

    public void setDateMess(String dateMess) {
        DateMess = dateMess;
    }
}

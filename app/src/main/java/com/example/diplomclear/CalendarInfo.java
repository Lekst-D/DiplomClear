package com.example.diplomclear;

public class CalendarInfo {

    String Time = "";
    String Date = "";
    String Record = "";


    public CalendarInfo() {
        this.Time = "";
        this.Date = "";
        this.Record = "";
    }

    public CalendarInfo(String Time, String Date, String Record) {
        this.Time = Time;
        this.Date = Date;
        this.Record = Record;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getRecord() {
        return Record;
    }

    public void setRecord(String record) {
        Record = record;
    }

}

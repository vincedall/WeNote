package com.github.vincedall.wenote;

public class ListItems {
    private String title;
    private String number;
    private String date;
    private long modifiedTime;

    public ListItems(String title, String number, String date, long modifiedTime) {
        this.title = title;
        this.number = number;
        this.date = date;
        this.modifiedTime = modifiedTime;
    }

    public String getTitle(){
        return title;
    }
    public String getNumber(){
        return number;
    }
    public String getDate() { return date; }
    public long getModifiedTime(){ return modifiedTime; }
    public void setModifiedTime(long modifiedTime) { this.modifiedTime = modifiedTime; }
}

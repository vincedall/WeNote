package com.github.vincedall.wenote;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

    public int getLength(String note){
        int length;
        if (note.length() >=20)
            length = 20;
        else
            length = note.length();
        if (note.substring(0, length).contains("\n")){
            length = note.indexOf("\n");
        }
        return length;
    }

    public static String getCurrentDate(){
        SimpleDateFormat formatter= new SimpleDateFormat("'Creation:' dd-MM-yyyy");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }
}

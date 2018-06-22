package com.example.master.soundprocessingapp.Recording;

import java.io.Serializable;
import java.util.Calendar;


public class Recording implements Serializable {
    private String firstName;
    private String surName;
    private String title;
    private String desc;
    private Calendar cal;

    public Recording(String fname, String sname, String title, String des){
        firstName = fname;
        surName = sname;
        this.title = title;
        desc = des;
        cal = Calendar.getInstance();
    }
    public boolean equals(Object o){
        return ((Recording)o).title.equals(this.title);
    }
    public String getFullName(){
        return firstName + " " + surName;
    }
    public String getTitle(){
        return title;
    }
    public String getDateAndTime(){
        String res = "";

        res = "" + cal.get(Calendar.DAY_OF_MONTH) + ".";

        if (cal.get(Calendar.MONTH) < 9)
            res += "0" + (cal.get(Calendar.MONTH)+1);
        else
            res += (cal.get(Calendar.MONTH)+1);

        res += "." + cal.get(Calendar.YEAR);

        if (cal.get(Calendar.HOUR_OF_DAY) < 10)
            res += " 0" + cal.get(Calendar.HOUR_OF_DAY);
        else
            res += " " + cal.get(Calendar.HOUR_OF_DAY);
        if (cal.get(Calendar.MINUTE) < 10)
            res += ":0" + cal.get(Calendar.MINUTE);
        else
            res += ":" + cal.get(Calendar.MINUTE);
        if (cal.get(Calendar.SECOND) < 10)
            res += ":0" + cal.get(Calendar.SECOND);
        else
            res += ":" + cal.get(Calendar.SECOND);


        return res;
    }

    public String getDesc() {
        return desc;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

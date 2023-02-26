package com.laodev.dwbrouter.model;

import java.io.Serializable;

public class CheckListModel implements Serializable {
    public String id;
    public String day;
    public String month;
    public String numMonth;
    public String year;
    public String week;
    public String userid;
    public String carid;
    public String region;
    public String color;
    public String amount;

    public CheckListModel(){
        id = "";
        day = "";
        month = "";
        week = "";
        numMonth = "";
        userid = "";
        carid = "";
        region = "";
        color = "";
        amount = "";
    }

}

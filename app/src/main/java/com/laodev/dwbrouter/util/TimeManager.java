package com.laodev.dwbrouter.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeManager {

    private static final String[] formats = new String[] {
            "dd-MM-yyyy",
            "d-M-yyyy",
            "dd-MMM-yyyy"
    };

    public static boolean isSameDate(String strDate1, String strDate2) {
        strDate1 = strDate1.replace(" ", "");
        strDate2 = strDate2.replace(" ", "");
        try {
            int day1 = Integer.parseInt(strDate1.split("-")[0]);
            int month1 = Integer.parseInt(strDate1.split("-")[1]);
            int year1 = Integer.parseInt(strDate1.split("-")[2]);

            int day2 = Integer.parseInt(strDate2.split("-")[0]);
            int month2 = Integer.parseInt(strDate2.split("-")[1]);
            int year2 = Integer.parseInt(strDate2.split("-")[2]);

            return (day1 == day2 && month1 == month2 && year1 == year2);
        } catch (Exception e) {
            return false;
        }
    }

    public static String getCurrentDate() {
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat(formats[0]);
        return format.format(today);
    }

}

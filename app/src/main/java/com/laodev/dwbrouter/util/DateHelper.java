package com.laodev.dwbrouter.util;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {

    public static int getWeekFromDateString(String str_date, String typte) {
        SimpleDateFormat format = new SimpleDateFormat(typte);
        try {
            Date date = format.parse(str_date);

            Calendar c = Calendar.getInstance();
            c.setTime(date); // yourdate is an object of type Date

            return c.get(Calendar.DAY_OF_WEEK);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getDayFromDateString(String str_date, String typte) {
        SimpleDateFormat format = new SimpleDateFormat(typte);
        try {
            Date date = format.parse(str_date);

            Calendar c = Calendar.getInstance();
            c.setTime(date); // yourdate is an object of type Date

            return String.valueOf(DateFormat.format("dd",  date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getMonthFromDateString(String str_date, String typte) {
        SimpleDateFormat format = new SimpleDateFormat(typte);
        try {
            Date date = format.parse(str_date);

            Calendar c = Calendar.getInstance();
            c.setTime(date); // yourdate is an object of type Date
            return String.valueOf(DateFormat.format("MMMM",  date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

}

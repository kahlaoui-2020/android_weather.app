package fr.m1miage.weather.common;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Common {

    public static final String API_ID = "9387d7732a59e17de90e4c91d32b1936";
    public static Location current_location = null;

    public static String convertUnixToDate(long dt) {
        Date date  = new Date(dt*1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM");
        String formatted = simpleDateFormat.format(date);
        return formatted;
    }

    public static String convertUnixToTime(long dt) {
        Date date  = new Date(dt*1000L );
        TimeZone tz = TimeZone.getTimeZone("GMT");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        simpleDateFormat.setTimeZone(tz);
        String formatted = simpleDateFormat.format(date);
        return formatted;
    }
    public static String convertUnixToDay(long dt) {
        Date date  = new Date(dt*1000L );
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E");
        String formatted = simpleDateFormat.format(date);
        return formatted;
    }

    public static String convertUnixToWeekDay(long dt) {
        Date date  = new Date(dt*1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
        String formatted = simpleDateFormat.format(date);
        return formatted;
    }

}

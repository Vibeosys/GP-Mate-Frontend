package com.consultpal.android.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ines on 5/14/16.
 */
public class DateUtils {

    public static long stringDateToLong(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date;
        try {
            date = sdf.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static String longDateToString(long date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }

    public static String getLocalTimeInReadableFormat(java.util.Date date) {
        final SimpleDateFormat timeReadFormat = new SimpleDateFormat("hh:mm aa");
        return timeReadFormat.format(date);
    }


    public static long stringDateTimeToLong(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
        Date date;
        try {
            date = sdf.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

}

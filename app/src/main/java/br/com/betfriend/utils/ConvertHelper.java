package br.com.betfriend.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by amade on 10/09/2016.
 */
public class ConvertHelper {

    private static final String DATE_FORMAT_DEFAULT = "dd/MM/yyyy HH:mm";

    public static boolean isEmptyOrVoid(final Date date) {
        if (date == null) {
            return true;
        } else if (date.toString().isEmpty()) {
            return true;
        }
        return false;
    }

    public static String dateToView(final Date date) {

        if (!isEmptyOrVoid(date)) {
            final SimpleDateFormat mySdf = new SimpleDateFormat(
                    DATE_FORMAT_DEFAULT);
            mySdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-2"));
            return mySdf.format(date);
        }

        return null;
    }
}

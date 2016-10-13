package br.com.betfriend.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ConvertHelper {

    private static final String DATE_FORMAT_DEFAULT = "dd/MM/yy HH:mm";

    private static final String DATE_FORMAT_SHORT = "dd/MM HH:mm";

    public static boolean isEmptyOrVoid(final Date date) {
        if (date == null) {
            return true;
        } else if (date.toString().isEmpty()) {
            return true;
        }
        return false;
    }

    public static String dateToView(Date date) {

        if (!isEmptyOrVoid(date)) {
            date = new Date(date.getTime() - TimeUnit.HOURS.toMillis(Constants.SOCCER_API_TIMEZONE_OFFSET));
            final SimpleDateFormat mySdf = new SimpleDateFormat(
                    DATE_FORMAT_DEFAULT);
            return mySdf.format(date);
        }

        return null;
    }

    public static String dateToViewShort(Date date) {

        if (!isEmptyOrVoid(date)) {
            date = new Date(date.getTime() - TimeUnit.HOURS.toMillis(Constants.SOCCER_API_TIMEZONE_OFFSET));
            final SimpleDateFormat mySdf = new SimpleDateFormat(
                    DATE_FORMAT_SHORT);
            return mySdf.format(date);
        }

        return null;
    }
}

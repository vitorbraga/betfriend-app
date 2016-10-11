package br.com.betfriend.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by amade on 10/09/2016.
 */
public class ConvertHelper {

    private static final String DATE_FORMAT_DEFAULT = "dd/MM/yy HH:mm";

    private static final String DATE_FORMAT_SHORT = "dd/MM HH:mm";

    private static final Integer TIMEZONE_OFFSET = 2;

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
            date = new Date(date.getTime() - TimeUnit.HOURS.toMillis(TIMEZONE_OFFSET));
            final SimpleDateFormat mySdf = new SimpleDateFormat(
                    DATE_FORMAT_DEFAULT);
            return mySdf.format(date);
        }

        return null;
    }

    public static String dateToViewShort(Date date) {

        if (!isEmptyOrVoid(date)) {
            date = new Date(date.getTime() - TimeUnit.HOURS.toMillis(TIMEZONE_OFFSET));
            final SimpleDateFormat mySdf = new SimpleDateFormat(
                    DATE_FORMAT_SHORT);
            return mySdf.format(date);
        }

        return null;
    }
}

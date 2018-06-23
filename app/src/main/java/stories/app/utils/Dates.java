package stories.app.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Dates {

    public final static long MILLIS_PER_HOUR = 60 * 60 * 1000L;

    public static String getCurrentUtcTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return timeFormat.format(new Date());
    }

    public static String convertDateToUtcString(Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return timeFormat.format(date);
    }

    public static Date convertUtcStringToDate(String dateString) throws ParseException {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return timeFormat.parse(dateString);
    }

    public static boolean isFresh(String dateString) {

        try {
            Date currentTime = Dates.convertUtcStringToDate(Dates.getCurrentUtcTime());
            Date utcDate = Dates.convertUtcStringToDate(dateString);

            return Math.abs(currentTime.getTime() - utcDate.getTime()) < 4 * MILLIS_PER_HOUR;
        } catch(ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}

package nanodegree.annekenl.walk360.utility;

import android.annotation.TargetApi;
import android.os.Build.VERSION;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import static java.util.Locale.US;

public class TimeHelper
{
    public final static long minuteInMilliseconds = 60000;

    public static long nanosecondsToMilliseconds(long nanoseconds) {
        return nanoseconds / 1000000;
    }

    public static long millisecondsToNanoseconds(long milliseconds) {
        return milliseconds * 1000000;
    }

    public static long elapsedWallTimeMillisInMinutes(long startMillis)
    {
        long elapsedTime = System.currentTimeMillis() - startMillis;

        return elapsedTime / minuteInMilliseconds;
    }

    @TargetApi(26)
    public static String getTodayStr()
    {
        String today = "";
        if(VERSION.SDK_INT <= 25) {
            Calendar calendarDate = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("EE MM/dd", US);
            today += formatter.format(calendarDate);
        } else {
            LocalDate localDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EE LL/dd");
            today += localDate.format(formatter);
        }

        return today;
    }
}

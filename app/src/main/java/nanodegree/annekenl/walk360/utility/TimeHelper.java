package nanodegree.annekenl.walk360.utility;

import android.annotation.TargetApi;
import android.os.Build.VERSION;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
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

    public static long millisecondsToMinutes(long milliseconds)
    {
        long ret = milliseconds / 1000; //seconds

        ret = ret / 60; //minutes

        return ret;
    }

    //the three letter abbreviation of the day is the key for weekday "row"/document in firestore db
    // - later might want to change this so that it doesn't depend on USA spellings/Locale
    @TargetApi(26)
    public static String getTodayStr()
    {
        String today = "";

        if(VERSION.SDK_INT <= 25) {
            Calendar calendarToday = Calendar.getInstance();
            //today += calendarToday.DAY_OF_WEEK - 1;  //1 sun - sat 7
            SimpleDateFormat formatter = new SimpleDateFormat("EE MM/dd", US);
            today += formatter.format(calendarToday);
        } else {
            LocalDate localDate = LocalDate.now(ZoneId.of("America/New_York"));
            //today += localDate.getDayOfWeek(); //1 sun - sat 7
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EE LL/dd");
            today += localDate.format(formatter);
        }

        return today;
    }
}

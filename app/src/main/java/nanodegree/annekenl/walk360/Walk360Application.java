package nanodegree.annekenl.walk360;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build.VERSION;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import nanodegree.annekenl.walk360.activity_tracking.ActivityTrackerHelper;

public class Walk360Application extends Application
{
    private ActivityTrackerHelper mActivityTracker; //or a singleton?

    public static final String STORE_AND_RESET_DATA = "STORE_AND_RESET_DATA";

    @Override
    public void onCreate()
    {
        super.onCreate();

        Log.d("application","oncreate");

        mActivityTracker = new ActivityTrackerHelper(this);
        //mActivityTracker.requestActivityTransitionUpdates();  //start with monitoring for "still start" transition
    }

    public ActivityTrackerHelper getmActivityTracker()
    {
        return mActivityTracker;
    }

    @TargetApi(26)
    protected void checkForNewDay()
    {
        String today = "";
        if(VERSION.SDK_INT <= 25) {
            Calendar calendarDate = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyy");
            today += formatter.format(calendarDate);
        } else {
            LocalDate localDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLLLddyyyy");
            today += localDate.format(formatter);
        }

    }
}

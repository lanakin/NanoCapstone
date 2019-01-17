package nanodegree.annekenl.walk360;

import android.annotation.TargetApi;
import android.app.Application;
import android.preference.PreferenceManager;
import android.util.Log;

import nanodegree.annekenl.walk360.activity_tracking.ActivityTrackerHelper;
import nanodegree.annekenl.walk360.utility.TimeHelper;

public class Walk360Application extends Application
{
    private ActivityTrackerHelper mActivityTracker; //~

    public static final String TODAY_STR_KEY = "TODAY_STR_KEY";

    @Override
    public void onCreate()
    {
        super.onCreate();

        Log.d("application","oncreate");

        mActivityTracker = new ActivityTrackerHelper(this);
        //mActivityTracker.requestActivityTransitionUpdates();  //start with monitoring for "still start" transition

        checkDayStr();
    }

    public ActivityTrackerHelper getmActivityTracker()
    {
        return mActivityTracker;
    }


    /*check if day string is empty and if so, initialize it*/
    @TargetApi(26)
    protected void checkDayStr()
    {
        String today = TimeHelper.getTodayStr();

        String check = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(Walk360Application.TODAY_STR_KEY, "");

        if(check.isEmpty()) {
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putString(TODAY_STR_KEY, today) //can add test string here
                    .commit();

            Log.d("application","today string first set");
        }

    }
}

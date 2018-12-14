package nanodegree.annekenl.walk360;

import android.app.Application;
import android.util.Log;

import nanodegree.annekenl.walk360.activity_tracking.ActivityTrackerHelper;

public class Walk360Application extends Application
{
    private ActivityTrackerHelper mActivityTracker; //or a singleton?

    @Override
    public void onCreate()
    {
        super.onCreate();

        Log.d("application","oncreate");

        mActivityTracker = new ActivityTrackerHelper(this);
        mActivityTracker.requestActivityTransitionUpdates();  //start with monitoring for "still start" transition
    }

    public ActivityTrackerHelper getmActivityTracker()
    {
        return mActivityTracker;
    }
}

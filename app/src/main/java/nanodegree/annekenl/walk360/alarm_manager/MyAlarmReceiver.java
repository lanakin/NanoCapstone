package nanodegree.annekenl.walk360.alarm_manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.widget.Toast;

import nanodegree.annekenl.walk360.Walk360Application;
import nanodegree.annekenl.walk360.activity_tracking.ActivityTrackerHelper;

public class MyAlarmReceiver extends BroadcastReceiver
{
    private AlarmManagerHelper mAlarmManagerHelper;
    //private ActivityTrackerHelper mActivityTracker;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        long startTime = PreferenceManager.getDefaultSharedPreferences(context)
                    .getLong(ActivityTrackerHelper.DETECTED_NON_ACTIVITY, 0);

        if(startTime != 0)
        {
            long inactiveTime = AlarmManagerHelper.elapsedRealTimeMillisInMinutes(startTime);

            if(inactiveTime >= ActivityTrackerHelper.MAX_INACTIVE_TIME_MINUTES)
            {
                Toast.makeText(context, "Sitting for " + inactiveTime + " minutes. GET MOVING",
                        Toast.LENGTH_LONG).show();

                //START CLOSELY TRACKING WALKING ACTIVITY
                //mActivityTracker = new ActivityTrackerHelper(context);

                //mActivityTracker.stopActivityTransitionUpdates(); //maybe only worry about stopping this at indicated time in settings?

                try {
                    Walk360Application mApplication = (Walk360Application) context.getApplicationContext();
                    mApplication.getmActivityTracker().requestActivityDetectionUpdates();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(context, "Sitting for " + inactiveTime + " minutes." ,
                        Toast.LENGTH_LONG).show();

                //SET ALARM TO CHECK AGAIN FOR MAX INACTIVITY
                mAlarmManagerHelper = new AlarmManagerHelper(context);

                long minutesUntilTimeToMove =
                        ActivityTrackerHelper.MAX_INACTIVE_TIME_MINUTES - inactiveTime;

                mAlarmManagerHelper.setAlarm(minutesUntilTimeToMove);
            }

            // Vibrate the mobile phone -test
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            try {
                vibrator.vibrate(2000);
            } catch (Exception e) {}
        }
        else
        {
            //IF START TIME IS 0 THEN USER RECENTLY WAS ACTIVE;
            //STILLNESS START TIME WILL BE RESET WITH NEXT START OF INACTIVITY

            Toast.makeText(context, "STILLNESS ALREADY STOPPED", Toast.LENGTH_LONG).show();
        }
    }

}

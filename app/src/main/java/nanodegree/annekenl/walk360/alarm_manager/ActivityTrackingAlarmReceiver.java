package nanodegree.annekenl.walk360.alarm_manager;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import nanodegree.annekenl.walk360.MainActivity;
import nanodegree.annekenl.walk360.R;
import nanodegree.annekenl.walk360.activity_tracking.ActivityTrackerHelper;

//https://developers.google.com/android/reference/com/google/android/gms/common/GoogleApiAvailability

public class ActivityTrackingAlarmReceiver extends BroadcastReceiver
{
    private AlarmManagerHelper mAlarmManagerHelper;
    //private ActivityTrackerHelper mActivityTracker;
    protected final static long NOTIFICATION_EXPIRE_TIME = 30*AlarmManagerHelper.minuteInMilliseconds;


    @Override
    public void onReceive(Context context, Intent intent)
    {
        long startTime = PreferenceManager.getDefaultSharedPreferences(context)
                    .getLong(ActivityTrackerHelper.DETECTED_NON_ACTIVITY_KEY, 0);

        if(startTime != 0)
        {
            long inactiveTime = AlarmManagerHelper.elapsedWallTimeMillisInMinutes(startTime);

            if(inactiveTime >= ActivityTrackerHelper.MAX_INACTIVE_TIME_MINUTES)
            {
                Toast.makeText(context, "Sitting for " + inactiveTime + " minutes. GET MOVING",
                        Toast.LENGTH_LONG).show();

                alertTimeToMove(context);
                //SET ALARM TO CHECK AGAIN FOR MAX INACTIVITY
                setReminderCheckForMaxInactivity(context,NOTIFICATION_EXPIRE_TIME    //check again if user missed the notification to move
                                                            + AlarmManagerHelper.minuteInMilliseconds);   // and is still inactive

            }
            else
            {
                Toast.makeText(context, "Sitting for " + inactiveTime + " minutes." ,
                        Toast.LENGTH_LONG).show();

                //SET ALARM TO CHECK AGAIN FOR MAX INACTIVITY
                long minutesUntilTimeToMove =
                        ActivityTrackerHelper.MAX_INACTIVE_TIME_MINUTES - inactiveTime;

                setReminderCheckForMaxInactivity(context,minutesUntilTimeToMove);
            }
        }
        else
        {
            //IF START TIME IS 0 THEN USER RECENTLY WAS ACTIVE;
            //STILLNESS START TIME WILL BE RESET WITH NEXT START OF INACTIVITY
            //Toast.makeText(context, "STILLNESS ALREADY STOPPED", Toast.LENGTH_LONG).show();
        }
    }

    //set an alarm to check if user has been inactive/sitting for a set max period of time (60 min)
    private void setReminderCheckForMaxInactivity(Context context, Long minutes)
    {
        mAlarmManagerHelper = new AlarmManagerHelper(context);

        mAlarmManagerHelper.setAlarm(minutes);
    }


    private void alertTimeToMove(Context context)
    {
        Intent mIntent = new Intent(context, MainActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //~

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_pedestrian_walking)
                //.setContentTitle(textTitle)
                .setContentText("Time to Walk!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setTimeoutAfter(NOTIFICATION_EXPIRE_TIME);  //dismiss notification after long period and set alarm to check if user needs another reminder to move

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(360, mBuilder.build());
    }

}

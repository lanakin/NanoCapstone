package nanodegree.annekenl.walk360.alarm_manager;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import nanodegree.annekenl.walk360.MainActivity;
import nanodegree.annekenl.walk360.R;
import nanodegree.annekenl.walk360.activity_tracking.ActivityTrackerHelper;
import nanodegree.annekenl.walk360.utility.TimeHelper;

//https://developers.google.com/android/reference/com/google/android/gms/common/GoogleApiAvailability

public class ActivityTrackingAlarmReceiver extends BroadcastReceiver
{
    private AlarmManagerHelper mAlarmManagerHelper;
    protected final static long NOTIFICATION_EXPIRE_TIME = 15*TimeHelper.minuteInMilliseconds;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        long startTime = PreferenceManager.getDefaultSharedPreferences(context)
                    .getLong(ActivityTrackerHelper.DETECTED_NON_ACTIVITY_KEY, 0);

        if(startTime != 0)
        {
            long inactiveTime = TimeHelper.elapsedWallTimeMillisInMinutes(startTime);

            if(inactiveTime >= ActivityTrackerHelper.MAX_INACTIVE_TIME_MINUTES) {
                alertTimeToMove(context);

            }
            else {
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
        }
    }

    //set an alarm to check if user has been inactive/sitting for a set max period of time ~60 min
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
                .setContentText(context.getResources().getString(R.string.time_to_walk))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setTimeoutAfter(NOTIFICATION_EXPIRE_TIME);  //dismiss notification after long period

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(360, mBuilder.build());
    }

}

package nanodegree.annekenl.walk360.alarm_manager;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import nanodegree.annekenl.walk360.MainActivity;
import nanodegree.annekenl.walk360.R;
import nanodegree.annekenl.walk360.activity_tracking.ActivityTrackerHelper;

//https://developers.google.com/android/reference/com/google/android/gms/common/GoogleApiAvailability

public class MyAlarmReceiver extends BroadcastReceiver
{
    private AlarmManagerHelper mAlarmManagerHelper;
    //private ActivityTrackerHelper mActivityTracker;


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

                Intent mIntent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mIntent, 0);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_pedestrian_walking)
                        //.setContentTitle(textTitle)
                        .setContentText("Time to Walk!")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setCategory(NotificationCompat.CATEGORY_REMINDER)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(360, mBuilder.build());

                //idea to switch to activity detection here for short (20 seconds) intervals
                /*try {
                    Walk360Application mApplication = (Walk360Application) context.getApplicationContext();
                    mApplication.getmActivityTracker().requestActivityDetectionUpdates(ActivityTrackerHelper.ACTIVITY_DETECTION_INTERVAL);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
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

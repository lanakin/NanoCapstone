package nanodegree.annekenl.walk360.alarm_manager;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.widget.Toast;

import nanodegree.annekenl.walk360.utility.TimeHelper;

//reference: http://www.vogella.com/tutorials/AndroidBroadcastReceiver/article.html
public class AlarmManagerHelper
{
    private Context mContext;

    public AlarmManagerHelper(Context context)
    {
        mContext = context;
    }

    @TargetApi(23)
    public void setAlarm(long minutes)
    {
        Intent intent = new Intent(mContext, ActivityTrackingAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 300, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        if(VERSION.SDK_INT <= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                    + (minutes * TimeHelper.minuteInMilliseconds), pendingIntent);
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                    + (minutes * TimeHelper.minuteInMilliseconds), pendingIntent);
        }

        Toast.makeText(mContext, "Alarm set in " + minutes + " minutes",
                Toast.LENGTH_LONG).show();
    }

}

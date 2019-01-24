package nanodegree.annekenl.walk360.activity_tracking;

//https://developer.android.com/guide/topics/location/transitions
/* "The Activity Recognition Transition API can be used to detect changes in the user's activity.
 * Your app subscribes to a transition in activities of interest and the API notifies your app only
 * when needed."
 * "An activity type, represented by the DetectedActivity class. The Transition API supports the
 * following activities: IN_VEHICLE, STILL, ON_BICYCLE, RUNNING, & WALKING.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.Calendar;
import java.util.List;

import nanodegree.annekenl.walk360.alarm_manager.AlarmManagerHelper;
import nanodegree.annekenl.walk360.utility.TimeHelper;
import nanodegree.annekenl.walk360.widget.UpdateWidgetService;

public class ActivityTransitionBroadcastReceiver extends BroadcastReceiver
{
    public static final String INTENT_ACTION = "nanodegree.annekenl.walk360.TRANSITION_ACTION";
    // <intent-filter>
    //    <action android:name="nanodegree.annekenl.walk360.TRANSITION_ACTION" />
    //</intent-filter>

    private AlarmManagerHelper mAlarmManagerHelper;
    private long stillStartTime = 0;
    private long transitionTimeNanos = 0;
    private Context mContext;

    @Override        //called whenever an activity transition update is available
    public void onReceive(Context context, Intent intent)
    {
        if (ActivityTransitionResult.hasResult(intent))
        {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            String transTest = "";
            mContext = context;

            /*boolean isActive = PreferenceManager.getDefaultSharedPreferences(mContext)
                    .getBoolean(ActivityTrackerHelper.IS_ACTIVE_KEY, false);*/

            List<ActivityTransitionEvent> transitionEvents = result.getTransitionEvents();

            if(transitionEvents.size() >= 1)
            {
                ActivityTransitionEvent mostRecentTransition
                        = transitionEvents.get(transitionEvents.size() - 1);

                stillStartTime = System.currentTimeMillis();  //wall time

                transitionTimeNanos = mostRecentTransition.getElapsedRealTimeNanos(); //system time - will track exact start of event for chronometer/duration

                //test
                transTest += ActivityTrackerHelper.activityTypeToString(mContext, mostRecentTransition.getActivityType())
                        + " " + ActivityTrackerHelper.activityTransitionTypeToString(mContext, mostRecentTransition.getTransitionType())
                        + " " + Calendar.getInstance().getTime() //wall time
                        + "\n";

                PreferenceManager.getDefaultSharedPreferences(mContext)
                        .edit()
                        .putString(ActivityTrackerHelper.DETECTED_ACTIVITY_KEY, transTest)
                        .commit();

                Intent updateWidgetIntent = new Intent(mContext, UpdateWidgetService.class);
                mContext.startService(updateWidgetIntent);

                switch (mostRecentTransition.getActivityType())
                {
                    //in testing, "less is more" it seems, to keep it simple and fast detection -still and walking usually fire together as well
                    case DetectedActivity.STILL:
                        if ((mostRecentTransition.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_EXIT))
                                //&& !isActive)  //only reset time if transition event away from inactivity
                        {
                            handleUserIsActive();
                        }
                        else if((mostRecentTransition.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER))
                               // && isActive) //only reset time if transition event away from activity
                        {
                            handleUserIsInactive();
                        }
                        break;
                }
            }
        }
    }

    private void handleUserIsActive()
    {
        long elapsedSitTime = getPreviousEventDurationTime();
        long prevMaxSitTime = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getLong(ActivityTrackerHelper.MAX_SITTING_TIME_KEY, 0);

        if(elapsedSitTime > prevMaxSitTime)
        {
            PreferenceManager.getDefaultSharedPreferences(mContext)
                    .edit()
                    .putLong(ActivityTrackerHelper.DETECTED_NON_ACTIVITY_KEY, 0)  //0 indicates still/inactive time has ended
                    .putLong(ActivityTrackerHelper.CHRONOMETER_EVENT_START_KEY, transitionTimeNanos) //save new event time
                    .putBoolean(ActivityTrackerHelper.IS_ACTIVE_KEY, true)
                    .putLong(ActivityTrackerHelper.MAX_SITTING_TIME_KEY, elapsedSitTime) //update max time
                    .commit();
        } else {
            PreferenceManager.getDefaultSharedPreferences(mContext)
                    .edit()
                    .putLong(ActivityTrackerHelper.DETECTED_NON_ACTIVITY_KEY, 0)
                    .putLong(ActivityTrackerHelper.CHRONOMETER_EVENT_START_KEY, transitionTimeNanos) //save new event time
                    .putBoolean(ActivityTrackerHelper.IS_ACTIVE_KEY, true)
                    .commit();
        }

    }

    private void handleUserIsInactive()
    {
        long elapsedWalkTime = getPreviousEventDurationTime();
        long prevMaxWalkTime = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getLong(ActivityTrackerHelper.MAX_WALKING_TIME_KEY, 0);

        if(elapsedWalkTime > prevMaxWalkTime)
        {
            PreferenceManager.getDefaultSharedPreferences(mContext)
                    .edit()
                    .putLong(ActivityTrackerHelper.DETECTED_NON_ACTIVITY_KEY, stillStartTime)  //used to determine time in minutes
                    .putLong(ActivityTrackerHelper.CHRONOMETER_EVENT_START_KEY, transitionTimeNanos)
                    .putBoolean(ActivityTrackerHelper.IS_ACTIVE_KEY, false)
                    .putLong(ActivityTrackerHelper.MAX_WALKING_TIME_KEY, elapsedWalkTime) //update max time
                    .commit();
        } else {
            PreferenceManager.getDefaultSharedPreferences(mContext)
                    .edit()
                    .putLong(ActivityTrackerHelper.DETECTED_NON_ACTIVITY_KEY, stillStartTime)  //used to determine time in minutes
                    .putLong(ActivityTrackerHelper.CHRONOMETER_EVENT_START_KEY, transitionTimeNanos)
                    .putBoolean(ActivityTrackerHelper.IS_ACTIVE_KEY, false)
                    .commit();
        }

        //start alarm for reminder to move in 60 minutes
        mAlarmManagerHelper = new AlarmManagerHelper(mContext);
        mAlarmManagerHelper.setAlarm(2); //test with short time
    }

    private long getPreviousEventDurationTime()
    {
        long lastEventStartTime = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getLong(ActivityTrackerHelper.CHRONOMETER_EVENT_START_KEY, 0);

        long elapsedTime = 0;

        if(lastEventStartTime != 0) {
            long tempMilliSeconds = 0;
            tempMilliSeconds = TimeHelper.nanosecondsToMilliseconds(lastEventStartTime); //activity transition's time result
                                                                                            // is in real-time nanoseconds*

            elapsedTime = SystemClock.elapsedRealtime() - tempMilliSeconds;
        }

        return elapsedTime;
    }

}

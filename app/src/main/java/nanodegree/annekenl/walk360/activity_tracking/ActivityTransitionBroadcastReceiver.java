package nanodegree.annekenl.walk360.activity_tracking;

//https://developer.android.com/guide/topics/location/transitions
/* "The Activity Recognition Transition API can be used to detect changes in the user's activity.
 * Your app subscribes to a transition in activities of interest and the API notifies your app only
 * when needed."
 * "An activity type, represented by the DetectedActivity class. The Transition API supports the
 * following activities: IN_VEHICLE, ON_BICYCLE, RUNNING, STILL & WALKING.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.Calendar;
import java.util.List;

import nanodegree.annekenl.walk360.alarm_manager.AlarmManagerHelper;

public class ActivityTransitionBroadcastReceiver extends BroadcastReceiver
{
    public static final String INTENT_ACTION = "nanodegree.annekenl.walk360.TRANSITION_ACTION";
    // <intent-filter>
    //    <action android:name="nanodegree.annekenl.walk360.TRANSITION_ACTION" />
    //</intent-filter>

    private AlarmManagerHelper mAlarmManagerHelper;

    @Override
    public void onReceive(Context context, Intent intent) //called whenever an activity transition update is available
    {
        if (ActivityTransitionResult.hasResult(intent))
        {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            //for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                // chronological sequence of events....
            //}

            String transTest = "";
            long stillStartTime = 0;
            List<ActivityTransitionEvent> transitionEvents = result.getTransitionEvents();

            if(transitionEvents.size() >= 1)
            {
                ActivityTransitionEvent mostRecentTransition
                        = transitionEvents.get(transitionEvents.size() - 1);

                stillStartTime = System.currentTimeMillis();

                //display test
                transTest += ActivityTrackerHelper.activityTypeToString(context, mostRecentTransition.getActivityType())
                        + " " + ActivityTrackerHelper.activityTransitionTypeToString(context, mostRecentTransition.getTransitionType())
                        + " " + stillStartTime
                        + " " + Calendar.getInstance().getTime(); //display time

                PreferenceManager.getDefaultSharedPreferences(context)
                        .edit()
                        .putString(ActivityTrackerHelper.DETECTED_ACTIVITY, transTest)
                        .commit();

                    //either of the ( STILL OR IN_VEHICLE (also sitting) ) registered transitions
                if (mostRecentTransition.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                {
                    //log time of stillness started
                    PreferenceManager.getDefaultSharedPreferences(context)
                            .edit()
                            .putLong(ActivityTrackerHelper.DETECTED_NON_ACTIVITY, stillStartTime)
                            .putInt(ActivityTrackerHelper.ACTIVE_MIN_GOAL_PROGRESS, 0) //stop tracking active time
                            .commit();

                    //start alarm for reminder to move in 60 minutes
                    mAlarmManagerHelper = new AlarmManagerHelper(context);
                    mAlarmManagerHelper.setAlarm(2); //test 1 minute
                }
                else if( (mostRecentTransition.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        && mostRecentTransition.getActivityType() == DetectedActivity.STILL )
                {
                    //reset time of stillness started to 0
                    PreferenceManager.getDefaultSharedPreferences(context)
                            .edit()
                            .putLong(ActivityTrackerHelper.DETECTED_NON_ACTIVITY, 0)
                            .commit();
                }

            }

        }
    }

}
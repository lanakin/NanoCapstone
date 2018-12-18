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
import android.preference.PreferenceManager;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.Calendar;

import nanodegree.annekenl.walk360.alarm_manager.AlarmManagerHelper;

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
            boolean isActive = PreferenceManager.getDefaultSharedPreferences(mContext)
                    .getBoolean(ActivityTrackerHelper.IS_ACTIVE_KEY, false);

            for (ActivityTransitionEvent event : result.getTransitionEvents())
            {
                // chronological sequence of events....
                stillStartTime = System.currentTimeMillis();  //wall time

                transitionTimeNanos = event.getElapsedRealTimeNanos();

                //display test
                transTest += ActivityTrackerHelper.activityTypeToString(mContext, event.getActivityType())
                        + " " + ActivityTrackerHelper.activityTransitionTypeToString(mContext, event.getTransitionType())
                        //+ " " + stillStartTime
                        + " " + Calendar.getInstance().getTime() //display time
                        + "\n";

                PreferenceManager.getDefaultSharedPreferences(mContext)
                        .edit()
                        .putString(ActivityTrackerHelper.DETECTED_ACTIVITY_KEY, transTest)
                        .commit();

                switch (event.getActivityType())
                {
                    case DetectedActivity.ON_BICYCLE:
                    case DetectedActivity.RUNNING:
                    case DetectedActivity.WALKING:
                        if (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER
                                && !isActive)  //only reset time if transition event away from inactivity
                        {
                            handleUserIsActive();
                        }
                        break;
                    case DetectedActivity.STILL:
                        if (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_EXIT
                                && !isActive)  //only reset time if transition event away from inactivity
                        {
                            handleUserIsActive();
                        }
                        else if(event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER
                                && isActive) //only reset time if transition event away from activity
                        {
                            handleUserIsInactive();
                        }
                    case DetectedActivity.IN_VEHICLE:
                        if (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER
                                && isActive)  //only reset time if transition event away from activity
                        {
                            handleUserIsInactive();
                        }
                        break;
                }
            }

            /*String transTest = "";
            long stillStartTime = 0;
            long transitionTimeNanos = 0;*/
            //List<ActivityTransitionEvent> transitionEvents = result.getTransitionEvents();

            //if(transitionEvents.size() >= 1)
            //{
                //ActivityTransitionEvent mostRecentTransition
                        //= transitionEvents.get(transitionEvents.size() - 1);

             /*   stillStartTime = System.currentTimeMillis();  //wall time

                transitionTimeNanos = mostRecentTransition.getElapsedRealTimeNanos();

                //display test
                transTest += ActivityTrackerHelper.activityTypeToString(context, mostRecentTransition.getActivityType())
                        + " " + ActivityTrackerHelper.activityTransitionTypeToString(context, mostRecentTransition.getTransitionType())
                        //+ " " + stillStartTime
                        + " " + Calendar.getInstance().getTime(); //display time

                PreferenceManager.getDefaultSharedPreferences(context)
                        .edit()
                        .putString(ActivityTrackerHelper.DETECTED_ACTIVITY_KEY, transTest)
                        .commit();

                    //either of the ( STILL OR IN_VEHICLE (also sitting) ) registered transitions
                if (mostRecentTransition.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                {
                    //log time of stillness started
                    PreferenceManager.getDefaultSharedPreferences(context)
                            .edit()
                            .putLong(ActivityTrackerHelper.DETECTED_NON_ACTIVITY_KEY, stillStartTime)  //used to determine time in minutes
                            .putLong(ActivityTrackerHelper.CHRONOMETER_EVENT_START_KEY, transitionTimeNanos)
                            .putBoolean(ActivityTrackerHelper.IS_ACTIVE_KEY, false)
                            //.putInt(ActivityTrackerHelper.ACTIVE_MIN_GOAL_PROGRESS, 0) //stop tracking active time
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
                            .putLong(ActivityTrackerHelper.DETECTED_NON_ACTIVITY_KEY, 0)
                            .putLong(ActivityTrackerHelper.CHRONOMETER_EVENT_START_KEY, transitionTimeNanos)
                            .putBoolean(ActivityTrackerHelper.IS_ACTIVE_KEY, true)
                            .commit();
                }*/

            //}

        }
    }

    private void handleUserIsActive()
    {
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putLong(ActivityTrackerHelper.DETECTED_NON_ACTIVITY_KEY, 0)
                .putLong(ActivityTrackerHelper.CHRONOMETER_EVENT_START_KEY, transitionTimeNanos)
                .putBoolean(ActivityTrackerHelper.IS_ACTIVE_KEY, true)
                .commit();
    }

    private void handleUserIsInactive()
    {
        //log time of stillness started
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putLong(ActivityTrackerHelper.DETECTED_NON_ACTIVITY_KEY, stillStartTime)  //used to determine time in minutes
                .putLong(ActivityTrackerHelper.CHRONOMETER_EVENT_START_KEY, transitionTimeNanos)
                .putBoolean(ActivityTrackerHelper.IS_ACTIVE_KEY, false)
                .commit();

        //start alarm for reminder to move in 60 minutes
        mAlarmManagerHelper = new AlarmManagerHelper(mContext);
        mAlarmManagerHelper.setAlarm(2); //test with short time
    }

}
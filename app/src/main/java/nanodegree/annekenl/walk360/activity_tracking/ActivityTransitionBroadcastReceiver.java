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
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.Calendar;
import java.util.List;

import nanodegree.annekenl.walk360.R;

public class ActivityTransitionBroadcastReceiver extends BroadcastReceiver
{
    public static final String INTENT_ACTION = "nanodegree.annekenl.walk360.TRANSITION_ACTION";
    // <intent-filter>
    //    <action android:name="nanodegree.annekenl.walk360.TRANSITION_ACTION" />
    //</intent-filter>

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
            List<ActivityTransitionEvent> transitionEvents = result.getTransitionEvents();

            if(transitionEvents.size() >= 1)
            {
                ActivityTransitionEvent mostRecentTransition
                        = transitionEvents.get(transitionEvents.size() - 1);

                transTest += activityTypeToString(context, mostRecentTransition.getActivityType())
                        + " " + activityTransitionTypeToString(context, mostRecentTransition.getTransitionType())
                        + " " + Calendar.getInstance().getTime() + " ";

                PreferenceManager.getDefaultSharedPreferences(context)
                        .edit()
                        .putString(ActivityTrackerHelper.DETECTED_ACTIVITY, transTest)
                        .commit();

                if(mostRecentTransition.getActivityType() == DetectedActivity.STILL
                        || mostRecentTransition.getTransitionType() == DetectedActivity.IN_VEHICLE) //currently dont have, might want to add
                {
                    if (mostRecentTransition.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                        //log time of stillness started

                        //start alarm for reminder to move in 60 minutes
                            //(alarm manager when it fires handles the start of regular activity
                            //   updates if it determines that it's been >= 60 mins since user last
                            //   went still / inactive).
                    }
                }

            }

        }
    }

    static String activityTypeToString(Context context, int detectedActivityType) {
        Resources resources = context.getResources();
        switch(detectedActivityType) {
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.vehicle);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            default:
                return resources.getString(R.string.unknown_activity);
        }
    }

    static String activityTransitionTypeToString(Context context, int detectedTransitionType) {
        Resources resources = context.getResources();
        switch(detectedTransitionType) {
            case ActivityTransition.ACTIVITY_TRANSITION_ENTER:
                return resources.getString(R.string.activity_enter);
            case ActivityTransition.ACTIVITY_TRANSITION_EXIT:
                return resources.getString(R.string.activity_exit);
            default:
                return resources.getString(R.string.activity_error);
        }
    }
}
package nanodegree.annekenl.walk360;

// some code modified from tutorial instructions here:
// https://www.androidauthority.com/using-the-activity-recognition-api-829339/
/* "Since leaving a service running in the background is a good way to use up precious system
 * resources, the Activity Recognition API delivers its data via an intent, which contains a list
 * of activities the user may be performing at this particular time. By creating a PendingIntent
 * that’s called whenever your app receives this intent, you can monitor the user’s activities
 * without having to create a persistently running service. Your app can then extract the
 * ActivityRecognitionResult from this intent."
 */

import android.app.IntentService;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class ActivityDetectionIntentService extends IntentService
{
    protected static final String TAG = "USER_ACTIVITY";

    //Call the super IntentService constructor with the name for the worker thread
    public ActivityDetectionIntentService()
    {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    //Define an onHandleIntent() method, which will be called
    // whenever an activity detection update is available
    // whenever an activity transition update is available
    @Override
    protected void onHandleIntent(Intent intent)
    {
        /*if (ActivityTransitionResult.hasResult(intent))
        {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);

            for (ActivityTransitionEvent event : result.getTransitionEvents())
            {
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putString(MainActivity.DETECTED_ACTIVITY,
                            "" + event.getActivityType() + " " + event.getTransitionType() + " "
                                    + Calendar.getInstance().getTime())
                        .commit();
            }
        }*/

        //Check whether the Intent contains activity recognition data
        if (ActivityRecognitionResult.hasResult(intent))
        {
            //If data is available, then extract the ActivityRecognitionResult from the Intent
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            //Get an array of DetectedActivity objects
            ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

            /*PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putString(MainActivity.DETECTED_ACTIVITY,
                            result.getMostProbableActivity().toString())
                    .apply();*/

             /*PreferenceManager.getDefaultSharedPreferences(this)
                            .edit()
                            .putString(MainActivity.DETECTED_ACTIVITY,
                                    "" + currActivity.toString() + " "
                                            + Calendar.getInstance().getTime())
                            .commit();*/

            boolean determinedUserActivityLevel = false;
            boolean isMoving = false;
            String test = "test";

            for (DetectedActivity currActivity : detectedActivities)
            {
                if(!determinedUserActivityLevel)
                {
                    switch (currActivity.getType())
                    {
                        case DetectedActivity.ON_BICYCLE:
                        case DetectedActivity.ON_FOOT:
                        case DetectedActivity.RUNNING:
                        case DetectedActivity.WALKING:
                            if (currActivity.getConfidence() >= 75) {
                                isMoving = true;
                                determinedUserActivityLevel = true;
                                test = currActivity.toString();
                            }
                            break;
                        case DetectedActivity.STILL:
                        case DetectedActivity.IN_VEHICLE:
                        default:
                            //IS NOT MOVING / SITTING
                            if (currActivity.getConfidence() >= 75) {
                                isMoving = false;
                                determinedUserActivityLevel = true;
                                test = currActivity.toString();
                            }
                            break;
                    }
                }
            } //check list of probable activities

            try {
                JSONObject mJsonObj = new JSONObject();
                mJsonObj.put("isMoving", isMoving);
                mJsonObj.put("time", Calendar.getInstance().getTime());
                mJsonObj.put("test", test);

                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putString(MainActivity.DETECTED_ACTIVITY, mJsonObj.toString())
                        .commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
    }


    /*private long age_ms(long eventElapsedRealTimeNanos) {
        return (SystemClock.elapsedRealtimeNanos() - eventElapsedRealTimeNanos) / 1000000;
    }


    public long age_minutes(ActivityTransitionEvent event) {
        return age_ms(event.getElapsedRealTimeNanos() ) / (60*1000);
    }*/
}

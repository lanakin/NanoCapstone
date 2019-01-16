package nanodegree.annekenl.walk360.activity_tracking;

//references:
// https://www.androidauthority.com/using-the-activity-recognition-api-829339/
// and google api example code:
// https://developers.google.com/android/reference/com/google/android/gms/location
// /ActivityRecognitionClient

/* "Since leaving a service running in the background is a good way to use up precious system
 * resources, the Activity Recognition API delivers its data via an intent, which contains a list
 * of activities the user may be performing at this particular time. By creating a PendingIntent
 * that’s called whenever your app receives this intent, you can monitor the user’s activities
 * without having to create a persistently running service. Your app can then extract the
 * ActivityRecognitionResult from this intent."
 */

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

public class ActivityDetectionIntentService extends IntentService
{
    protected static final String TAG = "WALK360_USER_ACTIVITY";

    //public static final int ACTIVITY_DETECTION_INTERVAL = 20; //20 seconds

    //private ActivityTrackerHelper mActivityTracker;

    //Call the super IntentService constructor with the name for the worker thread
    public ActivityDetectionIntentService()
    {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    // onHandleIntent() method called whenever an activity detection update is available
    @Override
    protected void onHandleIntent(Intent intent)
    {
        //Check whether the Intent contains activity recognition data
        if (ActivityRecognitionResult.hasResult(intent))
        {
            //If data is available, then extract the ActivityRecognitionResult from the Intent
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            //Get an array of DetectedActivity objects
            ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

            boolean determinedUserActivityLevel = false;
            boolean isMoving = false;
            String test = "no-activity";

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
                            if (currActivity.getConfidence() >= 65) {
                                isMoving = true;
                                determinedUserActivityLevel = true;
                                test = currActivity.toString();
                            }
                            break;
                        case DetectedActivity.STILL:
                        case DetectedActivity.IN_VEHICLE:
                        default:
                            //IS NOT MOVING / SITTING
                            if (currActivity.getConfidence() >= 65) {
                                isMoving = false;
                                determinedUserActivityLevel = true;
                                test = currActivity.toString();
                            }
                            break;
                    }
                }
            } //check list of probable activities

        }
    }

    /*public void getQuickRestartOfUserActivity()
    {
        stillStartTime = System.currentTimeMillis();  //wall time

        transitionTimeNanos = mostRecentTransition.getElapsedRealTimeNanos(); //system time - will track exact start of event for chronometer/duration

        isActive
    }*/
}

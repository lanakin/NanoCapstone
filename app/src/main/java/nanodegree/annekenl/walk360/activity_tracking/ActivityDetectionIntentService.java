package nanodegree.annekenl.walk360.activity_tracking;

//references:
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

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

public class ActivityDetectionIntentService extends IntentService
{
    protected static final String TAG = "WALK360_USER_ACTIVITY";

    private ActivityTrackerHelper mActivityTracker;

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


            //part of a strategy to use activity detection to make sure user was moving for 3 minutes
           /* try
            {
                int activeTime = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .getInt(ActivityTrackerHelper.ACTIVE_MIN_GOAL_PROGRESS, 0);

                if(activeTime < 180 && isMoving)  //180 SECONDS = 3 MINUTES
                {
                    activeTime += ActivityTrackerHelper.ACTIVITY_DETECTION_INTERVAL;
                    //Toast.makeText(this, "Keep Going! Try to be active for 3 minutes.",
                           // Toast.LENGTH_LONG).show();

                    PreferenceManager.getDefaultSharedPreferences(this)
                            .edit()
                            .putInt(ActivityTrackerHelper.ACTIVE_MIN_GOAL_PROGRESS, activeTime)
                            .putString(ActivityTrackerHelper.DETECTED_ACTIVITY_KEY, test + " "
                                    + Calendar.getInstance().getTime() + " " + activeTime)  //UI TEST
                            .commit();
                }
                else if(activeTime >= 180) //stop active updates
                {
                    //Toast.makeText(this, "Good job! You have been active for at least 3 minutes.",
                            //Toast.LENGTH_LONG).show();

                    *//**START WAITING FOR TRANSITION BACK TO "STILL"**//*

                    //mActivityTracker = new ActivityTrackerHelper(getApplicationContext());

                    Walk360Application mApplication = (Walk360Application) getApplicationContext();
                    mActivityTracker = mApplication.getmActivityTracker();

                    mActivityTracker.stopActivityDetectionUpdates(); //is this working?  //maybe don't stop transitions - and let the next still start transition stop activity detection?

                    //mActivityTracker.requestActivityTransitionUpdates(); //maybe only worry about stopping this at indicated time in settings?

                    //UPDATE PROGRESS BAR TO FULL? NEED ONE LAST UI UPDATE UNTIL GO BACK TO "STILLNESS"
                    PreferenceManager.getDefaultSharedPreferences(this)
                            .edit()
                            .putString(ActivityTrackerHelper.DETECTED_ACTIVITY_KEY, test
                                        + " " + Calendar.getInstance().getTime() + " " + activeTime)  //UI TEST
                            .commit();

                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
*/
        }
    }
}

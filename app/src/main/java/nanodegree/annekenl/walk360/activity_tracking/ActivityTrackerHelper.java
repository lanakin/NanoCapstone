package nanodegree.annekenl.walk360.activity_tracking;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import nanodegree.annekenl.walk360.MainActivity;
import nanodegree.annekenl.walk360.R;
import nanodegree.annekenl.walk360.utility.TimeHelper;

public class ActivityTrackerHelper
{
    public static final String DETECTED_ACTIVITY_KEY = "DETECTED_ACTIVITY_360";  //test string

    public static final String DETECTED_NON_ACTIVITY_KEY = "DETECTED_NON_ACTIVITY_360"; //still/sitting start time
    public static final long MAX_INACTIVE_TIME_MINUTES = 5;

    public static final String CHRONOMETER_EVENT_START_KEY = "WALK_360_EVENT_START"; //transition start time (active or inactive)
    public static final String IS_ACTIVE_KEY = "WALK_360_ISACTIVE";
    public static final String MAX_SITTING_TIME_KEY = "WALK_360_MAXSIT";
    public static final String MAX_WALKING_TIME_KEY = "WALK_360_MAXWALK";

    private ActivityRecognitionClient mActivityRecognitionClient;
    private PendingIntent mActivityTransIntent;
    private PendingIntent mActivityDetectIntent;
    private Context mContext;

    public ActivityTrackerHelper(Context context)
    {
        mContext = context;

        mActivityRecognitionClient = new ActivityRecognitionClient(context);
    }


    /*ACTIVITY TRANSITION API*/
    public void requestActivityTransitionUpdates()
    {
        ActivityTransitionRequest request = buildTransitionRequest();

        // Your pending intent to receive callbacks.
        mActivityTransIntent = getActivityTransitionPendingIntent();

        Task task = mActivityRecognitionClient
                .requestActivityTransitionUpdates(request, mActivityTransIntent);
        task.addOnSuccessListener(
                new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.i("activityhelper", "Transitions successfully registered.");

                        long currRealTimeNanos = TimeHelper.millisecondsToNanoseconds(SystemClock.elapsedRealtime());
                        //activity transition's time result is in real-time nanoseconds*chronometer is expecting this
                        long currWallTime = System.currentTimeMillis();  //wall time

                        PreferenceManager.getDefaultSharedPreferences(mContext)
                                .edit()
                                .putBoolean(IS_ACTIVE_KEY, false)
                                .putLong(CHRONOMETER_EVENT_START_KEY, currRealTimeNanos)
                                .putLong(DETECTED_NON_ACTIVITY_KEY, currWallTime)
                                .putBoolean(MainActivity.TRACK_STATUS_KEY, true)
                                .commit();
                    }
                });
        task.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Handle failure...
                    }
                });
    }

    ActivityTransitionRequest buildTransitionRequest()
    {
        //in testing, "less is more" it seems, to keep it simple and accurate for this app's goal
        List transitions = new ArrayList<>();
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
        /*transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());*/
        /*transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());*/

        return new ActivityTransitionRequest(transitions);
    }

    /* "The interested activity transitions are specified by the ActivityTransitionRequest and
     * when such transition happens a callback intent will be generated by the provided
     * PendingIntent... associated to a BroadcastReceiver to receive a callback intent"
     * https://proandroiddev.com/new-activity-recognition-transition-api-f4cdb5cd5708
     */
    private PendingIntent getActivityTransitionPendingIntent()
    {
        Intent intent = new Intent(mContext, ActivityTransitionBroadcastReceiver.class);
        intent.setAction(ActivityTransitionBroadcastReceiver.INTENT_ACTION);

        return PendingIntent.getBroadcast(mContext, 200, intent,
                PendingIntent.FLAG_UPDATE_CURRENT); //Perform a broadcast

    }

    public void stopActivityTransitionUpdates()
    {
        Task<Void> task =
                ActivityRecognition.getClient(mContext)
                        .removeActivityTransitionUpdates(mActivityTransIntent);

        task.addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        //mActivityTransIntent.cancel();
                        Log.d("activtyhelper","Transitions successfully stopped");

                        PreferenceManager.getDefaultSharedPreferences(mContext)
                                .edit()
                                .putBoolean(MainActivity.TRACK_STATUS_KEY, false)
                                .commit();
                    }
                });

        task.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("activityhelper", e.getMessage());
                    }
                });
    }



    /* ACTIVITY DETECTION API  --- currently not used in the app */
    public void requestActivityDetectionUpdates(int interval)
    {
        mActivityDetectIntent = getActivityDetectionPendingIntent();

        Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                interval * 1000, //periodic interval in milliseconds
                mActivityDetectIntent);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d("activityhelper","detection successfully started");
            }
        });
    }

    private PendingIntent getActivityDetectionPendingIntent()
    {
        Intent intent = new Intent(mContext, ActivityDetectionIntentService.class);

        return PendingIntent.getService(mContext, 100, intent,
                PendingIntent.FLAG_UPDATE_CURRENT); //Starts IntentService class

    }

    public void stopActivityDetectionUpdates()
    {
        Task<Void> task =
                mActivityRecognitionClient.removeActivityUpdates(mActivityDetectIntent);

        task.addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        //mActivityDetectIntent.cancel();
                        Log.d("activtydetect","stopped");
                    }
                });

        task.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("activityhelper", e.getMessage());
                    }
                });
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

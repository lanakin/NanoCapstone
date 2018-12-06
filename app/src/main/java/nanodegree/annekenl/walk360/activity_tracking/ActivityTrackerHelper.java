package nanodegree.annekenl.walk360.activity_tracking;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

public class ActivityTrackerHelper
{
    public static final String DETECTED_ACTIVITY = "DETECTED_ACTIVITY_360";
    private ActivityRecognitionClient mActivityRecognitionClient;

    private PendingIntent mActivityTransIntent;
    private PendingIntent mActivityDetectIntent;
    private Context mContext;

    public ActivityTrackerHelper(Context context)
    {
        mContext = context;

        mActivityRecognitionClient = new ActivityRecognitionClient(context);
        requestActivityTransitionUpdates();
        //requestActivityDetectionUpdates();
    }

    /* ACTIVITY DETECTION API */
    public void requestActivityDetectionUpdates()
    {
        mActivityDetectIntent = getActivityDetectionPendingIntent();

        Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                10000, //10 seconds interval
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
                mActivityRecognitionClient.removeActivityTransitionUpdates(mActivityDetectIntent);

        task.addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        mActivityDetectIntent.cancel();
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
        List transitions = new ArrayList<>();
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
         /*transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());*/
        /*transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
        */
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
                        mActivityTransIntent.cancel();
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

}

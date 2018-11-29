package nanodegree.annekenl.walk360;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

// some activity recognition code modified from tutorial instructions here:
// https://www.androidauthority.com/using-the-activity-recognition-api-829339/
// and google api example code:
// https://developers.google.com/android/reference/com/google/android/gms/location
// /ActivityRecognitionClient

public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private Context mContext;
    public static final String DETECTED_ACTIVITY = "DETECTED_ACTIVITY_360";
    private ActivityRecognitionClient mActivityRecognitionClient;
    private PendingIntent mPendingIntent;

    //private ListView mTestTV;
    //private ArrayList<String> testTransitions = new ArrayList<String>();
    //private ArrayAdapter mAdapter;
    private TextView mTestTV;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mTestTV = findViewById(R.id.testTV);
       // mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
            //testTransitions);
        //testTransitions.add("TEST");
        //mTestTV.setAdapter(mAdapter);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);

        mActivityRecognitionClient = new ActivityRecognitionClient(this);
        //requestActivityTransitionUpdates(); //this
        requestActivityDetectionUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
        updateTestTV();
    }


    @Override
    protected void onPause() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }


    public void requestActivityDetectionUpdates()
    {
        //Set the activity detection interval. I’m using 3 seconds//
        Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                10000, //10 or 15 seconds
                getActivityDetectionPendingIntent());
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                updateTestTV();
            }
        });
    }


    public void requestActivityTransitionUpdates() //final Context context
    {
        ActivityTransitionRequest request = buildTransitionRequest();

        // Your pending intent to receive callbacks.
        mPendingIntent = getActivityDetectionPendingIntent();

        Task task = mActivityRecognitionClient   //ActivityRecognition.getClient(context)
                .requestActivityTransitionUpdates(request, mPendingIntent);
                task.addOnSuccessListener(
                        new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o)
                            {
                                Log.i("test", "Transitions successfully registered.");
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
                .setActivityType(DetectedActivity.TILTING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
        return new ActivityTransitionRequest(transitions);
    }


    private PendingIntent getActivityDetectionPendingIntent()
    {
        Intent intent = new Intent(this, ActivityDetectionIntentService.class);

        return PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT); //Starts DetectedActivitiesIntentService class

    }


    protected void updateTestTV() {
        String temp =
                PreferenceManager.getDefaultSharedPreferences(mContext)
                        .getString(DETECTED_ACTIVITY, "");

        mTestTV.setText(temp);

        //testTransitions.add(temp);

        //mAdapter.clear();
        //mAdapter.addAll(testTransitions);
        //// mAdapter.notifyDataSetChanged();
        //mTestTV.setAdapter(mAdapter);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(DETECTED_ACTIVITY)) {
            updateTestTV();
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            Fragment frag = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    frag = new HomeScreenFragment();
                    break;
                case R.id.navigation_history:
                    frag = new HistoryScreenFragment();
                    break;
                case R.id.navigation_snacks:
                    frag = new HealthySnacksScreenFragment();
                    break;
                case R.id.navigation_water_calc:
                    frag = new WaterCalculatorScreenFragment();
                    break;
                case R.id.navigation_settings:
                    frag = new SettingsScreenFragment();
                    break;
                default:
                    return false;
            }

            goToChosenScreen(frag);
            return true;
        }
    };

    private void goToChosenScreen(Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.screen_fragment, fragment)
                //.addToBackStack(null) //no specific name
                .commit();
    }

}

package nanodegree.annekenl.walk360;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import nanodegree.annekenl.walk360.activity_tracking.ActivityTrackerHelper;

// some activity recognition code modified from tutorial instructions here:
// https://www.androidauthority.com/using-the-activity-recognition-api-829339/
// and google api example code:
// https://developers.google.com/android/reference/com/google/android/gms/location
// /ActivityRecognitionClient

public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private Context mContext;
    private TextView mTestTV;
    private ActivityTrackerHelper mActivityTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTestTV = findViewById(R.id.testTV);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);

        mContext = this;
        mActivityTracker = new ActivityTrackerHelper(mContext);
        mActivityTracker.requestActivityTransitionUpdates(); //still started ..
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


    protected void updateTestTV() {
        String temp =
                PreferenceManager.getDefaultSharedPreferences(mContext)
                        .getString(ActivityTrackerHelper.DETECTED_ACTIVITY, "");

        mTestTV.setText(temp);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s)
    {
        if (s.equals(ActivityTrackerHelper.DETECTED_ACTIVITY)) {
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

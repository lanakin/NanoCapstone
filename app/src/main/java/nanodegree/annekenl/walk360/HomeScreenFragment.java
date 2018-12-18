package nanodegree.annekenl.walk360;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;

import nanodegree.annekenl.walk360.activity_tracking.ActivityTrackerHelper;
import nanodegree.annekenl.walk360.alarm_manager.AlarmManagerHelper;

public class HomeScreenFragment extends Fragment  implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private Context mContext;
    private Chronometer mChronometer;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HomeScreenFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.home_screen_layout, container, false);

        mChronometer = (Chronometer) rootView.findViewById(R.id.simpleChronometer); // initiate a chronometer

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .registerOnSharedPreferenceChangeListener(this);
        updateChronometer();
    }


    @Override
    public void onPause() {
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }


    protected void updateChronometer()

    {
        boolean isActive = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getBoolean(ActivityTrackerHelper.IS_ACTIVE_KEY, false);

        long startTime = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getLong(ActivityTrackerHelper.CHRONOMETER_EVENT_START_KEY, 0);

        if(startTime!=0)
        {
            startTime = AlarmManagerHelper.nanosecondsToMilliseconds(startTime); //activity transition event time result is in real-time nanoseconds*
            mChronometer.setBase(startTime);
            mChronometer.start(); // start a chronometer
        }

        if(isActive) {
            mChronometer.setTextColor(Color.GREEN);
            mChronometer.setFormat("%s" + " " + getResources().getString(R.string.active_time)); // set the format for a chronometer
        }
        else {
            mChronometer.setTextColor(Color.RED);
            mChronometer.setFormat("%s" + " " + getResources().getString(R.string.inactive_time)); // set the format for a chronometer
        }

        //mChronometer.start(); // start a chronometer
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s)
    {
        if (s.equals(ActivityTrackerHelper.CHRONOMETER_EVENT_START_KEY)) {
            updateChronometer();
        }
    }

}

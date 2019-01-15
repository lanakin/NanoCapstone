package nanodegree.annekenl.walk360;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import nanodegree.annekenl.walk360.activity_tracking.ActivityTrackerHelper;
import nanodegree.annekenl.walk360.utility.TimeHelper;

public class HomeScreenFragment extends Fragment  implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private Context mContext;
    private Chronometer mChronometer;
    private TextView mDate;
    //private TextView homeTV;
    private TextView sittingMaxTV;
    private TextView walkingMaxTV;

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

        mDate = rootView.findViewById(R.id.homeDateTV);
        //homeTV = rootView.findViewById(R.id.homeScreenTV);

        sittingMaxTV = rootView.findViewById(R.id.sitting_max);
        walkingMaxTV = rootView.findViewById(R.id.walking_max);

        return rootView;
    }

    @TargetApi(26)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        String today = "";
        if(VERSION.SDK_INT <= 25) {
           Calendar calendarDate = Calendar.getInstance();
           SimpleDateFormat formatter = new SimpleDateFormat("MM dd, yyyy");
           today += formatter.format(calendarDate);
        } else {
            LocalDate localDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLLL dd, yyyy");
            today += localDate.format(formatter);
        }
        mDate.setText(today);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .registerOnSharedPreferenceChangeListener(this);

        updateChronometer();
        updateMaxTimesTVs();
        //updateTestTV();
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

        mChronometer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

        if (startTime != 0)
        {
            long currTime = TimeHelper.nanosecondsToMilliseconds(startTime); //activity transition's time result is in real-time nanoseconds*
            mChronometer.setBase(currTime);

            if (isActive) {
                String chronoFormat = "%s" + " " + getResources().getString(R.string.active_time);
                mChronometer.setFormat(chronoFormat);
                mChronometer.setTextColor(Color.GREEN);
            } else {
                String chronoFormat = "%s " + " " + getResources().getString(R.string.inactive_time);
                mChronometer.setFormat(chronoFormat);
                mChronometer.setTextColor(Color.RED);
            }

            mChronometer.start();
        }
    }


    private void updateMaxTimesTVs()
    {
        long maxSitTime = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getLong(ActivityTrackerHelper.MAX_SITTING_TIME, 0);

        String maxSitTimeStr = milliSecondsToDisplayStr(maxSitTime);
        sittingMaxTV.setText(maxSitTimeStr);

        long maxWalkTime = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getLong(ActivityTrackerHelper.MAX_WALKING_TIME, 0);

        String maxWalkTimeStr = milliSecondsToDisplayStr(maxWalkTime);
        walkingMaxTV.setText(maxWalkTimeStr);
    }

    /* reference:
       https://www.skptricks.com/2018/09/convert-milliseconds-into-days-hours-minutes-seconds-in-java.html
       Determined with TimeUnit
     */
    private String milliSecondsToDisplayStr(long milliseconds)
    {
        final long hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(milliseconds));

        final long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds));

        final long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds));

        return String.format("%d Hour(s) \n%d Minute(s) \n%d Second(s)", hours, minutes, seconds);
    }


  /*  private void updateTestTV()
    {
        String temp =
                PreferenceManager.getDefaultSharedPreferences(mContext)
                        .getString(ActivityTrackerHelper.DETECTED_ACTIVITY_KEY, "");

        temp = temp.replace("Still Started","Walking Started At: ");
        temp = temp.replace("Still Stopped", "Sitting Started At: ");
        homeTV.setText(temp);
    }*/


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s)
    {
        if (s.equals(ActivityTrackerHelper.CHRONOMETER_EVENT_START_KEY)) {
            updateChronometer();
            updateMaxTimesTVs();
            //updateTestTV();
        }
        else if(s.equals(ActivityTrackerHelper.DETECTED_ACTIVITY_KEY)) {
            //updateTestTV();
        }
    }

}

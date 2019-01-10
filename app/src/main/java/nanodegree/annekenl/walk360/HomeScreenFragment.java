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
    //public static final String MAX_SITTING_STR = "WALK_360_MAXSIT_STR";
    //public static final String MAX_WALKING_STR = "WALK_360_MAXWALK_STR";
    private Context mContext;
    private Chronometer mChronometer;
    private TextView mDate;
    private TextView homeTV;
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
        homeTV = rootView.findViewById(R.id.homeScreenTV);

        sittingMaxTV = rootView.findViewById(R.id.sitting_max);
        walkingMaxTV = rootView.findViewById(R.id.walking_max);

        return rootView;
    }

    @TargetApi(26)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        updateChronometer();

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
        updateTestTV();
        updateMaxTimesTVs();
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

            mChronometer.start(); // start a chronometer
        }
    }

    private void updateTestTV()
    {
        String temp =
                PreferenceManager.getDefaultSharedPreferences(mContext)
                        .getString(ActivityTrackerHelper.DETECTED_ACTIVITY_KEY, "");
        homeTV.setText(temp);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s)
    {
        if (s.equals(ActivityTrackerHelper.CHRONOMETER_EVENT_START_KEY)) {
            updateChronometer();
            updateMaxTimesTVs();
        }
        else if(s.equals(ActivityTrackerHelper.DETECTED_ACTIVITY_KEY)) {
            updateTestTV();
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

    /*
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

        return String.format("%d Hours \n%d Minutes \n%d Seconds", hours, minutes, seconds);
    }


    /* called before re-starting the chronometer*/
   /* private void saveActivityDuration()
    {
        boolean isActiveNow = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getBoolean(ActivityTrackerHelper.IS_ACTIVE_KEY, false);

        //long elapsedMillis = SystemClock.elapsedRealtime() - mChronometer.getBase(); //need to determine if greater than previous saved duration
        Long currElapsedDuration = parseChronometerTimeByIntParts();

        String chronoTimeStr = parseChronometerTimeByStrParts();

        if(isActiveNow) //saved previous duration of inactive/sitting time
        {
            long prevMaxSitTime = PreferenceManager.getDefaultSharedPreferences(mContext)
                    .getLong(MAX_SITTING_TIME, 0);

            if(currElapsedDuration > prevMaxSitTime)
            {
                PreferenceManager.getDefaultSharedPreferences(mContext)
                        .edit()
                        .putLong(MAX_SITTING_TIME,currElapsedDuration)
                        .putString(MAX_SITTING_STR,chronoTimeStr)
                        .commit();

                sittingMaxTV.setText(chronoTimeStr);
            }
        }
        else //saved previous duration of walking time
        {
            long prevMaxWalkTime = PreferenceManager.getDefaultSharedPreferences(mContext)
                    .getLong(MAX_WALKING_TIME, 0);

            if(currElapsedDuration > prevMaxWalkTime)
            {
                PreferenceManager.getDefaultSharedPreferences(mContext)
                        .edit()
                        .putLong(MAX_WALKING_TIME,currElapsedDuration)
                        .putString(MAX_WALKING_STR,chronoTimeStr)
                        .commit();

                walkingMaxTV.setText(chronoTimeStr);
            }
        }
    }*/


  /*  //"%s" + " " + "Active/InActive Time" - chrono. display format
    private String parseChronometerTimeByStrParts()
    {
        String chronoStr = mChronometer.getText().toString();
        String temp[] = chronoStr.trim().split(" ");
        chronoStr = temp[0];

        String timeParts[] = chronoStr.split(":"); //00:00:00
        String ret = "0";

        if(timeParts.length == 3)
            ret = timeParts[0] + " hours" + timeParts[1] + " minutes"; //leaving off seconds
        else if(timeParts.length == 2)
            ret = timeParts[0] + " minutes"; //leaving off seconds
        else if(timeParts.length == 1)
            ret = timeParts[0] + " seconds";

        return ret;
    }


    //"%s" + " " + "Active/InActive Time" - chrono. display format
    private long parseChronometerTimeByIntParts()
    {
        String chronoStr = mChronometer.getText().toString();
        String temp[] = chronoStr.trim().split(" ");
        chronoStr = temp[0];

        String timeParts[] = chronoStr.split(":"); //00:00:00
        long totalMinutes = 0;

        try {
            if (timeParts.length == 3)  //hours, minutes, seconds
            {
                int hours = Integer.parseInt(timeParts[0]);
                int minutes = Integer.parseInt(timeParts[1]);

                totalMinutes = (hours*60) + minutes; //leaving off seconds
            }
            else if (timeParts.length == 2)  //minutes,seconds
                totalMinutes = Integer.parseInt(timeParts[0]); //leaving off seconds
            else if (timeParts.length == 1)
                totalMinutes = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return totalMinutes;
    }*/
}

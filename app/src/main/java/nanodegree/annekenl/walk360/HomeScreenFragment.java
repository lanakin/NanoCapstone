package nanodegree.annekenl.walk360;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import nanodegree.annekenl.walk360.activity_tracking.ActivityTrackerHelper;
import nanodegree.annekenl.walk360.utility.TimeHelper;
import nanodegree.annekenl.walk360.water.WaterCalculatorScreenFragment;

public class HomeScreenFragment extends Fragment  implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private Context mContext;
    private Chronometer mChronometer;
    private TextView mDate;
    private TextView sittingMaxTV;
    private TextView walkingMaxTV;
    private TextView waterTotalTV;
    private Button addWater;
    private Button subWater; //basically to allow user to undo a mistaken amt.

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
        sittingMaxTV = rootView.findViewById(R.id.sitting_max);
        walkingMaxTV = rootView.findViewById(R.id.walking_max);
        waterTotalTV = rootView.findViewById(R.id.water_total);
        addWater = rootView.findViewById(R.id.waterButton);
        subWater = rootView.findViewById(R.id.waterButtonSub);

        addWater.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                addWaterAmt();
            }
        });

        subWater.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                subWaterAmt();
            }
        });

        return rootView;
    }

    @TargetApi(26)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .registerOnSharedPreferenceChangeListener(this);

        updateDateTV();
        updateActivityChronometer(mChronometer, mContext);
        updateMaxTimesTVs();
        updateWaterTotalTV();
    }


    @Override
    public void onPause() {
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    /* hoping to re-use this method with walk360 widget but not currently setup in a way to do that with the widget's limitations*/
    protected void updateActivityChronometer(Chronometer theChronometer, Context context)
    {
        boolean isTracking = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(getString(R.string.track_status_key), false);

        if(isTracking) {
            boolean isActive = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(ActivityTrackerHelper.IS_ACTIVE_KEY, false);

            long startTime = PreferenceManager.getDefaultSharedPreferences(context)
                    .getLong(ActivityTrackerHelper.CHRONOMETER_EVENT_START_KEY, 0);

            theChronometer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

            if (startTime != 0) {
                long currTime = TimeHelper.nanosecondsToMilliseconds(startTime); //activity transition's time result is in real-time nanoseconds*
                theChronometer.setBase(currTime);

                if (isActive) {
                    String chronoFormat = "%s" + " " + context.getApplicationContext().getResources().getString(R.string.active_time);
                    theChronometer.setFormat(chronoFormat);
                    theChronometer.setTextColor(Color.GREEN);
                } else {
                    String chronoFormat = "%s " + " " + context.getApplicationContext().getResources().getString(R.string.inactive_time);
                    theChronometer.setFormat(chronoFormat);
                    theChronometer.setTextColor(Color.RED);
                }

                theChronometer.start();
            }
        }
        else {
            theChronometer.setBase(SystemClock.elapsedRealtime());
            theChronometer.setFormat("%s");
            theChronometer.setTextColor(Color.BLACK);
            theChronometer.stop();
        }
    }


    private void updateMaxTimesTVs()
    {
        long maxSitTime = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getLong(ActivityTrackerHelper.MAX_SITTING_TIME_KEY, 0);

        String maxSitTimeStr = milliSecondsToDisplayStr(maxSitTime);
        sittingMaxTV.setText(maxSitTimeStr);

        long maxWalkTime = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getLong(ActivityTrackerHelper.MAX_WALKING_TIME_KEY, 0);

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


    private void updateWaterTotalTV()
    {
        float waterTotalOZ =
                PreferenceManager.getDefaultSharedPreferences(mContext)
                        .getFloat(WaterCalculatorScreenFragment.WATER_DAILY_TOTAL_KEY, 0);

        float waterTotalCups = waterTotalOZ/8;

        waterTotalTV.setText(getString(R.string.todays_total)+": "+String.format("%.2f",waterTotalOZ) +" "+getString(R.string.ounces)
                + " (" + String.format("%.2f", waterTotalCups) +" "+getString(R.string.cups)+")");
    }

    private void updateDateTV()
    {
        String today = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(Walk360Application.TODAY_STR_KEY, "");
        mDate.setText(today);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s)
    {
        if(s.equals(getString(R.string.track_status_key))) {
            updateActivityChronometer(mChronometer, mContext);
        }
        else if (s.equals(ActivityTrackerHelper.CHRONOMETER_EVENT_START_KEY)) {
            updateActivityChronometer(mChronometer, mContext);
            updateMaxTimesTVs();
            //updateTestTV();
        }
        else if(s.equals(WaterCalculatorScreenFragment.WATER_DAILY_TOTAL_KEY)) {
            updateWaterTotalTV();
        }
        else if(s.equals(Walk360Application.TODAY_STR_KEY)) {
            updateDateTV();
        }
    }


    private void addWaterAmt()
    {
        final float waterTotalOZ = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getFloat(WaterCalculatorScreenFragment.WATER_DAILY_TOTAL_KEY, 0);

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getActivity());
        }

        // Set up the input
        final EditText input = new EditText(getActivity());

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);

        builder.setTitle(getString(R.string.add_water_amt))
                .setMessage(R.string.add_water_instruct)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            float waterOZ = Float.parseFloat(input.getText().toString());

                            float newTotal = waterTotalOZ + waterOZ;

                            PreferenceManager.getDefaultSharedPreferences(mContext)
                                    .edit()
                                    .putFloat(WaterCalculatorScreenFragment.WATER_DAILY_TOTAL_KEY, newTotal)
                                    .commit();
                        } catch (Exception e) {
                            Toast.makeText(mContext, R.string.water_input_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    private void subWaterAmt()
    {
        final float waterTotalOZ =
                PreferenceManager.getDefaultSharedPreferences(mContext)
                        .getFloat(WaterCalculatorScreenFragment.WATER_DAILY_TOTAL_KEY, 0);

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getActivity());
        }

        // Set up the input
        final EditText input = new EditText(getActivity());

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);

        builder.setTitle(R.string.sub_water_amt)
                .setMessage(R.string.sub_water_instruct)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            float waterOZ = Float.parseFloat(input.getText().toString());

                            float newTotal = waterTotalOZ - waterOZ;

                            PreferenceManager.getDefaultSharedPreferences(mContext)
                                    .edit()
                                    .putFloat(WaterCalculatorScreenFragment.WATER_DAILY_TOTAL_KEY, newTotal)
                                    .commit();
                        } catch (Exception e) {
                            Toast.makeText(mContext, R.string.water_input_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}

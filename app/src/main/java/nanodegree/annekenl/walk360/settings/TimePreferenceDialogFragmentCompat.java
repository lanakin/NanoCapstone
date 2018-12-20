package nanodegree.annekenl.walk360.settings;

import android.content.Context;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.widget.TimePicker;

//reference: https://medium.com/@JakobUlbrich/building-a-settings-screen-for-android-part-3-ae9793fd31ec
//reference: https://stackoverflow.com/questions/5533078/timepicker-in-preferencescreen/10608622 - code
//solution - Dalija Prasnikar
public class TimePreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat implements DialogPreference.TargetFragment
{
    TimePicker timePicker = null;

    @Override
    protected View onCreateDialogView(Context context)
    {
        timePicker = new TimePicker(context);
        return (timePicker);
    }

    @Override
    protected void onBindDialogView(View v)
    {
        super.onBindDialogView(v);
        timePicker.setIs24HourView(false); //true

        // Get the time from the related Preference
        TimePreference pref = (TimePreference) getPreference();
        // Set the time to the TimePicker
        timePicker.setCurrentHour(pref.hour);
        timePicker.setCurrentMinute(pref.minute);
    }

    //save the selected time when we click the OK button (positive result).
    @Override
    public void onDialogClosed(boolean positiveResult)
    {
        if (positiveResult)
        {
            TimePreference pref = (TimePreference) getPreference();
            pref.hour = timePicker.getCurrentHour();
            pref.minute = timePicker.getCurrentMinute();

            String value = TimePreference.timeToString(pref.hour, pref.minute);

            if (pref.callChangeListener(value)) pref.persistStringValue(value); // Save the value
            //	callChangeListener(Object newValue)
            //Call this method after the user changes the preference, but before the internal state is set.
        }
    }

    @Override
    public Preference findPreference(CharSequence charSequence)
    {
        return getPreference();
    }
}

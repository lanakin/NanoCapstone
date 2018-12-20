package nanodegree.annekenl.walk360.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;


//reference: https://medium.com/@JakobUlbrich/building-a-settings-screen-for-android-part-3-ae9793fd31ec
//reference: https://stackoverflow.com/questions/5533078/timepicker-in-preferencescreen/10608622 - code
//solution - Dalija Prasnikar
public class TimePreference extends DialogPreference
{
    protected int hour = 0;
    protected int minute = 0;

    public TimePreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index)
    {
        // Default value from attribute.
        return a.getString(index);
    }

    /* Implement this to set the initial value of the Preference.
     * If restorePersistedValue is true, you should restore the Preference value from the SharedPreferences.
     * If restorePersistedValue is false, you should set the Preference value to defaultValue that is given
     * (and possibly store to SharedPreferences if shouldPersist() is true).
     * In case of using PreferenceDataStore, the restorePersistedValue is always true but the
     * default value (if provided) is set.
     *
     * Preference.onSetInitialValue(boolean, Object) has been deprecated and replaced with onSetInitialValue(Object).
     * PreferenceDataStore now also correctly restores default values.  ---support 28.0.0
     */
    @Override
   /* protected void onSetInitialValue(boolean restoreValue, Object defaultValue)
    {
        String value;
       if (restoreValue)
        {
            if (defaultValue == null) value = getPersistedString("00:00"); //default value can be set in xml
            else value = getPersistedString(defaultValue.toString());
        }
        else
        {
            value = defaultValue.toString();
        }

        hour = parseHour(value);
        minute = parseMinute(value);
    }*/
    protected void onSetInitialValue(Object defaultValue)
    {
        String value;

        if(defaultValue == null)
           value = getPersistedString("00:00"); //default value can be set in xml
        else
            value = getPersistedString(defaultValue.toString());

        hour = parseHour(value);
        minute = parseMinute(value);
    }

    //save to shared preferences
    public void persistStringValue(String value)
    {
        persistString(value);
    }


    public static String timeToString(int h, int m)
    {
        return String.format("%02d", h) + ":" + String.format("%02d", m);
    }


    public static int parseHour(String value)
    {
        try
        {
            String[] time = value.split(":");
            return (Integer.parseInt(time[0]));
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    public static int parseMinute(String value)
    {
        try
        {
            String[] time = value.split(":");
            return (Integer.parseInt(time[1]));
        }
        catch (Exception e)
        {
            return 0;
        }
    }

}
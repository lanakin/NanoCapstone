package nanodegree.annekenl.walk360.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import nanodegree.annekenl.walk360.R;

public class SettingsScreenFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener
{
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SettingsScreenFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.settings, rootKey);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        Preference preference = findPreference(key);

        if (preference != null)
        {
            if(key.equals(getResources().getString(R.string.reminders_switch_walk360_key)))
            {
                boolean value = sharedPreferences.getBoolean(key, false);
                Log.d("settings_prefs",value+"");
            }
            else if(key.equals(getResources().getString(R.string.days_walk360_key)))
            {
                //multi list
                Set<String> value = new HashSet<String>();
                value = sharedPreferences.getStringSet(key,value);
                Log.d("settings_prefs",value.toString());  //days as DAYS enums: sun = 1 to sat = 7
            }
            else if(key.equals(getResources().getString(R.string.start_time_walk360_key)))
            {
                //time custom pref
                String value = sharedPreferences.getString(key, "");
                Log.d("settings_prefs",value); //time string in 24 hour format
            }
            else if(key.equals(getResources().getString(R.string.end_time_walk360_key)))
            {
                //time custom pref
                String value = sharedPreferences.getString(key, "");
                Log.d("settings_prefs",value); //time string in 24 hour format 
            }
         }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onDisplayPreferenceDialog(Preference preference)
    {
        DialogFragment dialogFragment = null;
        if (preference instanceof TimePreference)
        {
            dialogFragment = new TimePreferenceDialogFragmentCompat();
            Bundle bundle = new Bundle(1);
            bundle.putString("key", preference.getKey());
            dialogFragment.setArguments(bundle);
        }

        // If it was the custom Preferences, show its dialog
        if (dialogFragment != null)
        {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(this.getFragmentManager(), "android.support.v7.preference.PreferenceFragment.DIALOG");
        }
        else
        {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
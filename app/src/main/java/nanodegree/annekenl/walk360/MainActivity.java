package nanodegree.annekenl.walk360;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.AuthUI.IdpConfig;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nanodegree.annekenl.walk360.activity_tracking.ActivityTrackerHelper;
import nanodegree.annekenl.walk360.firestore.FirestoreHelper;
import nanodegree.annekenl.walk360.healthy_snacks.HealthySnacksScreenFragment;
import nanodegree.annekenl.walk360.history.HistoryScreenFragment;
import nanodegree.annekenl.walk360.settings.SettingsScreenFragment;
import nanodegree.annekenl.walk360.utility.TimeHelper;
import nanodegree.annekenl.walk360.water.WaterCalculatorScreenFragment;

// references:
//https://developers.google.com/android/reference/com/google/android/gms/common/GoogleApiAvailability
//https://stackoverflow.com/questions/22493465/check-if-correct-google-play-service-available-unfortunately-application-has-s
public class MainActivity extends AppCompatActivity
{
    public static final String AUTH_STATUS_KEY = "AUTH_STATUS_KEY";
    public static final String AUTH_USERID_KEY = "AUTH_USERID_KEY";
    public static final String TRACK_STATUS_KEY = "TRACK_STATUS_KEY";

    public final static String CHANNEL_ID = "TIME_TO_MOVE";
    public final static int RC_SIGN_IN = 500;

    private FirebaseUser mUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);

        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString(ActivityTrackerHelper.DETECTED_ACTIVITY_KEY, "test")
                .commit();
    }

    @Override
    protected void onResume() {

        super.onResume();

        boolean isSignedIn = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(AUTH_STATUS_KEY, false);

        checkGooglePlayServiceAvailability();

        if(!isSignedIn) {
            authenticate();
        } else {
            checkForDataStoreAndReset();
        }

        createNotificationChannel(); //if already exists, does nothing
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void checkForDataStoreAndReset()
    {
        boolean isSignedIn = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(AUTH_STATUS_KEY, false);

        if(!isSignedIn) {
            authenticate();
        } else {
            if(isANewDay())  //stored into firebase the previous day's data**
            {
                FirestoreHelper mFirestoreHelper = new FirestoreHelper();

                //documents
                String theUserID = PreferenceManager.getDefaultSharedPreferences(this)
                        .getString(AUTH_USERID_KEY, "");
                if (theUserID.isEmpty()) {
                    //alert error - and authenticate... to do later test if this is needed
                    theUserID = "error";
                }

                     //the three letter abbreviation of the day is the key for weekday "row"/document in firestore db
                        // - later might want to change this so that it doesn't depend on USA spellings/Locale
                String theDayID = "error";
                String tempDayStr = PreferenceManager.getDefaultSharedPreferences(this)
                        .getString(Walk360Application.TODAY_STR_KEY, "");
                String tempParts[] = tempDayStr.split(" "); //"Thu 01/17"
                if (tempParts.length > 0)
                    theDayID = tempParts[0];

                //data to store
                Map<String, Object> yesterdayData = new HashMap<>();
                yesterdayData.put("date_str", PreferenceManager.getDefaultSharedPreferences(this)
                        .getString(Walk360Application.TODAY_STR_KEY, ""));
                yesterdayData.put("max_walk", PreferenceManager.getDefaultSharedPreferences(this)
                        .getLong(ActivityTrackerHelper.MAX_WALKING_TIME_KEY, 0));
                yesterdayData.put("max_sit", PreferenceManager.getDefaultSharedPreferences(this)
                        .getLong(ActivityTrackerHelper.MAX_SITTING_TIME_KEY, 0));
                yesterdayData.put("water_total", PreferenceManager.getDefaultSharedPreferences(this)
                        .getFloat(WaterCalculatorScreenFragment.WATER_DAILY_TOTAL_KEY, 0));

                mFirestoreHelper.storeADailyTotals(theUserID, theDayID, yesterdayData);

                //reset local data for current day
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putLong(ActivityTrackerHelper.MAX_SITTING_TIME_KEY, 0)
                        .putLong(ActivityTrackerHelper.MAX_WALKING_TIME_KEY, 0)
                        .putFloat(WaterCalculatorScreenFragment.WATER_DAILY_TOTAL_KEY, 0)
                        .putString(Walk360Application.TODAY_STR_KEY, TimeHelper.getTodayStr())
                        .commit();

                Toast.makeText(this, "Daily Totals reset for a new day!", Toast.LENGTH_SHORT);
            }
        }
    }

    public boolean isANewDay()
    {
        String storedDayStr = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(Walk360Application.TODAY_STR_KEY, "");

        String currDayStr = TimeHelper.getTodayStr();

        boolean ret;

        if(currDayStr.equals(storedDayStr)) {
            ret = false;
        }
        else {
            ret = true;
        }

        return ret;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.user_signin) {
            boolean isSignedIn = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .getBoolean(AUTH_STATUS_KEY, false);

            if(!isSignedIn)
                authenticate();
            else
                signout();
        }
        else if (item.getItemId()==R.id.track_activity) {
            boolean isTracking = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .getBoolean(TRACK_STATUS_KEY, false);

            if(!isTracking) {
               resumeTracking();
            }
            else {
               stopTracking();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void resumeTracking()
    {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Activity Tracking")
                .setMessage("Do you want the app to start tracking activity?"
                        +"\n\n Note: It may initially take a minute or two for your device to detect walking activity."
                        +"\n\n Activity Tracking can also be managed by navigating to the Settings Tab.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //requestActivitytrans
                        try {
                            Walk360Application mApplication = (Walk360Application) getApplication();
                            mApplication.getmActivityTracker().requestActivityTransitionUpdates();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void stopTracking()
    {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Activity Tracking")
                .setMessage("Do you want the app to stop tracking walking activity?"
                        +"\n\n Activity Tracking can also be managed by navigating to the Settings Tab.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //stopActivityTrans
                        try {
                            Walk360Application mApplication = (Walk360Application) getApplication();
                            mApplication.getmActivityTracker().stopActivityTransitionUpdates();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    private void signout()
    {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Log Out")
                .setMessage("Are you sure you want to log out? The app will close and you must sign in again later.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AuthUI.getInstance()
                                .signOut(getApplicationContext())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d("signout", "completed");
                                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                                .edit()
                                                .putBoolean(AUTH_STATUS_KEY, false)
                                                //.putString(AUTH_USERID_KEY, "") //will only overwrite with new authenticated user
                                                .commit();
                                        mUser = null;

                                        finish(); //close app - current design i need an unique user to save data for in firebase - perhaps in future switch to a local database instead
                                    }
                                });
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void authenticate()
    {
        // Choose authentication providers
        List<IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                mUser = FirebaseAuth.getInstance().getCurrentUser();
                Log.d("myfirstore","user id: "+mUser.getUid());

                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putString(AUTH_USERID_KEY, mUser.getUid())
                        .putBoolean(AUTH_STATUS_KEY, true)
                        .commit();

                checkForDataStoreAndReset();

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                Log.d("signin", response.getError().getErrorCode()+"");
                Toast.makeText(this, getResources().getString(R.string.user_signin_error_msg), Toast.LENGTH_SHORT);
            }
        }
    }


    private void checkGooglePlayServiceAvailability()
    {
        // Google Play Services check - needed for Firebase ~push
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this,status,1111).show();
            }

            //googleApiAvailability.makeGooglePlayServicesAvailable(this);
        }
    }


    private void createNotificationChannel()
    {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notif_channel_name);
            String description = getString(R.string.notif_channel_desc);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    /* NAVIGATION */
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

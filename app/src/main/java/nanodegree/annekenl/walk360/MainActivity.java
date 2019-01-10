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
import java.util.List;

import nanodegree.annekenl.walk360.settings.SettingsScreenFragment;

// references:
//https://developers.google.com/android/reference/com/google/android/gms/common/GoogleApiAvailability
//https://stackoverflow.com/questions/22493465/check-if-correct-google-play-service-available-unfortunately-application-has-s
public class MainActivity extends AppCompatActivity
{
    public static final String AUTH_STATUS = "AUTH_STATUS";
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

       /* FloatingActionButton fab = findViewById(R.id.fab);
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_notifications_black_24dp));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                       // .setAction("Action", null).show();
            }
        });*/

        //sign-in
        boolean isSignedIn = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(AUTH_STATUS, false);

        if(!isSignedIn)
            authenticate();

        checkGooglePlayServiceAvailability();
        createNotificationChannel();
    }

    @Override
    protected void onResume() {

        super.onResume();

        checkGooglePlayServiceAvailability();
        createNotificationChannel(); //if already exists, does nothing
    }

    @Override
    protected void onPause() {
        super.onPause();
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
                    .getBoolean(AUTH_STATUS, false);

            if(!isSignedIn)
                authenticate();
            else
                signout();
        }

        return super.onOptionsItemSelected(item);
    }

    private void signout()
    {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AuthUI.getInstance()
                                .signOut(getApplicationContext())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d("signout", "completed");
                                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                                .edit()
                                                .putBoolean(AUTH_STATUS, false)
                                                .commit();
                                        mUser = null;
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
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit()
                        .putBoolean(AUTH_STATUS, true)
                        .commit();

                mUser = FirebaseAuth.getInstance().getCurrentUser();

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

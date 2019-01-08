package nanodegree.annekenl.walk360.notifications;

//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;

//https://firebase.google.com/docs/cloud-messaging/android/client?authuser=0

public class MyFirebaseMessagingService{} /*extends FirebaseMessagingService {

    public static final String TAG = "MsgFirebaseServ";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //"To receive notifications in foregrounded apps, to receive data payload,
        // to send upstream messages"
    }


    *//**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     *//*
    @Override
    public void onNewToken(String token)
    {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    *//**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     *//*
    private void sendRegistrationToServer(String token) {
        // Implement this method to send token to your app server.
    }
}*/
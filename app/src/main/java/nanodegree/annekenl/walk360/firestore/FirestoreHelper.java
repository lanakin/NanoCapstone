package nanodegree.annekenl.walk360.firestore;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import nanodegree.annekenl.walk360.data.SingleDayTotals;

public class FirestoreHelper
{
    public static final String USERS_COLLECTION_FIREDB = "walk360users";
    public static final String ACTIVITY_TOTALS_COLLECTION_FIREDB = "dailytotals";

    private  FirebaseFirestore db;
    private OnDailyTotalsReadFinished readFinishedListener;

    public FirestoreHelper() {
        getDatabase();
    }

    public FirestoreHelper(OnDailyTotalsReadFinished listener) {
        getDatabase();
        readFinishedListener = listener;
    }

    private void getDatabase()
    {
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        // With this change, timestamps stored in Cloud Firestore will be read back as
        // com.google.firebase.Timestamp objects instead of as system java.util.Date objects.
        // So you will also need to update code expecting a java.util.Date to instead expect a Timestamp.
        FirebaseFirestore.setLoggingEnabled(true);
    }


    public interface OnDailyTotalsReadFinished
    {
        void onDailyTotalsReadFinished();
    }

    public void readDailyTotals(String theUserID, final HashMap<String, SingleDayTotals> mDailyTotalsData)
    {
        CollectionReference dailyTotalsRef = db
                .collection(USERS_COLLECTION_FIREDB).document(theUserID)
                .collection(ACTIVITY_TOTALS_COLLECTION_FIREDB);

        dailyTotalsRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                //Log.d("myfirestore", document.getId() + " => " + document.getData());
                                SingleDayTotals currSingleDayTotals = document.toObject(SingleDayTotals.class);
                                mDailyTotalsData.put(document.getId(),currSingleDayTotals);
                            }
                        }
                        else {
                            Log.w("myfirestore", "Error getting documents.", task.getException());
                        }
                        readFinishedListener.onDailyTotalsReadFinished();
                    }
                });
    }


    /* ALSO UPDATE - If the document does not exist, it will be created. If the document does
     * exist, its contents will be overwritten with the newly provided data, unless you specify that
     * the data should be merged into the existing document
     *
     * Overwriting exist data will work for this app, only storing and displaying the past week /
     * past 7 days that user interacted with and generated data for the app.
     */
    public void storeADailyTotals(String theUserID, String theDayID, Map<String,Object> theData)
    {
        //Log.d("myfirestore",theUserID);

        DocumentReference dayTotalRef = db
                .collection(USERS_COLLECTION_FIREDB).document(theUserID)
                .collection(ACTIVITY_TOTALS_COLLECTION_FIREDB).document(theDayID);

        dayTotalRef
                .set(theData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d("myfirestore", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("myfirestore", "Error writing document", e);
                    }
                });
    }

}

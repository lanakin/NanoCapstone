package nanodegree.annekenl.walk360.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

/* IntentService is a base class for Services that handle asynchronous requests (expressed as Intents) on demand.
Clients send requests through Context.startService(Intent) calls; the service is started as needed, handles each
Intent in turn using a worker thread, and stops itself when it runs out of work.
https://developer.android.com/reference/android/app/IntentService

https://stackoverflow.com/questions/46445265/android-8-0-java-lang-illegalstateexception-not-allowed-to-start-service-inten
**Need to switch to JobIntentService for Android 8.0+
 */
public class UpdateWidgetService extends JobIntentService
{
    public static final int JOB_ID = 1;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, UpdateWidgetService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent)
    {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this
                .getApplicationContext());

        ComponentName theWidget = new ComponentName(this, Walk360Widget.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(theWidget);

        for (int widgetId : allWidgetIds) {
            Walk360Widget.updateAppWidget(getApplicationContext(), appWidgetManager, widgetId);
        }
    }
}


/*
extends IntentService
    {
        private static final String TAG = "nanodegree.annekenl.walk360.widget.service";

        //Call the super IntentService constructor with the name for the worker thread
    public UpdateWidgetService()
        {
            super(TAG);
        }

        @Override
        public void onCreate() {
        super.onCreate();
    }

        @Override
        protected void onHandleIntent(Intent intent)
        {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this
                    .getApplicationContext());

            ComponentName theWidget = new ComponentName(this, Walk360Widget.class);
            int[] allWidgetIds = appWidgetManager.getAppWidgetIds(theWidget);

            for (int widgetId : allWidgetIds) {
                Walk360Widget.updateAppWidget(getApplicationContext(), appWidgetManager, widgetId);
            }
        }

        @Override
        public IBinder onBind(Intent intent) {
        return null;
    }
 */

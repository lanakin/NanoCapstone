package nanodegree.annekenl.walk360.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;

/* IntentService is a base class for Services that handle asynchronous requests (expressed as Intents) on demand.
Clients send requests through Context.startService(Intent) calls; the service is started as needed, handles each
Intent in turn using a worker thread, and stops itself when it runs out of work.
https://developer.android.com/reference/android/app/IntentService
 */
public class UpdateWidgetService extends IntentService
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
}

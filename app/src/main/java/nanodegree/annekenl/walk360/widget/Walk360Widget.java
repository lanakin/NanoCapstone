package nanodegree.annekenl.walk360.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Color;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import nanodegree.annekenl.walk360.MainActivity;
import nanodegree.annekenl.walk360.R;
import nanodegree.annekenl.walk360.activity_tracking.ActivityTrackerHelper;
import nanodegree.annekenl.walk360.utility.TimeHelper;

/**
 * Implementation of App Widget functionality.
 */
public class Walk360Widget extends AppWidgetProvider
{
    /* RemoteViews method for Chronometer: views.setChronometer(viewid,base,format,boolean-started); */
    static void updateWidgetChronometerInfo(RemoteViews views, Context context)
    {
        boolean isTracking = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(MainActivity.TRACK_STATUS_KEY, false);

        if(isTracking) {
            boolean isActive = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean(ActivityTrackerHelper.IS_ACTIVE_KEY, false);

            long startTime = PreferenceManager.getDefaultSharedPreferences(context)
                    .getLong(ActivityTrackerHelper.CHRONOMETER_EVENT_START_KEY, 0);

            long currTime = 0;
            String chronoFormat = "";

            if (startTime != 0) {
                currTime = TimeHelper.nanosecondsToMilliseconds(startTime); //activity transition's time result is in real-time nanoseconds*

                if (isActive) {
                    chronoFormat = "%s" + " " + context.getApplicationContext().getResources().getString(R.string.active_time);
                    views.setTextColor(R.id.widgetChronometer, Color.GREEN);

                } else {
                    chronoFormat = "%s " + " " + context.getApplicationContext().getResources().getString(R.string.inactive_time);
                    views.setTextColor(R.id.widgetChronometer, Color.RED);
                }

                views.setChronometer(R.id.widgetChronometer,currTime,chronoFormat,true);
            }
        }
        else {
            views.setChronometer(R.id.widgetChronometer,SystemClock.elapsedRealtime(),"%s",false);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId)
    {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.walk360_widget);

        updateWidgetChronometerInfo(views,context);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        /* handled in updatewidgetservice class*/

        // There may be multiple widgets active, so update all of them
        //for (int appWidgetId : appWidgetIds) {
           // updateAppWidget(context, appWidgetManager, appWidgetId);
        //}
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}


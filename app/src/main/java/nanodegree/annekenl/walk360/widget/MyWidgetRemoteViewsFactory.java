package nanodegree.annekenl.walk360.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

/**
 * Created by annekenl
 *
 * References: https://www.sitepoint.com/killer-way-to-show-a-list-of-items-in-android-collection-widget/
 * Udacity Android Nanodegree widget lessons
 * http://www.vogella.com/tutorials/AndroidWidgets/article.html
 * https://laaptu.wordpress.com/2013/07/24/populate-appwidget-listview-with-remote-datadata-from-web/
 * and others listed in MyWidgetProvider.
 */

public class MyWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory
{
    private Context mContext;


    public MyWidgetRemoteViewsFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;
    }

    //called when the appwidget is created for the first time.
    @Override
    public void onCreate() { }

    @Override
    public void onDestroy() { }


    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public RemoteViews getViewAt(int i) {
        return null;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public void onDataSetChanged() { }
}

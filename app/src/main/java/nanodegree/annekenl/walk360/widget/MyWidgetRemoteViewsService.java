package nanodegree.annekenl.walk360.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by annekenl
 *
 * "The main purpose of RemoteViewsService is to return a RemoteViewsFactory object
 *   which further handles the task of filling the widget with appropriate data."
 *   -https://www.sitepoint.com/killer-way-to-show-a-list-of-items-in-android-collection-widget/
 */
public class MyWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MyWidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

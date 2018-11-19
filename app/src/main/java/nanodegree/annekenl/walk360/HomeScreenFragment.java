package nanodegree.annekenl.walk360;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeScreenFragment extends Fragment
{
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HomeScreenFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(savedInstanceState==null) {
            //try {
                //if(getResources().getBoolean(R.bool.IsTablet)) { //by suggestion, this will work for sw600dp and higher
                    // Two pane mode on tablets - recipe list is visible on the left side
                    //mTwoPane = true;
               // }
            //} catch (Exception e) {
               // e.printStackTrace();
           // }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.home_screen_layout, container, false);

        return rootView;
    }


}

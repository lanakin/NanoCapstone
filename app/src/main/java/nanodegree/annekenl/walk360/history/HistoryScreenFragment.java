package nanodegree.annekenl.walk360.history;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import nanodegree.annekenl.walk360.MainActivity;
import nanodegree.annekenl.walk360.R;
import nanodegree.annekenl.walk360.data.SingleDayTotals;
import nanodegree.annekenl.walk360.firestore.FirestoreHelper;

/*mpandroid graph tutorial reference:
https://blog.fossasia.org/plot-a-horizontal-bar-graph-using-mpandroidchart-library-in-susi-ai-android-app/ */
public class HistoryScreenFragment extends Fragment implements FirestoreHelper.OnDailyTotalsReadFinished
{
    private BarChart mSittingChart;
    private BarChart mWalkingChart;
    private BarChart mWaterChart;

    private FirestoreHelper mFirestoreHelper;
    private HashMap<String, SingleDayTotals> mDailyTotalsData;


    public HistoryScreenFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(savedInstanceState==null) { }

        mFirestoreHelper = new FirestoreHelper(this);
        mDailyTotalsData = new HashMap<>();
        setupDefaultBlankData();

        readInDailyTotalsData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.history_screen_layout, container, false);

        mSittingChart= rootView.findViewById(R.id.sitting_chart);
        //setBarChart(mSittingChart);

        mWalkingChart= rootView.findViewById(R.id.walking_chart);
        //setBarChart(mWalkingChart);

        mWaterChart= rootView.findViewById(R.id.water_chart);
        //setBarChart(mWaterChart);

        return rootView;
    }

    private void setupDefaultBlankData()
    {
        SingleDayTotals satData
                = new SingleDayTotals("Sat N/A",0,0,0);
        mDailyTotalsData.put("Sat", satData);

        SingleDayTotals friData = new SingleDayTotals("Fri N/A",0,0,0);
        mDailyTotalsData.put("Fri", friData);

        SingleDayTotals thurData  = new SingleDayTotals("Thur N/A",0,0,0);
        mDailyTotalsData.put("Thur", thurData);

        SingleDayTotals wedData  = new SingleDayTotals("Wed N/A",0,0,0);
        mDailyTotalsData.put("Wed", wedData);

        SingleDayTotals tueData  = new SingleDayTotals("Tue N/A",0,0,0);
        mDailyTotalsData.put("Tue", tueData);

        SingleDayTotals monData  = new SingleDayTotals("Mon N/A",0,0,0);
        mDailyTotalsData.put("Mon", monData);

        SingleDayTotals sunData = new SingleDayTotals("Sun N/A",0,0,0);
        mDailyTotalsData.put("Sun", sunData);
    }

    private void readInDailyTotalsData()
    {
        String theUserID = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(MainActivity.AUTH_USERID_KEY, "");
        if (theUserID.isEmpty()) {
            //to do later - alert error
            theUserID = "error";
        }

        mFirestoreHelper.readDailyTotals(theUserID,mDailyTotalsData);


    }

    @Override
    public void onDailyTotalsReadFinished() //HashMap<String, SingleDayTotals> resultData)
    {
        //mDailyTotalsData = resultData;

        if(mDailyTotalsData.size() != 0) {
            setSittingBarChart(mSittingChart);
            setWalkingBarChart(mWalkingChart);
            setWaterBarChart(mWaterChart);
        }
    }

    /* Similar Charts at the moment but use separate methods to make it easier to vary in the future */
    /**
     * Set up the axes along with other necessary details for the horizontal bar chart.
     */
    private void setSittingBarChart(BarChart barChart)
    {
        //skill_rating_chart is the id of the XML layout

        barChart.setDrawBarShadow(false);
        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);

        barChart.getLegend().setEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDrawValueAboveBar(true); /* false - 0 no bar to be drawn runs into label a tiny bit */
        barChart.setTouchEnabled(false);

        //Display the axis on the left
        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setEnabled(true);
        xAxis.setDrawAxisLine(false);

        YAxis yLeft = barChart.getAxisLeft();
        //yLeft.setAxisMaximum(100f);
        yLeft.setAxisMinimum(0);
        yLeft.setEnabled(false);

        setLabels(xAxis);

        YAxis yRight = barChart.getAxisRight();
        yRight.setDrawAxisLine(true);
        yRight.setDrawGridLines(false);
        yRight.setEnabled(false);

        //Set bar entries and add necessary formatting
        setSittingGraphData(barChart);

        //Add animation to the graph
        barChart.animateY(1000);
    }


    /**
     * Set up the axes along with other necessary details for the horizontal bar chart.
     */
    private void setWalkingBarChart(BarChart barChart)
    {
        //skill_rating_chart is the id of the XML layout

        barChart.setDrawBarShadow(false);
        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);

        barChart.getLegend().setEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDrawValueAboveBar(true); /* false - 0 no bar to be drawn runs into label a tiny bit */
        barChart.setTouchEnabled(false);

        //Display the axis on the left
        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setEnabled(true);
        xAxis.setDrawAxisLine(false);

        YAxis yLeft = barChart.getAxisLeft();
        //yLeft.setAxisMaximum(100f);
        yLeft.setAxisMinimum(0);
        yLeft.setEnabled(false);

        setLabels(xAxis);

        YAxis yRight = barChart.getAxisRight();
        yRight.setDrawAxisLine(true);
        yRight.setDrawGridLines(false);
        yRight.setEnabled(false);

        //Set bar entries and add necessary formatting
        setWalkingGraphData(barChart);

        //Add animation to the graph
        barChart.animateY(1000);
    }


    /**
     * Set up the axes along with other necessary details for the horizontal bar chart.
     */
    private void setWaterBarChart(BarChart barChart)
    {
        //skill_rating_chart is the id of the XML layout

        barChart.setDrawBarShadow(false);
        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);

        barChart.getLegend().setEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDrawValueAboveBar(true); /* false - 0 no bar to be drawn runs into label a tiny bit */
        barChart.setTouchEnabled(false);

        //Display the axis on the left
        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setEnabled(true);
        xAxis.setDrawAxisLine(false);

        YAxis yLeft = barChart.getAxisLeft();
        //yLeft.setAxisMaximum(100f);
        yLeft.setAxisMinimum(0);
        yLeft.setEnabled(false);

        setLabels(xAxis);

        YAxis yRight = barChart.getAxisRight();
        yRight.setDrawAxisLine(true);
        yRight.setDrawGridLines(false);
        yRight.setEnabled(false);

        //Set bar entries and add necessary formatting
        setWaterGraphData(barChart);

        //Add animation to the graph
        barChart.animateY(1000);
    }


    /* at this time - same day labels for all 3 graphs */
    //set labels
    private void setLabels(XAxis xAxis)
    {
        String[] labels = new String[mDailyTotalsData.size()];
        labels[0]= mDailyTotalsData.get("Sat").getDate_str();  //"Sat 1/12";
        labels[1]= mDailyTotalsData.get("Fri").getDate_str(); //"Fri 1/11";
        labels[2]= mDailyTotalsData.get("Thur").getDate_str(); //"Thur 1/10";
        labels[3]= mDailyTotalsData.get("Wed").getDate_str();  //"Wed 1/09";
        labels[4]= mDailyTotalsData.get("Tue").getDate_str(); //"Tue 1/08";
        labels[5]= mDailyTotalsData.get("Mon").getDate_str(); //"Mon 1/08";
        labels[6]= mDailyTotalsData.get("Sun").getDate_str();  //"Sun 1/13";

        xAxis.setLabelCount(labels.length);

        xAxis.setValueFormatter(new XAxisValueFormatter(labels));
    }

    public class XAxisValueFormatter implements IAxisValueFormatter {

        private String[] values;

        public XAxisValueFormatter(String[] values) {
            this.values = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return this.values[(int) value];
        }
    }


    /**
     * Set the bar entries
     *
     * Set the colors for different bars and the bar width of the bars.
     */
    private void setWalkingGraphData(BarChart barChart) {

       long satMinutes = TimeUnit.MILLISECONDS.toMinutes(mDailyTotalsData.get("Sat").getMax_walk())
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mDailyTotalsData.get("Sat").getMax_walk()));

        //Add a list of bar entries
        ArrayList<BarEntry> entries = new ArrayList();
        entries.add(new BarEntry(0, satMinutes));
        entries.add(new BarEntry(1, 99));
        entries.add(new BarEntry(2, 65));
        entries.add(new BarEntry(3, 17));
        entries.add(new BarEntry(4, 93));
        entries.add(new BarEntry(5, 73));
        entries.add(new BarEntry(6, 83));

        //Note : These entries can be replaced by real-time data, say, from an API

        //To display the data in a bar chart, you need to initialize a BarDataSet instance.
        //BarDataSet is the Subclass of DataSet class. Now, initialize the BarDataSet and pass the
        //argument as an ArrayList of BarEntry object.
        BarDataSet barDataSet = new BarDataSet(entries, "Bar Data Set");

        //barDataSet.setDrawValues(true);

        //Set bar shadows
        barChart.setDrawBarShadow(true);
        barDataSet.setBarShadowColor(Color.argb(40, 150, 150, 150));

        //To load the data into Bar Chart, you need to initialize a BarData object with bardataset.
        //This BarData object is then passed into setData() method to load Bar Chart with data.
        BarData data = new BarData(barDataSet);

        //Set the bar width
        //Note : To increase the spacing between the bars set the value of barWidth to < 1f
        data.setBarWidth(0.9f);

        //Finally set the data and refresh the graph
        barChart.setData(data);
        barChart.invalidate();
    }


    /**
     * Set the bar entries
     *
     * Set the colors for different bars and the bar width of the bars.
     */
    private void setSittingGraphData(BarChart barChart) {

        //Add a list of bar entries
        ArrayList<BarEntry> entries = new ArrayList();
        entries.add(new BarEntry(0, 60));
        entries.add(new BarEntry(1, 99));
        entries.add(new BarEntry(2, 65));
        entries.add(new BarEntry(3, 17));
        entries.add(new BarEntry(4, 93));
        entries.add(new BarEntry(5, 73));
        entries.add(new BarEntry(6, 83));

        //Note : These entries can be replaced by real-time data, say, from an API

        //To display the data in a bar chart, you need to initialize a BarDataSet instance.
        //BarDataSet is the Subclass of DataSet class. Now, initialize the BarDataSet and pass the
        //argument as an ArrayList of BarEntry object.
        BarDataSet barDataSet = new BarDataSet(entries, "Bar Data Set");

        //barDataSet.setDrawValues(true);

        //Set bar shadows
        barChart.setDrawBarShadow(true);
        barDataSet.setBarShadowColor(Color.argb(40, 150, 150, 150));

        //To load the data into Bar Chart, you need to initialize a BarData object with bardataset.
        //This BarData object is then passed into setData() method to load Bar Chart with data.
        BarData data = new BarData(barDataSet);

        //Set the bar width
        //Note : To increase the spacing between the bars set the value of barWidth to < 1f
        data.setBarWidth(0.9f);

        //Finally set the data and refresh the graph
        barChart.setData(data);
        barChart.invalidate();
    }


    /**
     * Set the bar entries
     *
     * Set the colors for different bars and the bar width of the bars.
     */
    private void setWaterGraphData(BarChart barChart) {

        //Add a list of bar entries
        ArrayList<BarEntry> entries = new ArrayList();
        entries.add(new BarEntry(0, 60));
        entries.add(new BarEntry(1, 99));
        entries.add(new BarEntry(2, 65));
        entries.add(new BarEntry(3, 17));
        entries.add(new BarEntry(4, 93));
        entries.add(new BarEntry(5, 73));
        entries.add(new BarEntry(6, 83));

        //Note : These entries can be replaced by real-time data, say, from an API

        //To display the data in a bar chart, you need to initialize a BarDataSet instance.
        //BarDataSet is the Subclass of DataSet class. Now, initialize the BarDataSet and pass the
        //argument as an ArrayList of BarEntry object.
        BarDataSet barDataSet = new BarDataSet(entries, "Bar Data Set");

        //barDataSet.setDrawValues(true);

        //Set bar shadows
        barChart.setDrawBarShadow(true);
        barDataSet.setBarShadowColor(Color.argb(40, 150, 150, 150));

        //To load the data into Bar Chart, you need to initialize a BarData object with bardataset.
        //This BarData object is then passed into setData() method to load Bar Chart with data.
        BarData data = new BarData(barDataSet);

        //Set the bar width
        //Note : To increase the spacing between the bars set the value of barWidth to < 1f
        data.setBarWidth(0.9f);

        //Finally set the data and refresh the graph
        barChart.setData(data);
        barChart.invalidate();
    }

}

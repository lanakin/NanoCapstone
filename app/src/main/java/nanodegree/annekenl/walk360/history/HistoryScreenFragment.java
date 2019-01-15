package nanodegree.annekenl.walk360.history;

import android.graphics.Color;
import android.os.Bundle;
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

import nanodegree.annekenl.walk360.R;

/*mpandroid graph tutorial reference:
https://blog.fossasia.org/plot-a-horizontal-bar-graph-using-mpandroidchart-library-in-susi-ai-android-app/ */
public class HistoryScreenFragment extends Fragment
{
    private BarChart mSittingChart;
    private BarChart mWalkingChart;
    private BarChart mWaterChart;

    public HistoryScreenFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(savedInstanceState==null) { }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.history_screen_layout, container, false);

        mSittingChart= rootView.findViewById(R.id.sitting_chart);
        setBarChart(mSittingChart);

        mWalkingChart= rootView.findViewById(R.id.walking_chart);
        setBarChart(mWalkingChart);

        mWaterChart= rootView.findViewById(R.id.water_chart);
        setBarChart(mWaterChart);

        return rootView;
    }


    /**
     * Set up the axes along with other necessary details for the horizontal bar chart.
     */
    private void setBarChart(BarChart barChart)
    {
        //skill_rating_chart is the id of the XML layout

        barChart.setDrawBarShadow(false);
        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);

        barChart.getLegend().setEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDrawValueAboveBar(false); /**/
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

        xAxis.setLabelCount(7);

        String[] values = new String[7];
        values[0]="Sat 1/12";
        values[1]="Fri 1/11";
        values[2]="Thur 1/10";
        values[3]="Wed 1/09";
        values[4]="Tue 1/08";
        values[5]="Mon 1/07";
        values[6]="Sun 1/13";

        xAxis.setValueFormatter(new XAxisValueFormatter(values));

        YAxis yRight = barChart.getAxisRight();
        yRight.setDrawAxisLine(true);
        yRight.setDrawGridLines(false);
        yRight.setEnabled(false);

        //Set bar entries and add necessary formatting
        setGraphData(barChart);

        //Add animation to the graph
        barChart.animateY(1000);
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
    private void setGraphData(BarChart barChart) {

        //Add a list of bar entries
        ArrayList<BarEntry> entries = new ArrayList();
        entries.add(new BarEntry(0, 60));
        entries.add(new BarEntry(1, 90));
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
        //skillRatingChart.setFitBars(true);
        barChart.invalidate();
    }

}

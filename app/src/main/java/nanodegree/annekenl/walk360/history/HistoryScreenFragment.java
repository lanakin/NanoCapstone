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

public class HistoryScreenFragment extends Fragment
{
    private BarChart skillRatingChart;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HistoryScreenFragment() {
    }

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

        skillRatingChart = rootView.findViewById(R.id.skill_rating_chart);
        setSkillGraph();

        //chart = (BarChart) rootView.findViewById(R.id.chart1);
/*
        ArrayList<BarEntry> entries = new ArrayList();
            entries.add(new BarEntry(4f, 0));
            entries.add(new BarEntry(8f, 1));
            entries.add(new BarEntry(6f, 2));
            entries.add(new BarEntry(12f, 3));
            entries.add(new BarEntry(18f, 4));
            entries.add(new BarEntry(9f, 5));

        BarDataSet dataset = new BarDataSet(entries, "Calls");

        // creating labels<br />
        ArrayList labels = new ArrayList<String>();
            labels.add("Sun");
            labels.add("Mon");
            labels.add("Tue");
            labels.add("Wed");
            labels.add("Thur");
            labels.add("Fri");
            labels.add("Sat");

        BarData data = new BarData(dataset);
        //data.addDataSet(dataset);

        chart.setData(data); */// set the data and list of lables into chart<br />

        //chart.setDescription("test");

      /*  GraphView graph = (GraphView) rootView.findViewById(R.id.graph);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(1,6),
                new DataPoint(2,5),
                new DataPoint(3,4),
                new DataPoint(4,3),
                new DataPoint(5,2),
                new DataPoint(6,1),
                new DataPoint(7,1),
        });
        graph.addSeries(series);

        series.setSpacing(5);
        graph.setTitle("Max Continous Time Spent Sitting");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Minutes");

        // activate horizontal scrolling
        //graph.getViewport().setScrollable(true);

        // draw values on top
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);
        //series.setValuesOnTopSize(50);

        GraphView graph2 = (GraphView) rootView.findViewById(R.id.graph2);
        BarGraphSeries<DataPoint> series2 = new BarGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(1,6),
                new DataPoint(2,5),
                new DataPoint(3,4),
                new DataPoint(4,3),
                new DataPoint(5,2),
                new DataPoint(6,1),
                new DataPoint(7,1),
        });
        graph2.addSeries(series2);

        series2.setSpacing(5);
        graph2.setTitle("Max Continous Time Spent Sitting");
        graph2.getGridLabelRenderer().setVerticalAxisTitle("Minutes");

        // activate horizontal scrolling
        //graph2.getViewport().setScrollable(true);

        // draw values on top
        series2.setDrawValuesOnTop(true);
        series2.setValuesOnTopColor(Color.RED);
        //series.setValuesOnTopSize(50);

        GraphView graph3 = (GraphView) rootView.findViewById(R.id.graph3);
        BarGraphSeries<DataPoint> series3 = new BarGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(1,6),
                new DataPoint(2,5),
                new DataPoint(3,4),
                new DataPoint(4,3),
                new DataPoint(5,2),
                new DataPoint(6,1),
                new DataPoint(7,1),
        });
        graph3.addSeries(series3);

        series3.setSpacing(5);
        graph3.setTitle("Max Continous Time Spent Sitting");
        graph3.getGridLabelRenderer().setVerticalAxisTitle("Minutes");

        // activate horizontal scrolling
        //graph3.getViewport().setScrollable(true);

        // draw values on top
        series3.setDrawValuesOnTop(true);
        series3.setValuesOnTopColor(Color.RED);
        //series.setValuesOnTopSize(50);*/

        return rootView;
    }


    /**
     * Set up the axes along with other necessary details for the horizontal bar chart.
     */
    private void setSkillGraph()
    {
        //skill_rating_chart is the id of the XML layout

        skillRatingChart.setDrawBarShadow(false);
         Description description = new Description();
        description.setText("Longest Period Sitting Each Day (Minutes)");
        skillRatingChart.setDescription(description);

        skillRatingChart.getLegend().setEnabled(false);
        skillRatingChart.setPinchZoom(false);
        skillRatingChart.setDrawValueAboveBar(false);

        //Display the axis on the left (contains the labels 1*, 2* and so on)
        XAxis xAxis = skillRatingChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setEnabled(true);
        xAxis.setDrawAxisLine(false);


        YAxis yLeft = skillRatingChart.getAxisLeft();

        //Set the minimum and maximum bar lengths as per the values that they represent
        yLeft.setAxisMaximum(100f);
        yLeft.setAxisMinimum(0f);
        yLeft.setEnabled(false);

        //Set label count to 5 as we are displaying 5 star rating
        //xAxis.setLabelCount(5);
        xAxis.setLabelCount(7);

        //Now add the labels to be added on the vertical axis
        /*String[] values = new String[5];  //5 labels in example
        values[0]="1";
        values[1]="2";
        values[2]="3";
        values[3]="4";
        values[4]="5";*/
        String[] values = new String[7];  //5 labels in example
        values[0]="Sat";
        values[1]="Fri";
        values[2]="Thur";
        values[3]="Wed";
        values[4]="Tue";
        values[5]="Mon";
        values[6]="Sun";

        xAxis.setValueFormatter(new XAxisValueFormatter(values));

        YAxis yRight = skillRatingChart.getAxisRight();
        yRight.setDrawAxisLine(true);
        yRight.setDrawGridLines(false);
        yRight.setEnabled(false);

        //Set bar entries and add necessary formatting
        setGraphData();

        //Add animation to the graph
        skillRatingChart.animateY(2000);
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
     * Set the bar entries i.e. the percentage of users who rated the skill with
     * a certain number of stars.
     *
     * Set the colors for different bars and the bar width of the bars.
     */
    private void setGraphData() {

        //Add a list of bar entries
        /*ArrayList<BarEntry> entries = new ArrayList();
        entries.add(new BarEntry(0f, 27f));
        entries.add(new BarEntry(1f, 45f));
        entries.add(new BarEntry(2f, 65f));
        entries.add(new BarEntry(3f, 17f));
        entries.add(new BarEntry(4f, 93f));*/
        ArrayList<BarEntry> entries = new ArrayList();
        entries.add(new BarEntry(0f, 60f));
        entries.add(new BarEntry(1f, 90f));
        entries.add(new BarEntry(2f, 65f));
        entries.add(new BarEntry(3f, 17f));
        entries.add(new BarEntry(4f, 93f));
        entries.add(new BarEntry(5f, 73f));
        entries.add(new BarEntry(6f, 83f));

        //Note : These entries can be replaced by real-time data, say, from an API

        //To display the data in a bar chart, you need to initialize a BarDataSet instance.
        //BarDataSet is the Subclass of DataSet class. Now, initialize the BarDataSet and pass the
        //argument as an ArrayList of BarEntry object.
        BarDataSet barDataSet = new BarDataSet(entries, "Bar Data Set");

        //Set the colors for bars with first color for 1*, second for 2* and so on
        /*barDataSet.setColors(
                ContextCompat.getColor(skillRatingChart.getContext(), a),
                ContextCompat.getColor(skillRatingChart.getContext(), R.color.md_deep_orange_400),
                ContextCompat.getColor(skillRatingChart.getContext(), R.color.md_yellow_A700),
                ContextCompat.getColor(skillRatingChart.getContext(), R.color.md_green_700),
                ContextCompat.getColor(skillRatingChart.getContext(), R.color.md_indigo_700)*/

        //To load the data into Bar Chart, you need to initialize a BarData object  with bardataset.
        // This BarData object is then passed into setData() method to load Bar Chart with data.

        //Set bar shadows
        skillRatingChart.setDrawBarShadow(true);
        barDataSet.setBarShadowColor(Color.argb(40, 150, 150, 150));
        BarData data = new BarData(barDataSet);

        //Set the bar width
        //Note : To increase the spacing between the bars set the value of barWidth to < 1f
        data.setBarWidth(0.9f);

        //Finally set the data and refresh the graph
        skillRatingChart.setData(data);
        skillRatingChart.invalidate();
    }


}
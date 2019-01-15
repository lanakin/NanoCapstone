package nanodegree.annekenl.walk360.water;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import nanodegree.annekenl.walk360.R;

/*
 * https://www.myfooddiary.com/resources/ask_the_expert/recommended_daily_water_intake.asp
 */
public class WaterCalculatorScreenFragment extends Fragment
{
    private float bodyWeight = 0;
    private Button calculateBtn;
    private EditText input;
    private TextView result;

    public static final String WATER_DAILY_TOTAL = "WATER_DAILY_TOTAL";

    public WaterCalculatorScreenFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(savedInstanceState==null) {
           //
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.water_calc_screen_layout, container, false);

        calculateBtn = rootView.findViewById(R.id.calculateBtn);
        input = rootView.findViewById(R.id.weightInput);
        result = rootView.findViewById(R.id.waterCalcResult);

        calculateBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    bodyWeight = Float.parseFloat(input.getText().toString());

                    double waterAmtOZ = bodyWeight * 0.5; //0.5 ounces * body weight in lbs. = water requirement in ounces
                    double waterAmtCUPS = waterAmtOZ / 8;

                    String waterAmtStr = String.format("%.2f", waterAmtOZ) + " ounces" + " (" + String.format("%.2f", waterAmtCUPS) + " cups)";
                    result.setText(waterAmtStr);

                } catch (Exception e) {
                    result.setText("Error: No Body Weight was Entered.");
                }
            }
        });

        return rootView;
    }

}
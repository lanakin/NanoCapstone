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
import android.widget.Toast;

import nanodegree.annekenl.walk360.R;

/*
 * https://www.myfooddiary.com/resources/ask_the_expert/recommended_daily_water_intake.asp
 */
public class WaterCalculatorScreenFragment extends Fragment
{
    private int bodyWeight = 0;
    private Button calculateBtn;
    private EditText input;
    private TextView result;

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
                    bodyWeight = Integer.parseInt(input.getText().toString());

                    double waterAmtOZ = bodyWeight * 0.5; //0.5 ounces * body weight in lbs. = water requirement in ounces
                    double waterAmtCUPS = waterAmtOZ / 8;

                    String waterAmtStr = String.format("%.2f", waterAmtOZ) + " ounces" + " (" + String.format("%.2f", waterAmtCUPS) + " cups)";
                    result.setText(waterAmtStr);

                } catch (Exception e) {
                    Toast.makeText(getActivity(), "No Body Weight was entered", Toast.LENGTH_SHORT);
                }
            }
        });

        return rootView;
    }


   /* AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Light_Dialog_Alert);
} else {
    builder = new AlertDialog.Builder(getActivity());
}

    // Set up the input
    final EditText input = new EditText(getActivity());
    float scale = getResources().getDisplayMetrics().density;
    int dpAsPixels = (int) (5*scale + 0.5f); //25dp
                input.setWidth(dpAsPixels);

    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                builder.setTitle("Body Weight")
                        .setMessage("Enter your body weight below to help calculate a recommended daily amount of water to drink: ")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
    public void onClick(DialogInterface dialog, int which) {
        try {
            bodyWeight = Integer.parseInt(input.getText().toString());
        } catch (Exception e) {
            Toast.makeText(getActivity(), "No Body Weight was entered", Toast.LENGTH_SHORT);
        }
    }
})
        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
    public void onClick(DialogInterface dialog, int which) {
        // do nothing
    }
})
        //.setIcon(android.R.drawable.ic_dialog_alert)
        .show();*/

}
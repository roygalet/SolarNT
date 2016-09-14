package com.roygalet.www.solarnt;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class CompareActivity extends AppCompatActivity {
    LineChart lineChart;
    String[] dateData = "14-09-16\t13-09-16\t12-09-16\t11-09-16\t10-09-16\t09-09-16\t08-09-16\t07-09-16\t06-09-16\t05-09-16\t04-09-16\t03-09-16\t02-09-16\t01-09-16\t31-08-16\t30-08-16\t29-08-16\t28-08-16\t27-08-16\t26-08-16\t25-08-16\t24-08-16\t23-08-16\t22-08-16\t21-08-16\t20-08-16\t19-08-16\t18-08-16\t17-08-16\t16-08-16\t15-08-16\t14-08-16\t13-08-16\t12-08-16\t11-08-16\t10-08-16\t09-08-16\t08-08-16\t07-08-16\t06-08-16".split("\t");
    String[] lineData1 = "206.534\t158.564\t172.731\t25.493\t190.082\t158.108\t185.349\t174.621\t207.811\t192.071\t59.363\t172.149\t167.699\t165.528\t190.731\t167.494\t197.943\t25.456\t202.140\t158.639\t204.761\t200.369\t182.272\t185.675\t23.748\t193.965\t117.343\t194.398\t183.097\t179.884\t207.764\t170.708\t190.470\t203.398\t177.518\t92.716\t76.282\t71.756\t75.009\t78.674".split("\t");
    String[] lineData2 = "13.565\t12.664\t12.170\t12.809\t13.480\t10.527\t11.769\t12.051\t13.772\t13.780\t8.643\t12.815\t12.256\t12.250\t13.881\t13.443\t14.414\t13.101\t14.394\t13.526\t14.147\t13.459\t13.214\t14.238\t13.835\t14.296\t7.177\t13.011\t13.157\t13.906\t14.240\t14.118\t13.928\t14.623\t13.172\t14.067\t13.030\t10.576\t12.803\t14.143".split("\t");
    String[] lineData3 = "20.508\t23.355\t25.428\t27.859\t27.413\t24.426\t27.391\t27.434\t29.945\t27.887\t15.583\t18.593\t20.584\t16.618\t21.295\t21.623\t24.920\t24.132\t25.585\t20.807\t23.839\t23.944\t21.450\t21.957\t24.279\t24.093\t14.078\t17.605\t23.704\t24.911\t25.357\t24.454\t24.407\t24.986\t24.105\t24.298\t23.612\t19.527\t24.472\t25.507".split("\t");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lineChart = (LineChart)findViewById(R.id.compareLineChart);

        List<String> dateDataSet = new ArrayList<>();
        List<Entry> dataset1 = new ArrayList<>();
        List<Entry> dataset2 = new ArrayList<>();
        List<Entry> dataset3 = new ArrayList<>();
        for(int index=0; index < lineData1.length; index++){
            dateDataSet.add(dateData[index]);
            dataset1.add(new Entry(Float.parseFloat(lineData1[index]),index));
            dataset2.add(new Entry(Float.parseFloat(lineData2[index]),index));
            dataset3.add(new Entry(Float.parseFloat(lineData3[index]),index));
        }
        List<ILineDataSet> lineDataSets = new ArrayList<> ();
        LineDataSet lineDataSet1 = new LineDataSet(dataset1,"66 Benison Road");
        lineDataSet1.setColor(Color.RED);
        LineDataSet lineDataSet2 = new LineDataSet(dataset2,"Anula Heights");
        lineDataSet2.setColor(Color.GREEN);
        LineDataSet lineDataSet3 = new LineDataSet(dataset3,"Bees Creek");
        lineDataSet3.setColor(Color.BLUE);

        lineDataSets.add(lineDataSet1);
        lineDataSets.add(lineDataSet2);
        lineDataSets.add(lineDataSet3);

        lineChart.setData(new LineData(dateData, lineDataSets));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


}

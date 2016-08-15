package com.roygalet.www.solarnt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.math.BigDecimal;

public class Calculator extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        WeatherData weatherData = bundle.getBundle("weather").getParcelable("weather");

        ((TextView) findViewById(R.id.calcSuburb)).setText(weatherData.getSuburb());
        ((TextView) findViewById(R.id.calcStation)).setText("Data from ".concat(weatherData.getSolarname()).concat(" station ").concat(Float.toString(weatherData.getSolardistance())).concat(" kilometres away."));
        ((TextView) findViewById(R.id.calcSolarExposure)).setText(Float.toString(weatherData.getSolarmean()));



        ((SeekBar)findViewById(R.id.calcSeekbarCapacity)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((TextView)findViewById(R.id.calcCapacity)).setText(String.valueOf((seekBar.getProgress()+1)/10.0));
                calculate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ((SeekBar)findViewById(R.id.calcSeekbarCost)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((TextView)findViewById(R.id.calcCost)).setText(String.valueOf(seekBar.getProgress()));
                calculate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        calculate();
    }


    private void calculate(){
        float solarExposure = Float.parseFloat(((TextView)findViewById(R.id.calcSolarExposure)).getText().toString());
        float capacity = Float.parseFloat(((TextView)findViewById(R.id.calcCapacity)).getText().toString());
        int cost = Integer.parseInt(((TextView)findViewById(R.id.calcCost)).getText().toString());
        double minEfficiency = 0.735851183;
        double maxEfficiency = 1.001635544;
        double minOutput = solarExposure * capacity * minEfficiency;
        double maxOutput = solarExposure * capacity * maxEfficiency;
        double minSavings = minOutput * 0.2595 * 365.25;
        double maxSavings = maxOutput * 0.2595 * 365.25;
        double minReturns = cost / maxSavings;
        double maxReturns = cost / minSavings;
        ((TextView)findViewById(R.id.calcPotentialPower)).setText(String.valueOf(BigDecimal.valueOf(minOutput).setScale(1, BigDecimal.ROUND_HALF_UP)).concat(" - ").concat(String.valueOf(BigDecimal.valueOf(maxOutput).setScale(1, BigDecimal.ROUND_HALF_UP))));
        ((TextView)findViewById(R.id.calcSavings)).setText("$ ".concat(String.valueOf(((Double)Math.floor(minSavings)).intValue())).concat(" - ").concat(String.valueOf(((Double)Math.ceil(maxSavings)).intValue())));
        ((TextView)findViewById(R.id.calcReturns)).setText(String.valueOf(((Double)Math.floor(minReturns)).intValue()).concat(" - ").concat(String.valueOf(((Double)Math.ceil(maxReturns)).intValue())));
    }

}

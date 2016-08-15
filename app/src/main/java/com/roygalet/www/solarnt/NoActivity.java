package com.roygalet.www.solarnt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class NoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("SolarNT");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().getExtras()!=null){
            Bundle bundle = getIntent().getExtras();
            final WeatherData weatherData = bundle.getBundle("weather").getParcelable("weather");
            if(weatherData != null){
                ((CardView)findViewById(R.id.noCardWeather)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.noSuburb)).setText(weatherData.getSuburb());
                ((TextView) findViewById(R.id.noSolar)).setText(Float.toString(weatherData.getSolarmean()));
                ((TextView) findViewById(R.id.noRainFall)).setText(Integer.toString(Math.round(weatherData.getRainmean())));
                ((TextView) findViewById(R.id.noTemp)).setText(Integer.toString(Math.round(weatherData.getTempminmean())).concat("-").concat(Integer.toString(Math.round(weatherData.getTempmaxmean()))));
                ((CardView)findViewById(R.id.noCardWeather)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("weather", weatherData);
                        Intent intent = new Intent(NoActivity.this, Weather.class);
                        intent.putExtra("weather", bundle);
                        startActivity(intent);
                    }
                });
            }else{
                ((CardView)findViewById(R.id.noCardWeather)).setVisibility(View.INVISIBLE);
            }
        }

        ((CardView)findViewById(R.id.noCardCalculator)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoActivity.this, Calculator.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("weather", (WeatherData)getIntent().getExtras().getBundle("weather").getParcelable("weather"));
                intent.putExtra("weather", bundle);
                startActivity(intent);
            }
        });

    }


}

package com.roygalet.www.solarnt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class YesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("SolarNT");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().getExtras()!=null){
            Bundle bundle = getIntent().getExtras();
            final WeatherData weatherData = bundle.getBundle("weather").getParcelable("weather");
            if(weatherData != null){
                ((CardView)findViewById(R.id.yesCardWeather)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.yesSuburb)).setText(weatherData.getSuburb());
                ((TextView) findViewById(R.id.yesSolar)).setText(Float.toString(weatherData.getSolarmean()));
                ((TextView) findViewById(R.id.yesRainFall)).setText(Integer.toString(Math.round(weatherData.getRainmean())));
                ((TextView) findViewById(R.id.yesTemp)).setText(Integer.toString(Math.round(weatherData.getTempminmean())).concat("-").concat(Integer.toString(Math.round(weatherData.getTempmaxmean()))));
                ((CardView)findViewById(R.id.yesCardWeather)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("weather", weatherData);
                        Intent intent = new Intent(YesActivity.this, Weather.class);
                        intent.putExtra("weather", bundle);
                        startActivity(intent);
                    }
                });
            }else{
                ((CardView)findViewById(R.id.yesCardWeather)).setVisibility(View.INVISIBLE);
            }
        }

        CardView dustAnalyzer = (CardView) findViewById(R.id.yesCardDustAnalyzer);
        dustAnalyzer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDustAnalyzer();
            }
        });

        ((CardView)findViewById(R.id.yesCardMonitor)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Uri uri = Uri.parse("http://138.80.64.225");
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                Intent intent = new Intent(YesActivity.this, Monitor.class);
                startActivity(intent);
            }
        });

        ((CardView)findViewById(R.id.yesCardCalculator)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(YesActivity.this, Calculator.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("weather", (WeatherData)getIntent().getExtras().getBundle("weather").getParcelable("weather"));
                intent.putExtra("weather", bundle);
                startActivity(intent);
            }
        });
    }

    private void showDustAnalyzer(){
        Intent intent = new Intent(YesActivity.this, DustAnalyzer.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("weather", (WeatherData)getIntent().getExtras().getBundle("weather").getParcelable("weather"));
        intent.putExtra("weather", bundle);
        startActivity(intent);
    }

}

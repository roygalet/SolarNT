package com.roygalet.www.solarnt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class Weather extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        WeatherData weatherData = bundle.getBundle("weather").getParcelable("weather");

        ((TextView) findViewById(R.id.weatherSuburb)).setText(weatherData.getSuburb());
        ((TextView) findViewById(R.id.weatherLatitude)).setText(Float.toString(weatherData.getLatitude()));
        ((TextView) findViewById(R.id.weatherLongitude)).setText(Float.toString(weatherData.getLongitude()));
        ((TextView) findViewById(R.id.weatherSolarStation)).setText("Data from ".concat(weatherData.getSolarname()).concat(" station ").concat(Float.toString(weatherData.getSolardistance())).concat(" kilometres away."));
        ((TextView) findViewById(R.id.weatherSolarDry)).setText(Float.toString(weatherData.getSolardry()));
        ((TextView) findViewById(R.id.weatherSolarMean)).setText(Float.toString(weatherData.getSolarmean()));
        ((TextView) findViewById(R.id.weatherSolarWet)).setText(Float.toString(weatherData.getSolarwet()));
        ((TextView) findViewById(R.id.weatherRainStation)).setText("Data from ".concat(weatherData.getRainname()).concat(" station ").concat(Float.toString(weatherData.getRaindistance())).concat(" kilometres away."));
        ((TextView) findViewById(R.id.weatherRainDry)).setText(Integer.toString(Math.round(weatherData.getRaindry())));
        ((TextView) findViewById(R.id.weatherRainMean)).setText(Integer.toString(Math.round(weatherData.getRainmean())));
        ((TextView) findViewById(R.id.weatherRainWet)).setText(Integer.toString(Math.round(weatherData.getRainwet())));
        ((TextView) findViewById(R.id.weatherTempStation)).setText("Data from ".concat(weatherData.getTempname()).concat(" station ").concat(Float.toString(weatherData.getTempdistance())).concat(" kilometres away."));
        ((TextView) findViewById(R.id.weatherTempDry)).setText(Integer.toString(Math.round(weatherData.getTempmindry())).concat("-").concat(Integer.toString(Math.round(weatherData.getTempmaxdry()))));
        ((TextView) findViewById(R.id.weatherTempMean)).setText(Integer.toString(Math.round(weatherData.getTempminmean())).concat("-").concat(Integer.toString(Math.round(weatherData.getTempmaxmean()))));
        ((TextView) findViewById(R.id.weatherTempWet)).setText(Integer.toString(Math.round(weatherData.getTempminwet())).concat("-").concat(Integer.toString(Math.round(weatherData.getTempmaxwet()))));
    }

}

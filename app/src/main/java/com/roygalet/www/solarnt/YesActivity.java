package com.roygalet.www.solarnt;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class YesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("SolarNT");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CardView dustAnalyzer = (CardView) findViewById(R.id.yesCardDustAnalyzer);
        dustAnalyzer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDustAnalyzer();
            }
        });
    }

    private void showDustAnalyzer(){
        Intent intent = new Intent(this,DustAnalyzer.class);
        startActivity(intent);
    }

}

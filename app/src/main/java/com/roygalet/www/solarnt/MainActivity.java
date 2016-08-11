package com.roygalet.www.solarnt;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardView yesCard = (CardView) findViewById(R.id.mainCardYes);
        yesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickYes();
            }
        });
    }

    private void loadSuburbs(){
        InputStreamReader is = null;
        try {
            is = new InputStreamReader(getAssets().open("data.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader reader = new BufferedReader(is);
        try {
            reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClickYes() {
        Intent intent = new Intent(this, YesActivity.class);
        startActivity(intent);
    }
}

package com.roygalet.www.solarnt;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

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

    public void onClickYes() {
        Intent intent = new Intent(this, YesActivity.class);
        startActivity(intent);
    }
}

package com.roygalet.www.solarnt;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        this.setTitle("SolarNT");

        ((ImageButton)findViewById(R.id.homeImageButtonCDU)).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://www.cdu.edu.au"));
                startActivity(intent);
            }
        });
        ((ImageButton)findViewById(R.id.homeImageButtonCRE)).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://www.cdu.edu.au/cre"));
                startActivity(intent);
            }
        });
        ((ImageButton)findViewById(R.id.homeImageButtonHelp)).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(HomeActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });

        ((Button)findViewById(R.id.homeButtonSettings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, Settings.class));
            }
        });

        ((Button)findViewById(R.id.homeButtonApps)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, AppsActivity.class));
            }
        });

        ((Button)findViewById(R.id.homeButtonDonate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, DonateActivity.class));
            }
        });

        ((Switch)findViewById(R.id.homeSwitchHavePanels)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()) {
                    ((Switch) findViewById(R.id.homeSwitchIsLogging)).setVisibility(View.VISIBLE);
                }else{
                    ((Switch) findViewById(R.id.homeSwitchIsLogging)).setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}

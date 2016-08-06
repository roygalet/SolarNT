package com.roygalet.www.solarnt;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class DustAnalyzer extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_PICTURE = 100;
    Bitmap photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dust_analyzer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton buttonCamera = (FloatingActionButton) findViewById(R.id.dustButtonCamera);
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCamera();
            }
        });

        FloatingActionButton buttonGallery = (FloatingActionButton) findViewById(R.id.dustButtonGallery);
        buttonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

    }

    private void onClickCamera(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView imageView = (ImageView) findViewById(R.id.dustImgImage);
        TextView textNoImageSelected = (TextView) findViewById(R.id.dustTextNoImage);
        TextView textPercent = (TextView) findViewById(R.id.dustTextPercent);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // Get the url from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    imageView.setImageURI(selectedImageUri);
                    textNoImageSelected.setText("");
                    try {
                        photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    textPercent.setText(Double.toString(analyzePhoto(photo)));
                }
            }
            if (requestCode == CAMERA_REQUEST) {
                photo = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(photo);
                textNoImageSelected.setText("");
                textPercent.setText(Double.toString(analyzePhoto(photo)));
            }
        }

    }

    private double analyzePhoto(Bitmap photo){
        double dust;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        dust = ByteBuffer.wrap(byteArray).getDouble();
        System.out.println(dust);
        return dust;
    }

}

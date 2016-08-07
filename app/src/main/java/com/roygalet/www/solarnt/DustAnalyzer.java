package com.roygalet.www.solarnt;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
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

    private double analyzePhoto(Bitmap inputPhoto){
//        double dust=0;
//
//        int width = inputPhoto.getWidth();
//        int height = inputPhoto.getHeight();
//
//        Bitmap bmpMonochrome = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bmpMonochrome);
//        ColorMatrix ma = new ColorMatrix();
//        ma.setSaturation(0);
//        Paint paint = new Paint();
//        paint.setColorFilter(new ColorMatrixColorFilter(ma));
//        canvas.drawBitmap(inputPhoto, 0, 0, paint);
//
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                int pixel = bmpMonochrome.getPixel(x, y);
//                int lowestByte = pixel & 0xff;
//                if(lowestByte >= 97 && pixel <= 203){
//                    dust = dust + 1;
//                }
//            }
//        }
//
//        dust = dust / (width * height) * 100;
//        dust = 1.1469*dust - 15.657;
////        dust = dust + (0.104 * dust - 6.7006);
//        if(dust < 0)dust=0;
//        if(dust > 100)dust=100;
        return getDustPixelsHSV(inputPhoto);
    }

    private double getDustPixelsHSV(Bitmap inputPhoto){
        double dust=0;
        float [] hsv = new float[3];

        int width = inputPhoto.getWidth();
        int height = inputPhoto.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = inputPhoto.getPixel(x, y);
                Color.colorToHSV(pixel,hsv);
                if(hsv[0]<90 && hsv[1] > 0.2){
                    dust = dust + 1;
                }
            }
        }
        dust = dust / (width * height) * 100;
        return dust;
    }

    private double getDustPixelsRGB(Bitmap inputPhoto){
        double dust=0;
        int iRed=0;
        int iGreen=0;
        int iBlue=0;

        int width = inputPhoto.getWidth();
        int height = inputPhoto.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = inputPhoto.getPixel(x, y);
                iRed = Color.red(pixel);
                iGreen = Color.green(pixel);
                iBlue = Color.blue(pixel);
                if(iRed >= 75 && iRed <= 145 && iGreen>=95 && iGreen <=155 && iBlue >=80 && iBlue <= 140){
                    dust = dust + 1;
                }
            }
        }
        dust = dust / (width * height) * 100;
        return dust;
    }

}

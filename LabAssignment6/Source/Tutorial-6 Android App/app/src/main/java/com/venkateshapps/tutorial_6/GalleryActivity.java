package com.venkateshapps.tutorial_6;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URI;

public class GalleryActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;
    private ImageView mimageview;
    private ImageButton spark;
    private ImageButton clarifai;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);


        mimageview = (ImageView) findViewById(R.id.Image) ;
        Intent intent = getIntent();
        final String image_path= intent.getStringExtra("ImagePath");
        Uri fileUri = Uri.parse(image_path);
        mimageview.setImageURI(fileUri);


        spark =(ImageButton)findViewById(R.id.SparkButton);
        spark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sparkIntent= new Intent();
                sparkIntent.setClass(GalleryActivity.this,ResultActivity.class);
                sparkIntent.putExtra("Image",image_path);
                sparkIntent.putExtra("Selection",1);
                startActivity(sparkIntent);
            }
        });



        clarifai =(ImageButton)findViewById(R.id.clarifaiButton);
        clarifai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ClarifaiIntent=new Intent();
                ClarifaiIntent.setClass(GalleryActivity.this,ResultActivity.class);
                ClarifaiIntent.putExtra("Image",image_path);
                ClarifaiIntent.putExtra("Selection",2);
                startActivity(ClarifaiIntent);
            }
        });
    }


}

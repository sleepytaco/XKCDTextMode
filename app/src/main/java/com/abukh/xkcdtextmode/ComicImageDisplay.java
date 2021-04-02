package com.abukh.xkcdtextmode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ComicImageDisplay extends AppCompatActivity {

    private ImageView comicImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_image_display);

        comicImageView = findViewById(R.id.comicImageView);

        Intent intent = getIntent();

        String comicURL = intent.getStringExtra("imageLink");

        Picasso.get().load(comicURL).into(comicImageView);

    }
}
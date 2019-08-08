package com.libgdx.ahmed_fathy.socialapp.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.libgdx.ahmed_fathy.socialapp.R;

public class FullImageView extends AppCompatActivity {
    ImageView fullImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image_view);

        //set status bar to transparent
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS , WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getSupportActionBar().hide();


        fullImageView = findViewById(R.id.full_image_view);
        String postImage = getIntent().getExtras().getString("postImage");

        Glide.with(this).load(postImage).into(fullImageView);
    }
}

package com.example.sanghyunj.speckerapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.example.sanghyunj.speckerapp.R;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        TextView _userName = (TextView) findViewById(R.id.userName);
        ImageView _profileImage = (ImageView) findViewById(R.id.profileImage);
        ImageView _backgroundImage = (ImageView) findViewById(R.id.backgroundImage);

        String userName = getIntent().getStringExtra("userName");
        String profileImage = getIntent().getStringExtra("profileImage");

        _userName.setText(userName);

        DrawableTypeRequest<String> glide = Glide.with(this).load(profileImage);
        glide.bitmapTransform(new CropCircleTransformation(this)).into(_profileImage);
        glide.bitmapTransform(new BlurTransformation(this)).into(_backgroundImage);
    }
}

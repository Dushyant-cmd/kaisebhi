package com.kaisebhi.kaisebhi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import static com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient.BASE_URL;

public class ViewPic extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pic);
        ImageView img = findViewById(R.id.fullImage);

        Bundle extras = getIntent().getExtras();


        if (extras != null) {

            Glide.with(this).load(extras.getString("photourl")).fitCenter().into(img);

        }

    }
}
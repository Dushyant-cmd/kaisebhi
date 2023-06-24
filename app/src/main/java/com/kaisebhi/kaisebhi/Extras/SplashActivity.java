package com.kaisebhi.kaisebhi.Extras;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.kaisebhi.kaisebhi.HomeActivity;
import com.kaisebhi.kaisebhi.MainActivity;
import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;


public class SplashActivity extends AppCompatActivity {

    private String mURL,mTitle,mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());

        final Intent intent = getIntent();
        final Uri uri = intent.getData();
        if (uri != null) {

            mTitle = uri.getQueryParameter("id");

            Intent i = new Intent(this, HomeActivity.class);
            i.putExtra("key", mTitle);
            i.putExtra("share",1);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
            return;

        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            if(sharedPrefManager.getsUser().getName() != null)
            {

                Intent loginIntent = new Intent(SplashActivity.this, HomeActivity.class);
             //   loginIntent.putExtra("Frag","home");
                startActivity(loginIntent);
            }
            else
            {
                Intent loginIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(loginIntent);
            }
                finish();

            }
        },1000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.themecolor));
        }

    }


}


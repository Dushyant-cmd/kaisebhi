package com.kaisebhi.kaisebhi.Extras;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaisebhi.kaisebhi.HomeActivity;
import com.kaisebhi.kaisebhi.MainActivity;
import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.Utility.ApplicationCustom;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;
import com.kaisebhi.kaisebhi.Utility.Utility;
import com.kaisebhi.kaisebhi.room.PortalsEntity;
import com.kaisebhi.kaisebhi.room.RoomDb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class SplashActivity extends AppCompatActivity {

    private String mURL,mTitle,mType, TAG = "SplashActivity.java";
    private RoomDb roomDb;
    private AlertDialog alertDialog;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        roomDb = ((ApplicationCustom) getApplication()).roomDb;
        mFirestore = ((ApplicationCustom) getApplication()).mFirestore;

        if(Utility.isNetworkAvailable(SplashActivity.this)) {
            init();
        } else {
            noNetworkDialog(SplashActivity.this);
        }
    }

    /**Below is AlertDialog which is prompt window layout to display an alert dialog which is not gonna dismiss until
     * the network is not connected. */
    public void noNetworkDialog(Context ctx) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setCancelable(false);
            builder.setMessage("Internet not Available");
            /**Below lambda have DialogInterface instance and which int type. */
            builder.setPositiveButton("Okay", (dialog, which) -> {
                Log.d(TAG, "noNetworkDialog: " + Utility.isNetworkAvailable(ctx));
                if(Utility.isNetworkAvailable(ctx)) {
                    Utility.toast(ctx, "Internet Connected Successfully");
                    init();
                } else {
                    noNetworkDialog(ctx);
                }
            });

            builder.create().show();
        } catch (Exception e) {
            Log.d(TAG, "noNetworkDialog: " + e);
        }
    }

    private void init() {
        PortalsEntity portalsEntity = roomDb.getPortalDao().getPortals();
        if(portalsEntity == null) {
            mFirestore.collection("appData").document("portals")
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()) {
                                try {
                                    DocumentSnapshot d = task.getResult();
                                    List<String> list = new ArrayList<>();
                                    int i=1;
                                    while(d.getString(i + "") != null) {
                                        list.add(d.getString(i + ""));
                                        i++;
                                    }

                                    PortalsEntity portalsEntity1 = new PortalsEntity(
                                            list.toArray(new String[0]),
                                            System.currentTimeMillis()
                                    );
                                    roomDb.getPortalDao().insertPortals(portalsEntity1);
                                    Log.d(TAG, "onComplete: protals " + task.getResult());
                                } catch(Exception e) {
                                    Log.d(TAG, "onCatch: " + e);
                                }
                            }
                        }
                    });
        }

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


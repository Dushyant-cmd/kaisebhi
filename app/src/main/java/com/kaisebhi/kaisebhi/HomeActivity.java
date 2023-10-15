package com.kaisebhi.kaisebhi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.kaisebhi.kaisebhi.HomeNavigation.AddQuestion.Add_Queastion;
import com.kaisebhi.kaisebhi.HomeNavigation.Notifications.Notifications;
import com.kaisebhi.kaisebhi.HomeNavigation.home.FavoriteFragment;
import com.kaisebhi.kaisebhi.HomeNavigation.home.HomeFragment;
import com.kaisebhi.kaisebhi.HomeNavigation.home.MenuFragment;
import com.kaisebhi.kaisebhi.HomeNavigation.home.MineQuestFragment;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;
import com.kaisebhi.kaisebhi.Utility.Utility;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private String TAG = "HomeActivity.java";
    private FrameLayout MainFrame;

    BottomNavigationView bottomNavigation;

    ImageView floating_add_button;

    SharedPrefManager sharedPrefManager;
    private Fragment mineFrag = new MineQuestFragment();
    private Fragment homeFrag = new HomeFragment();
    private Fragment favFrag = new FavoriteFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        getDelegate().setLocalNightMode(
                AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_home);

        checkPerm();
        sharedPrefManager = new SharedPrefManager(getApplication());

//        FirebaseDatabase realDb = FirebaseDatabase.getInstance();
//        realDb.getReference("fcmNotifications").get().addOnCompleteListener(
//                new OnCompleteListener<DataSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DataSnapshot> task) {
//                        if(task.isSuccessful()) {
//                            Log.d(TAG, "onComplete: " + task.getResult());
//                        }
//                    }
//                }
//        );

        floating_add_button = findViewById(R.id.floating_add_button);
        floating_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cart = new Intent(getApplicationContext(), Add_Queastion.class);
                startActivity(cart);
            }
        });

        MainFrame = findViewById(R.id.nav_host_fragment);

        bottomNavigation = findViewById(R.id.navigation);

        bottomNavigation.setSelectedItemId(R.id.nav_qna);
        bottomNavigation.setItemIconTintList(null);


        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.nav_menu:
                        changeFragment(new MenuFragment());
                        break;
                    case R.id.nav_star:
                        favFrag = new FavoriteFragment();
                        changeFragment(favFrag);
                        break;
                    case R.id.nav_qna:
                        homeFrag = new HomeFragment();
                        changeFragment(homeFrag);
                        break;
                    case R.id.nav_min_ques:
                        mineFrag = new MineQuestFragment();
                        changeFragment(mineFrag);
                        break;
                    case R.id.nav_notify:
                        changeFragment(new Notifications());
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        bottomNavigation.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_menu:
                        changeFragment(new MenuFragment());
                        break;
                    case R.id.nav_star:
//                        if(favFrag != null)
//                            ((FavoriteFragment) favFrag).stopExo();
//                        favFrag = new FavoriteFragment();
                        changeFragment(new FavoriteFragment());
                        break;
                    case R.id.nav_qna:
//                        if (homeFrag != null) {
//                            ((HomeFragment) homeFrag).stopExo();
//                        }
                        homeFrag = new HomeFragment();
                        changeFragment(homeFrag);
                        break;
                    case R.id.nav_min_ques:
//                        if (mineFrag != null) {
//                            ((MineQuestFragment) mineFrag).stopExo();
//                        }
                        mineFrag = new MineQuestFragment();
                        changeFragment(mineFrag);
                        break;
                    case R.id.nav_notify:
                        changeFragment(new Notifications());
                        break;
                    default:
                        break;
                }
            }
        });

        if (!Utility.isNetworkAvailable(HomeActivity.this)) {
            Utility.noNetworkDialog(HomeActivity.this);
        }
    }


    private void checkPerm() {
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_DENIED) {
            String[] permArr = new String[]{Manifest.permission.RECORD_AUDIO
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.CAMERA};

            ActivityCompat.requestPermissions(HomeActivity.this, permArr, 101);
        }

    }
    private void changeFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(MainFrame.getId(), fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();

    }


}


package com.kaisebhi.kaisebhi;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.kaisebhi.kaisebhi.HomeNavigation.home.HidesAnsFragment;
import com.kaisebhi.kaisebhi.HomeNavigation.home.MineQuestFragment;
import com.kaisebhi.kaisebhi.HomeNavigation.home.PaidAnsFragment;
import com.kaisebhi.kaisebhi.HomeNavigation.home.SearchQuestionFragment;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;


public class ActivityForFrag extends AppCompatActivity {

    private FrameLayout MainMainFrame;
    String tabType = "hide";
    public static String onResetFragment = "null";

    String Frag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_frag);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        getDelegate().setLocalNightMode(
                AppCompatDelegate.MODE_NIGHT_NO);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Frag = extras.getString("Frag");
        }


        SharedPrefManager sh  = new SharedPrefManager(this);

        TextView toolbar = findViewById(R.id.textHeader);

       MainMainFrame = findViewById(R.id.actvityforFrag);

        if(Frag.equals("minQ"))
        {  setDefaultFragment(new MineQuestFragment());
            toolbar.setText("Mine Questions");
        }

        if(Frag.equals("showAns"))
        {
            tabType = extras.getString("tabType");
            if(tabType.equals("show")){
                setDefaultFragment(new PaidAnsFragment());
                toolbar.setText("Paid Answers");
            }else{
                setDefaultFragment(new HidesAnsFragment());
                toolbar.setText("Hide Answers");
            }
        }

        if(Frag.equals("searchQ"))
        {
            setDefaultFragment(new SearchQuestionFragment());
            toolbar.setText("Mine Questions");
            findViewById(R.id.rl_mainActionbar).setVisibility(View.GONE);
        }
    }

    private void setDefaultFragment(Fragment fragment)
    {
        Bundle bundle = new Bundle();
        bundle.putString("tabType",tabType);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(MainMainFrame.getId(),fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }




    public void backtoActivity(View view) {
        super.onBackPressed();
    }
}

package com.kaisebhi.kaisebhi.HomeNavigation.terms;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.databinding.ActivityTermsAndPolicyBinding;

public class TermsAndPolicyActivity extends AppCompatActivity {
    private ActivityTermsAndPolicyBinding binding;
    private String TAG = "TermsAndPolicyActivity.java";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTermsAndPolicyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
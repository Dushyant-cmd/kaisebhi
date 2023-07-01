package com.kaisebhi.kaisebhi.Utility;

import android.app.Application;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ApplicationCustom extends Application {
    public FirebaseFirestore mFirestore;
    public FirebaseAuth mAuth;

    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
    }

    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
    }
}

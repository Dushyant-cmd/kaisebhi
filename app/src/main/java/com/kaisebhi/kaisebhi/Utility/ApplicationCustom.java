package com.kaisebhi.kaisebhi.Utility;

import android.app.Application;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaisebhi.kaisebhi.room.RoomDb;

public class ApplicationCustom extends Application {
    public FirebaseFirestore mFirestore;
    public FirebaseAuth mAuth;
    public RoomDb roomDb;

    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
    }

    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        roomDb = RoomDb.getDbInstance(getApplicationContext());
    }
}

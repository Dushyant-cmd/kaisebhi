package com.kaisebhi.kaisebhi.Utility;

import android.app.Application;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.kaisebhi.kaisebhi.room.RoomDb;

public class ApplicationCustom extends Application {
    public FirebaseFirestore mFirestore;
    public FirebaseAuth mAuth;
    public RoomDb roomDb;
    public FirebaseStorage storage;
    public FirebaseMessaging fcm;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        initialize();
    }

    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        roomDb = RoomDb.getDbInstance(getApplicationContext());
        storage = FirebaseStorage.getInstance();
        fcm = FirebaseMessaging.getInstance();
    }
}

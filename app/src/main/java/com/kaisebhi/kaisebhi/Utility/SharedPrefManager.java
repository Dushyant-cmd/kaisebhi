package com.kaisebhi.kaisebhi.Utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;


public class SharedPrefManager {

    public static final String SHARED_PREF_NAME = "mySharedPreffFladoAgra";

    public static SharedPrefManager mInstance;
    private Context ctx;
    private FirebaseAuth mAuth;

    public SharedPrefManager(Context ctx)
    {
        mAuth = FirebaseAuth.getInstance();
        this.ctx = ctx;
    }


    public static synchronized SharedPrefManager getInstance(Context ctx)
    {
        if(mInstance == null)
        {
            mInstance = new SharedPrefManager(ctx);
        }
        return mInstance;
    }


    public void saveUser(String name, String mobile,String Uid,String profile, String email)
    {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name",name);
        editor.putString("mobile",mobile);
        editor.putString("uid",Uid);
        editor.putString("profile",profile);
        editor.putString("email", email);
        editor.apply();
    }


    public User getsUser()
    {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getString("name",null),
                sharedPreferences.getString("mobile",null),
                sharedPreferences.getString("uid",null),
                sharedPreferences.getString("profile",null),
                sharedPreferences.getString("location","Agra"),
                sharedPreferences.getInt("item",0),
                sharedPreferences.getBoolean("edelivery",false)
        );
    }

    public void logoutUser()
    {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        mAuth.signOut();
    }

}

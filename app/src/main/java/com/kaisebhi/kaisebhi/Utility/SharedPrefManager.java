package com.kaisebhi.kaisebhi.Utility;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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


    public void saveUser(String name, String mobile,String Uid,String profile, String email, String address)
    {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name",name);
        editor.putString("mobile",mobile);
        editor.putString("uid",Uid);
        editor.putString("profile",profile);
        editor.putString("email", email);
        editor.putString("address", address);
        editor.apply();
    }

    public void saveProfilePic(String picUrl) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("picUrl", picUrl).apply();
    }

    public String getProfilePic() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("picUrl", "");
    }

    public User getsUser() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getString("name",null),
                sharedPreferences.getString("mobile",null),
                sharedPreferences.getString("uid",null),
                sharedPreferences.getString("profile",null),
                sharedPreferences.getString("address","NA"),
                sharedPreferences.getInt("item",0),
                sharedPreferences.getBoolean("edelivery",false),
                sharedPreferences.getString("email", "NA")
        );
    }

    public void logoutUser()
    {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        GoogleSignInClient client = GoogleSignIn.getClient(ctx, GoogleSignInOptions.DEFAULT_SIGN_IN);
        client.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    mAuth.signOut();
                } else {
                    mAuth.signOut();
                }
            }
        });
    }

}

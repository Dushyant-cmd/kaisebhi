package com.kaisebhi.kaisebhi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {


    RelativeLayout signProgress;
    int RC_SIGN_IN = 0;
    RelativeLayout relativeLayout;
    String Token;
    public GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirestore;
    private String TAG = "MainActivity.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        relativeLayout = findViewById(R.id.emailBox);
        signProgress = findViewById(R.id.pgore);
        findViewById(R.id.root).isInEditMode();

        Token = FirebaseInstanceId.getInstance().getToken();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("316855122484-l6bun4vaf6ve6rt08fekbrjo5712qhqa.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);

        findViewById(R.id.signGoogle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInGoogle();
            }
        });

        findViewById(R.id.signEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                relativeLayout.setVisibility(View.VISIBLE);

            }

        });

        findViewById(R.id.emailSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText em = findViewById(R.id.emailInput);
                EditText pass = findViewById(R.id.passInput);
                

                if (em.getText().toString().isEmpty() || pass.getText().toString().isEmpty()) {
                    em.setError("Add Email Address!");
                    pass.setError("Add 6 characters Password!");
                } else {
                    //Regex expression of Java for email id validation
                    String formatExp = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
                    //Convert String regex expression into Pattern
                    Pattern pattern = Pattern.compile(formatExp, Pattern.CASE_INSENSITIVE);
                    //Matcher class object contains result of matching email
                    Matcher matcher = pattern.matcher(em.getText().toString());
                    if(matcher.matches()) {
                        //Email is in valid format
                        loginEmail(em.getText().toString(), pass.getText().toString());
                    } else {
                        //email is not in valid format.
                        Toast.makeText(MainActivity.this, "Please enter correct email!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }


    public void backtoActivity(View view) {
        relativeLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (relativeLayout.getVisibility() == View.VISIBLE)
            relativeLayout.setVisibility(View.GONE);
        else
            super.onBackPressed();

    }

    private void SignInGoogle() {
//        signProgress.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            if (completedTask.isSuccessful()) {
                GoogleSignInAccount googleSignInAccount = completedTask.getResult(ApiException.class);
                if (googleSignInAccount != null) {
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                    SignUp(authCredential, googleSignInAccount);
                }
            }
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//            SignUp(account.getDisplayName(), account.getEmail());
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }


    /**
     * Method to sign-in/sign-up with google.
     */
    private void SignUp(final AuthCredential authCredential, GoogleSignInAccount googleSignInAccount) {
        signProgress.setVisibility(View.VISIBLE);
        String name = googleSignInAccount.getDisplayName();
        String email = googleSignInAccount.getEmail();
        Toast.makeText(MainActivity.this, name + ", " + email, Toast.LENGTH_SHORT).show();
        mFirestore.collection("ids").document("userId").get().addOnCompleteListener(
                task -> {
                    //get current id of users
                    if (task.isSuccessful()) {
                        try {
                            DocumentSnapshot doc = task.getResult();
                            long updatedUserId = doc.getLong("id") + 1;
                            mFirestore.collection("users").whereEqualTo("email", email).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                if (task.getResult().getDocuments().size() > 0) {
                                                    //user exist
                                                    DocumentSnapshot d = task.getResult().getDocuments().get(0);
                                                    Log.d(TAG, "onComplete: user exist " + d.toString());
                                                    signProgress.setVisibility(View.GONE);
                                                    SharedPrefManager.getInstance(getApplicationContext()).saveUser(d.getString("name"), d.getLong("mobile").toString(),
                                                            (updatedUserId - 1) + "", d.getString("profile"), d.getString("email"));
                                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    //user does not exist.
                                                    Log.d(TAG, "onComplete: user not exist ");
                                                    mFirebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> googleTask) {
                                                            if (googleTask.isSuccessful()) {
                                                                HashMap<String, Object> map = new HashMap<>();
                                                                map.put("name", name);
                                                                map.put("mobile", 0);
                                                                map.put("userId", updatedUserId);
                                                                map.put("profile", "incomplete");
                                                                map.put("email", email);
                                                                map.put("timestamp", System.currentTimeMillis());

                                                                mFirestore.collection("users").document(updatedUserId + "")
                                                                        .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    HashMap<String, Object> updateMap = new HashMap<>();
                                                                                    updateMap.put("id", updatedUserId);
                                                                                    mFirestore.collection("ids").document("userId")
                                                                                                    .update(updateMap).addOnCompleteListener(
                                                                                                    new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if(task.isSuccessful()) {
                                                                                                                signProgress.setVisibility(View.VISIBLE);
                                                                                                                SharedPrefManager.getInstance(getApplicationContext()).saveUser(name, 0 + "", updatedUserId + "", "inComplete", email);
                                                                                                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                                                                                                startActivity(intent);
                                                                                                                finish();
                                                                                                            } else {
                                                                                                                Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                            );
                                                                                } else {
                                                                                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                            } else {
                                                                Toast.makeText(MainActivity.this, googleTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    });

                        } catch (Exception e) {
                            Log.d(TAG, "SignUp: failure google" + e);
                        }
                    }
                }
        );
    }


    /**
     * Below method check user is already on firebase else createUserWithEmailAndPassword method of
     * FirebaseAuth and pass email and pass
     */
    private void loginEmail(final String email, final String pass) {
        signProgress.setVisibility(View.VISIBLE);
        mFirestore.collection("ids").document("userId").get().addOnCompleteListener(
                task -> {
                    //get current id of users
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        long updatedUserId = doc.getLong("id") + 1;
                        mFirestore.collection("users").whereEqualTo("email", email).get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.getResult().getDocuments().size() > 0) {
                                            //sign user
                                            Log.d(TAG, "loginEmail: sign-in" + updatedUserId);
                                            mFirebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task1 -> {
                                                try {
                                                    if (task1.isSuccessful()) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot doc1 = task.getResult().getDocuments().get(0);
                                                            signProgress.setVisibility(View.GONE);
                                                            SharedPrefManager.getInstance(getApplicationContext()).saveUser(doc1.getString("name"), doc1.getLong("mobile").toString(),
                                                                    (updatedUserId - 1) + "", doc1.getString("profile"), doc1.getString("email"));
                                                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            signProgress.setVisibility(View.GONE);
                                                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        signProgress.setVisibility(View.GONE);
                                                        Toast.makeText(MainActivity.this, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (Exception e) {
                                                    Log.d(TAG, "loginEmail: " + e);
                                                }
                                            });
                                        } else {
                                            //create user
                                            Log.d(TAG, "loginEmail: sign-up");
                                            mFirebaseAuth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener(authResult -> {
                                                try {
                                                    HashMap<String, Object> map = new HashMap<>();
                                                    map.put("name", "Guest_" + updatedUserId);
                                                    map.put("mobile", 0);
                                                    map.put("userId", updatedUserId);
                                                    map.put("profile", "incomplete");
                                                    map.put("email", email);
                                                    map.put("timestamp", System.currentTimeMillis());

                                                    mFirestore.collection("users").document(updatedUserId + "").set(map)
                                                            .addOnCompleteListener(task12 -> {
                                                                if (task12.isSuccessful()) {
                                                                    HashMap<String, Object> map1 = new HashMap<>();
                                                                    map1.put("id", updatedUserId);
                                                                    mFirestore.collection("ids").document("userId").update(map1)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    Log.d(TAG, "onSuccess: id updated");
                                                                                }
                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(Exception e) {
                                                                                    Log.d(TAG, "onSuccess: " + e);
                                                                                }
                                                                            });
                                                                    signProgress.setVisibility(View.GONE);
                                                                    SharedPrefManager.getInstance(getApplicationContext()).saveUser("Guest_" + updatedUserId, "0",
                                                                            updatedUserId + "", "inComplete", email);
                                                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                } else {
                                                                    signProgress.setVisibility(View.GONE);
                                                                    Toast.makeText(MainActivity.this, task12.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });

                                                } catch (Exception e) {
                                                    signProgress.setVisibility(View.GONE);
                                                    Toast.makeText(getApplicationContext(), "Invalid Credentials. Please try Again!", Toast.LENGTH_LONG).show();
                                                    Log.d(TAG, "onSuccess: " + e);
                                                }
                                            });
                                        }
                                    }
                                });

                    }
                });

//        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().loginEmail(email, pass, Token);
//
//        call.enqueue(new Callback<DefaultResponse>() {
//            @Override
//            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
//                DefaultResponse dr = response.body();
//                if (response.code() == 201) {
//                    String data = dr.getMessage();
//                    String[] dif = data.split("#");
//                    signProgress.setVisibility(View.GONE);
//                    SharedPrefManager.getInstance(getApplicationContext()).saveUser(dif[0], dif[1], dif[2], dif[3]);
//                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    signProgress.setVisibility(View.GONE);
//                    Toast.makeText(getApplicationContext(), "Invalid Credentials. Please try Again!", Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<DefaultResponse> call, Throwable t) {
//                signProgress.setVisibility(View.GONE);
//                Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_LONG).show();
//            }
//        });

    }


}

package com.kaisebhi.kaisebhi;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kaisebhi.kaisebhi.Utility.ApplicationCustom;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;
import com.kaisebhi.kaisebhi.Utility.Utility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
    private SharedPrefManager sharedPreferences;
    private FirebaseMessaging fcm;
    private String token = "";
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        sharedPreferences = SharedPrefManager.getInstance(getApplicationContext());
        progressDialog = Utility.createAlertDialog(MainActivity.this);
        progressDialog.setCancelable(false);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        relativeLayout = findViewById(R.id.emailBox);
        signProgress = findViewById(R.id.pgore);
        signProgress.setEnabled(false);
        findViewById(R.id.root).isInEditMode();
        fcm = ((ApplicationCustom) getApplication()).fcm;

        /**FirebaseInstanceId not working so call getToken and add listener when get token string. */
        fcm.getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()) {
                    token = task.getResult();
                }
            }
        });

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
                

                if (em.getText().toString().isEmpty() || pass.getText().toString().isEmpty() || pass.getText().toString().length() < 6) {
                    em.setError("Add Email Address!");
                    pass.setError("Add 6 characters Password!");
                    progressDialog.dismiss();
                    signProgress.setVisibility(View.GONE);
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
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }


    /**
     * Method to sign-in/sign-up with google.
     */
    private void SignUp(final AuthCredential authCredential, GoogleSignInAccount googleSignInAccount) {
        signProgress.setVisibility(View.VISIBLE);
        progressDialog.show();
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
                                                    progressDialog.dismiss();
                                                    SharedPrefManager.getInstance(getApplicationContext())
                                                            .saveUser(d.getString("name"), d.getLong("mobile").toString(),
                                                            d.getLong("userId").toString(), d.getString("profile"),
                                                                    d.getString("email"), d.getString("address"),
                                                                    d.getLong("rewards"), d.getString("referId"),
                                                                    d.getString("fcmToken") + "," + token);
                                                    Map<String, Object> m = new HashMap<>();
                                                    m.put("fcmToken", d.getString("fcmToken") + "," + token);
                                                    mFirestore.collection("users")
                                                            .document(d.getLong("userId") + "").update(m)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            Log.d(TAG, "onSuccess: fcm success");
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.d(TAG, "onFailure: fcm error" + e);
                                                                }
                                                            });
                                                    SharedPrefManager.getInstance(getApplicationContext()).saveProfilePic
                                                            (d.getString("picUrl"));
                                                    sharedPreferences.setImageRef(d.getString("imageRef"));
                                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    //user does not exist.
                                                    Log.d(TAG, "onComplete: user not exist ");
                                                    mFirebaseAuth.signInWithCredential(authCredential)
                                                            .addOnCompleteListener(MainActivity.this,
                                                                    new OnCompleteListener<AuthResult>() {
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
                                                                map.put("picUrl", "");
                                                                map.put("address", "");
                                                                map.put("rewards", 0);
                                                                String imageRef = UUID.randomUUID().toString();
                                                                map.put("imageRef", imageRef);
                                                                map.put("pass", "loginByGoogle");
                                                                String referId = UUID.randomUUID().toString().substring(0, 6);
                                                                map.put("referId", referId);
                                                                map.put("fcmToken", token);

                                                                mFirestore.collection("users")
                                                                        .document(updatedUserId + "")
                                                                        .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    HashMap<String, Object> updateMap = new HashMap<>();
                                                                                    updateMap.put("id", updatedUserId);
                                                                                    mFirestore.collection("ids")
                                                                                            .document("userId")
                                                                                                    .update(updateMap)
                                                                                            .addOnCompleteListener(
                                                                                                    new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if(task.isSuccessful()) {
                                                                                                                signProgress.setVisibility(View.VISIBLE);
                                                                                                                progressDialog.show();
                                                                                                                SharedPrefManager.getInstance(getApplicationContext())
                                                                                                                        .saveUser(name, 0 + "", updatedUserId + "", "inComplete",
                                                                                                                                email, "", 0, referId, token);
                                                                                                                SharedPrefManager.getInstance(getApplicationContext()).saveProfilePic("");
                                                                                                                sharedPreferences.setImageRef(imageRef);
                                                                                                                displayReferralDialog();
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
        progressDialog.show();
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
                                        if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
                                            //sign user
                                            Log.d(TAG, "loginEmail: sign-in" + updatedUserId);
                                            mFirebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task1 -> {
                                                try {
                                                    if (task1.isSuccessful()) {
                                                            DocumentSnapshot doc1 = task.getResult().getDocuments().get(0);
                                                            signProgress.setVisibility(View.GONE);
                                                            progressDialog.dismiss();
                                                            SharedPrefManager.getInstance(getApplicationContext()).saveUser(doc1.getString("name"),
                                                                    doc1.getLong("mobile").toString(),
                                                                    doc1.getLong("userId").toString(), doc1.getString("profile"),
                                                                    doc1.getString("email"), doc1.getString("address"),
                                                                    doc1.getLong("rewards"), doc1.getString("referId"),
                                                                    doc1.getString("fcmToken") + "," + token);
                                                            SharedPrefManager.getInstance(getApplicationContext()).saveProfilePic(doc.getString("picUrl"));
                                                            sharedPreferences.setImageRef(doc1.getString("imageRef"));
                                                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(intent);
                                                            finish();
//                                                        } else {
//                                                            signProgress.setVisibility(View.GONE);
//                                                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                                        }
                                                    } else {
                                                        signProgress.setVisibility(View.GONE);
                                                        progressDialog.dismiss();
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
                                                    map.put("pass", pass);
                                                    map.put("address", "");
                                                    map.put("picUrl", "");
                                                    map.put("rewards", 0);
                                                    map.put("timestamp", System.currentTimeMillis());
                                                    String imageRef = UUID.randomUUID().toString();
                                                    String referId = UUID.randomUUID().toString().substring(0, 6);
                                                    map.put("referId", referId);
                                                    map.put("imageRef", imageRef);
                                                    map.put("fcmToken", token);

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
                                                                    progressDialog.dismiss();
                                                                    SharedPrefManager.getInstance(getApplicationContext())
                                                                            .saveUser("Guest_" + updatedUserId, "0",
                                                                            updatedUserId + "", "inComplete", email, "", 0, referId,
                                                                                    token);
                                                                    sharedPreferences.setImageRef(imageRef);
                                                                    displayReferralDialog();
                                                                } else {
                                                                    signProgress.setVisibility(View.GONE);
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(MainActivity.this, task12.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });

                                                } catch (Exception e) {
                                                    signProgress.setVisibility(View.GONE);
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "Invalid Credentials. Please try Again!", Toast.LENGTH_LONG).show();
                                                    Log.d(TAG, "onSuccess: " + e);
                                                }
                                            });
                                        }
                                    }
                                });

                    }
                });
    }

    private void displayReferralDialog() {
        try {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
            View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.referral_layout, null);
            EditText referralET = v.findViewById(R.id.referralET);
            bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            v.findViewById(R.id.cancelBtn).setOnClickListener(view -> {
                //cancel button
                bottomSheetDialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
            bottomSheetDialog.setCancelable(false);

            v.findViewById(R.id.applyBtn).setOnClickListener(view -> {
                //apply button
                if(!referralET.getText().toString().isEmpty()) {
                    mFirestore.collection("users").whereEqualTo("referId", referralET.getText().toString())
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()) {
                                        List<DocumentSnapshot> list = task.getResult().getDocuments();
                                        if(!list.isEmpty()) {
                                            long totalReward = list.get(0).getLong("rewards") + 10;
                                            HashMap<String, Object> map = new HashMap<>();
                                            map.put("rewards", totalReward);
                                            mFirestore.collection("users").document(list.get(0).getLong("userId").toString())
                                                    .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()) {
                                                                sharedPreferences.setReward(totalReward);
                                                            } else {
                                                                Log.d(TAG, "onComplete: " + task.getException());
                                                            }
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(MainActivity.this, "Referral ID is not valid", Toast.LENGTH_SHORT).show();
                                        }
                                    } else
                                        Log.d(TAG, "onComplete: " + task.getException());
                                }
                            });

                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
            bottomSheetDialog.setContentView(v);
            bottomSheetDialog.show();
        } catch (Exception e) {
            Log.d(TAG, "displayReferralDialog: " + e);
        }
    }
}

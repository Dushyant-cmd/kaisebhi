package com.kaisebhi.kaisebhi.HomeNavigation.Profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaisebhi.kaisebhi.HomeActivity;
import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.Utility.ApplicationCustom;
import com.kaisebhi.kaisebhi.Utility.DefaultResponse;
import com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;
import com.kaisebhi.kaisebhi.Utility.Utility;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileUpdate extends AppCompatActivity {

    private CircleImageView ProfileImage;
    String pCheck = null;
    String ID = null;
    private Uri postUri = null;
    private RelativeLayout updateBtn, updatePass;
    private SharedPrefManager sharedPrefManager;
    private LinearLayout pro, pass;
    private TextView dd;
    private EditText edName, edAdd, edMobile, edEmail, edPass;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore mFirestore;

    TextView btnText;
    ProgressBar btnProgress;
    TextView passText;
    int Check = 0;
    ProgressBar passProgress;
    FrameLayout updateProgress;
    private String TAG = "ProfileUpdate.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        sharedPrefManager = SharedPrefManager.getInstance(ProfileUpdate.this);
        mFirestore = ((ApplicationCustom) getApplication()).mFirestore;
        firebaseStorage = ((ApplicationCustom) getApplication()).storage;

        ID = sharedPrefManager.getsUser().getUid();
        edName = findViewById(R.id.dName);
        edPass = findViewById(R.id.dpass);
        dd = findViewById(R.id.uploadText);
        edMobile = findViewById(R.id.dMobile);
        edAdd = findViewById(R.id.dAddress);
        pass = findViewById(R.id.pDetails);
        pro = findViewById(R.id.uDetails);
        updateBtn = findViewById(R.id.updateProfile);
        updateProgress = findViewById(R.id.updateProgress);
        updatePass = findViewById(R.id.updatePassword);
        edEmail = findViewById(R.id.dEmail);
        edEmail.setEnabled(false);
        ProfileImage = findViewById(R.id.Dprofile);


        btnProgress = findViewById(R.id.loginProgress);
        btnText = findViewById(R.id.login_text);

        passProgress = findViewById(R.id.passProgress);
        passText = findViewById(R.id.passtext);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String D = extras.getString("Update");
            if (D.equals("pass")) {
                pro.setVisibility(View.GONE);
                pass.setVisibility(View.VISIBLE);
                dd.setVisibility(View.INVISIBLE);
            } else {
                pro.setVisibility(View.VISIBLE);
                ProfileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profile();
                    }
                });
            }
        }


        fetchProfile();

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBtn.setEnabled(false);
                updateProfile();
            }
        });

        updatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePass();
            }
        });
    }

    public void fetchProfile() {
        updateProgress.setVisibility(View.VISIBLE);
        mFirestore.collection("users").document(sharedPrefManager.getsUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot d = task.getResult();
                            Glide.with(getApplicationContext()).load(d.getString("picUrl")).dontAnimate().centerCrop().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).placeholder(R.drawable.profile).into(ProfileImage);
                            edName.setText(d.getString("name"));
                            String email = d.getString("email");
                            String address = d.getString("address");
                            String mobile = d.getLong("mobile").toString();
                            if (!email.isEmpty())
                                edEmail.setText(email);

                            if (!address.isEmpty())
                                edAdd.setText(address);

                            if (!mobile.isEmpty())
                                edMobile.setText(mobile);

                            updateProgress.setVisibility(View.GONE);
                        } else {
                            Log.d(TAG, "onComplete: " + task.getException());
                        }
                    }
                });
    }


    public void profile() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .setMinCropResultSize(512, 512)
                .start(ProfileUpdate.this);
    }


    public void updateProfile() {
        final String name, mob, email, address;
        name = edName.getText().toString();
        mob = edMobile.getText().toString();
        email = edEmail.getText().toString();
        address = edAdd.getText().toString();

        if (mob.length() != 10) {
            edMobile.setError("Number must be of 10 digits");
            updateBtn.setEnabled(true);
            return;
        }

        if(Long.parseLong(mob.substring(0, 1)) < 6 || mob.contains("000000")) {
            edMobile.setError("Add valid mobile number");
            updateBtn.setEnabled(true);
            return;
        }

        Random rand = new Random();
        final String otp = String.format("%04d", rand.nextInt(10000));

        if (name.isEmpty()) {
            edName.setError("Add Name");
            updateBtn.setEnabled(true);
            return;
        }

        if (email.isEmpty()) {
            edEmail.setError("Add Email");
            updateBtn.setEnabled(true);
            return;
        }

        if (address.isEmpty()) {
            edAdd.setError("Add Address");
            updateBtn.setEnabled(true);
            return;
        }

        if (pCheck != null) {

            btnProgress.setVisibility(View.VISIBLE);
            btnText.setVisibility(View.INVISIBLE);
            StorageReference storageReference = firebaseStorage.getReference();
            StorageReference imageRef;
            if(sharedPrefManager.getImageRef().isEmpty()) {
                imageRef = storageReference.child("profileImages/" + UUID.randomUUID());
            } else {
                String prevRef = "profileImages/" + sharedPrefManager.getImageRef();
                imageRef = storageReference.child(prevRef);
            }
            UploadTask uploadTask = imageRef.putFile(postUri);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()) {
                        Task<Uri> imageTask = imageRef.getDownloadUrl();
                        imageTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Map<String,Object> map = new HashMap<>();
                                map.put("name", name);
                                map.put("mobile", Long.parseLong(mob));
                                map.put("address", address);
                                map.put("picUrl", uri.toString());
                                Log.d(TAG, "onComplete: with img " + map);
                                mFirestore.collection("users").document(sharedPrefManager.getsUser().getUid()).update(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Utility.toast(ProfileUpdate.this, "Profile Updated Successfully");
                                                sharedPrefManager.saveProfilePic(uri.toString());
                                                sharedPrefManager.saveUser(name, mob, sharedPrefManager.getsUser().getUid(),
                                                        sharedPrefManager.getProfilePic(), sharedPrefManager.getsUser().getEmail(),
                                                        address, sharedPrefManager.getsUser().getReward(), sharedPrefManager.getsUser().getReferId(),
                                                        sharedPrefManager.getsUser().getFcmToken());
                                                Check = 1;
                                                btnText.setVisibility(View.VISIBLE);
                                                btnProgress.setVisibility(View.INVISIBLE);
                                                onBackPressed();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                updateBtn.setEnabled(true);
                                                Log.d(TAG, "onFailure: " + e);
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e);
                                updateBtn.setEnabled(true);
                                Utility.toast(ProfileUpdate.this, e.toString());
                            }
                        });
                    } else {
                        Utility.toast(ProfileUpdate.this, task.getException().toString());
                        Log.d(TAG, "onComplete: " + task.getException());
                        updateBtn.setEnabled(true);
                    }
                }
            });
        } else {

            btnText.setVisibility(View.INVISIBLE);
            btnProgress.setVisibility(View.VISIBLE);
            Map<String,Object> map = new HashMap<>();
            map.put("name", name);
            map.put("mobile", Long.parseLong(mob));
            map.put("address", address);
            Log.d(TAG, "onComplete: no image" + map);
            mFirestore.collection("users").document(sharedPrefManager.getsUser().getUid()).update(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Utility.toast(ProfileUpdate.this, "Profile Updated Successfully");
                            sharedPrefManager.saveUser(name, mob, sharedPrefManager.getsUser().getUid(),
                                    sharedPrefManager.getProfilePic(), sharedPrefManager.getsUser().getEmail(),
                                    address, sharedPrefManager.getsUser().getReward(), sharedPrefManager.getsUser().getReferId(),
                                    sharedPrefManager.getsUser().getFcmToken());
                            Check = 1;
                            btnText.setVisibility(View.VISIBLE);
                            btnProgress.setVisibility(View.INVISIBLE);
                            onBackPressed();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            updateBtn.setEnabled(true);
                            Log.d(TAG, "onFailure: " + e);
                        }
                    });
        }
    }


    public void updatePass() {
        String newpass;
        newpass = edPass.getText().toString();

        if (newpass.isEmpty()) {
            edPass.setError("Add Paasword!");
            return;
        }

        passText.setVisibility(View.VISIBLE);
        passProgress.setVisibility(View.INVISIBLE);

        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().updatePassword(newpass, ID);
        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                DefaultResponse dr = response.body();

                if (response.code() == 201) {
                    String data = dr.getMessage();
                    Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Some error occured!", Toast.LENGTH_SHORT).show();
                }
                passProgress.setVisibility(View.INVISIBLE);
                passText.setVisibility(View.VISIBLE);

            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
            }

        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                postUri = result.getUri();
                ProfileImage.setImageURI(postUri);
                pCheck = "Profile";

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    public void backtoActivity(View view) {
        if (Check == 1) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if (Check == 1) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        } else {
            super.onBackPressed();
        }
    }

    public void uploadProfile(View view) {
        profile();
    }
}


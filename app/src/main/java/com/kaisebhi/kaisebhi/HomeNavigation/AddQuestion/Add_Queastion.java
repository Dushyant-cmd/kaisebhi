package com.kaisebhi.kaisebhi.HomeNavigation.AddQuestion;

import static com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient.BASE_URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaisebhi.kaisebhi.Extras.SplashActivity;
import com.kaisebhi.kaisebhi.HomeActivity;
import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.Utility.ApplicationCustom;
import com.kaisebhi.kaisebhi.Utility.DefaultResponse;
import com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;
import com.kaisebhi.kaisebhi.Utility.Utility;
import com.kaisebhi.kaisebhi.room.PortalsEntity;
import com.kaisebhi.kaisebhi.room.RoomDb;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class Add_Queastion extends AppCompatActivity {

    private Button addDelivery;
    private EditText quesTitle, quesDesc, otherPortalEditText;
    private Uri postUri = null;
    private SharedPrefManager sharedPrefManager;
    private FirebaseFirestore mFirestore;
    private Button uploadBtn;
    private FirebaseStorage storage;
    String pCheck = null, TAG = "Add_Question.java";
    ProgressDialog progressDialog;
    ImageView selectQues;
    String Qid = "", selectedPortal = "";
    private Spinner spinner;
    private RoomDb roomDb;
    private TextInputLayout portalIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_ques);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        sharedPrefManager = new SharedPrefManager(Add_Queastion.this);
        mFirestore = ((ApplicationCustom) getApplication()).mFirestore;
        storage = ((ApplicationCustom) getApplication()).storage;
        progressDialog = new ProgressDialog(Add_Queastion.this);
        progressDialog.setCancelable(false);
        roomDb = ((ApplicationCustom) getApplication()).roomDb;
        spinner = findViewById(R.id.portalSpinner);
        otherPortalEditText = findViewById(R.id.otherPortal);
        portalIL = findViewById(R.id.portalIL);

        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Question details processing....");

        selectQues = findViewById(R.id.quesImage);
        if (roomDb.getPortalDao().getPortals() == null) {
            mFirestore.collection("appData").document("portals")
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                try {
                                    DocumentSnapshot d = task.getResult();
                                    List<String> list = new ArrayList<>();
                                    int i = 1;
                                    while (d.getString(i + "") != null) {
                                        list.add(d.getString(i + ""));
                                        i++;
                                    }

                                    PortalsEntity portalsEntity1 = new PortalsEntity(
                                            list.toArray(new String[0]),
                                            System.currentTimeMillis()
                                    );
                                    roomDb.getPortalDao().insertPortals(portalsEntity1);
                                    spinner.setAdapter(new ArrayAdapter<String>(Add_Queastion.this, android.R.layout.simple_dropdown_item_1line,
                                            roomDb.getPortalDao().getPortals().portals));
                                    Log.d(TAG, "onComplete: protals " + task.getResult());
                                } catch (Exception e) {
                                    Log.d(TAG, "onCatch: " + e);
                                }
                            }
                        }
                    });
        } else {
            Log.d(TAG, "onCreate: hasData");
            spinner.setAdapter(new ArrayAdapter<String>(Add_Queastion.this, android.R.layout.simple_dropdown_item_1line,
                    roomDb.getPortalDao().getPortals().portals));
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPortal = spinner.getSelectedItem().toString();
                Log.d(TAG, "onItemSelected: " + selectedPortal);
                if (selectedPortal.matches("other")) {
                    portalIL.setVisibility(View.VISIBLE);
                    selectedPortal = "";
                } else {
                    portalIL.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        quesTitle = findViewById(R.id.q_title);
        quesDesc = findViewById(R.id.a_desc);
        uploadBtn = findViewById(R.id.uploadQues);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            Qid = extras.getString("key");
            quesTitle.setText(extras.getString("title"));
            quesDesc.setText(extras.getString("desc"));

            if (extras.getString("qimg").length() > 0) {
                Glide.with(getApplicationContext()).load(extras.getString("qimg")).fitCenter().into(selectQues);
            }
            Button uploadBtn = findViewById(R.id.uploadQues);
            uploadBtn.setText("Update Question");
        }

        selectQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .setMinCropResultSize(512, 512)
                        .start(Add_Queastion.this);
            }
        });


        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (pCheck == null) {
                    findViewById(R.id.uploadQues).setClickable(false);
                    uploadQues();
                } else {
                    findViewById(R.id.uploadQues).setClickable(false);
                    uploadQuesImage();
                }
            }
        });


    }


    private void uploadQues() {
        String title = quesTitle.getText().toString();
        String desc = quesDesc.getText().toString();

        if (title.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Add Question !", Toast.LENGTH_SHORT).show();
            uploadBtn.setClickable(true);
            return;
        }

        if (spinner.getSelectedItem().toString().matches("Please Select a Portal")) {
            Toast.makeText(getApplicationContext(), "Select Portal !", Toast.LENGTH_SHORT).show();
            uploadBtn.setClickable(true);
            return;
        } else if (spinner.getSelectedItem().toString().matches("other") && otherPortalEditText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Add Portal !", Toast.LENGTH_SHORT).show();
            uploadBtn.setClickable(true);
            return;
        } else {
            selectedPortal = otherPortalEditText.getText().toString();
        }

        progressDialog.show();
        if (uploadBtn.getText().toString().matches("Update Question")) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("title", title);
            map.put("desc", desc);
            mFirestore.collection("questions").document(Qid).update(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Log.d(TAG, "onSuccess: updated success");
                            Utility.toast(Add_Queastion.this, "Question Waiting for Approve! ");
                            Intent cart = new Intent(getApplicationContext(), HomeActivity.class);
                            cart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            cart.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(cart);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e);
                        }
                    });
        } else {
            mFirestore.collection("ids").document("questionId").get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                long updateId = task.getResult().getLong("id") + 1;
                                HashMap<String, Object> questionMap = new HashMap<>();
                                questionMap.put("title", title);
                                questionMap.put("desc", desc);
                                questionMap.put("likes", "0");
                                questionMap.put("qpic", "na");
                                questionMap.put("checkFav", false);
                                questionMap.put("checkLike", false);
                                questionMap.put("tanswers", "false");
                                questionMap.put("uname", sharedPrefManager.getsUser().getName());
                                questionMap.put("userId", sharedPrefManager.getsUser().getUid());
                                questionMap.put("id", updateId + "");
                                questionMap.put("qualityCheck", false);
                                questionMap.put("timestamp", System.currentTimeMillis());
                                questionMap.put("likedByUser", "");
                                questionMap.put("image", "");
                                questionMap.put("userPicUrl", sharedPrefManager.getProfilePic());
                                questionMap.put("imageRef", "");
                                questionMap.put("portal", selectedPortal);
                                mFirestore.collection("questions").document(updateId + "").set(questionMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                HashMap<String, Object> map = new HashMap<>();
                                                map.put("id", updateId);
                                                mFirestore.collection("ids").document("questionId").update(map)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Log.d(TAG, "onSuccess: success");
                                                                progressDialog.dismiss();
                                                                Utility.toast(Add_Queastion.this, "Question Waiting for Approve! ");
                                                                Intent cart = new Intent(getApplicationContext(), HomeActivity.class);
                                                                cart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                cart.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(cart);
                                                                finish();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.d(TAG, "onFailure: " + e);
                                                            }
                                                        });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(Exception e) {
                                                Log.d(TAG, "onFailure: " + e);
                                            }
                                        });
                            }
                        }
                    });

        }
    }

    private void uploadQuesImage() {

        String title = quesTitle.getText().toString();
        String desc = quesDesc.getText().toString();

        if (title.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Add Question !", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinner.getSelectedItem().toString().matches("Please Select a Portal")) {
            Toast.makeText(getApplicationContext(), "Select Portal !", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        try {
            if (uploadBtn.getText().toString().matches("Update Question")) {
                Log.d(TAG, "uploadQuesImage: " + getIntent().getStringExtra("quesImgPath"));
                File file = new File(postUri.getPath());
                StorageReference storageReference = storage.getReference();
                StorageReference fileRef = storageReference.child("images/" + getIntent().getStringExtra("quesImgPath"));
                InputStream stream = new FileInputStream(file);
                UploadTask uploadTask = fileRef.putStream(stream);
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Task<Uri> taskUri = fileRef.getDownloadUrl();
                        taskUri.addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task1) {
                                if (task1.isSuccessful()) {
                                    String uri = task1.getResult().toString();
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("title", title);
                                    map.put("desc", desc);
                                    map.put("image", uri);
                                    mFirestore.collection("questions").document(Qid).update(map)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    try {
                                                        Log.d(TAG, "onSuccess: updated success");
                                                        Utility.toast(Add_Queastion.this, "Question Updated for Approve! ");
                                                        Intent cart = new Intent(Add_Queastion.this, HomeActivity.class);
                                                        cart.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        cart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(cart);
                                                        finish();
                                                    } catch (Exception e) {
                                                        Log.d(TAG, "onSuccess: " + e);
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "onFailure: " + e);
                                                    progressDialog.dismiss();
                                                }
                                            });
                                }
                            }
                        });
                    }
                });
            } else {
                File file = new File(postUri.getPath());
                StorageReference storageRef = storage.getReference();
                String imagePath = UUID.randomUUID().toString();
                StorageReference imageRef = storageRef.child("images/" + imagePath);
                InputStream inputStream = new FileInputStream(file);
                UploadTask uploadTask = imageRef.putStream(inputStream);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> imageTsk = imageRef.getDownloadUrl();
                        imageTsk.addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> downloadImgTask) {
                                if (downloadImgTask.isSuccessful()) {
                                    mFirestore.collection("ids").document("questionId").get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        long updateId = task.getResult().getLong("id") + 1;
                                                        HashMap<String, Object> questionMap = new HashMap<>();
                                                        questionMap.put("title", title);
                                                        questionMap.put("desc", desc);
                                                        questionMap.put("likes", "0");
                                                        questionMap.put("qpic", "na");
                                                        questionMap.put("checkFav", false);
                                                        questionMap.put("checkLike", false);
                                                        questionMap.put("tanswers", "false");
                                                        questionMap.put("uname", sharedPrefManager.getsUser().getName());
                                                        questionMap.put("userId", sharedPrefManager.getsUser().getUid());
                                                        questionMap.put("id", updateId + "");
                                                        questionMap.put("qualityCheck", false);
                                                        questionMap.put("timestamp", System.currentTimeMillis());
                                                        questionMap.put("likedByUser", "");
                                                        questionMap.put("image", downloadImgTask.getResult().toString());
                                                        questionMap.put("userPicUrl", sharedPrefManager.getProfilePic());
                                                        questionMap.put("imageRef", imagePath);
                                                        questionMap.put("portal", spinner.getSelectedItem().toString());
                                                        mFirestore.collection("questions").document(updateId + "").set(questionMap)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        HashMap<String, Object> map = new HashMap<>();
                                                                        map.put("id", updateId);
                                                                        mFirestore.collection("ids").document("questionId").update(map)
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {
                                                                                        Log.d(TAG, "onSuccess: success");
                                                                                        progressDialog.dismiss();
                                                                                        Toast.makeText(getApplicationContext(), "Question Waiting for Approve! ", Toast.LENGTH_SHORT).show();
                                                                                        Intent cart = new Intent(getApplicationContext(), HomeActivity.class);
                                                                                        cart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                                        cart.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                        startActivity(cart);
                                                                                        finish();
                                                                                    }
                                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        Log.d(TAG, "onFailure: " + e);
                                                                                    }
                                                                                });
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(Exception e) {
                                                                        Log.d(TAG, "onFailure: " + e);
                                                                    }
                                                                });
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e);
                        progressDialog.dismiss();
                        Toast.makeText(Add_Queastion.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                    }
                });
            }
        } catch (Exception e) {
            Log.d(TAG, "uploadQuesImage: " + e);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postUri = result.getUri();
                selectQues.setImageURI(postUri);
                pCheck = "Profile";

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaisebhi.kaisebhi.HomeActivity;
import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.Utility.ApplicationCustom;
import com.kaisebhi.kaisebhi.Utility.DefaultResponse;
import com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;
import com.kaisebhi.kaisebhi.Utility.Utility;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Add_Queastion extends AppCompatActivity {

    private Button addDelivery;
    private EditText quesTitle, quesDesc;
    private Uri postUri = null;
    private SharedPrefManager sharedPrefManager;
    private FirebaseFirestore mFirestore;
    String pCheck = null, TAG = "Add_Question.java";
    ProgressDialog progressDialog;
    ImageView selectQues;
    String Qid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_ques);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        sharedPrefManager = new SharedPrefManager(Add_Queastion.this);
        mFirestore = ((ApplicationCustom) getApplication()).mFirestore;
        progressDialog = new ProgressDialog(Add_Queastion.this);
        progressDialog.setCancelable(false);

        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Question details processing....");

        selectQues = findViewById(R.id.quesImage);

        quesTitle = findViewById(R.id.q_title);
        quesDesc = findViewById(R.id.a_desc);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            Qid = extras.getString("key");
            quesTitle.setText(extras.getString("title"));
            quesDesc.setText(extras.getString("desc"));

            if (extras.getString("qimg").length() > 0) {
                Glide.with(getApplicationContext()).load(BASE_URL + "qimg/" + extras.getString("qimg")).fitCenter().into(selectQues);
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


        findViewById(R.id.uploadQues).setOnClickListener(new View.OnClickListener() {
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
            return;
        }
        progressDialog.show();

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
                            questionMap.put("timestamp", System.currentTimeMillis());
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


//        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().addQuestion(title, desc, "", SharedPrefManager.getInstance(getApplicationContext()).getsUser().getUid());
//        call.enqueue(new Callback<DefaultResponse>() {
//            @Override
//            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
//                DefaultResponse dr = response.body();
//                if (response.code() == 201) {
//                    progressDialog.dismiss();
//                    Toast.makeText(getApplicationContext(), "Question Waiting for Approve! ", Toast.LENGTH_SHORT).show();
//                    Intent cart = new Intent(getApplicationContext(), HomeActivity.class);
//                    cart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    cart.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(cart);
//                    finish();
//
//                } else if (response.code() == 422) {
//                    progressDialog.dismiss();
//                    Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<DefaultResponse> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
//
//            }
//        });
    }

    private void uploadQuesImage() {

        String title = quesTitle.getText().toString();
        String desc = quesDesc.getText().toString();

        if (title.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Add Question !", Toast.LENGTH_SHORT).show();
            return;
        }


        progressDialog.show();

        File file = new File(postUri.getPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().addImgQuestion(fileToUpload, filename, title, desc, "", SharedPrefManager.getInstance(getApplicationContext()).getsUser().getUid());
        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                DefaultResponse dr = response.body();
                if (response.code() == 201) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Question Waiting for Approve! ", Toast.LENGTH_SHORT).show();
                    Intent cart = new Intent(getApplicationContext(), HomeActivity.class);
                    cart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    cart.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(cart);
                    finish();

                } else if (response.code() == 422) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                }
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
                selectQues.setImageURI(postUri);
                pCheck = "Profile";

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}

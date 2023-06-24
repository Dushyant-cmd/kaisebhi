package com.kaisebhi.kaisebhi.HomeNavigation.AddQuestion;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.kaisebhi.kaisebhi.HomeActivity;
import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.Utility.DefaultResponse;
import com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient.BASE_URL;

public class Add_Queastion extends AppCompatActivity {

    private Button addDelivery;
    private EditText quesTitle,quesDesc;
    private Uri postUri = null;
    String pCheck = null;
    ProgressDialog progressDialog;
    ImageView selectQues;
    String Qid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_ques);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);

        progressDialog = new ProgressDialog(Add_Queastion.this);
        progressDialog.setCancelable(false);

        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Question details proccesing....");

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

                if(pCheck == null){
                    findViewById(R.id.uploadQues).setClickable(false);
                    uploadQues();
                }
                else {
                    findViewById(R.id.uploadQues).setClickable(false);
                    uploadQuesImage();
                }
            }
        });


    }


    private void uploadQues() {
        String title = quesTitle.getText().toString();
        String desc = quesDesc.getText().toString();

        if(title.isEmpty()) {  Toast.makeText(getApplicationContext(),"Add Question !",Toast.LENGTH_SHORT).show(); return; }
        progressDialog.show();

        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().addQuestion(title,desc,"", SharedPrefManager.getInstance(getApplicationContext()).getsUser().getUid());
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
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        });


    }
    private void uploadQuesImage() {

        String title = quesTitle.getText().toString();
        String desc = quesDesc.getText().toString();

        if(title.isEmpty()) {  Toast.makeText(getApplicationContext(),"Add Question !",Toast.LENGTH_SHORT).show(); return; }


        progressDialog.show();

        File file = new File(postUri.getPath());
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().addImgQuestion(fileToUpload,filename,title,desc,"",SharedPrefManager.getInstance(getApplicationContext()).getsUser().getUid());
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

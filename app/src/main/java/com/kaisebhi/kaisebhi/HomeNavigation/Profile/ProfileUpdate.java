package com.kaisebhi.kaisebhi.HomeNavigation.Profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.kaisebhi.kaisebhi.HomeActivity;
import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.Utility.DefaultResponse;
import com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;

import java.io.File;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient.BASE_URL;

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

    TextView btnText;
    ProgressBar btnProgress;
    TextView passText;
    int Check = 0;
    ProgressBar passProgress;
    FrameLayout updateProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        sharedPrefManager = new SharedPrefManager(this);

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
        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().getFullProfile(ID);
        call.enqueue(new Callback<DefaultResponse>() {

            @SuppressLint("ResourceType")
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                DefaultResponse dr = response.body();

                if (response.code() == 201) {

                    String data = dr.getMessage();
                    String[] dif = data.split("#");
                    Glide.with(getApplicationContext()).load(BASE_URL + "user/" + dif[0]).dontAnimate().centerCrop().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).placeholder(R.drawable.profile).into(ProfileImage);
                    edName.setText(dif[1]);

                    if(!dif[2].contains("empty"))
                    edEmail.setText(dif[2]);

                    if(!dif[3].contains("empty"))
                    edAdd.setText(dif[3]);

                    if(!dif[4].contains("empty"))
                    edMobile.setText(dif[4]);
                }
                updateProgress.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getLocalizedMessage(),Toast.LENGTH_SHORT).show();

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

        if (mob.isEmpty()) {
            edMobile.setError("Add Mobile");
            return;
        }

        Random rand = new Random();
        final String otp = String.format("%04d", rand.nextInt(10000));

            if (name.isEmpty()) {
                edName.setError("Add Name");
                return;
            }

            if (email.isEmpty()) {
                edEmail.setError("Add Email");
                return;
            }

            if (address.isEmpty()) {
                edAdd.setError("Add Address");
                return;
            }

            if (pCheck != null) {

                btnProgress.setVisibility(View.VISIBLE);
                btnText.setVisibility(View.INVISIBLE);

                File file = new File(postUri.getPath());
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
                RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

                Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().uploadProfile(fileToUpload, filename, name, mob,email, address, ID);
                call.enqueue(new Callback<DefaultResponse>() {
                    @Override
                    public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                        DefaultResponse dr = response.body();

                        if (response.code() == 201) {
                            String data = dr.getMessage();
                            Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();
//                            sharedPrefManager.saveUser(edName.getText().toString(), sharedPrefManager.getsUser().getMobile(), sharedPrefManager.getsUser().getUid(),"");
                        } else {

                        }
                        Check = 1;

                        btnText.setVisibility(View.VISIBLE);
                        btnProgress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailure(Call<DefaultResponse> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();

                    }

                });
            } else {

                btnText.setVisibility(View.INVISIBLE);
                btnProgress.setVisibility(View.VISIBLE);

                Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().updatePro(name, mob, email, address, ID);
                call.enqueue(new Callback<DefaultResponse>() {
                    @Override
                    public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                        DefaultResponse dr = response.body();

                        if (response.code() == 201) {
                            String data = dr.getMessage();
                            Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();
//                            sharedPrefManager.saveUser(edName.getText().toString(), sharedPrefManager.getsUser().getMobile(), sharedPrefManager.getsUser().getUid(),"");

                        } else {
                            Toast.makeText(getApplicationContext(), "Some error occured!", Toast.LENGTH_SHORT).show();
                        }

                        btnText.setVisibility(View.VISIBLE);
                        btnProgress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailure(Call<DefaultResponse> call, Throwable t) {
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
        if(Check==1)
        {
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            finish();
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if(Check==1)
        {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
        else
        {
            super.onBackPressed();
        }
    }

    public void uploadProfile(View view) {
        profile();
    }
}


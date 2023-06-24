package com.kaisebhi.kaisebhi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kaisebhi.kaisebhi.Utility.DefaultResponse;
import com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {


    RelativeLayout signProgress;
    int RC_SIGN_IN = 0;
    RelativeLayout relativeLayout;
    String Token;
    public GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        getDelegate().setLocalNightMode(
                AppCompatDelegate.MODE_NIGHT_NO);
        relativeLayout = findViewById(R.id.emailBox);
        signProgress = findViewById(R.id.pgore);

        Token = FirebaseInstanceId.getInstance().getToken();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
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
                    pass.setError("Add 6 charecters Password!");
                } else {
                    loginEmail(em.getText().toString(), pass.getText().toString());
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
        signProgress.setVisibility(View.VISIBLE);
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
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            SignUp(account.getDisplayName(), account.getEmail());

        } catch (ApiException e) {
            Log.w("Errorsign", "signInResult:failed code=" + e.getStatusCode());
        }
    }


    void SignUp(final String name, final String email) {
        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().createUserEmail(name, email, Token);

        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                DefaultResponse dr = response.body();
                if (response.code() == 201) {
                    String data = dr.getMessage();
                    String[] dif = data.split("#");

                    signProgress.setVisibility(View.VISIBLE);
                    SharedPrefManager.getInstance(getApplicationContext()).saveUser(name, dif[0], dif[1], dif[2]);
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_LONG).show();
            }
        });

    }


    void loginEmail(final String email, final String pass) {

        signProgress.setVisibility(View.VISIBLE);

        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().loginEmail(email, pass, Token);

        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                DefaultResponse dr = response.body();
                if (response.code() == 201) {
                    String data = dr.getMessage();
                    String[] dif = data.split("#");
                    signProgress.setVisibility(View.GONE);
                    SharedPrefManager.getInstance(getApplicationContext()).saveUser(dif[0], dif[1], dif[2], dif[3]);
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    signProgress.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Invalid Credentials. Please try Again!", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                signProgress.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_LONG).show();
            }
        });

    }


}

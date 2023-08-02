package com.kaisebhi.kaisebhi.Utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.kaisebhi.kaisebhi.ActivityForFrag;
import com.kaisebhi.kaisebhi.HomeActivity;
import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity implements PaymentResultWithDataListener {

    TextView check;
    ImageView gif;
    ProgressDialog progressDialog;
    Button MyOrders;
    SharedPrefManager sharedPrefManager;
    Bundle b;
    String qid = null;
    String Amount = null;

    String orderId = null;
    String userid = "";
    private String TAG = "PaymentActivity.java", answerDocId;
    private Double amount = 0.00;
    private boolean isSelfAns = false;
    private FirebaseFirestore mFirestore;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_succesfull);

//        TextView header = findViewById(R.id.textHeader);

        sharedPrefManager = new SharedPrefManager(getApplication());
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        mFirestore = ((ApplicationCustom) getApplication()).mFirestore;

        Random rand = new Random();
        final String otp = String.format("%04d", rand.nextInt(10000));
        orderId = "ORDX" + otp + sharedPrefManager.getsUser().getUid();

        progressDialog.setTitle("Order Progressing!");
        progressDialog.setMessage("Keep Calm! Status is Updating.... ");

        check = findViewById(R.id.text);
        MyOrders = findViewById(R.id.OrderButton);
        MyOrders.setVisibility(View.GONE);


        MyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MyOrders.getText().toString().contains("My Orders")) {
                    Intent id = new Intent(getApplicationContext(), HomeActivity.class);
                    id.putExtra("Frag", "myorder");
                    startActivity(id);
                    finish();
                } else {
                    onBackPressed();
                }
            }
        });

        gif = findViewById(R.id.statusGif);

        if (savedInstanceState == null) {
            b = getIntent().getExtras();
            if (b == null) {
            } else {
                amount = Double.parseDouble(b.getString("oamount"));
                userid = b.getString("userId");
                qid = b.getString("qid");
                answerDocId = b.getString("ansId");
                isSelfAns = b.getBoolean("isSelfAns");
                startPayment(amount);
            }
        }
        Log.d(TAG, "onCreate: " + answerDocId);

    }

    public void startPayment(double am) {

        final Activity activity = this;
        Checkout checkout = new Checkout();
        checkout.setKeyID(getResources().getString(R.string.razorpay_key_id));
        checkout.setImage(R.drawable.logo);

        try {

            JSONObject options = new JSONObject();

            options.put("name", "Kaisebhi");
            options.put("description", "Reference No. " + orderId);
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", (am * 100));
            options.put("theme", new JSONObject("{color: '#1278dd'}"));

            Log.d(TAG, "startPayment: payment json: " + options);
            JSONObject preFill = new JSONObject();
            options.put("prefill", preFill);

            checkout.open(activity, options);

        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    public void backtoActivity(View view) {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        finish();
    }

    @SuppressLint("ResourceType")
    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {

        check.setText("Your Payment has Successful! \n Connect you Soon!");
        gif.setImageResource(R.drawable.check);
        final ProgressDialog dialog = new ProgressDialog(PaymentActivity.this);
        dialog.setMessage("Please Wait...");
        dialog.show();
        dialog.setCancelable(false);

        Map<String, Object> map = new HashMap<>();
        if (b.getString("payType").equals("show")) {
            map.clear();
            map.put("ansDocId", answerDocId);
            map.put("userId", sharedPrefManager.getsUser().getUid());
            map.put("quesId", qid);
            map.put("hideAmount", amount);
            mFirestore.collection("paidAnswers").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Intent seac = new Intent(PaymentActivity.this, ActivityForFrag.class);
                    seac.putExtra("Frag","showAns");
                    seac.putExtra("tabType","show");
                    startActivity(seac);
                    dialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e);
                    dialog.dismiss();
                }
            });
//            Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().showAns(qid,b.getString("oamount"),SharedPrefManager.getInstance(getApplication()).getsUser().getUid());
//            call.enqueue(new Callback<DefaultResponse>() {
//                @Override
//                public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
//                    Intent seac = new Intent(PaymentActivity.this, ActivityForFrag.class);
//                    seac.putExtra("Frag","showAns");
//                    seac.putExtra("tabType","show");
//                    startActivity(seac);
//                    dialog.dismiss();
//                }
//
//                @Override
//                public void onFailure(Call<DefaultResponse> call, Throwable t) {
//                    dialog.dismiss();
//                }
//            });
        } else {
            map.clear();
            map.put("checkHideAnswer", true);
            map.put("checkOwnQuestion", true);
            map.put("paidAmount", amount.toString());
            if (isSelfAns) {
                map.put("selfHideAnswer", true);
                mFirestore.collection("answers").document(answerDocId).update(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Answer Hide Successfully!", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                    onBackPressed();
                                } else {
                                    dialog.dismiss();
                                    onBackPressed();
                                }
                            }
                        });
            } else {
                mFirestore.collection("answers").document(answerDocId).update(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Answer Hide Successfully!", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                    onBackPressed();
                                } else {
                                    dialog.dismiss();
                                    onBackPressed();
                                }
                            }
                        });
            }

//            onBackPressed();

//            Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().hideAns(qid,b.getString("oamount"),SharedPrefManager.getInstance(getApplication()).getsUser().getUid());
//            call.enqueue(new Callback<DefaultResponse>() {
//                @Override
//                public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
//                    Toast.makeText(getApplicationContext(),"Answer Hide Successfully!",Toast.LENGTH_LONG).show();
//                    dialog.dismiss();
//                }
//
//                @Override
//                public void onFailure(Call<DefaultResponse> call, Throwable t) {
//                    dialog.dismiss();
//                }
//            });
        }


    }

    @SuppressLint("ResourceType")
    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {

        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
        check.setText("Your Payment has Unsuccessful! \n");
        gif.setImageResource(R.drawable.cross);
        MyOrders.setText("Try Again!");

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }
}

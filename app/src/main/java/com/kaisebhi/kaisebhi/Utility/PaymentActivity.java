package com.kaisebhi.kaisebhi.Utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.kaisebhi.kaisebhi.ActivityForFrag;
import com.kaisebhi.kaisebhi.HomeActivity;
import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient;

import org.json.JSONObject;

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

    String orderId=null;
    String userid = "";
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_succesfull);

        TextView header = findViewById(R.id.textHeader);

        sharedPrefManager = new SharedPrefManager(getApplication());
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        Random rand = new Random();
        final String otp = String.format("%04d", rand.nextInt(10000));
        orderId = "ORDX"+otp+sharedPrefManager.getsUser().getUid();

        progressDialog.setTitle("Order Progressing!");
        progressDialog.setMessage("Keep Calm! Status is Updating.... ");

        check = findViewById(R.id.text);
        MyOrders = findViewById(R.id.OrderButton);
        MyOrders.setVisibility(View.GONE);


        MyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(MyOrders.getText().toString().contains("My Orders"))
                {
                    Intent id = new Intent(getApplicationContext(), HomeActivity.class);
                    id.putExtra("Frag", "myorder");
                    startActivity(id);
                    finish();
                }
                else
                {
                    onBackPressed();
                }
            }
        });

        gif = findViewById(R.id.statusGif);

        if (savedInstanceState == null) {
            b = getIntent().getExtras();
            if (b == null) {
            } else {
                Double amount = Double.parseDouble(b.getString("oamount"));
                userid = b.getString("userId");
                qid = b.getString("qid");
                startPayment(amount);
            }
        }

    }

    public void startPayment(double am) {

        final Activity activity = this;
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_live_a2XOrcelzZlkvx");
        checkout.setImage(R.drawable.logo);

        try {

            JSONObject options = new JSONObject();

            options.put("name", "Kaisebhi");
            options.put("description", "Reference No. "+orderId);
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount",am*100);
            options.put("theme",new JSONObject("{color: '#1278dd'}"));

            JSONObject preFill = new JSONObject();
            options.put("prefill", preFill);

            checkout.open(activity, options);

        } catch(Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }



    public void backtoActivity(View view) {
        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
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

        if(b.getString("payType").equals("show")){
            Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().showAns(qid,b.getString("oamount"),SharedPrefManager.getInstance(getApplication()).getsUser().getUid());
            call.enqueue(new Callback<DefaultResponse>() {
                @Override
                public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                    Intent seac = new Intent(PaymentActivity.this, ActivityForFrag.class);
                    seac.putExtra("Frag","showAns");
                    seac.putExtra("tabType","show");
                    startActivity(seac);
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Call<DefaultResponse> call, Throwable t) {
                    dialog.dismiss();
                }
            });
        }
        else{
            Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().hideAns(qid,b.getString("oamount"),SharedPrefManager.getInstance(getApplication()).getsUser().getUid());
            call.enqueue(new Callback<DefaultResponse>() {
                @Override
                public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                    Toast.makeText(getApplicationContext(),"Answer Hide Successfully!",Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Call<DefaultResponse> call, Throwable t) {
                    dialog.dismiss();
                }
            });
        }


    }

    @SuppressLint("ResourceType")
    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {

            Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
            check.setText("Your Payment has Unsuccessful! \n");
            gif.setImageResource(R.drawable.cross);
            MyOrders.setText("Try Again!");

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        finish();
    }
}

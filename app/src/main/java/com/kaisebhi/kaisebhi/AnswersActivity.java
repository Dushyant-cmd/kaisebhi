package com.kaisebhi.kaisebhi;

import static com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient.BASE_URL;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.annotations.SerializedName;
import com.kaisebhi.kaisebhi.HomeNavigation.home.AnswersAdapter;
import com.kaisebhi.kaisebhi.HomeNavigation.home.AnswersModel;
import com.kaisebhi.kaisebhi.Utility.ApplicationCustom;
import com.kaisebhi.kaisebhi.Utility.DefaultResponse;
import com.kaisebhi.kaisebhi.Utility.Main_Interface;
import com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnswersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<AnswersModel> answers;
    private AnswersAdapter adapter;
    private FirebaseFirestore mFirestore;
    SharedPrefManager sh;

    EditText msg;
    ImageView sendBtn;
    CircleImageView userImage;
    private ShimmerFrameLayout shimmerFrameLayout;
    private SharedPrefManager sharedPrefManager;

    ImageView pro, questionimg, shareBtn, ansImg;
    TextView Title, Desc, totalAns, userHead;
    CheckBox favBtn, likeBtn;
    ProgressBar loadAns;
    private String id, title, userName, userPic, desc, qImg, tAns, tLikes;


    String Qid;
    private String TAG = "AnswersActivity.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        getDelegate().setLocalNightMode(
                AppCompatDelegate.MODE_NIGHT_NO);
        shimmerFrameLayout = findViewById(R.id.SearchloadingShimmer);
        answers = new ArrayList<>();

        pro = findViewById(R.id.userPro);
        questionimg = findViewById(R.id.quesImage);
        userImage = findViewById(R.id.addMsgImage);
        loadAns = findViewById(R.id.loadAns);
        shareBtn = findViewById(R.id.shareQuestion);
        ansImg = findViewById(R.id.answers);
        likeBtn = findViewById(R.id.like);
        favBtn = findViewById(R.id.addFav);
        userHead = findViewById(R.id.userHeader);
        Title = findViewById(R.id.quesTitle);
        Desc = findViewById(R.id.quesDesc);
        totalAns = findViewById(R.id.totalAns);
        mFirestore = ((ApplicationCustom) getApplication()).mFirestore;
        sharedPrefManager = SharedPrefManager.getInstance(AnswersActivity.this);

        msg = findViewById(R.id.msg);
        sendBtn = findViewById(R.id.sendMsg);

        sh = new SharedPrefManager(getApplication());
        Glide.with(getApplicationContext()).load(BASE_URL + "user/" + sh.getsUser().getProfile()).dontAnimate().centerInside().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).placeholder(R.drawable.profile).into(userImage);

        recyclerView = findViewById(R.id.allanswers);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            Qid = extras.getString("key");
            Title.setText(extras.getString("title"));
            Desc.setText(extras.getString("desc"));
            userHead.setText(extras.getString("user"));
            totalAns.setText(extras.getString("tans"));
            likeBtn.setChecked(extras.getBoolean("tlikes"));


            if (extras.getString("qimg").length() > 0) {
                questionimg.setVisibility(View.VISIBLE);
                Glide.with(getApplicationContext()).load(BASE_URL + "qimg/" + extras.getString("qimg")).fitCenter().into(questionimg);
            }
            Glide.with(getApplicationContext()).load(BASE_URL + "user/" + extras.getString("userpic")).placeholder(R.drawable.profile).fitCenter().into(pro);


            fetchAnsers();
        }


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = msg.getText().toString();

                if (!text.isEmpty()) {

                    String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";

                    Pattern p = Pattern.compile(URL_REGEX);
                    Matcher m = p.matcher(text);//replace with string to compare
                    if (m.find()) {
                        Toast.makeText(getApplicationContext(), "Url or any HyperLink is not allowed!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Pattern mo = Pattern.compile("(0/91)?[7-9][0-9]{9}");
                    Matcher mm = mo.matcher(text);
                    if (mm.find()) {
                        Toast.makeText(getApplicationContext(), "Any Number Sequence / Phone number not allowed!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    msg.setText("");

                    sendBtn.setEnabled(false);

                    sendBtn.setVisibility(View.GONE);
                    loadAns.setVisibility(View.VISIBLE);

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("id", getIntent().getStringExtra("key"));
                    map.put("checkOwnQuestion", false);
                    map.put("uname", sharedPrefManager.getsUser().getName());
                    map.put("upro", sharedPrefManager.getsUser().getProfile());
                    map.put("likes", getIntent().getStringExtra("likes"));
                    map.put("qdesc", getIntent().getStringExtra("desc"));
                    map.put("qimg", getIntent().getStringExtra("qimg"));
                    map.put("likeCheck", getIntent().getBooleanExtra("tlikes", false));
                    map.put("answer", text);
                    map.put("checkHideAnswer", false);
                    map.put("paidCheck", false);
                    map.put("paidAmount", "0");
                    map.put("selfAnswer", false);
                    map.put("selfHideAnswer", false);
                    map.put("userReportCheck", false);
                    mFirestore.collection("answers").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "onSuccess: " + documentReference.toString());
                            Toast.makeText(getApplicationContext(), "Answer Added!", Toast.LENGTH_LONG).show();
                            sendBtn.setEnabled(true);
                            fetchAnsers();
                            loadAns.setVisibility(View.GONE);
                            sendBtn.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onSuccess: " + e);
                            Toast.makeText(getApplicationContext(), "There is and error posting answer! ", Toast.LENGTH_SHORT).show();
                            loadAns.setVisibility(View.GONE);
                            sendBtn.setEnabled(true);
                            sendBtn.setVisibility(View.VISIBLE);
                        }
                    });

//                    Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().addAns(text, Qid, sh.getsUser().getUid());
//                    call.enqueue(new Callback<DefaultResponse>() {
//                        @Override
//                        public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
//                            Toast.makeText(getApplicationContext(), "Answer Added!", Toast.LENGTH_LONG).show();
//                            sendBtn.setEnabled(true);
//                            fetchAnsers();
//                            loadAns.setVisibility(View.GONE);
//                            sendBtn.setVisibility(View.VISIBLE);
//
//                        }
//
//                        @Override
//                        public void onFailure(Call<DefaultResponse> call, Throwable t) {
//
//                            Toast.makeText(getApplicationContext(), "There is and error posting answer! ", Toast.LENGTH_SHORT).show();
//                            loadAns.setVisibility(View.GONE);
//                            sendBtn.setEnabled(true);
//                            sendBtn.setVisibility(View.VISIBLE);
//                        }
//                    });
                } else {
                    msg.setError("Type Answer First!");
                }

            }
        });


        SharedPrefManager sh = new SharedPrefManager(this);
        TextView toolbar = findViewById(R.id.textHeader);

    }


    public void fetchAnsers() {
        answers.clear();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmerAnimation();
        mFirestore.collection("answers").whereEqualTo("id", getIntent().getStringExtra("key"))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            try {
                                for(DocumentSnapshot d: task.getResult().getDocuments()) {
                                    answers.add(new AnswersModel(
                                            d.getString("id"), d.getBoolean("checkOwnQuestion"),
                                            d.getString("uname"), d.getString("upro"), d.getString("likes"),
                                            d.getString("qdesc"), d.getString("qimg"), d.getBoolean("likeCheck"),
                                            d.getString("answer"), d.getBoolean("checkHideAnswer"), d.getBoolean("paidCheck"),
                                            d.getString("paidAmount"), d.getBoolean("selfAnswer"), d.getBoolean("selfHideAnswer"),
                                            d.getBoolean("userReportCheck")
                                    ));
                                }
                                adapter = new AnswersAdapter(answers, getApplicationContext());
                                recyclerView.setAdapter(adapter);
                                shimmerFrameLayout.stopShimmerAnimation();
                                shimmerFrameLayout.setVisibility(View.GONE);
                            } catch (Exception e) {
                                Log.d(TAG, "onComplete: catch " + e);
                            }
                        } else {
                            Log.d(TAG, "onComplete: " + task.getException());
                        }
                    }
                });
//        Main_Interface main_interface = RetrofitClient.getApiClient().create(Main_Interface.class);
//
//        Call<List<AnswersModel>> call = main_interface.getAnswers(Qid, SharedPrefManager.getInstance(getApplication()).getsUser().getUid());
//
//        call.enqueue(new Callback<List<AnswersModel>>() {
//            @Override
//            public void onResponse(Call<List<AnswersModel>> call, Response<List<AnswersModel>> response) {
//
//                if (response.code() != 404) {
//                    answers = response.body();
//                    adapter = new AnswersAdapter(answers, getApplicationContext());
//                    recyclerView.setAdapter(adapter);
//                }
//                shimmerFrameLayout.stopShimmerAnimation();
//                shimmerFrameLayout.setVisibility(View.GONE);
//
//            }
//
//            @Override
//            public void onFailure(Call<List<AnswersModel>> call, Throwable t) {
//
//            }
//        });

    }


    public void backtoActivity(View view) {
        super.onBackPressed();
    }
}

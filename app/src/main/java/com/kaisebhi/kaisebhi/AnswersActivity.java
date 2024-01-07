package com.kaisebhi.kaisebhi;

import android.net.Uri;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kaisebhi.kaisebhi.HomeNavigation.home.AnswersAdapter;
import com.kaisebhi.kaisebhi.HomeNavigation.home.AnswersModel;
import com.kaisebhi.kaisebhi.Utility.ApplicationCustom;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;
import com.kaisebhi.kaisebhi.Utility.Utility;
import com.kaisebhi.kaisebhi.databinding.ActivityAnswerBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

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
    private String id, title, userName, userPic, desc, qImg, tAns, tLikes, userId = "", portal;


    String Qid;
    private String TAG = "AnswersActivity.java";
    private ActivityAnswerBinding binding;
    private String audioUrl;
    private SimpleExoPlayer exoPlayer;
    private SimpleExoPlayerView simpleExoPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnswerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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
        simpleExoPlayerView = findViewById(R.id.exoPlayer);
        mFirestore = ((ApplicationCustom) getApplication()).mFirestore;
        sharedPrefManager = SharedPrefManager.getInstance(AnswersActivity.this);

        msg = findViewById(R.id.msg);
        sendBtn = findViewById(R.id.sendMsg);

        sh = new SharedPrefManager(AnswersActivity.this);
        Log.d(TAG, "onCreate profile pic: " + sh.getProfilePic());
        Glide.with(getApplicationContext()).load(sh.getProfilePic()).dontAnimate().centerInside()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).placeholder(R.drawable.profile).into(userImage);

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
            userId = extras.getString("userId");
            portal = extras.getString("portal");
            audioUrl = extras.getString("audio");

            if (!extras.getString("qimg").matches("na")) {
                questionimg.setVisibility(View.VISIBLE);
                Glide.with(getApplicationContext()).load(extras.getString("qimg")).fitCenter().into(questionimg);
            }

            Glide.with(getApplicationContext()).load(extras.getString("userpic")).placeholder(R.drawable.profile).fitCenter().into(pro);

            if (Utility.isNetworkAvailable(AnswersActivity.this)) {
                fetchAnsers();
            } else {
                Utility.noNetworkDialog(AnswersActivity.this);
            }
        }

        binding.ansSwipeRefLay.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.ansSwipeRefLay.setRefreshing(false);
                if (Utility.isNetworkAvailable(AnswersActivity.this)) {
                    fetchAnsers();
                } else {
                    Utility.noNetworkDialog(AnswersActivity.this);
                }
            }
        });

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
                    map.put("uname", sharedPrefManager.getsUser().getName());
                    map.put("upro", sh.getProfilePic());
                    map.put("likes", "0");
                    map.put("qdesc", getIntent().getStringExtra("desc"));
                    map.put("qimg", getIntent().getStringExtra("qimg"));
                    map.put("likeCheck", getIntent().getBooleanExtra("tlikes", false));
                    map.put("answer", text);
                    map.put("checkHideAnswer", false);
                    map.put("paidCheck", false);
                    map.put("timestamp", System.currentTimeMillis());
                    map.put("paidAmount", "0");
                    if (userId.matches(sh.getsUser().getUid())) {
                        map.put("selfAnswer", true);
                        map.put("checkOwnQuestion", true);
                    } else {
                        map.put("checkOwnQuestion", false);
                        map.put("selfAnswer", false);
                    }
                    map.put("selfHideAnswer", false);
                    map.put("userReportCheck", false);
                    map.put("userId", sharedPrefManager.getsUser().getUid());
                    map.put("title", extras.getString("title"));
                    map.put("reportBy", "");
                    map.put("likedBy", "");
                    map.put("audio", audioUrl);
                    map.put("portal", portal);
                    Log.d(TAG, "onClick answer post map: " + map);
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
                } else {
                    msg.setError("Type Answer First!");
                }

            }
        });

        if (!audioUrl.isEmpty())
            setupAudio();
    }

    private void setupAudio() {
        simpleExoPlayerView.setVisibility(View.VISIBLE);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
        DefaultHttpDataSourceFactory dataSource = new DefaultHttpDataSourceFactory("exoPlayer_agent");
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(audioUrl), dataSource, extractorsFactory, null, null);
        exoPlayer = ExoPlayerFactory.newSimpleInstance(AnswersActivity.this, trackSelector);
        simpleExoPlayerView.setPlayer(exoPlayer);
        exoPlayer.prepare(mediaSource);
    }

    public void fetchAnsers() {
        Log.d(TAG, "fetchAnsers: " + Qid);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmerAnimation();
        mFirestore.collection("answers")
                .whereEqualTo("id", Qid)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            try {
                                answers.clear();
                                List<DocumentSnapshot> list = task.getResult().getDocuments();
                                for (int i = 0; i < list.size(); i++) {
                                    DocumentSnapshot d = list.get(i);
                                    AnswersModel ans = new AnswersModel(
                                            d.getString("id"), d.getBoolean("checkOwnQuestion"),
                                            d.getString("uname"), d.getString("upro"), d.getString("likes"),
                                            d.getString("qdesc"), d.getString("qimg"), d.getBoolean("likeCheck"),
                                            d.getString("answer"), d.getBoolean("checkHideAnswer"), d.getBoolean("paidCheck"),
                                            d.getString("paidAmount"), d.getBoolean("selfAnswer"), d.getBoolean("selfHideAnswer"),
                                            d.getBoolean("userReportCheck"), d.getString("title"), d.getId(), d.getString("reportBy"),
                                            d.getString("likedBy"), d.getString("userId")
                                    );

                                    ans.setAudioUrl(d.getString("audio"));
                                    ans.setPortal(d.getString("portal"));
                                    answers.add(ans);
                                }
                                Log.d(TAG, "onComplete: answer list: " + answers.size());

                                for (int i = 0; i < answers.size(); i++) {
                                    AnswersModel ans = answers.get(i);
                                    int j = i;
                                    mFirestore.collection("paidAnswers").whereEqualTo("ansDocId", ans.getAnswerDocId())
                                            .whereEqualTo("userId", sh.getsUser().getUid().toString()).get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful() && !task.getResult().getDocuments().isEmpty()) {
                                                        ans.setPaidAmount(task.getResult().getDocuments().get(0).getLong("hideAmount").toString());
                                                        ans.setCheckPaid(true);
                                                        if (userId.matches(sh.getsUser().getUid())) {
                                                            ans.setCheckOwnQuestion(true);
                                                            ans.setSelfAnswer(true);
                                                        } else {
                                                            ans.setCheckOwnQuestion(false);
                                                            ans.setSelfHideAnswer(false);
                                                            ans.setSelfAnswer(false);
                                                        }
                                                        Log.d(TAG, "onComplete: doc paid result: " + task.getResult().getDocuments().get(0));
                                                    } else {
                                                        if (userId.matches(sh.getsUser().getUid())) {
                                                            ans.setCheckOwnQuestion(true);
                                                            ans.setSelfAnswer(true);
                                                        } else {
                                                            ans.setCheckOwnQuestion(false);
                                                            ans.setSelfHideAnswer(false);
                                                            ans.setSelfAnswer(false);
                                                        }
                                                        Log.d(TAG, "onFailure: " + task.getException());
                                                    }

                                                    if (j == answers.size() - 1) {
                                                        adapter = new AnswersAdapter(answers, getApplicationContext());
                                                        adapter.qUserId = userId;
                                                        recyclerView.setAdapter(adapter);
                                                        shimmerFrameLayout.stopShimmerAnimation();
                                                        shimmerFrameLayout.setVisibility(View.GONE);
                                                    }
//                                                        adapter.notifyDataSetChanged();
                                                }
                                            });
                                }

                                if(answers.isEmpty()) {
                                    adapter = new AnswersAdapter(answers, getApplicationContext());
                                    adapter.qUserId = userId;
                                    recyclerView.setAdapter(adapter);
                                    shimmerFrameLayout.stopShimmerAnimation();
                                    shimmerFrameLayout.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                Log.d(TAG, "onComplete: catch " + e);
                            }
                        } else {
                            Log.d(TAG, "onComplete: " + task.getException());
                        }
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
        }
    }

    public void backtoActivity(View view) {
        super.onBackPressed();
    }
}

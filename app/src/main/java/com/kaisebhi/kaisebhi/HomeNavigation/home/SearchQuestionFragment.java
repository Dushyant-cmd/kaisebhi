package com.kaisebhi.kaisebhi.HomeNavigation.home;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.Utility.ApplicationCustom;
import com.kaisebhi.kaisebhi.Utility.Main_Interface;
import com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchQuestionFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<QuestionsModel> questions = new ArrayList<>();
    private QuestionsAdapter adapter;
    private Main_Interface main_interface;
    private FirebaseFirestore mFirestore;
    EditText SearchQues;
    FrameLayout framLa;
    private ShimmerFrameLayout shimmerFrameLayout;
    private String TAG = "SearchQuestionsFrag.java";
    private ApplicationCustom applicationCustom;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search_ques, container, false);
        shimmerFrameLayout = root.findViewById(R.id.SearchloadingShimmer);
        mFirestore = ((ApplicationCustom) getActivity().getApplication()).mFirestore;
        questions = new ArrayList<>();
        applicationCustom = ((ApplicationCustom) getActivity().getApplication());

        SearchQues = root.findViewById(R.id.searchQues);
        framLa = root.findViewById(R.id.framLa);

        SearchQues.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    fetchQuestions(SearchQues.getText().toString().toLowerCase());
                    return true;
                }
                return false;
            }
        });

        recyclerView = root.findViewById(R.id.allquestions);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        return root;
    }

    public void fetchQuestions(String search) {
        framLa.setVisibility(View.GONE);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmerAnimation();

        main_interface = RetrofitClient.getApiClient().create(Main_Interface.class);

        mFirestore.collection("questions").get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            questions.clear();
                            for (DocumentSnapshot d : task.getResult().getDocuments()) {
                                if(d.getString("title").toLowerCase().contains(search)) {
                                    questions.add(new QuestionsModel(
                                            d.getString("id"), d.getString("title"), d.getString("desc"),
                                            d.getString("qpic"), d.getString("uname"), d.getString("upro"),
                                            d.getBoolean("checkFav"), d.getString("likes"), d.getBoolean("checkLike"),
                                            d.getString("tanswers"), d.getString("likedByUser"), d.getString("image"),
                                            d.getString("userId"), d.getString("userPicUrl"), d.getString("imageRef"),
                                            d.getString("portal"), d.getString("audio"), d.getString("audioRef"),
                                            d.getString("qualityCheck")
                                    ));
                                }
                            }

//                            if(adapter != null) {
//                                adapter.exoPlayer.stop();
//                                adapter.exoPlayer.release();
//                            }
                            adapter = new QuestionsAdapter(questions, getActivity(), mFirestore, applicationCustom.roomDb, applicationCustom.storage, getActivity().getSupportFragmentManager());
                            recyclerView.setAdapter(adapter);
                            framLa.setVisibility(View.GONE);
                            shimmerFrameLayout.stopShimmerAnimation();
                            shimmerFrameLayout.setVisibility(View.GONE);
                        } else {
                            Log.d(TAG, "onComplete: " + task.getException().getMessage());
                        }
                    }
                }
        );
    }


}

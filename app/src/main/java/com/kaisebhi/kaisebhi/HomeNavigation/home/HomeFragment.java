package com.kaisebhi.kaisebhi.HomeNavigation.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.kaisebhi.kaisebhi.ActivityForFrag;
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

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private List<QuestionsModel> questions;
    private QuestionsAdapter adapter;
    private Main_Interface main_interface;
    private ShimmerFrameLayout shimmerFrameLayout;
    private String TAG = "HomeFragment.java";
    private FirebaseFirestore mFirestore;
    private long lastItemTimestamp;
    private DocumentSnapshot lastItem;
    private FirebaseStorage storage;
    private ApplicationCustom applicationCustom;
    int totalItems,currentItems,scrollOutItems=0;

    SwipeRefreshLayout refreshQuesitons;
    int currentPage = 1;
    ProgressBar loadMoreProgress;
    Boolean isScrolling = false;

    NestedScrollView nestRecy;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        refreshQuesitons = root.findViewById(R.id.refreshQuesitons);
        questions = new ArrayList<>();
        applicationCustom = ((ApplicationCustom) getActivity().getApplication());

        shimmerFrameLayout = root.findViewById(R.id.SearchloadingShimmer);
        loadMoreProgress = root.findViewById(R.id.loadMoreProgress);
        nestRecy = root.findViewById(R.id.nestRecy);
        mFirestore = ((ApplicationCustom) getActivity().getApplication()).mFirestore;
        storage = FirebaseStorage.getInstance();
        root.findViewById(R.id.searchQues).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent seac = new Intent(getContext(), ActivityForFrag.class);
                seac.putExtra("Frag","searchQ");
                getActivity().startActivity(seac);

            }
        });


        refreshQuesitons.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchQuestions();
            }
        });

        recyclerView = root.findViewById(R.id.allquestions);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        fetchQuestions();

        nestRecy.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if(scrollY == v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
                    currentPage+=1;
                    loadMoreProgress.setVisibility(View.VISIBLE);
                    fetchMore();
                }

            }
        });

        return root;
    }



    public void fetchQuestions() {
        questions.clear();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmerAnimation();
        recyclerView.setAdapter(null);
        main_interface = RetrofitClient.getApiClient().create(Main_Interface.class);

        mFirestore.collection("questions").whereEqualTo("qualityCheck", true).orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(2).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            try {
                                Log.d(TAG, "success: " + task.getResult());
                                List<DocumentSnapshot> list = task.getResult().getDocuments();
                                for (int i=0; i<list.size(); i++) {
                                    DocumentSnapshot d = list.get(i);
                                    questions.add(new QuestionsModel(
                                            d.getString("id"), d.getString("title"), d.getString("desc"),
                                            d.getString("qpic"), d.getString("uname"), d.getString("upro"),
                                            d.getBoolean("checkFav"), d.getString("likes"), d.getBoolean("checkLike"),
                                            d.getString("tanswers"), d.getString("likedByUser"), d.getString("image"),
                                            d.getString("userId"), d.getString("userPicUrl")
                                    ));
                                    if(i == list.size() - 1) {
                                        lastItemTimestamp = d.getLong("timestamp");
                                        lastItem = d;
                                    }
                                }

                                adapter = new QuestionsAdapter(questions,getActivity(), mFirestore, "home", ((ApplicationCustom) getActivity().getApplication()).roomDb, applicationCustom.storage);
                                recyclerView.setAdapter(adapter);
                                shimmerFrameLayout.stopShimmerAnimation();
                                shimmerFrameLayout.setVisibility(View.GONE);
                                refreshQuesitons.setRefreshing(false);
                            } catch (Exception e) {
                                Log.d(TAG, "onCatch: " + e);
                            }
                        } else {
                            Log.d(TAG, "onComplete: " + task.getException().getMessage());
                        }
                    }
                }
        );
    }


    public void fetchMore() {
        main_interface = RetrofitClient.getApiClient().create(Main_Interface.class);
        mFirestore.collection("questions").whereEqualTo("qualityCheck", true)
                .orderBy("timestamp", Query.Direction.DESCENDING).startAfter(lastItem).limit(2).get().addOnCompleteListener(    
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            try {
                                for (DocumentSnapshot d : task.getResult().getDocuments()) {
                                    questions.add(new QuestionsModel(
                                            d.getString("id"), d.getString("title"), d.getString("desc"),
                                            d.getString("qpic"), d.getString("uname"), d.getString("upro"),
                                            d.getBoolean("checkFav"), d.getString("likes"), d.getBoolean("checkLike"),
                                            d.getString("tanswers"), d.getString("likedByUser"), d.getString("image"),
                                            d.getString("userId"), d.getString("userPicUrl")
                                    ));
                                }
                                if(!task.getResult().getDocuments().isEmpty()) {
                                    lastItem = task.getResult().getDocuments().get(task.getResult().getDocuments().size() - 1);
                                    lastItemTimestamp = task.getResult().getDocuments().get(task.getResult().getDocuments().size() - 1).getLong("timestamp");
                                } else {
                                    loadMoreProgress.setVisibility(View.GONE);
                                    return;
                                }


                                adapter = new QuestionsAdapter(questions,getActivity(), mFirestore, "home", ((ApplicationCustom) getActivity().getApplication()).roomDb, applicationCustom.storage);
                                recyclerView.setAdapter(adapter);
                                loadMoreProgress.setVisibility(View.GONE);
                            } catch (Exception e) {
                                Log.d(TAG, "onComplete: catch: " + e);
                            }
                        } else {
                            Log.d(TAG, "onComplete: " + task.getException().getMessage());
                        }
                    }
                }
        );
    }



}

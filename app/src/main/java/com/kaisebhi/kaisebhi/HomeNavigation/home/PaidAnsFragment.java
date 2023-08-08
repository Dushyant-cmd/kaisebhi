package com.kaisebhi.kaisebhi.HomeNavigation.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class PaidAnsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<HideAnswersModel> answers = new ArrayList<>();
    private HideAnswersAdapter adapter;
    private Main_Interface main_interface;
    private ShimmerFrameLayout shimmerFrameLayout;
    private FirebaseFirestore mFirestore;
    private String TAG = "PaidAnsFragment.java";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_hide_ans, container, false);

        shimmerFrameLayout = root.findViewById(R.id.SearchloadingShimmer);

        recyclerView = root.findViewById(R.id.allquestions);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        mFirestore = ((ApplicationCustom) getActivity().getApplication()).mFirestore;

        fetchAnswers();
        return root;
    }

    public void fetchAnswers() {
        shimmerFrameLayout.startShimmerAnimation();
        SharedPrefManager sh = new SharedPrefManager(getActivity());
        main_interface = RetrofitClient.getApiClient().create(Main_Interface.class);
        mFirestore.collection("paidAnswers").whereEqualTo("userId", sh.getsUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot task) {
                        List<DocumentSnapshot> list = task.getDocuments();
                        for(DocumentSnapshot d: list) {
                            answers.add(new HideAnswersModel(d.getString("ques"), d.getString("qDesc"),
                                    d.getString("qImg"), d.getString("ans"), d.getString("author"), ""
                                    ));
                        }
                        adapter = new HideAnswersAdapter(answers, getActivity());
                        recyclerView.setAdapter(adapter);

                        shimmerFrameLayout.stopShimmerAnimation();
                        shimmerFrameLayout.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.d(TAG, "onFailure: " + e);
                    }
                });

//        Call<List<HideAnswersModel>> call = main_interface.getHideAns(sh.getsUser().getUid(), "show");
//        call.enqueue(new Callback<List<HideAnswersModel>>() {
//            @Override
//            public void onResponse(Call<List<HideAnswersModel>> call, Response<List<HideAnswersModel>> response) {
//
//                answers = response.body();
//                adapter = new HideAnswersAdapter(answers, getActivity());
//                recyclerView.setAdapter(adapter);
//
//                shimmerFrameLayout.stopShimmerAnimation();
//                shimmerFrameLayout.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onFailure(Call<List<HideAnswersModel>> call, Throwable t) {
//
//            }
//        });
    }


}

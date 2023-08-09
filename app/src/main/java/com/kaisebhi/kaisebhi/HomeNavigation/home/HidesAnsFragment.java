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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.Utility.Main_Interface;
import com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HidesAnsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<HideAnswersModel> answers;
    private HideAnswersAdapter adapter;
    private Main_Interface main_interface;
    private ShimmerFrameLayout shimmerFrameLayout;
    private FirebaseFirestore mFirestore;
    private String TAG = "HidesAnsFragment.java";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_hide_ans, container, false);

        shimmerFrameLayout = root.findViewById(R.id.SearchloadingShimmer);

        recyclerView = root.findViewById(R.id.allquestions);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        mFirestore = FirebaseFirestore.getInstance();
        fetchAnswers();
        return root;
    }

    public void fetchAnswers()
    {
        shimmerFrameLayout.startShimmerAnimation();
        SharedPrefManager sh = new SharedPrefManager(getActivity());
        main_interface = RetrofitClient.getApiClient().create(Main_Interface.class);
        mFirestore.collection("answers").whereEqualTo("userId", sh.getsUser().getUid())
                .whereEqualTo("checkHideAnswer", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            List<DocumentSnapshot> list = task.getResult().getDocuments();
//                            for(DocumentSnapshot d: list) {
//                                answers.add(new HideAnswersModel(
//                                        d.getString("id"), d.getBoolean("checkOwnQuestion"),
//                                        d.getString("uname"), d.getString("upro"), d.getString("likes"),
//                                        d.getString("qdesc"), d.getString("qimg"), d.getBoolean("likeCheck"),
//                                        d.getString("answer"), d.getBoolean("checkHideAnswer"), d.getBoolean("paidCheck"),
//                                        d.getString("paidAmount"), d.getBoolean("selfAnswer"), d.getBoolean("selfHideAnswer"),
//                                        d.getBoolean("userReportCheck"), d.getString("title"), d.getId(), d.getString("reportBy"),
//                                        d.getString("likedBy"), d.getString("userId")
//                                );
//                            }

                            adapter = new HideAnswersAdapter(answers,getActivity());
                            recyclerView.setAdapter(adapter);

                            shimmerFrameLayout.stopShimmerAnimation();
                            shimmerFrameLayout.setVisibility(View.GONE);
                        } else {
                            Log.d(TAG, "onComplete: " + task.getException());
                        }
                    }
                });
//        Call<List<HideAnswersModel>> call = main_interface.getHideAns(sh.getsUser().getUid(),"hide");
//        call.enqueue(new Callback<List<HideAnswersModel>>() {
//            @Override
//            public void onResponse(Call<List<HideAnswersModel>> call, Response<List<HideAnswersModel>> response) {
//
//                answers = response.body();
//                adapter = new HideAnswersAdapter(answers,getActivity());
//                recyclerView.setAdapter(adapter);
//
//                shimmerFrameLayout.stopShimmerAnimation();
//                shimmerFrameLayout.setVisibility(View.GONE);
//            }
//            @Override
//            public void onFailure(Call<List<HideAnswersModel>> call, Throwable t) {
//
//            }
//        });
    }


}

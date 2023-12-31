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
import com.kaisebhi.kaisebhi.Utility.ApplicationCustom;
import com.kaisebhi.kaisebhi.Utility.Main_Interface;
import com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class MineAnsFragment extends Fragment {

    private String TAG = "MineAnsFragment.java";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<AnswersModel> answers = new ArrayList<>();
    private MineAnswersAdapter adapter;
    private Main_Interface main_interface;
    private ShimmerFrameLayout shimmerFrameLayout;
    private FirebaseFirestore mFirestore;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_mine_ans, container, false);
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

        mFirestore.collection("answers").whereEqualTo("userId", sh.getsUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        try {
                            if (task.isSuccessful()) {
                                List<DocumentSnapshot> list = task.getResult().getDocuments();
                                Log.d(TAG, "onComplete: size of list " + list.size());
                                for (DocumentSnapshot d : list) {
                                    AnswersModel data = new AnswersModel(
                                            d.getString("id"), d.getBoolean("checkOwnQuestion"),
                                            d.getString("uname"), d.getString("upro"), d.getString("likes"),
                                            d.getString("qdesc"), d.getString("qimg"), d.getBoolean("likeCheck"),
                                            d.getString("answer"), d.getBoolean("checkHideAnswer"), d.getBoolean("paidCheck"),
                                            d.getString("paidAmount"), d.getBoolean("selfAnswer"), d.getBoolean("selfHideAnswer"),
                                            d.getBoolean("userReportCheck"), d.getString("title"), d.getId(), d.getString("reportBy"),
                                            d.getString("likedBy"), d.getString("userId")
                                    );
                                    data.setAudioUrl(d.getString("audio"));
                                    data.setPortal(d.getString("portal"));
                                    answers.add(data);
                                }

                                adapter = new MineAnswersAdapter(answers, getActivity(), requireActivity().getSupportFragmentManager());
                                recyclerView.setAdapter(adapter);
                                shimmerFrameLayout.stopShimmerAnimation();
                                shimmerFrameLayout.setVisibility(View.GONE);
                            } else {
                                Log.d(TAG, "onComplete: " + task.getException());
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "onComplete: " + e);
                        }
                    }
                });
    }

}

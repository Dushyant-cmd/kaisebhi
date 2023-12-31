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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.Utility.ApplicationCustom;
import com.kaisebhi.kaisebhi.Utility.Main_Interface;
import com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class MineQuestFragment extends Fragment {


    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<QuestionsModel> questions = new ArrayList<>();
    private MineQuestionsAdapter adapter;
    private Main_Interface main_interface;
    private ShimmerFrameLayout shimmerFrameLayout;
    private FirebaseFirestore mFirestore;
    private String TAG = "MineQuestFragment.java";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_min, container, false);


        shimmerFrameLayout = root.findViewById(R.id.SearchloadingShimmer);
        mFirestore = ((ApplicationCustom) getActivity().getApplication()).mFirestore;

        recyclerView = root.findViewById(R.id.allquestions);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        fetchQuestions();

        return root;
    }


    public void fetchQuestions() {
        Log.d(TAG, "fetchQuestions: adapter: " + adapter);
        shimmerFrameLayout.startShimmerAnimation();
        main_interface = RetrofitClient.getApiClient().create(Main_Interface.class);
        Log.d(TAG, "fetchQuestions uId: " + SharedPrefManager.getInstance(getActivity()).getsUser().getUid());
        mFirestore.collection("questions")
                .whereEqualTo("userId", SharedPrefManager.getInstance(getActivity()).getsUser().getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            questions.clear();
                            List<DocumentSnapshot> list = task.getResult().getDocuments();
                            for(DocumentSnapshot d: list) {
                                questions.add(new QuestionsModel(d.getString("id"), d.getString("title"), d.getString("desc"),
                                        d.getString("qpic"), d.getString("uname"), "NA", d.getBoolean("checkFav"),
                                        d.getString("likes"), d.getBoolean("checkLike"), d.getString("tanswers"),
                                        d.getString("likedByUser"), d.getString("image"), d.getString("userId")
                                        , d.getString("userPicUrl"), d.getString("imageRef"),
                                        d.getString("portal"), d.getString("audio"), d.getString("audioRef"),
                                        d.getString("qualityCheck")
                                ));
                            }

                            adapter = new MineQuestionsAdapter(questions,getActivity(), mFirestore,
                                    ((ApplicationCustom) requireActivity().getApplication()).storage,
                                    getActivity().getSupportFragmentManager());
                            recyclerView.setAdapter(adapter);
                            shimmerFrameLayout.stopShimmerAnimation();
                            shimmerFrameLayout.setVisibility(View.GONE);
                            Log.d(TAG, "onComplete: " + questions.size());
                        } else {
                            shimmerFrameLayout.stopShimmerAnimation();
                            shimmerFrameLayout.setVisibility(View.GONE);
                            Log.d(TAG, "onComplete: " + task.getException());
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: resume ");
    }
}

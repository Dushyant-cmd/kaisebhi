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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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


    public void fetchQuestions()
    {
        shimmerFrameLayout.startShimmerAnimation();

        SharedPrefManager sh = new SharedPrefManager(getActivity());

        main_interface = RetrofitClient.getApiClient().create(Main_Interface.class);

        mFirestore.collection("questions").whereEqualTo("userId", SharedPrefManager.getInstance(getActivity()).getsUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            List<DocumentSnapshot> list = task.getResult().getDocuments();
                            for(DocumentSnapshot d: list) {
                                questions.add(new QuestionsModel(d.getString("id"), d.getString("title"), d.getString("desc"),
                                        d.getString("qpic"), d.getString("uname"), "NA", d.getBoolean("checkFav"),
                                        d.getString("likes"), d.getBoolean("checkLike"), d.getString("tanswers"),
                                        d.getString("likedByUser"), d.getString("image")));
                            }
                            adapter = new MineQuestionsAdapter(questions,getActivity(), mFirestore, ((ApplicationCustom) requireActivity().getApplication()).storage);
                            recyclerView.setAdapter(adapter);

                            shimmerFrameLayout.stopShimmerAnimation();
                            shimmerFrameLayout.setVisibility(View.GONE);
                        } else {
                            shimmerFrameLayout.stopShimmerAnimation();
                            shimmerFrameLayout.setVisibility(View.GONE);
                            Log.d(TAG, "onComplete: " + task.getException());
                        }
                    }
                });

//        Call<List<QuestionsModel>> call = main_interface.getallQuestions(sh.getsUser().getUid(),"minQues");
//
//        call.enqueue(new Callback<List<QuestionsModel>>() {
//            @Override
//            public void onResponse(Call<List<QuestionsModel>> call, Response<List<QuestionsModel>> response) {
//
//                questions = response.body();
//                adapter = new MineQuestionsAdapter(questions,getActivity());
//                recyclerView.setAdapter(adapter);
//
//                shimmerFrameLayout.stopShimmerAnimation();
//                shimmerFrameLayout.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onFailure(Call<List<QuestionsModel>> call, Throwable t) {
//
//            }
//        });
    }


}

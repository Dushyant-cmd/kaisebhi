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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.Utility.ApplicationCustom;
import com.kaisebhi.kaisebhi.Utility.Main_Interface;
import com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;
import com.kaisebhi.kaisebhi.room.RoomDb;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<QuestionsModel> questions;
    private QuestionsAdapter adapter;
    private Main_Interface main_interface;
    private GifImageView progressImg;
    private ShimmerFrameLayout shimmerFrameLayout;
    private FirebaseFirestore mFirestore;
    private String TAG = "FavoriteFragment.java";
    private RoomDb roomDb;
    private FirebaseStorage storage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_favorites, container, false);
        mFirestore = ((ApplicationCustom) getActivity().getApplication()).mFirestore;
        shimmerFrameLayout = root.findViewById(R.id.SearchloadingShimmer);
        questions = new ArrayList<>();
        roomDb = ((ApplicationCustom) getActivity().getApplication()).roomDb;

        recyclerView = root.findViewById(R.id.allquestions);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        storage = FirebaseStorage.getInstance();

        fetchQuestions();

        return root;
    }

    public void fetchQuestions() {
        shimmerFrameLayout.startShimmerAnimation();
        SharedPrefManager sh = new SharedPrefManager(getActivity());
        main_interface = RetrofitClient.getApiClient().create(Main_Interface.class);
        mFirestore.collection("favorite").whereEqualTo("userId", SharedPrefManager.getInstance(getActivity()).getsUser().getUid()).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot d : task.getResult().getDocuments()) {
                                QuestionsModel model = new QuestionsModel(
                                        d.getString("id"), d.getString("title"), d.getString("desc"),
                                        d.getString("qpic"), d.getString("uname"), d.getString("upro"),
                                        d.getBoolean("checkFav"), d.getString("likes"), d.getBoolean("checkLike"),
                                        d.getString("tanswers"), d.getString("likedByUser"), d.getString("image"),
                                        d.getString("userId"), d.getString("userPicUrl"), "",
                                        d.getString("portal"), d.getString("audio"), d.getString("audioRef")
                                );
                                questions.add(model);
                            }

                            if(adapter != null) {
                                adapter.exoPlayer.stop();
                                adapter.exoPlayer.release();
                            }
                            
                            adapter = new QuestionsAdapter(questions,getActivity(), mFirestore, roomDb, storage);
                            recyclerView.setAdapter(adapter);
                            shimmerFrameLayout.stopShimmerAnimation();
                            shimmerFrameLayout.setVisibility(View.GONE);
                        } else {
                            Log.d(TAG, "onComplete: " + task.getException().getMessage());
                        }
                    }
                });
    }

    /**Below method is to stp exoplayer if reselected same fragment. */
    public void stopExo() {
        adapter.exoPlayer.stop();
        adapter.exoPlayer.release();
        Log.d(TAG, "stopExo: home exo");
    }
}

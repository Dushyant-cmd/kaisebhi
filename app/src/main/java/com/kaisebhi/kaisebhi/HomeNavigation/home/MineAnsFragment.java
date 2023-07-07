package com.kaisebhi.kaisebhi.HomeNavigation.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.Utility.ApplicationCustom;
import com.kaisebhi.kaisebhi.Utility.Main_Interface;
import com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MineAnsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<AnswersModel> answers;
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


    public void fetchAnswers()
    {
        shimmerFrameLayout.startShimmerAnimation();
        SharedPrefManager sh = new SharedPrefManager(getActivity());
        main_interface = RetrofitClient.getApiClient().create(Main_Interface.class);

        Call<List<AnswersModel>> call = main_interface.getMineAnswers(sh.getsUser().getUid());

        call.enqueue(new Callback<List<AnswersModel>>() {
            @Override
            public void onResponse(Call<List<AnswersModel>> call, Response<List<AnswersModel>> response) {

                answers = response.body();
                adapter = new MineAnswersAdapter(answers,getActivity());
                recyclerView.setAdapter(adapter);

                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<AnswersModel>> call, Throwable t) {

            }
        });
    }


}

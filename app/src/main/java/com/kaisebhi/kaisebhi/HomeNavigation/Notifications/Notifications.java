package com.kaisebhi.kaisebhi.HomeNavigation.Notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class Notifications extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<NotifyViewModel> notify = new ArrayList<>();
    private NotifyAdapter adapter;
    private Main_Interface main_interface;
    private ProgressBar progressImg;
    private FirebaseFirestore mFirestore;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_notify, container, false);

        progressImg = root.findViewById(R.id.updateImgProgress);
        mFirestore = ((ApplicationCustom) getActivity().getApplication()).mFirestore;
        recyclerView = root.findViewById(R.id.all_notify);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        fetchNotification();

        return root;
    }


    public void fetchNotification()
    {
        mFirestore.collection("notifications").get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(DocumentSnapshot d: task.getResult().getDocuments()) {
                                notify.add(new NotifyViewModel(d.getString("qId"),
                                        d.getString("title"), d.getString("msg"),
                                        d.getString("thumbnail"), d.getString("status")));
                            }
                            adapter = new NotifyAdapter(notify,getActivity());
                            recyclerView.setAdapter(adapter);
                            progressImg.setVisibility(View.INVISIBLE);
                        }
                    }
                });

//        main_interface = RetrofitClient.getApiClient().create(Main_Interface.class);
//
//        Call<List<NotifyViewModel>> call = main_interface.getNotifications(sh.getsUser().getUid());
//
//        call.enqueue(new Callback<List<NotifyViewModel>>() {
//            @Override
//            public void onResponse(Call<List<NotifyViewModel>> call, Response<List<NotifyViewModel>> response) {
//
//                notify = response.body();
//
//            }
//
//            @Override
//            public void onFailure(Call<List<NotifyViewModel>> call, Throwable t) {
//
//            }
//        });
    }

}
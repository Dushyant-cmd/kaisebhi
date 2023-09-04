package com.kaisebhi.kaisebhi.HomeNavigation.Reward;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.Utility.ApplicationCustom;
import com.kaisebhi.kaisebhi.Utility.DefaultResponse;
import com.kaisebhi.kaisebhi.Utility.Main_Interface;
import com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RewardFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<ModelWalletHistory> history = new ArrayList<>();
    private WalletHistoryRecylerView adapter;
    private Main_Interface main_interface;
    private ProgressBar progressBar;
    Button claimReward;
    LinearLayout showBox;
    LinearLayout Box;
    EditText id, otherBox;
    Spinner paySelect;
    SharedPrefManager sharedPrefManager;

    long balance;
    private TextView bal;
    private FirebaseFirestore mFirestore;
    private String TAG = "RewardFragment.java";
    private String downloadLink = "https://play.google.com/store/apps/details?id=com.traidev.kaisebhi";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        sharedPrefManager = new SharedPrefManager(getActivity());

        View root = inflater.inflate(R.layout.fragment_reward, container, false);

        bal = root.findViewById(R.id.balance);
        progressBar = root.findViewById(R.id.updateProgHistory);
        claimReward = root.findViewById(R.id.claimReward);
        showBox = root.findViewById(R.id.showBox);
        otherBox = root.findViewById(R.id.otherBox);
        Box = root.findViewById(R.id.box);
        TextView close = root.findViewById(R.id.close);
        paySelect = root.findViewById(R.id.paySelect);
        id = root.findViewById(R.id.editId);
        mFirestore = ((ApplicationCustom) getActivity().getApplication()).mFirestore;

        String days = "Select Upi, Paytm, Phonepe, Googlepe, BHIM Upi, Amazon Pay Upi, Others";
        String[] elements = days.split(",");
        List<String> fixedLenghtList = Arrays.asList(elements);
        List<String> daysd = new ArrayList<String>(fixedLenghtList);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplication(), android.R.layout.simple_spinner_item, daysd);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);

        paySelect.setAdapter(dataAdapter);
        paySelect.setSelected(true);

        showBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (balance >= 100) {
                    Box.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getActivity(), "Reward points must be 100 Points!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Box.setVisibility(View.GONE);
                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        });


        paySelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (paySelect.getSelectedItem().toString().toLowerCase().trim().contains("others")) {
                    Toast.makeText(getContext(), paySelect.getSelectedItem().toString().toLowerCase(), Toast.LENGTH_SHORT).show();
                    otherBox.setVisibility(View.VISIBLE);
                } else {
                    otherBox.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        claimReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (paySelect.getSelectedItem().toString().isEmpty() || id.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please Enter Details for Payment!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (balance >= 100) {

                    claimReward.setClickable(false);
                    claimReward.setText("Wait.... ");
                    String typeUpi = "";

                    if (paySelect.getSelectedItem().toString().toLowerCase().trim().contains("others")) {
                        typeUpi = otherBox.getText().toString();
                    } else {
                        typeUpi = paySelect.getSelectedItem().toString();
                    }

                    Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().moneyRequest(sharedPrefManager.getsUser().getUid(), id.getText().toString(), typeUpi);
                    call.enqueue(new Callback<DefaultResponse>() {
                        @Override
                        public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                            DefaultResponse dr = response.body();
                            if (response.code() == 201) {
                                Toast.makeText(getActivity(), "Request has been sent to KaiseBhi!", Toast.LENGTH_SHORT).show();
                                bal.setText("0.0");
                                balance = 0;
                                fetchHistory(sharedPrefManager.getsUser().getUid());
                                Box.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(getActivity(), "Problem in Sending!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<DefaultResponse> call, Throwable t) {
                        }

                    });
                } else {
                    Toast.makeText(getActivity(), "Reward points must be 100 Points!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        fetchBal(sharedPrefManager.getsUser().getUid());

        recyclerView = root.findViewById(R.id.wallet_hitory_recy);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        fetchHistory(sharedPrefManager.getsUser().getUid());


        root.findViewById(R.id.referBtn).setOnClickListener(view -> {
            ((ApplicationCustom) getActivity().getApplication()).mFirestore.collection("appData")
                    .document("links").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            downloadLink = documentSnapshot.getString("appDownloadLink");
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("text/plain");
                            i.putExtra(Intent.EXTRA_TEXT, "Download The KaiseBhi & Earn money. Use my referral code : " +
                                    sharedPrefManager.getsUser().getReferId() + " | Download Now: "
                                    + downloadLink);
                            getActivity().startActivity(i);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e);
                            Toast.makeText(getActivity(), "No connection", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        return root;
    }

    public void fetchHistory(String id) {
        progressBar.setVisibility(View.VISIBLE);
        main_interface = RetrofitClient.getApiClient().create(Main_Interface.class);
        mFirestore.collection("rewardHistory").whereEqualTo("userId", id).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            List<DocumentSnapshot> list = task.getResult().getDocuments();
                            for(DocumentSnapshot d: list) {
                                history.add(new ModelWalletHistory(
                                        d.getString("date"), d.getLong("amount").toString(), d.getString("type")
                                        , d.getString("remark")
                                ));
                            }
                            adapter = new WalletHistoryRecylerView(history, getActivity());
                            recyclerView.setAdapter(adapter);
                            progressBar.setVisibility(View.GONE);
                        } else {

                        }
                    }
                });

//        Call<List<ModelWalletHistory>> call = main_interface.getWalletHistory(id);
//        call.enqueue(new Callback<List<ModelWalletHistory>>() {
//            @Override
//            public void onResponse(Call<List<ModelWalletHistory>> call, Response<List<ModelWalletHistory>> response) {
//                if (response.code() != 404) {
//                    history = response.body();
//                    adapter = new WalletHistoryRecylerView(history, getActivity());
//                    recyclerView.setAdapter(adapter);
//                } else {
//                }
//                progressBar.setVisibility(View.GONE);
//
//            }
//
//            @Override
//            public void onFailure(Call<List<ModelWalletHistory>> call, Throwable t) {
//
//            }
//        });
    }


    public void fetchBal(String id) {
        mFirestore.collection("users").document(sharedPrefManager.getsUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "onComplete: " + task.getResult());
                            balance = task.getResult().getLong("rewards");
                            sharedPrefManager.setReward(balance);
                            bal.setText(balance + ".0");
                        } else {
                            Log.d(TAG, "onComplete: " + task.getException());
                        }
                    }
                });
//        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().getBal(id);
//        call.enqueue(new Callback<DefaultResponse>() {
//            @Override
//            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
//                DefaultResponse dr = response.body();
//                if (response.code() == 201) {
//                    String data = dr.getMessage();
//                    bal.setText(data + ".0");
//                    balance = Integer.parseInt(data);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<DefaultResponse> call, Throwable t) {
//            }
//
//        });
    }

}
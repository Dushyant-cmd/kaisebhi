package com.kaisebhi.kaisebhi.HomeNavigation.Reward;

import android.content.Context;
import android.os.Bundle;
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


import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.Utility.DefaultResponse;
import com.kaisebhi.kaisebhi.Utility.Main_Interface;
import com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RewardFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<ModelWalletHistory> history;
    private WalletHistoryRecylerView adapter;
    private Main_Interface main_interface;
    private ProgressBar progressBar;
    Button claimReward;
    LinearLayout showBox;
    LinearLayout Box;
    EditText id,otherBox;
    Spinner paySelect;
    SharedPrefManager sharedPrefManager;

    int balance;
    private TextView bal;
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
                if(balance >= 100) {
                    Box.setVisibility(View.VISIBLE);
                }else {
                    Toast.makeText(getActivity(),"Reward points must be 100 Points!",Toast.LENGTH_SHORT).show();
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

                if(paySelect.getSelectedItem().toString().toLowerCase().trim().contains("others")){
                    Toast.makeText(getContext(),paySelect.getSelectedItem().toString().toLowerCase(),Toast.LENGTH_SHORT).show();
                    otherBox.setVisibility(View.VISIBLE);
                }else{
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

                if(paySelect.getSelectedItem().toString().isEmpty() || id.getText().toString().isEmpty())
                {
                    Toast.makeText(getActivity(),"Please Enter Details for Payment!",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(balance >= 100){

                    claimReward.setClickable(false);
                    claimReward.setText("Wait.... ");
                    String typeUpi = "";

                    if(paySelect.getSelectedItem().toString().toLowerCase().trim().contains("others")){
                        typeUpi = otherBox.getText().toString();
                    }else{
                        typeUpi = paySelect.getSelectedItem().toString();
                    }

                    Call<DefaultResponse> call =  RetrofitClient.getInstance().getApi().moneyRequest(sharedPrefManager.getsUser().getUid(),id.getText().toString(),typeUpi);
                    call.enqueue(new Callback<DefaultResponse>() {
                        @Override
                        public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                            DefaultResponse dr = response.body();
                            if(response.code() == 201) {
                                Toast.makeText(getActivity(),"Request has been sent to KaiseBhi!",Toast.LENGTH_SHORT).show();
                                bal.setText("0.0");
                                balance  = 0;
                                fetchHistory(sharedPrefManager.getsUser().getUid());
                                Box.setVisibility(View.GONE);
                            }
                            else
                            {
                                Toast.makeText(getActivity(),"Problem in Sending!",Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<DefaultResponse> call, Throwable t) {
                        }

                    });
                }
                else
                {
                    Toast.makeText(getActivity(),"Reward points must be 100 Points!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        fetchBal(sharedPrefManager.getsUser().getUid());

        recyclerView = root.findViewById(R.id.wallet_hitory_recy);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        fetchHistory(sharedPrefManager.getsUser().getUid());


        return root;
    }

    public void fetchHistory(String id)
    {
        progressBar.setVisibility(View.VISIBLE);
        main_interface = RetrofitClient.getApiClient().create(Main_Interface.class);

        Call<List<ModelWalletHistory>> call = main_interface.getWalletHistory(id);
        call.enqueue(new Callback<List<ModelWalletHistory>>() {
            @Override
            public void onResponse(Call<List<ModelWalletHistory>> call, Response<List<ModelWalletHistory>> response) {
                if(response.code() != 404)
                {
                    history = response.body();
                    adapter = new WalletHistoryRecylerView(history,getActivity());
                    recyclerView.setAdapter(adapter);
                }
                else
                { }
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<List<ModelWalletHistory>> call, Throwable t) {

            }
        });
    }


    public void fetchBal(String id)
    {
        Call<DefaultResponse> call =  RetrofitClient.getInstance().getApi().getBal(id);
        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                DefaultResponse dr = response.body();
                if(response.code() == 201) {
                    String data = dr.getMessage();
                    bal.setText(data + ".0");
                     balance = Integer.parseInt(data);
                }
            }
            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
            }

        });
    }

}
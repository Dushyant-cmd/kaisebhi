package com.kaisebhi.kaisebhi.HomeNavigation.Reward;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.kaisebhi.kaisebhi.R;

import java.util.List;

public class WalletHistoryRecylerView extends RecyclerView.Adapter<WalletHistoryRecylerView.ViewHolder> {

    private List<ModelWalletHistory> modelWalletHistoryList;
    private Context context;


    public WalletHistoryRecylerView(List<ModelWalletHistory> modelWalletHistoryList, Context context) {
        this.modelWalletHistoryList = modelWalletHistoryList;
        this.context = context;

    }

    @NonNull
    @Override
    public WalletHistoryRecylerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.wallet_history_modal,viewGroup,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String tDate = modelWalletHistoryList.get(position).getDate();
        String tType = modelWalletHistoryList.get(position).getType();
        String tRemark = modelWalletHistoryList.get(position).getRemark();
        String tAmount = modelWalletHistoryList.get(position).getAmount();
        holder.settDate(tDate);
        holder.settAmount(tAmount,tType);
        holder.settRemark(tRemark);
        holder.settType(tType);
    }


    @Override
    public int getItemCount() {
        return modelWalletHistoryList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tDate,tType,tRemark,tAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tDate = itemView.findViewById(R.id.TDate);
            tType = itemView.findViewById(R.id.TType);
            tRemark = itemView.findViewById(R.id.TRemark);
            tAmount = itemView.findViewById(R.id.TAmount);
        }

        public void settDate(String tdate) {
            tDate.setText(tdate);
        }

        public void settType(String ttype) {
            tType.setText(ttype);
        }

        public void settAmount(String tamount,String ttype)
        {
                  if(ttype.equals("DEBIT"))
                      tAmount.setText("- "+tamount);
                  else
                      tAmount.setText("+ "+tamount);
        }

        public void settRemark(String temark) {
            tRemark.setText(temark);
        }

    }


}

package com.kaisebhi.kaisebhi.HomeNavigation.home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;

import java.util.List;

import static com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient.BASE_URL;

public class MineAnswersAdapter extends RecyclerView.Adapter<MineAnswersAdapter.ViewHolder>{

    int amount = 0;

    private List<AnswersModel> nlist;
    private Context context;
    String qid;

    private Dialog myDialog;

    public MineAnswersAdapter(List<AnswersModel> nlist, Context context)
    {
        this.nlist = nlist;
        this.context = context;
    }

    @NonNull
    @Override
    public MineAnswersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modal_mine_answers,parent,false);
        return new MineAnswersAdapter.ViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.Answer.setText(nlist.get(position).getTansers());
        holder.Author.setText(nlist.get(position).getUname());
        holder.Question.setText(nlist.get(position).getQuestionsTitle());
        final String Id = nlist.get(position).getID();
        SharedPrefManager sh = new SharedPrefManager(context);
        final String uid = sh.getsUser().getUid();
        holder.Desc.setText(nlist.get(position).getDesc());

        if(nlist.get(position).getQimg().length()>0) {
            holder.qImg.setVisibility(View.VISIBLE);
            Glide.with(context).load(nlist.get(position).getQimg()).fitCenter().into((holder).qImg);
        } else
            holder.qImg.setVisibility(View.VISIBLE);
        Glide.with(context).load(BASE_URL + nlist.get(position).getUpro()).placeholder(R.drawable.profile).fitCenter().into((holder).pro);

    }

    @Override
    public int getItemCount() {
        return nlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView pro,qImg;
        TextView Answer,Author,Question,Desc;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pro = itemView.findViewById(R.id.ansPro);
            Answer = itemView.findViewById(R.id.userAnswer);
            Question = itemView.findViewById(R.id.quesTitle);
            Desc = itemView.findViewById(R.id.hide_desc);
            qImg = itemView.findViewById(R.id.hide_quesImage);
            Author = itemView.findViewById(R.id.ansUser);

        }

    }









}

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

import java.util.List;

import static com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient.BASE_URL;

public class HideAnswersAdapter extends RecyclerView.Adapter<HideAnswersAdapter.ViewHolder>{

    int amount = 0;

    private List<HideAnswersModel> nlist;
    private Context context;
    String qid;
    private Dialog myDialog;

    public HideAnswersAdapter(List<HideAnswersModel> nlist, Context context)
    {
        this.nlist = nlist;
        this.context = context;
    }

    @NonNull
    @Override
    public HideAnswersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modal_hide_ans,parent,false);
        return new HideAnswersAdapter.ViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.Answer.setText(nlist.get(position).getAns());
        if(!nlist.get(position).getAuthor().isEmpty()){
            holder.author.append(nlist.get(position).getAuthor());
        }
        holder.Question.setText(nlist.get(position).getQues());
        holder.Desc.setText(nlist.get(position).getDesc());

        if(!nlist.get(position).getThumb().equals("default"))
        {
            holder.qImg.setVisibility(View.VISIBLE);
            Glide.with(context).load(BASE_URL + nlist.get(position).getThumb()).fitCenter().into((holder).qImg);
        }
    }

    @Override
    public int getItemCount() {
        return nlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView qImg;
        TextView Answer,Desc,Question,author;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            qImg = itemView.findViewById(R.id.hide_quesImage);
            Answer = itemView.findViewById(R.id.hide_ans);
            Question = itemView.findViewById(R.id.hide_qus);
            Desc = itemView.findViewById(R.id.hide_desc);
            author = itemView.findViewById(R.id.hide_au);
        }

    }









}

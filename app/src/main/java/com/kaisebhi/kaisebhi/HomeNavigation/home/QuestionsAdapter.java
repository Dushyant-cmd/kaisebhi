package com.kaisebhi.kaisebhi.HomeNavigation.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kaisebhi.kaisebhi.AnswersActivity;
import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.Utility.DefaultResponse;
import com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;
import com.kaisebhi.kaisebhi.ViewPic;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient.BASE_URL;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder>{


    private List<QuestionsModel> nlist;
    private Context context;

    public QuestionsAdapter(List<QuestionsModel> nlist, Context context)
    {
        this.nlist = nlist;
        this.context = context;
    }

    @NonNull
    @Override
    public QuestionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modal_questions,parent,false);
            return new QuestionsAdapter.ViewHolder(view);

    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.Title.setText(nlist.get(position).getTitle());
        holder.Desc.setText(nlist.get(position).getDesc());
        holder.Author.setText("By "+ nlist.get(position).getUname());

        if(!nlist.get(position).getTansers().equals("0")) {
            holder.totalAns.setText(nlist.get(position).getTansers());
        }

        if(!nlist.get(position).getLikes().equals("0")) {
            holder.totalLike.setText(nlist.get(position).getLikes());
        }


        final String Id = nlist.get(position).getID();

        SharedPrefManager sh = new SharedPrefManager(context);
        final String uid = sh.getsUser().getUid();

        holder.favBtn.setChecked(nlist.get(position).getCheckFav());
        holder.likeBtn.setChecked(nlist.get(position).getCheckLike());

        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey, Answer this Question on KaiseBhi  & Get Extra Discount | "+nlist.get(position).getTitle()+"\nGet Kaisebhi App at: https://play.google.com/store/apps/details?id=" + context.getPackageName());

                sendIntent.setType("text/plain");
                context.startActivity(sendIntent);
            }

        });


        holder.answers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, AnswersActivity.class);
                i.putExtra("key",Id);
                i.putExtra("title",nlist.get(position).getTitle());
                i.putExtra("user",nlist.get(position).getUname());
                i.putExtra("userpic",nlist.get(position).getUpro());
                i.putExtra("desc",nlist.get(position).getDesc());
                i.putExtra("qimg",nlist.get(position).getQpic());
                i.putExtra("tans",nlist.get(position).getTansers());
                i.putExtra("tlikes",nlist.get(position).getCheckLike());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }

        });

        holder.openQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, AnswersActivity.class);
                i.putExtra("key",Id);
                i.putExtra("title",nlist.get(position).getTitle());
                i.putExtra("user",nlist.get(position).getUname());
                i.putExtra("userpic",nlist.get(position).getUpro());
                i.putExtra("desc",nlist.get(position).getDesc());
                i.putExtra("qimg",nlist.get(position).getQpic());
                i.putExtra("tans",nlist.get(position).getTansers());
                i.putExtra("tlikes",nlist.get(position).getCheckLike());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }

        });


        holder.favBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Call<DefaultResponse> call =  RetrofitClient.getInstance().getApi().favQues(Id,String.valueOf(isChecked),uid);
                call.enqueue(new Callback<DefaultResponse>() {
                    @Override
                    public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                        DefaultResponse dr = response.body();
                    }
                    @Override
                    public void onFailure(Call<DefaultResponse> call, Throwable t) {

                    }

                });
            }
        });


        holder.questionimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context.getApplicationContext(), ViewPic.class);
                i.putExtra("photourl", nlist.get(position).getQpic());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });



        holder.likeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Call<DefaultResponse> call =  RetrofitClient.getInstance().getApi().likeQues(Id,String.valueOf(isChecked),uid);
                call.enqueue(new Callback<DefaultResponse>() {
                    @Override
                    public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                        DefaultResponse dr = response.body();

                    }
                    @Override
                    public void onFailure(Call<DefaultResponse> call, Throwable t) {

                    }

                });
            }
        });



        if(nlist.get(position).getQpic().length()>0)
        {
            holder.questionimg.setVisibility(View.VISIBLE);
            Glide.with(context).load(BASE_URL + "qimg/"+ nlist.get(position).getQpic()).fitCenter().into((holder).questionimg);
        }

        Glide.with(context).load(BASE_URL +"user/"+nlist.get(position).getUpro()).dontAnimate().centerCrop().placeholder(R.drawable.profile).fitCenter().into((holder).pro);


    }


    @Override
    public int getItemCount() {
            return nlist.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView pro;
        ImageView questionimg,shareBtn,answers;
        TextView Title,Desc,Author,totalAns,totalLike;
        CheckBox favBtn,likeBtn;
        CardView openQues;


       public ViewHolder(@NonNull View itemView) {
            super(itemView);

           pro = itemView.findViewById(R.id.userPro);
           questionimg = itemView.findViewById(R.id.quesImage);
           shareBtn = itemView.findViewById(R.id.shareQuestion);
           answers = itemView.findViewById(R.id.answers);
           likeBtn = itemView.findViewById(R.id.like);
           favBtn = itemView.findViewById(R.id.addFav);
           openQues = itemView.findViewById(R.id.openQues);
           Title = itemView.findViewById(R.id.quesTitle);
           Desc = itemView.findViewById(R.id.quesDesc);
           Author = itemView.findViewById(R.id.username);
           totalAns = itemView.findViewById(R.id.totalAns);
           totalLike = itemView.findViewById(R.id.totalLike);

       }

    }




}

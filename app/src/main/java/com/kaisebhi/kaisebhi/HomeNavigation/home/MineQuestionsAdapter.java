package com.kaisebhi.kaisebhi.HomeNavigation.home;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kaisebhi.kaisebhi.AnswersActivity;
import com.kaisebhi.kaisebhi.HomeNavigation.AddQuestion.Add_Queastion;
import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.Utility.DefaultResponse;
import com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient.BASE_URL;

public class MineQuestionsAdapter extends RecyclerView.Adapter<MineQuestionsAdapter.ViewHolder>{


    private List<QuestionsModel> nlist;
    private Context context;

    ProgressDialog progressDialog;

    public MineQuestionsAdapter(List<QuestionsModel> nlist, Context context)
    {
        this.nlist = nlist;
        this.context = context;
    }

    @NonNull
    @Override
    public MineQuestionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modal_min_ques,parent,false);
            return new MineQuestionsAdapter.ViewHolder(view);

    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.Title.setText(nlist.get(position).getTitle());
        holder.Desc.setText(nlist.get(position).getDesc());

        final String Id = nlist.get(position).getID();

        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey, Answer this Question on QNA & Get Extra Discount | "+nlist.get(position).getTitle());
                sendIntent.setType("text/plain");
                context.startActivity(sendIntent);
            }

        });


        holder.deleteQues.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                openProgress(true);

                Call<DefaultResponse> call =  RetrofitClient.getInstance().getApi().deleteQues(Id);
                call.enqueue(new Callback<DefaultResponse>() {
                    @Override
                    public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                        DefaultResponse dr = response.body();
                        openProgress(false);
                        Toast.makeText(context,"Question Deleted Succesfully!",Toast.LENGTH_SHORT).show();

                    }
                    @Override
                    public void onFailure(Call<DefaultResponse> call, Throwable t) {
                        openProgress(false);

                    }

                });

            }
        });



        holder.editQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, Add_Queastion.class);
                i.putExtra("key",Id);
                i.putExtra("title",nlist.get(position).getTitle());
                i.putExtra("desc",nlist.get(position).getDesc());
                i.putExtra("qimg",nlist.get(position).getQpic());
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


        if(nlist.get(position).getQpic().length()>0) {
            holder.questionimg.setVisibility(View.VISIBLE);
            Glide.with(context).load(BASE_URL + "qimg/"+ nlist.get(position).getQpic()).fitCenter().into((holder).questionimg);
        }



    }


    @Override
    public int getItemCount() {
            return nlist.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView pro,questionimg,shareBtn,answers,editQues,deleteQues;
        TextView Title,Desc;
        CardView openQues;


       public ViewHolder(@NonNull View itemView) {
            super(itemView);

           questionimg = itemView.findViewById(R.id.quesImage);
           shareBtn = itemView.findViewById(R.id.shareQuestion);
           answers = itemView.findViewById(R.id.answers);
           openQues = itemView.findViewById(R.id.openQues);
           Title = itemView.findViewById(R.id.quesTitle);
           Desc = itemView.findViewById(R.id.quesDesc);
           editQues = itemView.findViewById(R.id.edit_question);
           deleteQues = itemView.findViewById(R.id.delete_ques);

       }

    }


    public void openProgress(Boolean check){

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Question Deleting proccesing.");

        if(check)
        progressDialog.show();
        else
        progressDialog.dismiss();


    }




}

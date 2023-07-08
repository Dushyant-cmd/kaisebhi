package com.kaisebhi.kaisebhi.HomeNavigation.home;

import static com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient.BASE_URL;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaisebhi.kaisebhi.AnswersActivity;
import com.kaisebhi.kaisebhi.HomeNavigation.AddQuestion.Add_Queastion;
import com.kaisebhi.kaisebhi.R;

import java.util.List;

public class MineQuestionsAdapter extends RecyclerView.Adapter<MineQuestionsAdapter.ViewHolder> {


    private List<QuestionsModel> nlist;
    private Context context;
    private FirebaseFirestore mFirestore;
    ProgressDialog progressDialog;
    private String TAG = "MineQuestionsAdapter.java";

    public MineQuestionsAdapter(List<QuestionsModel> nlist, Context context, FirebaseFirestore mFirestore) {
        this.nlist = nlist;
        this.context = context;
        this.mFirestore = mFirestore;
    }

    @NonNull
    @Override
    public MineQuestionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modal_min_ques, parent, false);
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
                        "Hey, Answer this Question on QNA & Get Extra Discount | " + nlist.get(position).getTitle());
                sendIntent.setType("text/plain");
                context.startActivity(sendIntent);
            }

        });


        holder.deleteQues.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                openProgress(true);

                mFirestore.collection("questions").document(nlist.get(position).getID()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                nlist.remove(position);
                                notifyDataSetChanged();
                                Toast.makeText(context, "Question Deleted Successfully!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Log.d(TAG, "onFailure: " + e);
                            }
                        });
            }
        });


        holder.editQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, Add_Queastion.class);
                i.putExtra("key", Id);
                i.putExtra("title", nlist.get(position).getTitle());
                i.putExtra("desc", nlist.get(position).getDesc());
                i.putExtra("qimg", nlist.get(position).getQpic());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

            }

        });

        holder.openQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, AnswersActivity.class);
                i.putExtra("key", Id);
                i.putExtra("title", nlist.get(position).getTitle());
                i.putExtra("user", nlist.get(position).getUname());
                i.putExtra("userpic", nlist.get(position).getUpro());
                i.putExtra("desc", nlist.get(position).getDesc());
                i.putExtra("qimg", nlist.get(position).getQpic());
                i.putExtra("tans", nlist.get(position).getTansers());
                i.putExtra("tlikes", nlist.get(position).getCheckLike());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }

        });


        if (nlist.get(position).getQpic().length() > 0) {
            holder.questionimg.setVisibility(View.VISIBLE);
            Glide.with(context).load(BASE_URL + "qimg/" + nlist.get(position).getQpic()).fitCenter().into((holder).questionimg);
        }


    }


    @Override
    public int getItemCount() {
        return nlist.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView pro, questionimg, shareBtn, answers, editQues, deleteQues;
        TextView Title, Desc;
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


    public void openProgress(boolean check) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Question Deleting processing.");
        progressDialog.show();
    }


}

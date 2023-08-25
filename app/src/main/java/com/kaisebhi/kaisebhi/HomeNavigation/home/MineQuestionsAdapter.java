package com.kaisebhi.kaisebhi.HomeNavigation.home;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kaisebhi.kaisebhi.AnswersActivity;
import com.kaisebhi.kaisebhi.HomeNavigation.AddQuestion.Add_Queastion;
import com.kaisebhi.kaisebhi.R;

import java.util.List;

public class MineQuestionsAdapter extends RecyclerView.Adapter<MineQuestionsAdapter.ViewHolder> {


    private List<QuestionsModel> nlist;
    private Context context;
    private FirebaseFirestore mFirestore;
    ProgressDialog progressDialog;
    private FirebaseStorage storage;
    private String TAG = "MineQuestionsAdapter.java", url = "";
    public ExoPlayer exoPlayer;

    public MineQuestionsAdapter(List<QuestionsModel> nlist, Context context, FirebaseFirestore mFirestore, FirebaseStorage storage) {
        this.nlist = nlist;
        this.context = context;
        this.mFirestore = mFirestore;
        this.storage = storage;
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
        QuestionsModel model = nlist.get(position);
        holder.Title.setText(nlist.get(position).getTitle());
        holder.Desc.setText(nlist.get(position).getDesc());
        holder.portalTV.setText(model.getPortal());
        //ExoPlayer instance creation.
        setupAudio(holder.exoPlayer, model.getAudio());

        final String Id = nlist.get(position).getID();
        if (!nlist.get(position).getImage().isEmpty()) {
            holder.questionimg.setVisibility(View.VISIBLE);
            Glide.with(context).load(model.getImage()).fitCenter().into((holder).questionimg);
            url = model.getImage();
        } else {
            holder.questionimg.setVisibility(View.VISIBLE);
        }

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
                i.putExtra("qimg", nlist.get(position).getImage());
                i.putExtra("quesImgPath", nlist.get(position).getQuesImgPath());
                i.putExtra("portal", nlist.get(position).getPortal());
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
                i.putExtra("qimg", url);
                i.putExtra("tans", nlist.get(position).getTansers());
                i.putExtra("tlikes", nlist.get(position).getCheckLike());
                i.putExtra("userId", nlist.get(position).getUserId());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }

        });


    }

    private void setupAudio(StyledPlayerView playerView, String downloadUrl) {
        //Create a media item which is audio file can be a URI or download url for dynamic sourced http based
        //rendering
        exoPlayer = new ExoPlayer.Builder(context).build();
        playerView.setPlayer(exoPlayer);
        MediaItem mediaItem = MediaItem.fromUri(downloadUrl);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
    }


    @Override
    public int getItemCount() {
        return nlist.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView pro, questionimg, shareBtn, answers, editQues, deleteQues;
        StyledPlayerView exoPlayer;
        TextView Title, Desc, portalTV;
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
            portalTV = itemView.findViewById(R.id.portalTV);
            exoPlayer = itemView.findViewById(R.id.exoPlayer);

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

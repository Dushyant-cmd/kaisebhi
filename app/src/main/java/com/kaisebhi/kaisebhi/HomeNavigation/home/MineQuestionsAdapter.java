package com.kaisebhi.kaisebhi.HomeNavigation.home;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kaisebhi.kaisebhi.AnswersActivity;
import com.kaisebhi.kaisebhi.HomeNavigation.AddQuestion.Add_Queastion;
import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.databinding.PlayerSheetLayoutBinding;

import org.checkerframework.checker.units.qual.A;

import java.util.List;

public class MineQuestionsAdapter extends RecyclerView.Adapter<MineQuestionsAdapter.ViewHolder> {


    private List<QuestionsModel> nlist;
    private static Context context;
    private FirebaseFirestore mFirestore;
    ProgressDialog progressDialog;
    private FirebaseStorage storage;
    private String TAG = "MineQuestionsAdapter.java", url = "";
    private FragmentManager fm;

    public MineQuestionsAdapter(List<QuestionsModel> nlist, Context context, FirebaseFirestore mFirestore, FirebaseStorage storage,
    FragmentManager fm) {
        this.nlist = nlist;
        this.context = context;
        this.mFirestore = mFirestore;
        this.storage = storage;
        this.fm = fm;
    }

    @NonNull
    @Override
    public MineQuestionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modal_min_ques, parent, false);
        return new MineQuestionsAdapter.ViewHolder(view);

    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        QuestionsModel model = nlist.get(position);
        holder.Title.setText(nlist.get(position).getTitle());
        holder.Desc.setText(nlist.get(position).getDesc());
        holder.portalTV.setText(model.getPortal());
        //ExoPlayer setup and play when ready with controls and seekbar.
        if(!model.getAudio().isEmpty()) {
            holder.playAudioBtn.setVisibility(View.VISIBLE);
            holder.playAudioBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayerBottomSheet playerBottomSheet = new PlayerBottomSheet(model.getAudio());
                    playerBottomSheet.show(fm, "PlayerView");
                }
            });
        } else
            holder.playAudioBtn.setVisibility(View.GONE);

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
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference audioRef = storageReference.child("audios/" + nlist.get(position).getAudioRef());
                audioRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: audio success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e);
                    }
                });

                StorageReference imageRef = storageReference.child("images/" + nlist.get(position)
                        .getImageRef());
                imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: image success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e);
                    }
                });

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
                i.putExtra("audio", nlist.get(position).getAudio());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });
    }

    /**Below class is BottomSheetDialogFragment to display a sheet to play audio. */
    public static class PlayerBottomSheet extends BottomSheetDialogFragment {
        private String downloadUrl;
        private SimpleExoPlayer exoPlayer;
        private PlayerSheetLayoutBinding binding;
        private String TAG = "PlayerBottomSheet.java";

        public PlayerBottomSheet(String url) {
            this.downloadUrl = url;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
            View view = inflater.inflate(R.layout.player_sheet_layout, container, false);
            setupAudio(view.findViewById(R.id.exoPlayer), downloadUrl);
            return view;
        }

        /**Below method is to play audio media using ExoPlayer library with its default controller
         * @param playerView xml view on which all the control or media related view will be displayed
         * @param downloadUrl it is a http protocol url to play dynamic media over internet.*/
        private void setupAudio(SimpleExoPlayerView playerView, String downloadUrl) {
            //Create a media item which is audio file can be a URI or download url for dynamic sourced http based
            //rendering
            //Create a media item which is audio file can be a URI or download url for dynamic sourced
            //http based rendering
            try {
                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                DefaultHttpDataSourceFactory dataSource = new DefaultHttpDataSourceFactory("agent");
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
                Log.d(TAG, "setupAudio instance: " + exoPlayer);
                //MediaSource to add all the uri, httpDataSource, extractor etc.
                MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(downloadUrl), dataSource,
                        extractorsFactory, null, null);
                playerView.setPlayer(exoPlayer);
                exoPlayer.prepare(mediaSource);
                exoPlayer.setPlayWhenReady(true);
            } catch (Exception e) {
                Log.d(TAG, "setupAudio: " + e);
            }
        }
        @Override
        public void onDestroy() {
            super.onDestroy();
            exoPlayer.stop();
            exoPlayer.release();
            Log.d(TAG, "onDestroy: sheet destroyed");
        }
    }

    @Override
    public int getItemCount() {
        return nlist.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pro, questionimg, shareBtn, answers, editQues, deleteQues;
        Button playAudioBtn;
//        SimpleExoPlayerView exoPlayer;
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
            playAudioBtn = itemView.findViewById(R.id.playBtn);
//            exoPlayer = itemView.findViewById(R.id.exoPlayer);

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

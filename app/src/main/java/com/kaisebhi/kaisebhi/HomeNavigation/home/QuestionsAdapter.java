package com.kaisebhi.kaisebhi.HomeNavigation.home;

import static com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient.BASE_URL;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.kaisebhi.kaisebhi.AnswersActivity;
import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;
import com.kaisebhi.kaisebhi.ViewPic;
import com.kaisebhi.kaisebhi.databinding.PlayerSheetLayoutBinding;
import com.kaisebhi.kaisebhi.room.RoomDb;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {
    private boolean isPlaying = false;
    private List<QuestionsModel> nlist;
    //    private boolean isLiked = false;
    private static Context context;
    private FirebaseFirestore mFirestore;
    private String TAG = "QuestionsAdapter.java", comeFrom = "", usersLikedBy = "";
    ;
    private boolean isFavChecked = true;
    private FirebaseStorage storage;
    public RoomDb roomDb;
    private String url = "";
    private ProgressDialog progressDialog;
    public SimpleExoPlayer exoPlayer;
    private FragmentManager fm;

    public QuestionsAdapter(List<QuestionsModel> nlist, Context context, FirebaseFirestore firestore,
                            RoomDb roomDb, FirebaseStorage storage, FragmentManager fm) {
        this.nlist = nlist;
        this.context = context;
        this.mFirestore = firestore;
        this.roomDb = roomDb;
        this.storage = storage;
        this.fm = fm;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
    }

    public QuestionsAdapter(List<QuestionsModel> nlist, Context context, FirebaseFirestore firestore,
                            String comeFrom, RoomDb roomDb, FirebaseStorage storage, FragmentManager fm) {
        this.nlist = nlist;
        this.context = context;
        this.mFirestore = firestore;
        this.comeFrom = comeFrom;
        this.roomDb = roomDb;
        this.storage = storage;
        this.fm = fm;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
    }

    @NonNull
    @Override
    public QuestionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modal_questions, parent, false);
        return new QuestionsAdapter.ViewHolder(view);

    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        QuestionsModel q = nlist.get(position);
        Log.d(TAG, "onBindViewHolder: model: " + q);
        holder.Title.setText(nlist.get(position).getTitle());
        holder.Desc.setText(nlist.get(position).getDesc());
        holder.Author.setText("By " + nlist.get(position).getUname());
        holder.portalTV.setText(q.getPortal());
        if(!q.getAudio().isEmpty()) {
            holder.playBtn.setVisibility(View.VISIBLE);
            holder.playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayerBottomSheet playerBottomSheet = new PlayerBottomSheet(q.getAudio());
                    playerBottomSheet.show(fm, "playerView");
                }
            });
        } else {
            holder.playBtn.setVisibility(View.GONE);
        }

        if (!nlist.get(position).getTansers().equals("0")) {
            holder.totalAns.setText(nlist.get(position).getTansers());
        }

        if (!nlist.get(position).getLikes().equals("0")) {
            holder.totalLike.setText(nlist.get(position).getLikes());
        } else
            holder.totalLike.setText("");

        final String Id = nlist.get(position).getID();

        SharedPrefManager sh = new SharedPrefManager(context);
        final String uid = sh.getsUser().getUid();

        if (!q.getImage().isEmpty()) {
            url = q.getImage();
            holder.questionimg.setVisibility(View.VISIBLE);
            Glide.with(context).load(url).fitCenter().into((holder).questionimg);
//            Log.d(TAG, "onComplete: " + url);
        } else {
            holder.questionimg.setVisibility(View.VISIBLE);
        }

        if (comeFrom.matches("home")) {
            mFirestore.collection("favorite").whereEqualTo("id", q.getID())
                    .whereEqualTo("userId", SharedPrefManager.getInstance(context).getsUser().getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
//                                Log.d(TAG, "onComplete: success ");
                                if (!task.getResult().getDocuments().isEmpty()) {
                                    q.setCheckFav(true);
                                    holder.favBtn.setChecked(q.getCheckFav());
                                }
                            } else {
                                holder.favBtn.setChecked(nlist.get(position).getCheckFav());
                                Log.d(TAG, "onComplete: " + task.getException());
                            }
                        }
                    });
        } else
            holder.favBtn.setChecked(nlist.get(position).getCheckFav());

        Log.d(TAG, "onBindViewHolder: " + q.getLikedByUser());
        String[] likedByUsersArr = q.getLikedByUser().split(",");
        for (String userId : likedByUsersArr) {
            if (userId.matches(uid)) {
                holder.likeBtn.setChecked(true);
                q.setCheckLike(true);
                Log.d(TAG, "liked by user");
                break;
            } else {
                q.setCheckLike(false);
                holder.likeBtn.setChecked(false);
            }
        }

        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey, Answer this Question on KaiseBhi  & Get Extra Discount | " + nlist.get(position).getTitle() + "\nGet Kaisebhi App at: https://play.google.com/store/apps/details?id=" + context.getPackageName());

                sendIntent.setType("text/plain");
                context.startActivity(sendIntent);
            }

        });


        holder.answers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, AnswersActivity.class);
                i.putExtra("key", Id);
                i.putExtra("title", nlist.get(position).getTitle());
                i.putExtra("user", nlist.get(position).getUname());
                i.putExtra("userpic", q.getUserPicUrl());
                i.putExtra("desc", nlist.get(position).getDesc());
                i.putExtra("qimg", q.getImage());
                i.putExtra("tans", nlist.get(position).getTansers());
                i.putExtra("tlikes", nlist.get(position).getCheckLike());
                i.putExtra("likes", nlist.get(position).getLikes());
                i.putExtra("userId", q.getUserId());
                i.putExtra("audio", q.getAudio());
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
                i.putExtra("userpic", q.getUserPicUrl());
                i.putExtra("desc", nlist.get(position).getDesc());
                i.putExtra("qimg", q.getImage());
                i.putExtra("tans", nlist.get(position).getTansers());
                i.putExtra("tlikes", nlist.get(position).getCheckLike());
                i.putExtra("userId", q.getUserId());
                i.putExtra("audio", q.getAudio());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }

        });

        holder.favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.favBtn.setEnabled(false);
                if (isFavChecked) {
                    //not already favorite
                    HashMap<String, Object> questionMap = new HashMap<>();
                    questionMap.put("title", q.getTitle());
                    questionMap.put("desc", q.getDesc());
                    questionMap.put("likes", q.getLikes());
                    questionMap.put("qpic", q.getQpic());
                    questionMap.put("checkFav", true);
                    questionMap.put("checkLike", q.getCheckLike());
                    questionMap.put("tanswers", q.getTansers());
                    questionMap.put("uname", q.getUname());
                    questionMap.put("userId", SharedPrefManager.getInstance(context).getsUser().getUid());
                    questionMap.put("id", q.getID());
                    questionMap.put("timestamp", System.currentTimeMillis());
                    questionMap.put("likedByUser", q.getLikedByUser());
                    questionMap.put("image", q.getImage());
                    questionMap.put("userPicUrl", sh.getProfilePic());
                    questionMap.put("portal", q.getPortal());
                    questionMap.put("audio", q.getAudio());
                    questionMap.put("audioRef", q.getAudioRef());
                    mFirestore.collection("favorite").add(questionMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            holder.favBtn.setEnabled(true);
//                            Log.d(TAG, "onSuccess: success" + documentReference);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            holder.favBtn.setEnabled(true);
                            Log.d(TAG, "onFailure: " + e);
                        }
                    });
                } else {
                    //already favorite
                    mFirestore.collection("favorite").whereEqualTo("id", q.getID())
                            .whereEqualTo("userId", SharedPrefManager.getInstance(context).getsUser().getUid()).get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    String docId = queryDocumentSnapshots.getDocuments().get(0).getId();
                                    mFirestore.collection("favorite").document(docId).delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    try {
                                                        holder.favBtn.setEnabled(true);
                                                        if (comeFrom.matches("home")) {
//                                                            Log.d(TAG, "onSuccess: success");
//                                                            roomDb.getFavDao().deleteFav(q);
                                                            q.setCheckFav(false);
                                                            QuestionsAdapter.this.notifyDataSetChanged();
                                                        } else {
//                                                            Log.d(TAG, "onSuccess: success");
//                                                            roomDb.getFavDao().deleteFav(q);
                                                            q.setCheckFav(false);
                                                            nlist.remove(position);
                                                            QuestionsAdapter.this.notifyDataSetChanged();
                                                        }
                                                    } catch (Exception e) {
                                                        Log.d(TAG, "onSuccess: " + e);
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    holder.favBtn.setEnabled(true);
                                                    Log.d(TAG, "onFailure: " + e);
                                                }
                                            });
                                }
                            });
                }
            }
        });

        holder.favBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isFavChecked = isChecked;
            }
        });


        holder.questionimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + url);
                Intent i = new Intent(context.getApplicationContext(), ViewPic.class);
                i.putExtra("photourl", q.getImage());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });

        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.likeBtn.setEnabled(false);
                if (q.getCheckLike()) {
                    Log.d(TAG, "onCheckedChanged: checked");
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("checkLike", false);
                    map.put("likes", Long.parseLong(q.getLikes()) - 1 + "");
                    usersLikedBy = "";
                    for (String userId : likedByUsersArr) {
                        if (userId.matches(sh.getsUser().getUid()))
                            continue;
                        else
                            usersLikedBy += userId;

                    }
                    map.put("likedByUser", usersLikedBy);
                    Log.d(TAG, "onClick: like decreased. " + map);
                    mFirestore.collection("questions").document(q.getID()).update(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "onSuccess: questions liked success");
                                    holder.likeBtn.setEnabled(true);
                                    q.setLikes((Long.parseLong(q.getLikes()) - 1) + "");
                                    q.setLikedByUser(usersLikedBy);
                                    q.setCheckLike(false);
                                    QuestionsAdapter.this.notifyDataSetChanged();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    holder.likeBtn.setEnabled(true);
                                    Log.d(TAG, "onFailure: exception " + e);
                                }
                            });

                    mFirestore.collection("favorite")
                            .whereEqualTo("userId", SharedPrefManager.getInstance(context).getsUser().getUid())
                            .whereEqualTo("id", q.getID()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && !task.getResult().getDocuments().isEmpty()) {
                                        mFirestore.collection("favorite").document(task.getResult().getDocuments().get(0).getId() + "")
                                                .update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Log.d(TAG, "onSuccess: questions favorite success");
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d(TAG, "onFailure: " + e);
                                                    }
                                                });
                                    }
                                }
                            });
                } else {
                    Log.d(TAG, "onCheckedChanged: not checked");
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("checkLike", true);
                    map.put("likes", (Long.parseLong(q.getLikes()) + 1) + "");
                    usersLikedBy = q.getLikedByUser() + "," + sh.getsUser().getUid();
                    map.put("likedByUser", usersLikedBy);
                    Log.d(TAG, "onClick: liked map " + usersLikedBy);
                    mFirestore.collection("questions").document(q.getID()).update(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    holder.likeBtn.setEnabled(true);
                                    Log.d(TAG, "onSuccess: liked increased success");
                                    q.setLikes(Long.parseLong(q.getLikes()) + 1 + "");
                                    q.setLikedByUser(usersLikedBy);
                                    q.setCheckLike(true);
                                    QuestionsAdapter.this.notifyDataSetChanged();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    holder.likeBtn.setEnabled(true);
                                    Log.d(TAG, "onFailure: exception " + e);
                                }
                            });

                    mFirestore.collection("favorite")
                            .whereEqualTo("userId", SharedPrefManager.getInstance(context).getsUser().getUid())
                            .whereEqualTo("id", q.getID()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && !task.getResult().getDocuments().isEmpty()) {
                                        mFirestore.collection("favorite").document(task.getResult().getDocuments().get(0).getId() + "")
                                                .update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Log.d(TAG, "onSuccess: questions favorite success");
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d(TAG, "onFailure: " + e);
                                                    }
                                                });
                                    }
                                }
                            });
                }
            }
        });

        holder.likeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            }
        });

        Glide.with(context).load(q.getUserPicUrl()).dontAnimate().centerCrop().placeholder(R.drawable.profile).fitCenter().into((holder).pro);


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

        CircleImageView pro;
        ImageView questionimg, shareBtn, answers;
        TextView Title, Desc, Author, totalAns, totalLike, portalTV;
        SimpleExoPlayerView simpleExoPlayerView;
        CheckBox favBtn, likeBtn;
        CardView openQues;
        Button playBtn;


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
            portalTV = itemView.findViewById(R.id.portalTV);
            playBtn = itemView.findViewById(R.id.playBtn);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        Log.d(TAG, "onDetachedFromRecyclerView: detached");
    }
}

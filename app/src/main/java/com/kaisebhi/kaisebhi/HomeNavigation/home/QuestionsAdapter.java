package com.kaisebhi.kaisebhi.HomeNavigation.home;

import static com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient.BASE_URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kaisebhi.kaisebhi.AnswersActivity;
import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;
import com.kaisebhi.kaisebhi.ViewPic;
import com.kaisebhi.kaisebhi.room.RoomDb;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {


    private List<QuestionsModel> nlist;
    private Context context;
    private FirebaseFirestore mFirestore;
    private String TAG = "QuestionsAdapter.java", comeFrom = "";
    private boolean isFavChecked = true;
    public RoomDb roomDb;

    public QuestionsAdapter(List<QuestionsModel> nlist, Context context, FirebaseFirestore firestore, RoomDb roomDb) {
        this.nlist = nlist;
        this.context = context;
        this.mFirestore = firestore;
        this.roomDb = roomDb;
    }

    public QuestionsAdapter(List<QuestionsModel> nlist, Context context, FirebaseFirestore firestore, String comeFrom, RoomDb roomDb) {
        this.nlist = nlist;
        this.context = context;
        this.mFirestore = firestore;
        this.comeFrom = comeFrom;
        this.roomDb = roomDb;
    }

    @NonNull
    @Override
    public QuestionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modal_questions, parent, false);
        return new QuestionsAdapter.ViewHolder(view);

    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        QuestionsModel q = nlist.get(position);
        holder.Title.setText(nlist.get(position).getTitle());
        holder.Desc.setText(nlist.get(position).getDesc());
        holder.Author.setText("By " + nlist.get(position).getUname());

        if (!nlist.get(position).getTansers().equals("0")) {
            holder.totalAns.setText(nlist.get(position).getTansers());
        }

        if (!nlist.get(position).getLikes().equals("0")) {
            holder.totalLike.setText(nlist.get(position).getLikes());
        }

        final String Id = nlist.get(position).getID();

        SharedPrefManager sh = new SharedPrefManager(context);
        final String uid = sh.getsUser().getUid();

        if (comeFrom.matches("home")) {
            mFirestore.collection("favorite").whereEqualTo("id", q.getID()).whereEqualTo("userId", SharedPrefManager.getInstance(context).getsUser().getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: success ");
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

        holder.likeBtn.setChecked(nlist.get(position).getCheckLike());

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
                i.putExtra("userpic", nlist.get(position).getUpro());
                i.putExtra("desc", nlist.get(position).getDesc());
                i.putExtra("qimg", nlist.get(position).getQpic());
                i.putExtra("tans", nlist.get(position).getTansers());
                i.putExtra("tlikes", nlist.get(position).getCheckLike());
                i.putExtra("likes", nlist.get(position).getLikes());
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

        holder.favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    mFirestore.collection("favorite").add(questionMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "onSuccess: success" + documentReference);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
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
                                                        Log.d(TAG, "onSuccess: success");
                                                        roomDb.getFavDao().deleteFav(q);
                                                        q.setCheckFav(false);
                                                        nlist.remove(position);
                                                        QuestionsAdapter.this.notifyDataSetChanged();
                                                    } catch (Exception e) {
                                                        Log.d(TAG, "onSuccess: " + e);
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
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
                Intent i = new Intent(context.getApplicationContext(), ViewPic.class);
                i.putExtra("photourl", nlist.get(position).getQpic());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });


        holder.likeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                HashMap<String, Object> map = new HashMap<>();
                map.put("checkLike", true);
                map.put("likes", (Long.parseLong(q.getLikes()) + 1) + "");
                mFirestore.collection("questions").document(q.getID()).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e);
                    }
                });

            }
        });


        if (nlist.get(position).getQpic().length() > 0) {
            holder.questionimg.setVisibility(View.VISIBLE);
            Glide.with(context).load(BASE_URL + "qimg/" + nlist.get(position).getQpic()).fitCenter().into((holder).questionimg);
        }

        Glide.with(context).load(BASE_URL + "user/" + nlist.get(position).getUpro()).dontAnimate().centerCrop().placeholder(R.drawable.profile).fitCenter().into((holder).pro);


    }


    @Override
    public int getItemCount() {
        return nlist.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView pro;
        ImageView questionimg, shareBtn, answers;
        TextView Title, Desc, Author, totalAns, totalLike;
        CheckBox favBtn, likeBtn;
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

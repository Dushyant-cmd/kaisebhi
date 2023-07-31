package com.kaisebhi.kaisebhi.HomeNavigation.home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.Utility.DefaultResponse;
import com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient;
import com.kaisebhi.kaisebhi.Utility.PaymentActivity;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient.BASE_URL;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.ViewHolder> {

    int amount = 0;
    private List<AnswersModel> nlist;
    private String TAG = "AnswersAdapter.java";
    private Context context;
    String qid;
    private Dialog myDialog;

    FirebaseFirestore firestore;
    private String likedByUser = "";
    public AnswersAdapter(List<AnswersModel> nlist, Context context) {
        this.nlist = nlist;
        this.context = context;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public AnswersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modal_answers, parent, false);
        return new AnswersAdapter.ViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        AnswersModel dataObj = nlist.get(position);
        holder.Answer.setText(nlist.get(position).getTansers());
        try {
            if (!nlist.get(position).getUname().isEmpty()) {
                holder.Author.setText(nlist.get(position).getUname());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        final String Id = nlist.get(position).getID();
        SharedPrefManager sh = new SharedPrefManager(context);
        final String uid = sh.getsUser().getUid();

        //likes logic
        List<String> likedBy = Arrays.asList(dataObj.getLikedBy().split(","));
        if (likedBy.contains(uid)) {
            holder.likeBtn.setChecked(true);
            Log.d(TAG, "onBindViewHolder: liked");
        } else {
            Log.d(TAG, "onBindViewHolder: not liked");
            holder.likeBtn.setChecked(false);
        }

        //report logic
        List<String> reportByUsers = Arrays.asList(dataObj.getReportBy().split(","));
        if (reportByUsers.contains(uid)) {
            if (nlist.get(position).isUserReport()) {
                holder.Report.setClickable(false);
                holder.Report.setText("Reported");
            }
        } else {
            holder.Report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.Report.setText("Wait...");
                    holder.Report.setClickable(false);
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    Map<String, Object> reportMap = new HashMap<>();
                    reportMap.put("userReportCheck", true);
                    reportMap.put("reportBy", dataObj.getReportBy() + "," + uid);
                    firestore.collection("answers").document(dataObj.getAnswerDocId()).update(reportMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        holder.Report.setText("Thanks!");
                                        Toast.makeText(context, "Reported Abuse to this Answer!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.d(TAG, "onComplete: " + task.getException());
                                    }
                                }
                            });
//                    Call<DefaultResponse> call =  RetrofitClient.getInstance().getApi().reportAbuse(Id,uid);
//                    call.enqueue(new Callback<DefaultResponse>() {
//                        @Override
//                        public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
//                            DefaultResponse dr = response.body();
//                            holder.Report.setText("Thanks!");
//                            Toast.makeText(context,"Reported Abuse to this Answer!",Toast.LENGTH_SHORT).show();
//                        }
//                        @Override
//                        public void onFailure(Call<DefaultResponse> call, Throwable t) {
//                        }
//                    });
                }
            });
        }

//        holder.likeBtn.setChecked(nlist.get(position).getCheckLike());

        if (nlist.get(position).getCheckOwnQuestion()) {
            holder.hideAns.setVisibility(View.VISIBLE);
        }
        if (dataObj.getUserId().matches(uid)) {
            holder.Report.setVisibility(View.GONE);
        }
        if (nlist.get(position).checkHideAnswer()) {

            if (!nlist.get(position).isSelfAnswer()) {
                holder.ansHideBox.setVisibility(View.VISIBLE);
            }

            holder.hideAns.setVisibility(View.GONE);
            if (nlist.get(position).isSelfHideAnswer()) {
                holder.Report.setText("Hide by You!");
                holder.Report.setClickable(false);
                holder.ansHideBox.setVisibility(View.GONE);
            }
        }
        //CONDITION USER OWN ANSWER & ALL HIDE

        if (nlist.get(position).isCheckPaid()) {
            holder.Report.setClickable(false);
            holder.Report.setText("Answer Paid by You!");
            holder.ansHideBox.setVisibility(View.GONE);
            //holder.payBtnHideAns.setText("Already paid!");
        }
        //ANSWER LIKE AND HIDE ANSWER BY QUESTION OWNER
        holder.likeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Map<String, Object> map = new HashMap<>();
                likedByUser = "";
                if (isChecked) {
                    Log.d(TAG, "onCheckedChanged: checked");
                    map.put("likedBy", dataObj.getLikedBy() + "," + uid);
                    map.put("likes", String.valueOf(Long.parseLong(dataObj.getLikes()) + 1));
                    firestore.collection("answers").document(dataObj.getAnswerDocId()).update(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        dataObj.setLikedBy(dataObj.getLikedBy() + "," + uid);
                                        dataObj.setLikes(String.valueOf(Long.parseLong(dataObj.getLikes()) + 1));
                                        Log.d(TAG, "onComplete: answer liked");
                                    } else {
                                        Log.d(TAG, "onComplete: " + task.getException());
                                    }
                                }
                            });
                } else {
                    Log.d(TAG, "onCheckedChanged: not checked");
                    for (String userId : likedBy) {
                        if (userId.matches(uid))
                            continue;
                        likedByUser += "," + userId;
                    }
                    map.put("likedBy", likedByUser);
                    map.put("likes", String.valueOf(Long.parseLong(dataObj.getLikes()) - 1));
                    firestore.collection("answers").document(dataObj.getAnswerDocId()).update(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        dataObj.setLikedBy(likedByUser);
                                        dataObj.setLikes(String.valueOf(Long.parseLong(dataObj.getLikes()) - 1));
                                        Log.d(TAG, "onComplete: answer not liked");
                                    } else {
                                        Log.d(TAG, "onComplete: " + task.getException());
                                    }
                                }
                            });
                }
            }
        });

        holder.hideAns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog = new Dialog(v.getContext());
                myDialog.setContentView(R.layout.hide_answer_model);
                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                LinearLayout e1, e2, e3, e4, e5;
                Button payBtn;
                final TextView test;
                test = myDialog.findViewById(R.id.payBtn);
                e1 = myDialog.findViewById(R.id.em1);
                e2 = myDialog.findViewById(R.id.em2);
                e3 = myDialog.findViewById(R.id.em3);
                e4 = myDialog.findViewById(R.id.em4);
                e5 = myDialog.findViewById(R.id.em5);

                e1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        amount = 2;
                        test.setText("Pay for Hide Answer- \u20B92");
                    }
                });
                e2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        amount = 5;
                        test.setText("Pay for Hide Answer- \u20B95");
                    }
                });
                e3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        amount = 10;
                        test.setText("Pay for Hide Answer- \u20B910");
                    }
                });
                e4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        amount = 15;
                        test.setText("Pay for Hide Answer- \u20B915");
                    }
                });
                e5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        amount = 20;
                        test.setText("Pay for Hide Answer- \u20B920");
                    }
                });

                test.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (amount != 0) {
                            Intent i = new Intent(context, PaymentActivity.class);
                            i.putExtra("oamount", String.valueOf(amount));
                            i.putExtra("qid", Id);
                            i.putExtra("payType", "hide");
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                        } else {
                            Toast.makeText(context, "Please select your Expeirence!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                myDialog.show();

            }
        });


        holder.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, PaymentActivity.class);
                i.putExtra("oamount", nlist.get(position).getPaidAmount());
                i.putExtra("qid", Id);
                i.putExtra("payType", "show");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });

        Glide.with(context).load(dataObj.getUpro()).dontAnimate().centerCrop().placeholder(R.drawable.profile).fitCenter().into((holder).pro);

    }


    @Override
    public int getItemCount() {
        return nlist.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView pro;
        TextView Answer, Author, Report, ansUser, hideAns;
        CheckBox likeBtn;
        FrameLayout ansHideBox;
        Button btnPay;
        Button payBtnHideAns;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pro = itemView.findViewById(R.id.ansPro);
            hideAns = itemView.findViewById(R.id.hideClick);
            btnPay = itemView.findViewById(R.id.payBtnHideAns);
            Answer = itemView.findViewById(R.id.userAnswer);
            ansUser = itemView.findViewById(R.id.ansTest);
            likeBtn = itemView.findViewById(R.id.like);
            Author = itemView.findViewById(R.id.ansUser);
            Report = itemView.findViewById(R.id.reportAb);
            payBtnHideAns = itemView.findViewById(R.id.payBtnHideAns);
            ansHideBox = itemView.findViewById(R.id.ansHideBox);


        }

    }


}

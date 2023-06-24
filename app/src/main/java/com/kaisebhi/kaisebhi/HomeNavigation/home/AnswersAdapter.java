package com.kaisebhi.kaisebhi.HomeNavigation.home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.Utility.DefaultResponse;
import com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient;
import com.kaisebhi.kaisebhi.Utility.PaymentActivity;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient.BASE_URL;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.ViewHolder>{

    int amount = 0;
    private List<AnswersModel> nlist;
    private Context context;
    String qid;
    private Dialog myDialog;

    public AnswersAdapter(List<AnswersModel> nlist, Context context)
    {
        this.nlist = nlist;
        this.context = context;
    }

    @NonNull
    @Override
    public AnswersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modal_answers,parent,false);
        return new AnswersAdapter.ViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.Answer.setText(nlist.get(position).getTansers());
        try {
            if(!nlist.get(position).getUname().isEmpty()){
                holder.Author.setText(nlist.get(position).getUname());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        final String Id = nlist.get(position).getID();
        SharedPrefManager sh = new SharedPrefManager(context);
        final String uid = sh.getsUser().getUid();

        if(nlist.get(position).isUserReport()){
            holder.Report.setClickable(false);
            holder.Report.setText("Reported");
        }
        else{
            holder.Report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.Report.setText("Wait...");
                    holder.Report.setClickable(false);
                    Call<DefaultResponse> call =  RetrofitClient.getInstance().getApi().reportAbuse(Id,uid);
                    call.enqueue(new Callback<DefaultResponse>() {
                        @Override
                        public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                            DefaultResponse dr = response.body();
                            holder.Report.setText("Thanks!");
                            Toast.makeText(context,"Reported Abuse to this Answer!",Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(Call<DefaultResponse> call, Throwable t) {
                        }
                    });
                }
            });
        }

        holder.likeBtn.setChecked(nlist.get(position).getCheckLike());

        if(nlist.get(position).getCheckOwnQuestion()){
            holder.hideAns.setVisibility(View.VISIBLE);
        }
        if(nlist.get(position).isSelfAnswer()){
            holder.Report.setVisibility(View.GONE);
        }
        if(nlist.get(position).checkHideAnswer()){

            if(!nlist.get(position).isSelfAnswer()){
                holder.ansHideBox.setVisibility(View.VISIBLE);
            }

            holder.hideAns.setVisibility(View.GONE);
            if(nlist.get(position).isSelfHideAnswer()){
                holder.Report.setText("Hide by You!");
                holder.Report.setClickable(false);
                holder.ansHideBox.setVisibility(View.GONE);
            }
        }
        //CONDITION USER OWN ANSWER & ALL HIDE



        if(nlist.get(position).isCheckPaid()){
            holder.Report.setClickable(false);
            holder.Report.setText("Answer Paid by You!");
            holder.ansHideBox.setVisibility(View.GONE);
            //holder.payBtnHideAns.setText("Already paid!");
        }
        //ANSWER LIKE AND HIDE ANSWER BY QUESTION OWNER

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







        holder.hideAns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog = new Dialog(v.getContext());
                myDialog.setContentView(R.layout.hide_answer_model);
                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                LinearLayout e1,e2,e3,e4,e5;
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
                        if(amount != 0){
                            Intent i = new Intent(context, PaymentActivity.class);
                            i.putExtra("oamount", String.valueOf(amount));
                            i.putExtra("qid", Id);
                            i.putExtra("payType", "hide");
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                        }else {
                            Toast.makeText(context,"Please select your Expeirence!",Toast.LENGTH_SHORT).show();
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
                i.putExtra("oamount",nlist.get(position).getPaidAmount());
                i.putExtra("qid", Id);
                i.putExtra("payType", "show");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });

        Glide.with(context).load(BASE_URL +"user/"+nlist.get(position).getUpro()).dontAnimate().centerCrop().placeholder(R.drawable.profile).fitCenter().into((holder).pro);

    }


    @Override
    public int getItemCount() {
        return nlist.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView pro;
        TextView Answer,Author,Report,ansUser,hideAns;
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

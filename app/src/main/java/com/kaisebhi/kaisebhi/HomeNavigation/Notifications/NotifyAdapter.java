package com.kaisebhi.kaisebhi.HomeNavigation.Notifications;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.kaisebhi.kaisebhi.R;

import java.util.ArrayList;
import java.util.List;

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.ViewHolder>{


    private List<NotifyViewModel> nlist = new ArrayList<>();
    private Context context;

    public NotifyAdapter(List<NotifyViewModel> nlist, Context context)
    {
        this.nlist = nlist;
        this.context = context;
    }

    @NonNull
    @Override
    public NotifyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modal_notificaitons,parent,false);
            return new NotifyAdapter.ViewHolder(view);

    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.Title.setText(nlist.get(position).getTitle());
        holder.Content.setText(nlist.get(position).getMsg());
        final String Id = nlist.get(position).getID();

        if(nlist.get(position).getThumbnil().equals("1"))
        { holder.Img.setImageResource(R.drawable.check); }
        else if(nlist.get(position).getThumbnil().equals("0"))
        { holder.Img.setImageResource(R.drawable.cross);    }


        /*holder.nCLose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nlist.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, nlist.size());

                Call<DefaultResponse> call =  RetrofitClient.getInstance().getApi().clearNotify(Id);
                call.enqueue(new Callback<DefaultResponse>() {
                    @Override
                    public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                        DefaultResponse dr = response.body();
                        if(response.code() == 201) {
                            String data = dr.getMessage();
                            Toast.makeText(context,data, Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<DefaultResponse> call, Throwable t) {

                    }

                });

                }

        });*/

    }


    @Override
    public int getItemCount() {
            return nlist.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView Img,nCLose;
        TextView Title,Content;

       public ViewHolder(@NonNull View itemView) {
            super(itemView);

             Img = itemView.findViewById(R.id.n_icon);
             Content = itemView.findViewById(R.id.n_msg);
             Title =  itemView.findViewById(R.id.n_title);
             //nCLose = itemView.findViewById(R.id.n_close);

       }

    }




}

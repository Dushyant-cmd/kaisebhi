package com.kaisebhi.kaisebhi.HomeNavigation.home;

import static com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient.BASE_URL;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;
import com.kaisebhi.kaisebhi.databinding.PlayerSheetLayoutBinding;

import java.util.List;

public class MineAnswersAdapter extends RecyclerView.Adapter<MineAnswersAdapter.ViewHolder> {

    int amount = 0;

    private List<AnswersModel> nlist;
    private static Context context;
    String qid;

    private Dialog myDialog;
    private FragmentManager fm;

    public MineAnswersAdapter(List<AnswersModel> nlist, Context context, FragmentManager fm) {
        this.nlist = nlist;
        this.context = context;
        this.fm = fm;
    }

    @NonNull
    @Override
    public MineAnswersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modal_mine_answers, parent, false);
        return new MineAnswersAdapter.ViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        AnswersModel model = nlist.get(position);
        holder.Answer.setText(nlist.get(position).getTansers());
        holder.Author.setText(nlist.get(position).getUname());
        holder.Question.setText(nlist.get(position).getQuestionsTitle());
        final String Id = nlist.get(position).getID();
        SharedPrefManager sh = new SharedPrefManager(context);
        final String uid = sh.getsUser().getUid();
        holder.Desc.setText(nlist.get(position).getDesc());

        if (nlist.get(position).getQimg().length() > 0) {
            holder.qImg.setVisibility(View.VISIBLE);
            Glide.with(context).load(nlist.get(position).getQimg()).fitCenter().into((holder).qImg);
        } else
            holder.qImg.setVisibility(View.VISIBLE);


        Glide.with(context).load(BASE_URL + nlist.get(position).getUpro()).placeholder(R.drawable.profile).fitCenter().into((holder).pro);

        //ExoPlayer setup and play when ready with controls and seekbar.
        if(!model.getAudioUrl().isEmpty()) {
            holder.playAudioBtn.setVisibility(View.VISIBLE);
            holder.playAudioBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MineQuestionsAdapter.PlayerBottomSheet playerBottomSheet = new MineQuestionsAdapter.PlayerBottomSheet(model.getAudioUrl());
                    playerBottomSheet.show(fm, "PlayerView");
                }
            });
        } else
            holder.playAudioBtn.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return nlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView pro, qImg;
        TextView Answer, Author, Question, Desc, playAudioBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pro = itemView.findViewById(R.id.ansPro);
            Answer = itemView.findViewById(R.id.userAnswer);
            Question = itemView.findViewById(R.id.quesTitle);
            Desc = itemView.findViewById(R.id.hide_desc);
            qImg = itemView.findViewById(R.id.hide_quesImage);
            Author = itemView.findViewById(R.id.ansUser);
            playAudioBtn = itemView.findViewById(R.id.playBtn);

        }

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

}

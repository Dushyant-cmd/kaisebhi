package com.kaisebhi.kaisebhi.HomeNavigation.home;

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
import com.kaisebhi.kaisebhi.databinding.PlayerSheetLayoutBinding;

import java.util.List;

public class HideAnswersAdapter extends RecyclerView.Adapter<HideAnswersAdapter.ViewHolder> {

    int amount = 0;

    private List<HideAnswersModel> nlist;
    private static Context context;
    String qid;
    private Dialog myDialog;
    private FragmentManager fm;

    public HideAnswersAdapter(List<HideAnswersModel> nlist, Context context, FragmentManager fm) {
        this.nlist = nlist;
        this.context = context;
        this.fm = fm;
    }

    @NonNull
    @Override
    public HideAnswersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modal_hide_ans, parent, false);
        return new HideAnswersAdapter.ViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        HideAnswersModel model = nlist.get(position);
        holder.Answer.setText(nlist.get(position).getAns());
        if (!nlist.get(position).getAuthor().isEmpty()) {
            holder.author.append(nlist.get(position).getAuthor());
        }
        holder.Question.setText(nlist.get(position).getQues());
        holder.Desc.setText(nlist.get(position).getDesc());

        if (!nlist.get(position).getThumb().equals("default")) {
            holder.qImg.setVisibility(View.VISIBLE);
            Glide.with(context).load(nlist.get(position).getQimg()).fitCenter().into((holder).qImg);
        }

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

        ImageView qImg;
        TextView Answer, Desc, Question, author, playAudioBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            qImg = itemView.findViewById(R.id.hide_quesImage);
            Answer = itemView.findViewById(R.id.hide_ans);
            Question = itemView.findViewById(R.id.hide_qus);
            Desc = itemView.findViewById(R.id.hide_desc);
            author = itemView.findViewById(R.id.hide_au);
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

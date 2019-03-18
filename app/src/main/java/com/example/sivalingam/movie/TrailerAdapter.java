package com.example.sivalingam.movie;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    //list of video objects
    private List<Video> videoList;

    private int trailerNum = 0;
    private int teaserNum = 0;
    private int clipNum = 0;
    private int featuretteNum = 0;

    private final String TRAILER = "Trailer";
    private final String TEASER = "Teaser";
    private final String CLIP = "Clip";
    private final String FEATURETTE = "Featurette";

    private final TrailerAdapterOnClickHandler handler;

    public interface TrailerAdapterOnClickHandler{
        void onClick(int id);
    }

    public TrailerAdapter(TrailerAdapterOnClickHandler handler){
        this.handler = handler;
    }

    public void setVideoList(List<Video> videoList){
        this.videoList = videoList;
        notifyDataSetChanged();
    }

    /**
     *
     * @param viewGroup
     * @param i
     * @return TrailerAdapterViewHolder
     *
     * This overriden method is used to create a new view or recycle an existing view
     */
    @NonNull
    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_video;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new TrailerAdapterViewHolder(view);
    }

    /**
     *
     * @param trailerAdapterViewHolder
     * @param i
     *
     * This overriden method is used to bind the textview
     */
    @Override
    public void onBindViewHolder(@NonNull TrailerAdapterViewHolder trailerAdapterViewHolder, int i) {

        //If the videoList is not null
        if(videoList != null){

            String play;

            if(videoList.get(i).getType().equals(TRAILER)){
                trailerNum++;
                play = "Play " + videoList.get(i).getType() + " " + String.valueOf(trailerNum);
            } else if(videoList.get(i).getType().equals(TEASER)){
                teaserNum++;
                play = "Play " + videoList.get(i).getType() + " " + String.valueOf(teaserNum);
            } else if(videoList.get(i).getType().equals(CLIP)){
                clipNum++;
                play = "Play " + videoList.get(i).getType() + " " + String.valueOf(clipNum);
            } else {
                featuretteNum++;
                play = "Play " + videoList.get(i).getType() + " " + String.valueOf(featuretteNum);
            }

            trailerAdapterViewHolder.mTextView.setText(play);
        }
    }

    /**
     *
     * @return int
     *
     * This method size of videoList
     */
    @Override
    public int getItemCount() {
        if(videoList != null)
            return videoList.size();
        else
            return 0;
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView mTextView;

        public TrailerAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.trailerId);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            handler.onClick(adapterPosition);
        }
    }
}

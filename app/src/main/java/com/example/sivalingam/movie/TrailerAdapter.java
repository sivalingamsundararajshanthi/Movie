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

/**
 * This is the adapter for the trailer recycler view. This adapter also has an inner class which extends the view holder class.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    //list of video objects
    private List<Video> videoList;

    //handler for the particular view
    private final TrailerAdapterOnClickHandler handler;

    /**
     * Interface to handle click handling
     */
    public interface TrailerAdapterOnClickHandler{
        void onClick(int id);
    }

    /**
     *
     * @param handler
     *
     * This is the constructor for TrailerAdapter and it sets the onClick handler
     */
    public TrailerAdapter(TrailerAdapterOnClickHandler handler){
        this.handler = handler;
    }

    /**
     *
     * @param videoList
     *
     * This method is used to set the video list and tell the adapter that the data has changed
     */
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

            String play = "Play ";

            trailerAdapterViewHolder.mTextView.setText(String.format("%s%s", play, videoList.get(i).getType()));
        }
    }

    /**
     *
     * @return int
     *
     * This method returns the size of videoList
     */
    @Override
    public int getItemCount() {
        if(videoList != null)
            return videoList.size();
        else
            return 0;
    }

    /**
     * This inner class extends from the view holder class and is used to reference the layout item.
     */
    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //The text view in the layout
        private final TextView mTextView;

        //This constructor is used to refer the text view from the layout
        public TrailerAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            //Refer the textview from the layout
            mTextView = itemView.findViewById(R.id.trailerId);
            itemView.setOnClickListener(this);
        }

        /**
         *
         * @param v
         *
         * This overriden method is used to get video url from the clicked video object.
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            handler.onClick(adapterPosition);
        }
    }
}

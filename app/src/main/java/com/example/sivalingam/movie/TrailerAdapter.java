package com.example.sivalingam.movie;

import android.content.Context;
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

    public TrailerAdapter(){

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
            trailerAdapterViewHolder.mTextView.setText(videoList.get(i).getType());
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

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextView;

        public TrailerAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.trailerId);
        }
    }
}

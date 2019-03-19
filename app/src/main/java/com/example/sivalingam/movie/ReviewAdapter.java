package com.example.sivalingam.movie;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * This is the adapter for the review recycler view. This adapter also has an inner class which extends the view holder class.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    //list of review objects
    private List<Reviews> reviewsList;

    //handler for the particular view
    private final ReviewAdapterOnClickListener handler;

    /**
     * Interface to handle click handling
     */
    public interface ReviewAdapterOnClickListener{
        void onClick(int id);
    }

    /**
     *
     * @param handler
     *
     * This is the constructor is used to set the on click handler
     */
    public ReviewAdapter(ReviewAdapterOnClickListener handler){
        this.handler = handler;
    }

    /**
     *
     * @param viewGroup
     * @param i
     * @return
     *
     * This overriden method is used to create a new view or recycle an old view
     */
    @NonNull
    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_review;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ReviewAdapterViewHolder(view);
    }

    /**
     *
     * @param reviewAdapterViewHolder
     * @param i
     *
     * This overriden method is used to bind the textviews with data.
     */
    @Override
    public void onBindViewHolder(@NonNull ReviewAdapterViewHolder reviewAdapterViewHolder, int i) {
        if(reviewsList != null){
            reviewAdapterViewHolder.mName.setText(reviewsList.get(i).getAuthor());
            reviewAdapterViewHolder.mReview.setText(reviewsList.get(i).getContent());
        }
    }

    /**
     *
     * @return
     *
     * This overriden method is used to get the size of reviews list
     */
    @Override
    public int getItemCount() {
        if(reviewsList != null)
            return reviewsList.size();
        else
            return 0;
    }

    /**
     *
     * @param reviewsList
     *
     * This method is used to set the list of review objects
     */
    public void setReviewsList(List<Reviews> reviewsList){
        this.reviewsList = reviewsList;
        notifyDataSetChanged();
    }

    /**
     * This inner class extends from the view holder class and is ised to reference the layout item
     */
    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //The textviews in the layout
        private final TextView mName, mReview;

        //This constructor is used to refer the textviews from the layout
        public ReviewAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            //Refer the textviews from the layout
            mName = itemView.findViewById(R.id.review_name_id);
            mReview = itemView.findViewById(R.id.review_text_id);
            itemView.setOnClickListener(this);
        }

        /**
         *
         * @param v
         *
         * This overriden method is used to get the review URL from the review object.
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            handler.onClick(adapterPosition);
        }
    }
}

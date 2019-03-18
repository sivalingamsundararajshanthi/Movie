package com.example.sivalingam.movie;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private List<Reviews> reviewsList;

    private final ReviewAdapterOnClickListener handler;

    public interface ReviewAdapterOnClickListener{
        void onClick(int id);
    }

    public ReviewAdapter(ReviewAdapterOnClickListener handler){
        this.handler = handler;
    }

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

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapterViewHolder reviewAdapterViewHolder, int i) {
        if(reviewsList != null){
            reviewAdapterViewHolder.mName.setText(reviewsList.get(i).getAuthor());
            reviewAdapterViewHolder.mReview.setText(reviewsList.get(i).getContent());
        }
    }

    @Override
    public int getItemCount() {
        if(reviewsList != null)
            return reviewsList.size();
        else
            return 0;
    }

    public void setReviewsList(List<Reviews> reviewsList){
        this.reviewsList = reviewsList;
        notifyDataSetChanged();
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView mName, mReview;

        public ReviewAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.review_name_id);
            mReview = itemView.findViewById(R.id.review_text_id);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            handler.onClick(adapterPosition);
        }
    }
}

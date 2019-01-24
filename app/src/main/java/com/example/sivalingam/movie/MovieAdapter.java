package com.example.sivalingam.movie;

/**
 * This is the adapter for the recycler view. This adapter also has an inner class which extends the view holder class.
 */

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    //List which has the list of Movie objects
    private List<Movie> movieList;

    //handler for the particular view
    private final MovieAdapterOnClickHandler handler;

    /**
     * Interface to implement Click handling
     */
    public interface MovieAdapterOnClickHandler {
        void onClick(int id);
    }

    /**
     *
     * @param movieList
     *
     * This constructor is used to set the movieList, also to refresh the recycler view when the data set changes
     * and also sets the onClick handler
     */
    public MovieAdapter(List<Movie> movieList, MovieAdapterOnClickHandler handler){
        this.handler = handler;
        this.movieList = movieList;
        notifyDataSetChanged();
    }

    /**
     *
     * @param viewGroup
     * @param i
     * @return
     *
     * This overriden method is used to create a new view or recycle an existing view
     */
    @NonNull
    @Override
    public MovieAdapter.MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.image_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        //Setting the height of a row in recycler view
        int height = viewGroup.getMeasuredHeight()/2;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);

        //Setting the height of a row in recycler view
        view.setMinimumHeight(height);
        return new MovieAdapterViewHolder(view);
    }

    /**
     *
     * @param movieAdaptherViewHolder
     * @param i
     *
     * This is used to bind the view with data.
     */
    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.MovieAdapterViewHolder movieAdaptherViewHolder, int i) {

        //If movieList is not null
        if(this.movieList != null){

            //Get the poster_path from the object
            String url = movieList.get(i).getPoster_path();

            //Parse it into an URI with parameters
            //The URL to fetch the image
            String movieUrl = "http://image.tmdb.org/t/p/w185";
            Uri uri = Uri.parse(movieUrl).buildUpon()
                    .appendEncodedPath(url)
                    .build();

            //Pass it to Picassso to fetch the image from the URL and set the image button
            Picasso.get().load(uri).into(movieAdaptherViewHolder.mImageButton);
        }
    }

    /**
     *
     * @return
     *
     * This overriden function is used to return the size of movieList if it's not null
     */
    @Override
    public int getItemCount() {
        if(this.movieList != null){
            return this.movieList.size();
        }
        else
            return 0;
    }

    /**
     * This inner class extends from the view holder class and is used to reference the layout item.
     */
    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //The image button in the layout
        private final ImageButton mImageButton;

        //This constructor is used to refer the image button from the layout
        private MovieAdapterViewHolder(@NonNull View itemView) {
            //Passing the view to the super constructor
            super(itemView);

            //Refer the image button from the layout
            mImageButton = itemView.findViewById(R.id.image_btn_id);
            itemView.setOnClickListener(this);
        }

        /**
         *
         * @param v
         *
         * The overriden method is used to handle the item click. This gets the Movie object from the particular
         * position and returns it. From this the id can be obtained for a movie.
         *
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            handler.onClick(adapterPosition);
        }
    }
}

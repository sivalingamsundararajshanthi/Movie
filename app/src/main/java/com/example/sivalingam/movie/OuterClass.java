package com.example.sivalingam.movie;

/**
 * This class has the page number, total results, total pages and movie list
 */

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OuterClass {
    @SerializedName("page")
    private int page;

    @SerializedName("total_results")
    private int total_results;

    @SerializedName("total_pages")
    private int total_pages;

    @SerializedName("results")
    private List<Movie> movieList;

    public OuterClass(int page, int total_results, int total_pages, List<Movie> movieList) {
        this.page = page;
        this.total_results = total_results;
        this.total_pages = total_pages;
        this.movieList = movieList;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal_results() {
        return total_results;
    }

    public void setTotal_results(int total_results) {
        this.total_results = total_results;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public List<Movie> getMovieList() {
        return movieList;
    }

    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
    }
}


package com.example.sivalingam.movie;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OuterVideo {

    @SerializedName("id")
    private int id;

    @SerializedName("results")
    private List<Video> videoList;

    public OuterVideo(int id, List<Video> videoList) {
        this.id = id;
        this.videoList = videoList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Video> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<Video> videoList) {
        this.videoList = videoList;
    }
}

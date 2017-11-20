package com.example.sanghyunj.speckerapp.model;

import com.example.sanghyunj.speckerapp.retrofit.FeedData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rapsealk on 2017. 10. 14..
 */

public class FeedViewFactory {
    public static List<FeedData> createOpenProjects() {
        List<FeedData> openProjects = new ArrayList<>();

        /*
        openProjects.add(new OpenProjectModel("Loading Drawable", "Some beautiful android loading drawable", "#FF1B6070"));
        openProjects.add(new OpenProjectModel("Pull Zoom RecyclerView", "A pull to zoom-in RecyclerView", "#FF234458"));
        openProjects.add(new OpenProjectModel("Circle ProgressBar", "A circular android ProgressBar library", "#FF700957"));
        openProjects.add(new OpenProjectModel("Recycler Refresh Layout", "A pull to refresh layout", "#FF940044"));
        openProjects.add(new OpenProjectModel("Recycler ItemDecoration", "Some RecyclerView ItemDecorations", "#FF5B83ED"));
        openProjects.add(new OpenProjectModel("Efficient Framework", "Waiting", "#FFC47B3F"));
        */

        return openProjects;
    }
}

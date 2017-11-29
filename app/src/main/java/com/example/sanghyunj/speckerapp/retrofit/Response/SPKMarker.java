package com.example.sanghyunj.speckerapp.retrofit.Response;

/**
 * Created by rapsealk on 2017. 11. 30..
 */

public class SPKMarker {

    private String team;
    private String leader;
    private TeammateCount teammates;
    private MarkerPosition position;
    private Content content;
    private long timestamp;

    public String getTeam() {
        return team;
    }

    public String getLeader() {
        return leader;
    }

    public MarkerPosition getPosition() {
        return position;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTitle() {
        return content.getTitle();
    }

    public String getSnippet() {
        return content.getSnippet();
    }

    @Override
    public String toString() {
        return "{ team: " + team + ", leader: " + leader + ", position: { latitude: " + position.getLatitude() + ", longitude: " + position.getLongitude() + " } }";
    }
}

class TeammateCount {
    int max;
    int count;
    int male;
    int female;
}

class Content {

    private String title;
    private String snippet;

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }
}
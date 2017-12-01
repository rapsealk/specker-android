package com.example.sanghyunj.speckerapp.retrofit.Response;

import java.util.ArrayList;

/**
 * Created by GL552 on 2017-12-01.
 */

public class SearchTeamResponse {

    private String result;
    private ArrayList<Team> teams;

    public String getResult() {
        return result;
    }

    public ArrayList<Team> getTeams() {
        return teams;
    }
}

class Team {

    private String name;
    private String leader;
    private ArrayList<String> members;
    private String room;

    public String getName() {
        return name;
    }

    public String getLeader() {
        return leader;
    }

    public ArrayList<String> getMemebers() {
        return members;
    }

    public String getRoom() {
        return room;
    }
}

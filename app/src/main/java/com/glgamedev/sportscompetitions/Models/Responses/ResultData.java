package com.glgamedev.sportscompetitions.Models.Responses;

import com.glgamedev.sportscompetitions.Enums.League;

import java.util.ArrayList;

public class ResultData {
    public String eventName;

    public ArrayList<CompetitionResponse> competitions;

    public ArrayList<User> users;

    public User user;

    public ArrayList<TeamResponse> teamResults;

    public ArrayList<ChangeLogsResponse> changeLogs;

    public ArrayList<AdminInfoResponse> adminInfos;

    public int eventState;

    public class TeamResponse {
        public long id;

        public String name;

        public int league;

        public ArrayList<TeamResultResponse> results;

        public class TeamResultResponse {
            public long competitionId;

            public String total;

            public int league;

            public int place;

            public ArrayList<MemberResultResponse> memberResults;
        }

        public class MemberResultResponse {
            public long memberId;

            public String surname;

            public String name;

            public int login;

            public int age;

            public long teamId;

            public Result value;
        }
    }

    public class CompetitionResponse {
        public long id;

        public String name;

        public int stepsCount;

        public int type;
    }

    public class ChangeLogsResponse {
        public long competitionId;

        public long teamId;

        public long participantId;

        public String value;
    }

    public class Result {
        public ArrayList<String> steps;

        public String total;
    }

    public class User {
        public long id;

        public String surname;

        public String name;

        public int age;

        public int login;

        public long teamId;

        public int role;
    }

    public class AdminInfoResponse
    {
        public long adminId;

        public int login;

        public String surname;

        public String name;

        public int state;
    }
}

package com.glgamedev.sportscompetitions.Models.Requests;

import java.time.OffsetDateTime;
import java.util.ArrayList;

public class SetResultsData {
    public ArrayList<LocalResults> Data = new ArrayList<>();

    public long AdminId;

    public static class LocalResults {
        public long CompetitionId;

        public long UserId;

        public OffsetDateTime Moment;

        public ArrayList<String> Steps = new ArrayList<>();
    }
}

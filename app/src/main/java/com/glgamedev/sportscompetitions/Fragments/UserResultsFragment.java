package com.glgamedev.sportscompetitions.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.glgamedev.sportscompetitions.Activities.MainActivity;
import com.glgamedev.sportscompetitions.Models.Responses.ResultData;
import com.glgamedev.sportscompetitions.R;
import com.glgamedev.sportscompetitions.Dialogs.UserResultsDialog;
import com.glgamedev.sportscompetitions.Dialogs.PickableDialog;

import java.util.ArrayList;

public class UserResultsFragment extends Fragment {

    public LinearLayout content;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View viewGroup = inflater.inflate(R.layout.fragment_change_data, container, false);

        content = viewGroup.findViewById(R.id.buttons);
        updateList();

        return viewGroup;
    }

    private void updateList() {
        if (content != null) {
            content.removeAllViews();
            ResultData resultData = MainActivity.getResultData();
            ArrayList<ResultData.CompetitionResponse> competitions = resultData.competitions;
            ArrayList<ResultData.TeamResponse> teamResults = resultData.teamResults;
            long teamId = resultData.user.teamId;


            for (int i = 0; i < competitions.size(); i++) {
                ResultData.CompetitionResponse competition = competitions.get(i);
                if (competition.type <= 2) {
                    PickableDialog.Item item = new PickableDialog.Item(content.getContext(), competition.name);
                    item.setOnClickListener((v) -> {
                        ArrayList<ResultData.TeamResponse.MemberResultResponse> users = teamResults.get((int)teamId).results.get((int) competition.id-1).memberResults;
                        String[] usernames = new String[users.size()];
                        for (int x = 0; x < users.size(); x++) {
                            usernames[x] = users.get(x).surname + " # " + users.get(x).login;
                        }
                        new PickableDialog(content.getContext(), "Выберите участника", usernames, (memberId) -> {
                            new UserResultsDialog(content.getContext(), users.get(memberId), competition).Create(false).show();
                        }).show();
                    });
                    content.addView(item);
                }
            }
        }
    }
}
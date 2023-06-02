package com.glgamedev.sportscompetitions.Fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.glgamedev.sportscompetitions.Activities.MainActivity;
import com.glgamedev.sportscompetitions.Models.Responses.ResultData;
import com.glgamedev.sportscompetitions.R;
import com.glgamedev.sportscompetitions.Dialogs.UserResultsDialog;
import com.glgamedev.sportscompetitions.Dialogs.PickableDialog;

import java.util.ArrayList;

public class ChangeDataFragment extends Fragment {

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
            ArrayList<ResultData.CompetitionResponse> competitions = MainActivity.getResultData().competitions;
            if (competitions.isEmpty())
            {
                TextView text = new TextView(content.getContext());
                text.setGravity(Gravity.CENTER);
                text.setText("Нет активных мероприятий");
                text.setTextSize(20);
                text.setTextColor(content.getContext().getResources().getColor(R.color.black));
                content.addView(text, -1, -1);
                return;
            }
            for (int i = 0; i < competitions.size(); i++) {
                ResultData.CompetitionResponse competition = competitions.get(i);
                if (competition.type <= 2) {
                    PickableDialog.Item item = new PickableDialog.Item(content.getContext(), competition.name);
                    item.setOnClickListener((v) -> {
                        ArrayList<ResultData.TeamResponse> teams = MainActivity.getResultData().teamResults;
                        String[] names = new String[teams.size()];
                        for (int x = 0; x < teams.size(); x++) {
                            names[x] = teams.get(x).name;
                        }
                        new PickableDialog(content.getContext(), "Выберите команду", names, (id) -> {
                            ArrayList<ResultData.TeamResponse.MemberResultResponse> users = teams.get(id).results.get((int) competition.id-1).memberResults;
                            String[] usernames = new String[users.size()];
                            for (int x = 0; x < users.size(); x++) {
                                usernames[x] = users.get(x).surname + " # " + users.get(x).login;
                            }
                            new PickableDialog(content.getContext(), "Выберите участника", usernames, (memberId) -> {
                                new UserResultsDialog(content.getContext(), users.get(memberId), competition).Create(true).show();
                            }).show();
                        }).show();
                    });
                    content.addView(item);
                }
            }
        }
    }
}
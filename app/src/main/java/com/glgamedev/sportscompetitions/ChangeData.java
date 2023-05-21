package com.glgamedev.sportscompetitions;

import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.glgamedev.sportscompetitions.ui.Dialogs.ChangeUserDataDialog;
import com.glgamedev.sportscompetitions.ui.Dialogs.EnterableDialog;
import com.glgamedev.sportscompetitions.ui.Dialogs.PickableDialog;
import com.glgamedev.sportscompetitions.ui.adminpanel.AdminFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class ChangeData extends Fragment {

    public static ChangeData newInstance() {
        return new ChangeData();
    }

    private ChangeDataViewModel mViewModel;
    private boolean isAdmin = false;

    public static LinearLayout content;
    public static void updateList() {
        if (content != null) {
            content.removeAllViews();
            JsonArray competitions = MainActivity.data.get("data").getAsJsonObject().get("competitions").getAsJsonArray();
            for (int i = 0; i < competitions.size(); i++) {
                JsonObject competition = competitions.get(i).getAsJsonObject();
                if (competition.get("type").getAsInt() < 2) {
                    PickableDialog.Item item = new PickableDialog.Item(content.getContext(), competition.get("name").getAsString());
                    final int compId = competition.get("id").getAsInt();
                    item.setOnClickListener((v) -> {
                        ArrayList<JsonObject> teams = AdminFragment.getTeamList();
                        String[] names = new String[teams.size()];
                        for (int x = 0; x < teams.size(); x++) {
                            names[x] = teams.get(x).get("name").getAsString();
                        }
                        new PickableDialog(content.getContext(), "Выберите команду", names, (id) -> {
                            ArrayList<JsonObject> users = AdminFragment.getPlayersFromTeam(teams.get(id).get("id").getAsInt());
                            String[] usernames = new String[users.size()];
                            for (int x = 0; x < users.size(); x++) {
                                usernames[x] = users.get(x).get("surname").getAsString() + (MainActivity.data.get("data").getAsJsonObject().get("user").getAsJsonObject().get("role").getAsInt() == 0 ? " - " + users.get(x).get("login").getAsInt() : "");
                            }
                            new PickableDialog(content.getContext(), "Выберите участника", usernames, (memberId) -> {
                                new ChangeUserDataDialog(content.getContext(), users.get(memberId), compId, teams.get(id).get("id").getAsInt()).show();
                            }).show();
                        }).show();
                    });
                    content.addView(item);
                }
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View viewGroup = inflater.inflate(R.layout.fragment_change_data, container, false);
        isAdmin = MainActivity.data.get("data").getAsJsonObject().get("user").getAsJsonObject().get("role").getAsInt() == 0;

        content = viewGroup.findViewById(R.id.buttons);
        updateList();

        return viewGroup;
    }

}
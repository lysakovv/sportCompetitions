package com.glgamedev.sportscompetitions.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.glgamedev.sportscompetitions.Activities.MainActivity;
import com.glgamedev.sportscompetitions.Models.Responses.ResultData;
import com.glgamedev.sportscompetitions.R;
import com.glgamedev.sportscompetitions.Ui.Components.Table.TableViewer;
import com.glgamedev.sportscompetitions.databinding.FragmentLocalTableBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class LocalTableFragment extends Fragment {

    private FragmentLocalTableBinding binding;

    public static String[][] types = new String[][]{
            {"Баллы", "Место"},
            {"Время", "Место"},
            {"Баллы"},
            {"Баллы"},
            {"Баллы", "Место"}
    };

    static HashMap<Long, ResultData.CompetitionResponse> competitionList = new HashMap<>();

    public static View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentLocalTableBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        updateTable(getContext());

        return root;
    }


    public static String[][] convert(ArrayList<ArrayList<String>> list) {
        String[][] array = new String[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            ArrayList<String> sublist = list.get(i);
            array[i] = sublist.toArray(new String[sublist.size()]);
        }
        return array;
    }

    public static void updateTable(Context context) {
        if (root != null) {
            try {
                ((LinearLayout) root.findViewById(R.id.content)).removeAllViews();
            } catch (Exception e) {
            }
            TableViewer table = new TableViewer(context);
            new Handler().postDelayed(() -> {
                ArrayList<ResultData.CompetitionResponse> competitions = MainActivity.getResultData().competitions;
                if (competitions.isEmpty())
                {
                    TextView text = new TextView(context);
                    text.setGravity(Gravity.CENTER);
                    text.setText("Нет активных мероприятий");
                    text.setTextSize(20);
                    text.setTextColor(context.getResources().getColor(R.color.black));
                    ((LinearLayout) root.findViewById(R.id.content)).addView(text, -1, -1);
                    return;
                }

                for (int i = 0; i < competitions.size(); i++) {
                    competitionList.put(competitions.get(i).id, competitions.get(i));
                    table.newCol(competitions.get(i).name, types[competitions.get(i).type]);
                }

                table.newCollapse("Старшая лига");
                table.newCollapse("Средняя лига");
                table.newCollapse("Младшая лига");

                ArrayList<ResultData.TeamResponse> teamResult = MainActivity.getResultData().teamResults;
                for (int i = 0; i < teamResult.size(); i++) {
                    ResultData.TeamResponse team = teamResult.get(i);
                    ArrayList<ArrayList<String>> values = new ArrayList<>();
                    { // Put name in values
                        ArrayList<String> vals = new ArrayList<>();
                        vals.add(team.name);
                        values.add(vals);
                    }
                    AdminFragment.teams.put(team.id, team);
                    ArrayList<ResultData.TeamResponse.TeamResultResponse> results = team.results;
                    for (int x = 0; x < results.size(); x++) {
                        ResultData.TeamResponse.TeamResultResponse result = results.get(x);
                        ArrayList<String> vals = new ArrayList<>();
                        vals.add(result.total);
                        if (types[competitionList.get(result.competitionId).type].length == 2) {
                            vals.add(Integer.toString(result.place));
                        }
                        values.add(vals);
                    }
                    String[][] addData = convert(values);
                    table.createNewRow(2 - team.league, team.id, addData);
                }
                ((LinearLayout) root.findViewById(R.id.content)).addView(table, -1, -1);
            }, 0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
package com.glgamedev.sportscompetitions.ui.localtable;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.glgamedev.sportscompetitions.MainActivity;
import com.glgamedev.sportscompetitions.R;
import com.glgamedev.sportscompetitions.databinding.FragmentLocalTableBinding;
import com.glgamedev.sportscompetitions.ui.Components.Table.TableViewer;
import com.glgamedev.sportscompetitions.ui.adminpanel.AdminFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LocalTableFragment extends Fragment {

    private FragmentLocalTableBinding binding;


    public static String[][] types = new String[][] {
            {"Баллы", "Место"},
            {"Время", "Место"},
            {"Баллы"},
            {"Баллы"},
            {"Баллы", "Место"}
    };

    static HashMap<Integer, JsonObject> competitonList = new HashMap<>();

public static View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LocalTableViewModel homeViewModel =
                new ViewModelProvider(this).get(LocalTableViewModel.class);

        binding = FragmentLocalTableBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        TableViewer table = new TableViewer(getContext());
        new Handler().postDelayed(() -> {
            JsonArray cops = MainActivity.data.get("data").getAsJsonObject().get("competitions").getAsJsonArray();

            for (int i = 0; i < cops.size(); i++) {
                JsonObject com = cops.get(i).getAsJsonObject();
                competitonList.put(com.get("id").getAsInt(), com);
                typeList.put(com.get("id").getAsInt(), com.get("type").getAsInt());
                table.newCol(com.get("name").getAsString(), types[com.get("type").getAsInt()]);
            }

            table.newCollapse("Старшая лига");
            table.newCollapse("Средняя лига");
            table.newCollapse("Младшая лига");

            JsonArray teamResult = MainActivity.data.get("data").getAsJsonObject().get("teamResults").getAsJsonArray();
            for (int i = 0; i < teamResult.size(); i++) {
                JsonObject team = teamResult.get(i).getAsJsonObject();
                ArrayList<ArrayList<String>> values = new ArrayList<>();
                { // Put name in values
                    ArrayList<String> vals = new ArrayList<>();
                    vals.add(team.get("name").getAsString());
                    values.add(vals);
                }
                AdminFragment.teams.put(team.get("id").getAsInt(), team);
                JsonArray results = team.get("results").getAsJsonArray();
                for (int x = 0; x < results.size(); x++) {
                    JsonObject result = results.get(x).getAsJsonObject();
                    ArrayList<String> vals = new ArrayList<>();
                    vals.add(result.get("total").getAsString());
                    if (types[competitonList.get(result.get("competitionId").getAsInt()).get("type").getAsInt()].length == 2) {
                        vals.add(Integer.toString(result.get("place").getAsInt()));
                    }
                    values.add(vals);
                }
                String[][] addData = convert(values);
                table.createNewRow(2-team.get("league").getAsInt(), team.get("id").getAsInt(), addData);
            }
            ((LinearLayout) root.findViewById(R.id.content)).addView(table, -1, -1);
        }, 1000);

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

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static HashMap<Integer, Integer> typeList = new HashMap<>();

    public static void updateTable(Context context) {
        if (root != null) {
            try {
                ((LinearLayout) root.findViewById(R.id.content)).removeAllViews();
            } catch (Exception e) {
            }
            TableViewer table = new TableViewer(context);
            new Handler().postDelayed(() -> {
                JsonArray cops = MainActivity.data.get("data").getAsJsonObject().get("competitions").getAsJsonArray();

                for (int i = 0; i < cops.size(); i++) {
                    JsonObject com = cops.get(i).getAsJsonObject();
                    competitonList.put(com.get("id").getAsInt(), com);
                    table.newCol(com.get("name").getAsString(), types[com.get("type").getAsInt()]);
                }

                table.newCollapse("Старшая лига");
                table.newCollapse("Средняя лига");
                table.newCollapse("Младшая лига");

                JsonArray teamResult = MainActivity.data.get("data").getAsJsonObject().get("teamResults").getAsJsonArray();
                for (int i = 0; i < teamResult.size(); i++) {
                    JsonObject team = teamResult.get(i).getAsJsonObject();
                    ArrayList<ArrayList<String>> values = new ArrayList<>();
                    { // Put name in values
                        ArrayList<String> vals = new ArrayList<>();
                        vals.add(team.get("name").getAsString());
                        values.add(vals);
                    }
                    AdminFragment.teams.put(team.get("id").getAsInt(), team);
                    JsonArray results = team.get("results").getAsJsonArray();
                    for (int x = 0; x < results.size(); x++) {
                        JsonObject result = results.get(x).getAsJsonObject();
                        ArrayList<String> vals = new ArrayList<>();
                        vals.add(result.get("total").getAsString());
                        if (types[competitonList.get(result.get("competitionId").getAsInt()).get("type").getAsInt()].length == 2) {
                            vals.add(Integer.toString(result.get("place").getAsInt()));
                        }
                        values.add(vals);
                    }
                    String[][] addData = convert(values);
                    table.createNewRow(2 - team.get("league").getAsInt(), team.get("id").getAsInt(), addData);
                }
                ((LinearLayout) root.findViewById(R.id.content)).addView(table, -1, -1);
            }, 1000);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
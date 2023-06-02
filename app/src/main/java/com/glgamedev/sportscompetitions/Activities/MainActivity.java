package com.glgamedev.sportscompetitions.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.glgamedev.sportscompetitions.Localization;
import com.glgamedev.sportscompetitions.Models.Requests.SetResultsData;
import com.glgamedev.sportscompetitions.R;
import com.glgamedev.sportscompetitions.Utils;
import com.glgamedev.sportscompetitions.databinding.ActivityMainBinding;
import com.glgamedev.sportscompetitions.Enums.Role;
import com.glgamedev.sportscompetitions.Models.Responses.ResultData;
import com.glgamedev.sportscompetitions.Network.NetworkClient;
import com.glgamedev.sportscompetitions.Fragments.LocalTableFragment;
import com.glgamedev.sportscompetitions.Fragments.AdminFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private static SharedPreferences save;

    private static ResultData resultData;
    private static SetResultsData localResults;

    public static ResultData getResultData() {
        return resultData;
    }

    private static void setResultData(ResultData resultData) {
        MainActivity.resultData = resultData;
    }

    public static SetResultsData getLocalResults() {
        return localResults;
    }

    public static void setLocalResults(SetResultsData localResults) {
        MainActivity.localResults = localResults;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Utils.initRequest();

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_slideshow, R.id.nav_changedata)
                .setOpenableLayout(drawer)
                .build();

        View hView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView nameView = hView.findViewById(R.id.header_title);
        TextView exit = hView.findViewById(R.id.exit);

        ((ImageView) hView.findViewById(R.id.refresh)).setColorFilter(Color.WHITE);
        hView.findViewById(R.id.refresh).setOnClickListener((v) -> {
            update();
        });

        exit.setOnClickListener((v) -> {
            save.edit().clear().commit();
            startActivity(new Intent(this, LoginActivity.class));
        });

        Gson gson = new Gson();

        save = getSharedPreferences("local", Context.MODE_PRIVATE);
        setResultData(gson.fromJson(save.getString("main", "{}"), ResultData.class));

        String localResultsInfo = save.getString("localResults", "");
        if (!localResultsInfo.equals(""))
        {
            setLocalResults(gson.fromJson(localResultsInfo, SetResultsData.class));
        } else {
            setLocalResults(new SetResultsData());
        }

        String surname = getResultData().user.surname;
        String name = getResultData().user.name;

        navigationView.getMenu().getItem(1).setVisible(getResultData().user.role == Role.Admin.ordinal());
        navigationView.getMenu().getItem(2).setVisible(getResultData().user.role == Role.Admin.ordinal());
        navigationView.getMenu().getItem(3).setVisible(getResultData().user.role != Role.Admin.ordinal());
        nameView.setText(String.format("%s %s", surname, name));

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void update() {
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        json.add("userId", gson.toJsonTree(resultData.user.id));
        NetworkClient.Post("api/Competition/getResults", json.toString(), new NetworkClient.OnResponseListener() {
            @Override
            public void onSuccessful(int code, String data) {
                runOnUiThread(() -> {
                    try {
                        JsonObject response = new Gson().fromJson(data, JsonObject.class);
                        if (response.get("isSuccess").getAsBoolean()) {
                            updateMainResults(gson.fromJson(response.getAsJsonObject().get("data"), ResultData.class));
                            LocalTableFragment.updateTable(getApplicationContext());
                        }

                        AdminFragment.showToast(getApplicationContext(), response.get("isSuccess").getAsBoolean(), response.get("message").getAsString());
                    } catch (Exception e) {
                        AdminFragment.showToast(getApplicationContext(), false, Localization.server_error);
                    }
                });
            }

            @Override
            public void onError(int code, String data) {
                //
            }
        });
    }

    public static ResultData updateMainResults(ResultData data){
        setResultData(data);
        save.edit().putString("main", data.toString()).apply();
        return getResultData();
    }

    public static SetResultsData updateLocalResults(SetResultsData data){
        setLocalResults(data);
        save.edit().putString("localResults", data.toString()).apply();
        return getLocalResults();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
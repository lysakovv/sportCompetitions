package com.glgamedev.sportscompetitions;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.glgamedev.sportscompetitions.Network.NetworkClient;
import com.glgamedev.sportscompetitions.ui.Components.Table.TableViewer;
import com.glgamedev.sportscompetitions.ui.adminpanel.AdminFragment;
import com.glgamedev.sportscompetitions.ui.localtable.LocalTableFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.glgamedev.sportscompetitions.databinding.ActivityMainBinding;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    public static SharedPreferences save;
    public static JsonObject data;

    // Обновление данных о пользователе и таблицы
    private void update() {
        JsonObject data = new JsonObject();
        Gson gson = new Gson();
        data.add("login", gson.toJsonTree(save.getInt("login", 0)));
        data.add("password", gson.toJsonTree(save.getString("password", null)));
        NetworkClient.Post("api/User/login", data.toString(), new NetworkClient.OnResponseListener() {
            @Override
            public void onSuccessful(int code, String data) {
                runOnUiThread(() -> {
                    try {
                        JsonObject response = new Gson().fromJson(NetworkClient.resp1, JsonObject.class);
                        if (response.get("isSuccess").getAsBoolean()) {
                            response.add("login", gson.toJsonTree(save.getInt("login", 0)));
                            SharedPreferences.Editor editor = save.edit();
                            editor.putString("main", response.toString());
                            editor.putInt("login", save.getInt("login", 0));
                            editor.putString("password", save.getString("password", null));
                            editor.commit();

                            MainActivity.data = new Gson().fromJson(save.getString("main",  "{}"), JsonObject.class);

                            LocalTableFragment.updateTable(getApplicationContext());
                        } else {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Utils.initRequest();

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        // получение локалей, где хранятся локальные данные
        save = getSharedPreferences("local", Context.MODE_PRIVATE);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_slideshow, R.id.nav_changedata)
                .setOpenableLayout(drawer)
                .build();

        View hView =  navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView nameView = hView.findViewById(R.id.header_title);
        TextView exit = hView.findViewById(R.id.exit);

        ((ImageView) hView.findViewById(R.id.refresh)).setColorFilter(Color.WHITE);
        hView.findViewById(R.id.refresh).setOnClickListener((v) -> {
            update();
        });

        // локальный данные заменяются на пустые и выходит из активити
        exit.setOnClickListener((v) -> {
            save.edit().putString("main", "").commit();
            finish();
        });

        // загрузка локальных данных
        data = new Gson().fromJson(save.getString("main",  "{}"), JsonObject.class);

        JsonObject user = data.get("data").getAsJsonObject().get("user").getAsJsonObject();
        String surname = user.get("surname").getAsString();
        String name = user.get("name").getAsString();

        // если пользователь админ и у него есть действия сделанные в оффлайн, то они отправляются на сервер
        if (user.get("role").getAsInt() == 0) {
            JsonArray values = AdminFragment.getOfflineEvents();
            if (values.size() > 0) {
                NetworkClient.Post("api/User/change", values.toString(), new NetworkClient.OnResponseListener() {
                    @Override
                    public void onSuccessful(int code, String data) {
                        runOnUiThread(() -> {
                            try {
                                JsonObject response = new Gson().fromJson(data, JsonObject.class);
                                if (response.get("isSuccess").getAsBoolean()) {
                                    MainActivity.data = response;
                                    AdminFragment.saveOfflineEvents(new JsonArray());
                                    AdminFragment.saveData();
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
        }

        navigationView.getMenu().getItem(1).setVisible(user.get("role").getAsInt() == 0);
        nameView.setText(surname + " " + name);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    MenuItem mi;

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
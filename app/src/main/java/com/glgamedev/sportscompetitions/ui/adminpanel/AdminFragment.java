package com.glgamedev.sportscompetitions.ui.adminpanel;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.glgamedev.sportscompetitions.Localization;
import com.glgamedev.sportscompetitions.MainActivity;
import com.glgamedev.sportscompetitions.Network.NetworkClient;
import com.glgamedev.sportscompetitions.R;
import com.glgamedev.sportscompetitions.databinding.FragmentAdminBinding;
import com.glgamedev.sportscompetitions.ui.Dialogs.EnterableDialog;
import com.glgamedev.sportscompetitions.ui.Dialogs.RegisterUserDialog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminFragment extends Fragment {

    private FragmentAdminBinding binding;
    private boolean isAdmin = false;

    private Button registerEvent,
                   removeEvent,
                   registerTeam,
                   removeTeam,
                   registerUser,
                   removeUser;

    public static HashMap<Integer, JsonObject> teams = new HashMap<>();
    public static ArrayList<JsonObject> players = new ArrayList<>();
    private ArrayList<JsonObject> getMembers(JsonObject team) {
        return null;
    }

    public static ArrayList<String> getTeams() {
        ArrayList<String> names = new ArrayList<>();
        for (Integer key: teams.keySet()) {
            JsonObject team = teams.get(key).getAsJsonObject();
            names.add(team.get("name").getAsString());
        }
        return names;
    }

    public static ArrayList<JsonObject> getTeamList() {
        ArrayList<JsonObject> names = new ArrayList<>();
        for (Integer key: teams.keySet()) {
            JsonObject team = teams.get(key).getAsJsonObject();
            names.add(team);
        }
        return names;
    }

    public static int getTeamIdFromName(String name) {
        for (Integer key: teams.keySet()) {
            JsonObject team = teams.get(key).getAsJsonObject();
            if (name.equals(team.get("name").getAsString())) return team.get("id").getAsInt();
        }
        return 0;
    }

    public static JsonObject getPlayerFromId(int id) {
        JsonArray users = MainActivity.data.get("data").getAsJsonArray();
        for (int i = 0; i < users.size(); i++) {
            JsonObject user = users.get(i).getAsJsonObject();
            if (user.get("id").getAsInt() == id) {
                return user;
            }
        }
        return null;
    }

    public static ArrayList<JsonObject> getPlayersFromTeam(int teamId) {
        ArrayList<JsonObject> result = new ArrayList<>();
        JsonArray users = MainActivity.data.get("data").getAsJsonObject().get("users").getAsJsonArray();
        for (int i = 0; i < users.size(); i++) {
            JsonObject user = users.get(i).getAsJsonObject();
            if (user.get("teamId").getAsInt() == teamId) {
                result.add(user);
            }
        }
        return result;
    }

    public static void putPlayerInfo(int id, JsonObject data) {
        JsonArray users = MainActivity.data.get("data").getAsJsonArray();
        for (int i = 0; i < users.size(); i++) {
            JsonObject user = users.get(i).getAsJsonObject();
            if (user.get("id").getAsInt() == id) {
                MainActivity.data.get("data").getAsJsonArray().set(i, data);
                saveData();
            }
        }
    }

    public static void saveData() {
        SharedPreferences.Editor edit = MainActivity.save.edit();
        edit.putString("main", MainActivity.data.toString());
        edit.commit();
    }

    public static void showToast(Context ctx, boolean green, String message) {
        Toast toast = Toast.makeText(ctx, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.END | Gravity.TOP, 20, 160);
        TextView title = new TextView(ctx);
        {
            title.setText(message);
            title.setBackgroundColor(green ? Color.GREEN : Color.RED);
            title.setTextColor(Color.BLACK);
            title.setPadding(10,10,10,10);
            title.setTextSize(18f);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                title.setTypeface(ctx.getResources().getFont(R.font.googleregular));
            }
        }
        toast.setView(title);
        toast.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 1000);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static JsonArray getOfflineEvents() {
        return new Gson().fromJson(MainActivity.save.getString("local","[]"), JsonArray.class);
    }

    public static void saveOfflineEvents(JsonArray data) {
        SharedPreferences.Editor edit = MainActivity.save.edit();
        edit.putString("local", data.toString());
        edit.commit();
    }

    public static void addOfflineEvent(String method, JsonObject data) {
        JsonArray offline = getOfflineEvents();

        offline.add(data);
        saveOfflineEvents(offline);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AdminViewModel slideshowViewModel =
                new ViewModelProvider(this).get(AdminViewModel.class);

        binding = FragmentAdminBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        isAdmin = MainActivity.data.get("data").getAsJsonObject().get("user").getAsJsonObject().get("role").getAsInt() == 0;

        registerEvent = root.findViewById(R.id.registerEvent);
        removeEvent = root.findViewById(R.id.removeEvent);
        registerTeam = root.findViewById(R.id.registerTeam);
        removeTeam = root.findViewById(R.id.removeTeam);
        registerUser = root.findViewById(R.id.registerUser);
        removeUser = root.findViewById(R.id.removeUser);

        registerEvent.setOnClickListener((v) -> {
            new EnterableDialog(getContext(), "Введите название мероприятия", new String[] {"Название мероприятия"}, (d) -> {

                JsonObject data = new JsonObject();
                data.add("name", new Gson().toJsonTree(d.get(0)));
                NetworkClient.Post("api/Event/create", data.toString(), new NetworkClient.OnResponseListener() {
                    @Override
                    public void onSuccessful(int code, String data) {
                        ((Activity)getContext()).runOnUiThread(() -> {
                            try {
                                JsonObject response = new Gson().fromJson(data, JsonObject.class);
                                if (response.get("isSuccess").getAsBoolean()) {
                                    response.get("data").getAsJsonObject().add("user", MainActivity.data.get("data").getAsJsonObject().get("user").getAsJsonObject());
                                    MainActivity.data = response;
                                    AdminFragment.saveData();
                                }
                                AdminFragment.showToast(getContext(), response.get("isSuccess").getAsBoolean(), response.get("message").getAsString());
                            } catch (Exception e) {
                                AdminFragment.showToast(getContext(), false, Localization.server_error);
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String data) {
                        //
                    }
                });
            }).show();
        });

        removeEvent.setOnClickListener((v) -> {
            new EnterableDialog(getContext(), "Подтвердите удаление", new String[] {}, (d) -> {
                JsonObject data = new JsonObject();
                data.add("id", new Gson().toJsonTree(MainActivity.data.get("data").getAsJsonObject().get("user").getAsJsonObject().get("id").getAsInt()));
                NetworkClient.Post("api/Event/delete", data.toString(), new NetworkClient.OnResponseListener() {
                    @Override
                    public void onSuccessful(int code, String data) {
                        ((Activity)getContext()).runOnUiThread(() -> {
                            try {
                                JsonObject response = new Gson().fromJson(data, JsonObject.class);
                                if (response.get("isSuccess").getAsBoolean()) {
                                    response.get("data").getAsJsonObject().add("user", MainActivity.data.get("data").getAsJsonObject().get("user").getAsJsonObject());
                                    MainActivity.data = response;
                                    AdminFragment.saveData();
                                }
                                AdminFragment.showToast(getContext(), response.get("isSuccess").getAsBoolean(), response.get("message").getAsString());
                            } catch (Exception e) {
                                AdminFragment.showToast(getContext(), false, Localization.server_error);
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String data) {
                        //
                    }
                });
            }).show();
        });

        // регистрация команды
        registerTeam.setOnClickListener((v) -> {
            new EnterableDialog(getContext(), "Регистрация команды", new String[] {"Название команды"}, (d) -> {
                // создается обьект, записывается его имя
                JsonObject data = new JsonObject();
                data.add("name", new Gson().toJsonTree(d.get(0)));
                NetworkClient.Post("api/Team/create", data.toString(), new NetworkClient.OnResponseListener() {
                    @Override
                    public void onSuccessful(int code, String data) {
                        // если запрос успешный, то в ui потоке вызывается функция
                        ((Activity)getContext()).runOnUiThread(() -> {
                            try {
                                // парсинг ответа
                                JsonObject response = new Gson().fromJson(data, JsonObject.class);
                                if (response.get("isSuccess").getAsBoolean()) {
                                    // если ответ успешный, то в ответ добавляется логин пользователя и записывается в data
                                    response.get("data").getAsJsonObject().add("user", MainActivity.data.get("data").getAsJsonObject().get("user").getAsJsonObject());
                                    MainActivity.data = response;

                                    // сохраняется в локалку
                                    AdminFragment.saveData();
                                }

                                // вывод сообщения
                                AdminFragment.showToast(getContext(), response.get("isSuccess").getAsBoolean(), response.get("message").getAsString());
                            } catch (Exception e) {
                                AdminFragment.showToast(getContext(), false, Localization.server_error);
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String data) {
                        //
                    }
                });
            }).show();
        });

        // открытие диалога регистрации юзера
        registerUser.setOnClickListener((v) -> {
            new RegisterUserDialog(getContext(), getTeams()).show();
        });



        TextView admin = new TextView(getContext());
        {
            admin.setText("Вы не являетесь администратором.");
            admin.setTextColor(Color.BLACK);
            admin.setTextSize(20f);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                admin.setTypeface(getContext().getResources().getFont(R.font.googleregular));
            }
        }

        if (!isAdmin) {
            ((LinearLayout)root.findViewById(R.id.main)).removeAllViews();
            ((LinearLayout)root.findViewById(R.id.main)).addView(admin, -1, -1);
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
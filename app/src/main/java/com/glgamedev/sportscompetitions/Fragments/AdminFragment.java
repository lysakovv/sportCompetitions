package com.glgamedev.sportscompetitions.Fragments;

import android.app.Activity;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.glgamedev.sportscompetitions.Localization;
import com.glgamedev.sportscompetitions.Activities.MainActivity;
import com.glgamedev.sportscompetitions.Models.Responses.ResultData;
import com.glgamedev.sportscompetitions.Network.NetworkClient;
import com.glgamedev.sportscompetitions.R;
import com.glgamedev.sportscompetitions.Dialogs.EnterableDialog;
import com.glgamedev.sportscompetitions.Dialogs.RegisterUserDialog;
import com.glgamedev.sportscompetitions.databinding.FragmentAdminBinding;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminFragment extends Fragment {
    private FragmentAdminBinding binding;

    private Button registerEvent, removeEvent, registerTeam, removeTeam, registerUser, removeUser;

    public static HashMap<Long, ResultData.TeamResponse> teams = new HashMap<>();

    public static ArrayList<String> getTeams() {
        ArrayList<String> names = new ArrayList<>();
        for (Long key : teams.keySet()) {
            ResultData.TeamResponse team = teams.get(key);
            names.add(team.name);
        }
        return names;
    }

    public static long getTeamIdFromName(String name) {
        for (Long key : teams.keySet()) {
            ResultData.TeamResponse team = teams.get(key);
            if (name.equals(team.name)) return team.id;
        }
        return 0;
    }

    public static void showToast(Context ctx, boolean green, String message) {
        Toast toast = Toast.makeText(ctx, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.END | Gravity.TOP, 50, 160);
        TextView title = new TextView(ctx);
        {
            title.setText(message);
            title.setBackgroundColor(green ? Color.GREEN : Color.RED);
            title.setTextColor(Color.BLACK);
            title.setAlpha(0.7f);
            title.setPadding(10, 10, 10, 10);
            title.setTextSize(20f);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                title.setTypeface(ctx.getResources().getFont(R.font.googleregular));
            }
        }
        toast.setView(title);
        toast.show();

        new Handler().postDelayed(() -> toast.cancel(), 2000);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        registerEvent = root.findViewById(R.id.registerEvent);
        removeEvent = root.findViewById(R.id.removeEvent);
        registerTeam = root.findViewById(R.id.registerTeam);
        removeTeam = root.findViewById(R.id.removeTeam);
        registerUser = root.findViewById(R.id.registerUser);
        removeUser = root.findViewById(R.id.removeUser);

        registerEvent.setOnClickListener((v) -> {
            new EnterableDialog(getContext(), "Введите название мероприятия", new String[]{"Название мероприятия"}, (d) -> {

                JsonObject data = new JsonObject();
                data.add("name", new Gson().toJsonTree(d.get(0)));
                NetworkClient.Post("api/Event/create", data.toString(), new NetworkClient.OnResponseListener() {
                    @Override
                    public void onSuccessful(int code, String data) {
                        ((Activity) getContext()).runOnUiThread(() -> {
                            try {
                                Gson gson = new Gson();
                                JsonObject response = gson.fromJson(data, JsonObject.class);
                                if (response.get("isSuccess").getAsBoolean()) {
                                    MainActivity.updateMainResults(gson.fromJson(response.getAsJsonObject().get("data"), ResultData.class));
                                    LocalTableFragment.updateTable(getContext());
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
            new EnterableDialog(getContext(), "Подтвердите удаление", new String[]{}, (d) -> {
                JsonObject data = new JsonObject();
                data.add("userId", new Gson().toJsonTree(MainActivity.getResultData().user.id));
                NetworkClient.Post("api/Event/delete", data.toString(), new NetworkClient.OnResponseListener() {
                    @Override
                    public void onSuccessful(int code, String data) {
                        ((Activity) getContext()).runOnUiThread(() -> {
                            try {
                                Gson gson = new Gson();
                                JsonObject response = gson.fromJson(data, JsonObject.class);
                                if (response.get("isSuccess").getAsBoolean()) {
                                    MainActivity.updateMainResults(gson.fromJson(response.getAsJsonObject().get("data"), ResultData.class));
                                    LocalTableFragment.updateTable(getContext());
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
            new EnterableDialog(getContext(), "Регистрация команды", new String[]{"Название команды"}, (d) -> {
                // создается обьект, записывается его имя
                JsonObject data = new JsonObject();
                data.add("name", new Gson().toJsonTree(d.get(0)));
                NetworkClient.Post("api/Team/create", data.toString(), new NetworkClient.OnResponseListener() {
                    @Override
                    public void onSuccessful(int code, String data) {
                        // если запрос успешный, то в ui потоке вызывается функция
                        ((Activity) getContext()).runOnUiThread(() -> {
                            try {
                                Gson gson = new Gson();
                                JsonObject response = gson.fromJson(data, JsonObject.class);
                                if (response.get("isSuccess").getAsBoolean()) {
                                    MainActivity.updateMainResults(gson.fromJson(response.getAsJsonObject().get("data"), ResultData.class));
                                    LocalTableFragment.updateTable(getContext());
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

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
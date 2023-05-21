package com.glgamedev.sportscompetitions.ui.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.glgamedev.sportscompetitions.Localization;
import com.glgamedev.sportscompetitions.MainActivity;
import com.glgamedev.sportscompetitions.Network.NetworkClient;
import com.glgamedev.sportscompetitions.R;
import com.glgamedev.sportscompetitions.Utils;
import com.glgamedev.sportscompetitions.ui.adminpanel.AdminFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class ChangeUserDialog extends Dialog {
    public EnterableDialog.Item surname, name, age;
    public Spinner team;
    public Button register;

    String teamName;
    int teamid;

    public ChangeUserDialog(Context context, ArrayList<String> teams, JsonObject userInfo) {
        super(context);

        LinearLayout main = new LinearLayout(getContext());
        {
            main.setOrientation(LinearLayout.VERTICAL);
            main.setPadding(Utils.dp(getContext(), 5), Utils.dp(getContext(), 5), Utils.dp(getContext(), 5), Utils.dp(getContext(), 5));
        }

        surname = new EnterableDialog.Item(getContext(), "Фамилия");
        name = new EnterableDialog.Item(getContext(), "Имя");
        age = new EnterableDialog.Item(getContext(), "Возраст");

        List<String> spinnerValues = new ArrayList<String>();
        spinnerValues.add("Участник");
        spinnerValues.add("Капитан");
        spinnerValues.add("Администратор");

        team = new Spinner(getContext());
        ArrayAdapter<String> spinnerAdapterTeam = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, teams);
        spinnerAdapterTeam.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        team.setAdapter(spinnerAdapterTeam);
        team.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                teamName = teams.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        main.addView(surname);
        main.addView(name);
        main.addView(age);
        main.addView(team, -1, Utils.dp(getContext(), 35));

        register = new Button(getContext());
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                register.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(R.color.panelsBg)));
            }
            register.setText("Изменить");
            register.setTextSize(15f);
            register.setTextColor(Color.WHITE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                register.setTypeface(getContext().getResources().getFont(R.font.googleregular));
            }
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, Utils.dp(getContext(), 35));
            lp.topMargin = Utils.dp(getContext(), 5);
            main.addView(register, lp);

            register.setOnClickListener((v) -> {
                dismiss();
                int teamId = AdminFragment.getTeamIdFromName(teamName);
                try {
                    JsonObject data = new JsonObject();
                    Gson gson = new Gson();
                    data.add("surname", gson.toJsonTree(surname.getText().toString()));
                    data.add("name", gson.toJsonTree(name.getText().toString()));
                    data.add("age", gson.toJsonTree(Integer.parseInt(age.getText().toString())));
                    data.add("teamId", gson.toJsonTree(teamId));

                    NetworkClient.Post("api/User/change", data.toString(), new NetworkClient.OnResponseListener() {
                        @Override
                        public void onSuccessful(int code, String data) {
                            ((Activity)getContext()).runOnUiThread(() -> {
                                try {
                                    JsonObject response = new Gson().fromJson(data, JsonObject.class);
                                    if (response.get("isSuccess").getAsBoolean()) {
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

                } catch (Exception e) {

                }
            });
        }
        setContentView(main);
    }
}

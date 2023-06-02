package com.glgamedev.sportscompetitions.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.glgamedev.sportscompetitions.Enums.Role;
import com.glgamedev.sportscompetitions.Localization;
import com.glgamedev.sportscompetitions.Activities.MainActivity;
import com.glgamedev.sportscompetitions.Models.Responses.ResultData;
import com.glgamedev.sportscompetitions.Network.NetworkClient;
import com.glgamedev.sportscompetitions.R;
import com.glgamedev.sportscompetitions.Fragments.LocalTableFragment;
import com.glgamedev.sportscompetitions.Utils;
import com.glgamedev.sportscompetitions.Fragments.AdminFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class RegisterUserDialog extends Dialog {
    public EnterableDialog.Item surname, name, age, password;
    public Spinner role, team;
    public Button register;

    String teamName;
    int userRole;

    public RegisterUserDialog(Context context, ArrayList<String> teams) {
        super(context);
        LinearLayout main = new LinearLayout(getContext());
        {
            main.setOrientation(LinearLayout.VERTICAL);
            main.setPadding(Utils.dp(getContext(), 5), Utils.dp(getContext(), 5), Utils.dp(getContext(), 5), Utils.dp(getContext(), 5));
        }

        surname = new EnterableDialog.Item(getContext(), "Фамилия", true, false);
        name = new EnterableDialog.Item(getContext(), "Имя", true, false);
        age = new EnterableDialog.Item(getContext(), "Возраст", true, true);
        password = new EnterableDialog.Item(getContext(), "Пароль", true, false);
        password.setVisibility(View.GONE);

        List<String> spinnerValues = new ArrayList<String>();
        spinnerValues.add("Участник");
        spinnerValues.add("Капитан");
        spinnerValues.add("Администратор");

        role = new Spinner(getContext());
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerValues);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        role.setAdapter(spinnerAdapter);
        role.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                boolean adm = i == 2;
                userRole = adm ? Role.Admin.ordinal() : i + 1;
                age.setVisibility(adm || userRole == Role.Captain.ordinal() ? View.GONE : View.VISIBLE);
                password.setVisibility(adm ? View.VISIBLE : View.GONE);
                team.setVisibility(adm ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        team = new Spinner(getContext());
        ArrayAdapter<String> spinnerAdapterTeam = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, teams);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
        main.addView(role, -1, Utils.dp(getContext(), 35));
        main.addView(password);

        register = new Button(getContext());
        {
            register.setBackgroundColor(getContext().getResources().getColor(R.color.approve));
            register.setText("Зарегистрировать");
            register.setTextSize(15f);
            register.setTextColor(Color.BLACK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                register.setTypeface(getContext().getResources().getFont(R.font.googleregular));
            }
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, Utils.dp(getContext(), 35));
            lp.topMargin = Utils.dp(getContext(), 5);
            main.addView(register, lp);

            register.setOnClickListener((v) -> {
                dismiss();
                long teamId = AdminFragment.getTeamIdFromName(teamName);
                try {
                    JsonObject data = new JsonObject();
                    Gson gson = new Gson();
                    data.add("surname", gson.toJsonTree(surname.getText().toString()));
                    data.add("name", gson.toJsonTree(name.getText().toString()));
                    data.add("age", gson.toJsonTree(userRole == Role.Participant.ordinal() ? Integer.parseInt(age.getText().toString()) : null));
                    data.add("teamId", gson.toJsonTree(userRole == Role.Admin.ordinal() ? null : teamId));
                    data.add("role", gson.toJsonTree(userRole));
                    data.add("password", gson.toJsonTree(userRole == Role.Admin.ordinal() ? password.getText().toString() : null));
                    data.add("adminId", gson.toJsonTree(MainActivity.getResultData().user.id));
                    NetworkClient.Post("api/User/register", data.toString(), new NetworkClient.OnResponseListener() {
                        @Override
                        public void onSuccessful(int code, String data) {
                            Context ctx = context;
                            ((Activity)ctx).runOnUiThread(() -> {
                                try {
                                    JsonObject response = new Gson().fromJson(data, JsonObject.class);
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
                } catch (Exception e) {

                }
            });
        }
        setContentView(main);
    }
}

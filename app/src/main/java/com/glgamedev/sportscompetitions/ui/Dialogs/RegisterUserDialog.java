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
import android.widget.CheckBox;
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

        surname = new EnterableDialog.Item(getContext(), "Фамилия");
        name = new EnterableDialog.Item(getContext(), "Имя");
        age = new EnterableDialog.Item(getContext(), "Возраст");
        password = new EnterableDialog.Item(getContext(), "Пароль");
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
                userRole = i;
                age.setVisibility(adm || i == 1 ? View.GONE : View.VISIBLE);
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
                int teamId = AdminFragment.getTeamIdFromName(teamName);
                try {
                    JsonObject data = new JsonObject();
                    Gson gson = new Gson();
                    data.add("surname", gson.toJsonTree(surname.getText().toString()));
                    data.add("name", gson.toJsonTree(name.getText().toString()));
                    data.add("age", gson.toJsonTree(userRole == 0 ? Integer.parseInt(age.getText().toString()) : null));
                    data.add("teamId", gson.toJsonTree(userRole == 2 ? null : teamId));
                    data.add("role", gson.toJsonTree(userRole));
                    data.add("password", gson.toJsonTree(userRole == 2 ? password.getText().toString() : null));
                    NetworkClient.Post("api/User/register", data.toString(), new NetworkClient.OnResponseListener() {
                        @Override
                        public void onSuccessful(int code, String data) {
                            Context ctx = context;
                            ((Activity)ctx).runOnUiThread(() -> {
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

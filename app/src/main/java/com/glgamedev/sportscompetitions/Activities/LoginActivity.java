package com.glgamedev.sportscompetitions.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import com.glgamedev.sportscompetitions.Extensions.StringExtensions;
import com.glgamedev.sportscompetitions.Localization;
import com.glgamedev.sportscompetitions.Network.NetworkClient;
import com.glgamedev.sportscompetitions.Fragments.AdminFragment;
import com.glgamedev.sportscompetitions.R;
import com.google.android.material.textfield.TextInputLayout;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class LoginActivity extends AppCompatActivity {
    private CheckBox loginAdmin;
    private TextInputLayout passwordInput, loginInput;

    private SharedPreferences save;
    private Intent goMenu;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_login);

        loginAdmin = (CheckBox) findViewById(R.id.adminlogin);
        passwordInput = findViewById(R.id.password_text_input_layout);
        loginInput = findViewById(R.id.login_text_input_layout);

        goMenu = new Intent(this, MainActivity.class);
        save = getSharedPreferences("local", Context.MODE_PRIVATE);

        loginAdmin.setOnCheckedChangeListener((c, b) -> {
            passwordInput.setVisibility(b ? View.VISIBLE : View.GONE);
        });

        String mainInfo = save.getString("main", "");

        if (mainInfo.equals("")) {
            findViewById(R.id.login).setOnClickListener((v) -> {
                goAuth();
            });
        } else {
            findViewById(R.id.menuvisible).setVisibility(View.GONE);
            if (AdminFragment.isOnline(getApplicationContext())) {
                int login = save.getInt("login", 0);
                String password = save.getString("password", null);
                login(login, password);
            } else {
                startActivity(goMenu);
            }
        }
    }

    private void goAuth() {
        int login = StringExtensions.tryParseInt(loginInput.getEditText().getText().toString().trim(), 0);
        String password = loginAdmin.isChecked() ? passwordInput.getEditText().getText().toString().trim() : null;

        login(login, password);
    }

    private void login(int login, String password) {
        JsonObject data = new JsonObject();
        Gson gson = new Gson();
        data.add("login", gson.toJsonTree(login));
        data.add("password", gson.toJsonTree(password));

        if (!AdminFragment.isOnline(getApplicationContext())){
            AdminFragment.showToast(getApplicationContext(), false, "Отсутствие сети");
            return;
        }
        try {
            NetworkClient.Post("api/User/login", data.toString(), new NetworkClient.OnResponseListener() {
                @Override
                public void onSuccessful(int code, String data) {
                    runOnUiThread(() -> {
                        try {
                            JsonObject response = new Gson().fromJson(data, JsonObject.class);
                            if (response.get("isSuccess").getAsBoolean()) {
                                JsonElement main = response.getAsJsonObject().get("data");
                                SharedPreferences.Editor editor = save.edit();
                                editor.putString("main", main.toString());
                                editor.putInt("login", login);
                                editor.putString("password", password);
                                editor.commit();

                                startActivity(goMenu);
                            }

                        } catch (Exception e) {
                            AdminFragment.showToast(getApplicationContext(), false, Localization.server_error);
                        }
                    });
                }

                @Override
                public void onError(int code, String data) {
                    AdminFragment.showToast(getApplicationContext(), false, "Что-то пошло не так");
                }
            });
        } catch (Exception e) {
            AdminFragment.showToast(getApplicationContext(), false, "Что-то пошло не так");
        }
    }
}

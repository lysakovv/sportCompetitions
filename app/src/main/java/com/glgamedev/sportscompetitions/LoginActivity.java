package com.glgamedev.sportscompetitions;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.glgamedev.sportscompetitions.Network.NetworkClient;
import com.glgamedev.sportscompetitions.ui.adminpanel.AdminFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class LoginActivity extends AppCompatActivity {
    CheckBox loginAdmin;
    TextInputLayout passwordInput, loginInput;

    SharedPreferences save;
    Intent goMenu;

    void goAuth() {
        boolean isAdmin = loginAdmin.isChecked();
        String password = isAdmin ? passwordInput.getEditText().getText().toString().trim() : null;
        try {
            int login = Integer.parseInt(loginInput.getEditText().getText().toString().trim());
            loginInput.getEditText().setText("");
            JsonObject data = new JsonObject();
            Gson gson = new Gson();
            data.add("login", gson.toJsonTree(login));
            data.add("password", gson.toJsonTree(loginAdmin.isChecked() ? password : null));
            NetworkClient.Post("api/User/login", data.toString(), new NetworkClient.OnResponseListener() {
                @Override
                public void onSuccessful(int code, String data) {
                    runOnUiThread(() -> {
                        try {
                            JsonObject response = new Gson().fromJson(NetworkClient.resp1, JsonObject.class);
                            if (response.get("isSuccess").getAsBoolean()) {
                                response.add("login", gson.toJsonTree(login));
                                SharedPreferences.Editor editor = save.edit();
                                editor.putString("main", response.toString());
                                editor.putInt("login", login);
                                editor.putString("password", password   );
                                editor.commit();

                                startActivity(goMenu);
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
        } catch (Exception e) {
            AdminFragment.showToast(getApplicationContext(), false, "Неправильно указан логин");
        }

    }

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



        if (!save.getString("main", "").equals("")) {
            findViewById(R.id.menuvisible).setVisibility(View.GONE);
            if (AdminFragment.isOnline(getApplicationContext())) {
                loginInput.getEditText().setText("0");
                goAuth();
            } else {
                startActivity(goMenu);
            }
        }

        findViewById(R.id.login).setOnClickListener((v) -> {
            goAuth();
        });
    }
}

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
import android.widget.TextView;

import com.glgamedev.sportscompetitions.Localization;
import com.glgamedev.sportscompetitions.MainActivity;
import com.glgamedev.sportscompetitions.Network.NetworkClient;
import com.glgamedev.sportscompetitions.R;
import com.glgamedev.sportscompetitions.Utils;
import com.glgamedev.sportscompetitions.ui.adminpanel.AdminFragment;
import com.glgamedev.sportscompetitions.ui.localtable.LocalTableFragment;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class ChangeUserDataDialog extends Dialog {
    public EnterableDialog.Item surname, name, age;
    public Spinner team;
    public Button register;
    JsonArray steps;
    String teamName;
    int teamid;
    ArrayList<View> items = new ArrayList<>();

    int teamIndex = 0;
    int resultIndex = 0;
    int memberIndex = 0;
    public ChangeUserDataDialog(Context context, JsonObject userInfo, int compid, int teamid) {
        super(context);

        LinearLayout main = new LinearLayout(getContext());
        {
            main.setOrientation(LinearLayout.VERTICAL);
            main.setPadding(Utils.dp(getContext(), 5), Utils.dp(getContext(), 5), Utils.dp(getContext(), 5), Utils.dp(getContext(), 5));
        }


        JsonArray teamsResult = MainActivity.data.get("data").getAsJsonObject().get("teamResults").getAsJsonArray();
        for (int i = 0; i < teamsResult.size(); i++) {
            if (teamsResult.get(i).getAsJsonObject().get("id").getAsInt() == teamid) {
                teamIndex = i;
                break;
            }
        }
        JsonArray results = MainActivity.data.get("data").getAsJsonObject().get("teamResults").getAsJsonArray().get(teamIndex).getAsJsonObject().get("results").getAsJsonArray();
        for (int i = 0; i < results.size(); i++) {
            if (results.get(i).getAsJsonObject().get("competitionId").getAsInt() == compid) {
                resultIndex = i;
                break;
            }
        }
        JsonArray members = MainActivity.data.get("data").getAsJsonObject().get("teamResults").getAsJsonArray().get(teamIndex).getAsJsonObject().get("results").getAsJsonArray().get(resultIndex).getAsJsonObject().get("memberResults").getAsJsonArray();
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).getAsJsonObject().get("memberId").getAsInt() == userInfo.get("id").getAsInt()) {
                memberIndex = i;
                steps = members.get(i).getAsJsonObject().get("value").getAsJsonObject().get("steps").getAsJsonArray();
                break;
            }
        }

        for (int i = 0; i < steps.size(); i++) {
            TextView title = new TextView(getContext());
            {
                title.setText("Этап " + (i+1));
                title.setTextColor(Color.BLACK);
                title.setPadding(15, 5, 0, 5);
                title.setTextSize(15f);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    title.setTypeface(getContext().getResources().getFont(R.font.googlebold));
                }
                main.addView(title, -1, -2);
            }

            if (LocalTableFragment.typeList.get(compid) != 1) {
                EnterableDialog.Item item = new EnterableDialog.Item(getContext(), "Step " + (i + 1));
                items.add(item);
                item.setText(steps.get(i).getAsString());

                main.addView(item, Utils.dp(getContext(), 240), Utils.dp(getContext(), 35));
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) item.getLayoutParams();
                lp.setMargins(0, 5, 0, 0);
            } else {
                EnterableDialog.ItemTime item = new EnterableDialog.ItemTime(getContext());
                items.add(item);

                main.addView(item, Utils.dp(getContext(), 240), Utils.dp(getContext(), 35));
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) item.getLayoutParams();
                lp.setMargins(0, 5, 0, 0);
            }
        }

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
                try {
                    JsonArray data = new JsonArray();
                    Gson gson = new Gson();
                    for (View item: items) {
                        if (item instanceof EnterableDialog.Item)
                            data.add(gson.toJsonTree(((EnterableDialog.Item)item).getText().toString().trim()));
                        else
                            data.add(gson.toJsonTree(((EnterableDialog.ItemTime)item).getText().trim()));
                    }
                    MainActivity.data.get("data").getAsJsonObject().get("teamResults").getAsJsonArray().get(teamIndex).getAsJsonObject().get("results").getAsJsonArray().get(resultIndex).getAsJsonObject().get("memberResults").getAsJsonArray().get(memberIndex).getAsJsonObject().get("value").getAsJsonObject().add("steps", gson.toJsonTree(steps));
                    AdminFragment.saveData();
                    JsonObject req = new JsonObject();
                    req.add("steps", gson.toJsonTree(data));
                    req.add("competitionId", gson.toJsonTree(compid));
                    req.add("userId", gson.toJsonTree(userInfo.get("id").getAsInt()));
                    if (AdminFragment.isOnline(getContext())) {
                        NetworkClient.Post("api/User/changes222", req.toString(), new NetworkClient.OnResponseListener() {
                            @Override
                            public void onSuccessful(int code, String data) {
                                ((Activity)context).runOnUiThread(() -> {
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
                    } else {
                        AdminFragment.addOfflineEvent("api/User/change222", req);
                    }
                } catch (Exception e) {

                }
            });
        }
        setContentView(main);
    }
}

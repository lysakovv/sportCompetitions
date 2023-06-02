package com.glgamedev.sportscompetitions.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.glgamedev.sportscompetitions.Localization;
import com.glgamedev.sportscompetitions.Activities.MainActivity;
import com.glgamedev.sportscompetitions.Models.Requests.SetResultsData;
import com.glgamedev.sportscompetitions.Models.Responses.ResultData;
import com.glgamedev.sportscompetitions.Network.NetworkClient;
import com.glgamedev.sportscompetitions.R;
import com.glgamedev.sportscompetitions.Fragments.LocalTableFragment;
import com.glgamedev.sportscompetitions.Utils;
import com.glgamedev.sportscompetitions.Fragments.AdminFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.MessageFormat;
import java.util.ArrayList;

public class UserResultsDialog extends Dialog {
    private Context context;
    private ResultData.TeamResponse.MemberResultResponse userData;
    private ResultData.CompetitionResponse competitionData;

    public UserResultsDialog(Context ctx, ResultData.TeamResponse.MemberResultResponse userInfo, ResultData.CompetitionResponse competition) {
        super(ctx);
        context = ctx;
        userData = userInfo;
        competitionData = competition;
    }

    public UserResultsDialog Create(boolean isEditable) {

        LinearLayout main = new LinearLayout(getContext());
        {
            main.setOrientation(LinearLayout.VERTICAL);
            main.setPadding(Utils.dp(getContext(), 5), Utils.dp(getContext(), 5), Utils.dp(getContext(), 5), Utils.dp(getContext(), 5));
        }

        ArrayList<String> steps = userData.value.steps;
        ArrayList<View> items = new ArrayList<>();

        for (int i = 0; i < steps.size(); i++) {
            TextView title = new TextView(getContext());
            {
                title.setText(MessageFormat.format("Этап {0}", i + 1));
                title.setTextColor(Color.BLACK);
                title.setPadding(15, 5, 0, 5);
                title.setTextSize(15f);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    title.setTypeface(getContext().getResources().getFont(R.font.googlebold));
                }
                main.addView(title, -1, -2);
            }

            if (competitionData.type != 1) {
                EnterableDialog.Item item = new EnterableDialog.Item(getContext(), "Step " + (i + 1), isEditable, true);
                items.add(item);
                item.setText(steps.get(i));

                main.addView(item, Utils.dp(getContext(), 240), Utils.dp(getContext(), 35));
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) item.getLayoutParams();
                lp.setMargins(0, 5, 0, 0);
            } else {
                EnterableDialog.ItemTime item = new EnterableDialog.ItemTime(getContext(), steps.get(i), isEditable);
                items.add(item);

                main.addView(item, Utils.dp(getContext(), 240), Utils.dp(getContext(), 35));
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) item.getLayoutParams();
                lp.setMargins(0, 5, 0, 0);
            }
        }

        Button register = new Button(getContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            register.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(R.color.panelsBg)));
        }
        register.setText(isEditable ? "Изменить" : "Закрыть");
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
            if (!isEditable)
                return;
            try {
                ArrayList<String> data = new ArrayList<>();
                Gson gson = new Gson();
                for (View item : items) {
                    if (item instanceof EnterableDialog.Item)
                        data.add(((EnterableDialog.Item) item).getText().toString().trim());
                    else
                        data.add(((EnterableDialog.ItemTime) item).getText().trim());
                }
                SetResultsData localResults = MainActivity.getLocalResults();
                ResultData results = MainActivity.getResultData();
                localResults.AdminId = results.user.id;
                SetResultsData.LocalResults resultsData = new SetResultsData.LocalResults();
                resultsData.UserId = userData.memberId;
                resultsData.CompetitionId = competitionData.id;
                resultsData.Steps = data;
                localResults.Data.add(resultsData);

                if (AdminFragment.isOnline(getContext())) {
                    NetworkClient.Post("api/Competition/setResults", gson.toJson(localResults), new NetworkClient.OnResponseListener() {
                        @Override
                        public void onSuccessful(int code, String data) {
                            ((Activity) context).runOnUiThread(() -> {
                                try {
                                    JsonObject response = new Gson().fromJson(data, JsonObject.class);
                                    if (response.get("isSuccess").getAsBoolean()) {
                                        MainActivity.updateMainResults(gson.fromJson(response.getAsJsonObject().get("data"), ResultData.class));
                                        LocalTableFragment.updateTable(getContext());
                                    } else {
                                        MainActivity.updateLocalResults(localResults);
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
                    MainActivity.updateLocalResults(localResults);
                }
            } catch (Exception e) {

            }
        });

        setContentView(main);

        return this;
    }
}

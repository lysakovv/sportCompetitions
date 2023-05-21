package com.glgamedev.sportscompetitions.ui.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.glgamedev.sportscompetitions.R;
import com.glgamedev.sportscompetitions.Utils;

import java.util.ArrayList;

public class EnterableDialog extends Dialog {
    LinearLayout content, buttons;
    ScrollView scrollView;

    static class Item extends androidx.appcompat.widget.AppCompatEditText {
        public Item(Context context, String hint) {
            super(context);

            this.setHint(hint);
            this.setTextSize(15f);
            this.setHintTextColor(Color.argb(180, 255, 255, 255));
            this.setTextColor(Color.BLACK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.setBackgroundColor(getContext().getColor(R.color.panelsBg));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.setTypeface(getContext().getResources().getFont(R.font.googleregular));
            }
            this.setGravity(Gravity.CENTER);
            this.setPadding(0, 15, 0, 0);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, Utils.dp(getContext(), 35));
            lp.bottomMargin = 10;
            setLayoutParams(lp);
        }
    }

    static class ItemTime extends LinearLayout {
        Item m, s, ms;

        public String getText() {
            return m.getText().toString().trim() + ":" + s.getText().toString().trim() + "," + ms.getText().toString().trim();
        }

        public ItemTime(Context context) {
            super(context);

            setOrientation(LinearLayout.HORIZONTAL);

            m = new Item(context, "m");
            {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, Utils.dp(getContext(), 35), 1);
                lp.bottomMargin = 10;
                lp.rightMargin = 5;
                m.setLayoutParams(lp);

                addView(m);
            }

            s = new Item(context, "s");
            {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, Utils.dp(getContext(), 35), 1);
                lp.bottomMargin = 10;
                lp.rightMargin = 5;
                lp.leftMargin = 5;
                s.setLayoutParams(lp);

                addView(s);
            }

            ms = new Item(context, "ms");
            {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, Utils.dp(getContext(), 35), 1);
                lp.bottomMargin = 10;
                lp.leftMargin = 5;
                ms.setLayoutParams(lp);

                addView(ms);
            }

        }
    }

    Button cancel, enter;
    ArrayList<Item> inputs = new ArrayList<>();
    public static interface OnEnterListener {
        public void onEnter(ArrayList<String> data);
    }
    public OnEnterListener listener;

    public EnterableDialog(Context context, String title, String[] items, OnEnterListener listener) {
        super(context);

        content = new LinearLayout(getContext());
        {
            content.setOrientation(LinearLayout.VERTICAL);
            scrollView = new ScrollView(getContext());
            scrollView.setFillViewport(true);

            TextView titleView = new TextView(getContext());
            {
                titleView.setText(title);
                titleView.setTextSize(13f);
                titleView.setTextColor(Color.BLACK);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    titleView.setTypeface(getContext().getResources().getFont(R.font.googlebold));
                }
                titleView.setGravity(Gravity.CENTER);
                titleView.setPadding(0, 15, 0, 0);

                content.addView(titleView, -1, -2);
            }

            setContentView(content);
            content.addView(scrollView, Utils.dp(getContext(), 250), Utils.dp(getContext(), 120));

            LinearLayout settings = new LinearLayout(getContext());
            {
                settings.setOrientation(LinearLayout.HORIZONTAL);
                content.addView(settings, -1, -2);
            }

            cancel = new Button(getContext());
            {
                cancel.setBackgroundColor(getContext().getResources().getColor(R.color.decline));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    cancel.setTypeface(getContext().getResources().getFont(R.font.googleregular));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    cancel.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(R.color.decline)));
                }
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, Utils.dp(getContext(), 40), 1);
                lp.setMargins(5, 5, 10, 5);
                cancel.setText("Отменить");
                cancel.setTextColor(Color.BLACK);
                cancel.setTextSize(14f);
                settings.addView(cancel, lp);

                cancel.setOnClickListener((v) -> dismiss());
            }

            enter = new Button(getContext());
            {
                enter.setBackgroundColor(getContext().getResources().getColor(R.color.approve));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    enter.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(R.color.approve)));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    enter.setTypeface(getContext().getResources().getFont(R.font.googleregular));
                }

                enter.setOnClickListener((v) -> {
                    dismiss();
                    ArrayList<String> data = new ArrayList<>();
                    for (Item item: inputs) {
                        data.add(item.getText().toString());
                    }
                    if (listener != null) listener.onEnter(data);
                });

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, Utils.dp(getContext(), 40), 1);
                lp.setMargins(10, 5, 5, 5);
                enter.setText("Подтвердить");
                enter.setTextColor(Color.BLACK);
                enter.setTextSize(14f);
                settings.addView(enter, lp);
            }

            buttons = new LinearLayout(getContext());
            {
                buttons.setOrientation(LinearLayout.VERTICAL);
                buttons.setPadding(15, 15, 15, 15);
                for (String item: items) {
                    Item itemView = new Item(getContext(), item);
                    buttons.addView(itemView);
                    inputs.add(itemView);
                }
                scrollView.addView(buttons, -1, -1);
            }
        }
    }
}

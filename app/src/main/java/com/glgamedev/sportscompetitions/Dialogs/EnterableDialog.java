package com.glgamedev.sportscompetitions.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
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
        public Item(Context context, String hint, boolean isEditable, boolean isNumber) {
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
            this.setInputType(isEditable ? isNumber ? InputType.TYPE_CLASS_NUMBER
                    : InputType.TYPE_CLASS_TEXT
                    : InputType.TYPE_NULL);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, Utils.dp(getContext(), 35));
            lp.bottomMargin = 10;
            setLayoutParams(lp);
        }
    }

    static class ItemTime extends LinearLayout {
        Item m, s, ms;

        public String getText() {
            return m.getText().toString().trim() + ":" + s.getText().toString().trim() + ":" + ms.getText().toString().trim();
        }

        public ItemTime(Context context, String steps, boolean isEditable) {
            super(context);

            setOrientation(LinearLayout.HORIZONTAL);

            m = new Item(context, "m", isEditable, true);
            {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, Utils.dp(getContext(), 35), 1);
                lp.bottomMargin = 10;
                lp.rightMargin = 5;
                m.setLayoutParams(lp);
                m.setText(String.valueOf(steps.charAt(0)));
                m.setFilters(new InputFilter[] {new InputFilter.LengthFilter(2)});

                addView(m);
            }

            this.s = new Item(context, "s", isEditable, true);
            {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, Utils.dp(getContext(), 35), 1);
                lp.bottomMargin = 10;
                lp.rightMargin = 5;
                lp.leftMargin = 5;
                this.s.setLayoutParams(lp);
                s.setText(String.valueOf(steps.charAt(2)));
                s.setFilters(new InputFilter[] {new InputFilter.LengthFilter(2)});

                addView(this.s);
            }

            ms = new Item(context, "ms", isEditable, true);
            {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, Utils.dp(getContext(), 35), 1);
                lp.bottomMargin = 10;
                lp.leftMargin = 5;
                ms.setLayoutParams(lp);
                ms.setText(String.valueOf(steps.charAt(4)));
                ms.setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});

                addView(ms);
            }

        }
    }

    Button cancel, enter;
    ArrayList<Item> inputs = new ArrayList<>();
    public static interface OnEnterListener {
        public void onEnter(ArrayList<String> data);
    }

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
                    Item itemView = new Item(getContext(), item, true, false);
                    buttons.addView(itemView);
                    inputs.add(itemView);
                }
                scrollView.addView(buttons, -1, -1);
            }
        }
    }
}

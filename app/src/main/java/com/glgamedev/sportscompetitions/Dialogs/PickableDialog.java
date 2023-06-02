package com.glgamedev.sportscompetitions.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.glgamedev.sportscompetitions.R;
import com.glgamedev.sportscompetitions.Utils;

public class PickableDialog extends Dialog {
    LinearLayout content, buttons;
    ScrollView scrollView;

    public static class Item extends androidx.appcompat.widget.AppCompatTextView {
        public Item(Context context, String name) {
            super(context);

            this.setText(name);
            this.setTextSize(15f);
            this.setTextColor(Color.WHITE);
            {
                GradientDrawable draw = new GradientDrawable();
                draw.setColor(getContext().getResources().getColor(R.color.approve));
                draw.setCornerRadius(20f);
                this.setBackgroundDrawable(draw);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.setTypeface(getContext().getResources().getFont(R.font.googleregular));
            }
            this.setGravity(Gravity.CENTER);
            this.setPadding(0, 15, 0, 0);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, Utils.dp(getContext(), 35));
            lp.bottomMargin = 10;
            lp.rightMargin = 40;
            setLayoutParams(lp);
        }
    }

    public interface OnPickableListener {
        void onPick(int i);
    }

    public OnPickableListener listener;

    public PickableDialog(Context context, String title, String[] items, OnPickableListener listener) {
        super(context);
        this.listener = listener;

        content = new LinearLayout(getContext());

        content.setOrientation(LinearLayout.VERTICAL);
        scrollView = new ScrollView(getContext());
        scrollView.setFillViewport(true);
        content.setBackgroundColor(getContext().getResources().getColor(R.color.dialog));

        TextView titleView = new TextView(getContext());

        titleView.setText(title);
        titleView.setTextSize(13f);
        titleView.setTextColor(Color.BLACK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            titleView.setTypeface(getContext().getResources().getFont(R.font.googlebold));
        }
        titleView.setGravity(Gravity.CENTER);
        titleView.setPadding(0, 15, 0, 0);

        content.addView(titleView, -1, -2);

        setContentView(content);
        content.addView(scrollView, Utils.dp(getContext(), 250), Utils.dp(getContext(), 250));

        buttons = new LinearLayout(getContext());

        buttons.setOrientation(LinearLayout.VERTICAL);
        buttons.setPadding(15, 15, 15, 15);
        for (int i = 0; i < items.length; i++) {
            String item = items[i];
            Item itemView = new Item(getContext(), item);
            final int id = i;
            itemView.setOnClickListener((view -> {
                dismiss();
                if (listener != null) listener.onPick(id);
            }));
            buttons.addView(itemView);
        }
        scrollView.addView(buttons, -1, -1);
    }
}

package com.glgamedev.sportscompetitions.ui.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.glgamedev.sportscompetitions.R;
import com.glgamedev.sportscompetitions.Utils;

public class ReadbleDialog extends Dialog {
    LinearLayout content, buttons;
    ScrollView scrollView;

    class Item extends androidx.appcompat.widget.AppCompatTextView {
        public Item(Context context, String name) {
            super(context);

            this.setText(name);
            this.setTextSize(15f);
            this.setTextColor(Color.WHITE);
            this.setBackgroundColor(getContext().getResources().getColor(R.color.panelsBg));
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

    public ReadbleDialog(Context context, String title, String[] items) {
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
            content.addView(scrollView, Utils.dp(getContext(), 250), Utils.dp(getContext(), 250));

            buttons = new LinearLayout(getContext());
            {
                buttons.setOrientation(LinearLayout.VERTICAL);
                buttons.setPadding(15, 15, 15, 15);
                for (String item: items) {
                    Item itemView = new Item(getContext(), item);
                    buttons.addView(itemView);
                }
                scrollView.addView(buttons, -1, -1);
            }
        }
    }
}

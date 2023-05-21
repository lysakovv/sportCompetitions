package com.glgamedev.sportscompetitions.ui.Components.Table;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.glgamedev.sportscompetitions.R;
import com.glgamedev.sportscompetitions.Utils;

import java.util.ArrayList;

public class Column extends LinearLayout {
    public ArrayList<String> values = new ArrayList<>();
    public TextView title;
    public LinearLayout valuesLayout;

    @SuppressWarnings("deprecation")
    private void createValue(String name) {
        final TextView val = new TextView(getContext());
        GradientDrawable draw = new GradientDrawable();
        {
            draw.setColor(getContext().getResources().getColor(R.color.topCellBg2));
            draw.setStroke(2, getContext().getResources().getColor(R.color.topCellStroke));
            val.setBackgroundDrawable(draw);
        }
        val.setText(name);
        val.setTextSize(11f);
        val.setTextColor(Color.BLACK);
        val.setGravity(Gravity.CENTER);
        val.setPadding(0,0,0,0);

        valuesLayout.addView(val, new LayoutParams(-1, -1, 1));
    }

    public Column(Context context, String name, String[] values) {
        super(context);

        setOrientation(LinearLayout.VERTICAL);

        title = new TextView(getContext());
        {
            title.setText(name);
            title.setTextSize(11f);
            title.setTextColor(Color.BLACK);
            title.setGravity(Gravity.CENTER);

            GradientDrawable draw = new GradientDrawable();
            {
                draw.setColor(context.getResources().getColor(R.color.topCellBg));
                draw.setStroke(2, context.getResources().getColor(R.color.topCellStroke));
                title.setBackgroundDrawable(draw);
            }

            addView(title, new LayoutParams(-1, -1, 1));
        }

        valuesLayout = new LinearLayout(getContext());
        {
            addView(valuesLayout, -1, Utils.dp(getContext(), 30));
        }

        for (String val: values) {
            createValue(val);
        }
        setLayoutParams(new LayoutParams(Utils.dp(context, 130), Utils.dp(context, 90)));
    }
}
package com.glgamedev.sportscompetitions.Ui.Components.Table;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.glgamedev.sportscompetitions.R;
import com.glgamedev.sportscompetitions.Utils;

public class Collapse {
    public Context context;

    public boolean collapsed = true;

    public LinearLayout leftBar, mainBar, leftContent, mainContent;
    private View.OnClickListener clickListener;

    public Collapse(Context context, String name) {
        this.context = context;

        leftBar = new LinearLayout(context);
        {
            Utils.setCellDesign(leftBar, context.getResources().getColor(R.color.cellLeague));
        }

        mainBar = new LinearLayout(context);
        {
            TextView title = new TextView(context);
            {
                title.setText(name);
                Utils.setTextCell(title);
                title.setTextColor(Color.WHITE);
                title.setTextSize(14f);
            }
            mainBar.addView(title, -1, -1);
            Utils.setCellDesign(mainBar, context.getResources().getColor(R.color.cellLeague));
        }

        leftContent = new LinearLayout(context);
        {
            leftContent.setOrientation(LinearLayout.VERTICAL);
            leftContent.setLayoutParams(new AbsListView.LayoutParams(-1, -2));
        }

        mainContent = new LinearLayout(context);
        {
            mainContent.setOrientation(LinearLayout.VERTICAL);
            mainContent.setLayoutParams(new AbsListView.LayoutParams(-1, -2));
        }

        clickListener = new View.OnClickListener() {
            public void onClick(View v) {
                collapsed = !collapsed;

                leftContent.setVisibility(collapsed ? View.VISIBLE : View.GONE);
                mainContent.setVisibility(collapsed ? View.VISIBLE : View.GONE);

            }
        };
        leftBar.setOnClickListener(clickListener);
        mainBar.setOnClickListener(clickListener);

    }
}

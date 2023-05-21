package com.glgamedev.sportscompetitions.ui.Components.Table;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.glgamedev.sportscompetitions.R;
import com.glgamedev.sportscompetitions.Utils;

import java.util.ArrayList;

public class TableViewer extends ScrollView {
    LinearLayout table;
    LinearLayout lockRow;
    HorizontalScrollView scrollHorizontal;
    LinearLayout tableLayout, cols;

    public boolean isOdd = true, isColOdd = true;

    public ArrayList<Collapse> collapses = new ArrayList<>();
    public ArrayList<LinearLayout> rows = new ArrayList<>();

    public void createNewRow(int collapse, int index, String[][] values) {

        isOdd = !isOdd;

        TextView n = new TextView(getContext());
        {
            n.setText(Integer.toString(index));
            Utils.setTextCell(n);
            Utils.setCellDesign(n, isOdd ? getContext().getResources().getColor(R.color.cellOddBgRow) : getContext().getResources().getColor(R.color.cellNotOddBgRow));
            collapses.get(collapse).leftContent.addView(n, -1, Utils.dp(getContext(), 40));
        }

        LinearLayout layout = new LinearLayout(getContext());
        {
            layout.setOrientation(LinearLayout.HORIZONTAL);
            rows.add(layout);
            collapses.get(collapse).mainContent.addView(layout);
        }

        for (String[] vals: values) {
            int l = vals.length;
            LinearLayout valData = new LinearLayout(getContext());
            {
                valData.setOrientation(LinearLayout.HORIZONTAL);
            }
            for (String val: vals) {
                final TextView data = new TextView(getContext());
                Utils.setCellDesign(data, isOdd ? getContext().getResources().getColor(R.color.cellOddBgRow) : getContext().getResources().getColor(R.color.cellNotOddBgRow));
                Utils.setTextCell(data);
                data.setText(val);
                valData.addView(data,  new LayoutParams(Utils.dp(getContext(), 130 / l), -1, 1));
            }

            layout.addView(valData, Utils.dp(getContext(), 130), Utils.dp(getContext(), 40));
        }
    }

    public Column newCol(String name, String[] vals) {
        Column c = new Column(getContext(), name, vals);
        cols.addView(c);

        if (isColOdd)
            Utils.setCellDesign(c.title, getContext().getResources().getColor(R.color.cellOddBg));
        else
            Utils.setCellDesign(c.title, getContext().getResources().getColor(R.color.cellNotOddBg));

        isColOdd = !isColOdd;
        return c;
    }

    public void newCollapse(String name) {
        Collapse c = new Collapse(getContext(), name);
        lockRow.addView(c.leftBar, -1, Utils.dp(getContext(), 30));
        tableLayout.addView(c.mainBar, -1, Utils.dp(getContext(), 30));
        lockRow.addView(c.leftContent, -1, -2);
        tableLayout.addView(c.mainContent, -1, -2);

        collapses.add(c);
    }

    public TableViewer(Context context) {
        super(context);

        table = new LinearLayout(getContext());
        {
            table.setOrientation(LinearLayout.HORIZONTAL);
        }

        lockRow = new LinearLayout(getContext());
        {
            lockRow.setMinimumWidth(Utils.dp(getContext(), 25));
            lockRow.setOrientation(LinearLayout.VERTICAL);

            TextView n = new TextView(context);
            {
                n.setText("№");
                Utils.setTextCell(n);
                Utils.setCellDesign(n, context.getResources().getColor(R.color.topCellBg));
            }

            lockRow.addView(n, -1, Utils.dp(context, 90));


        }

        scrollHorizontal = new HorizontalScrollView(getContext());
        {
            tableLayout = new LinearLayout(getContext());
            {
                tableLayout.setOrientation(LinearLayout.VERTICAL);
                cols = new LinearLayout(getContext());
                {
                    cols.setOrientation(LinearLayout.HORIZONTAL);
                }

                Column name = newCol("Название команды", new String[]{});
                name.valuesLayout.setVisibility(View.GONE);

                tableLayout.addView(cols);

                LinearLayout l = new LinearLayout(getContext());
                {
                    Utils.setCellDesign(l, getContext().getResources().getColor(R.color.cellOddBg));
                    //tableLayout.addView(l, -1, Utils.dp(getContext(), 25));
                }
            }

            scrollHorizontal.addView(tableLayout, -1, -1);
            scrollHorizontal.setFillViewport(true);
        }

        table.addView(lockRow, Utils.dp(getContext(), 40), -1);
        table.addView(scrollHorizontal, new LayoutParams(-1, -1, 1));

        addView(table, -1, -1);

        setFillViewport(true);

        setBackgroundColor(Color.WHITE);

    }
}
package com.glgamedev.sportscompetitions;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.glgamedev.sportscompetitions.ui.Components.Table.TableViewer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collections;

public class Utils {
	public static String host = "gfx";

	public static void setCellDesign(View v, int color) {
		GradientDrawable draw = new GradientDrawable();
		{
			draw.setColor(color);
			draw.setStroke(2, Color.parseColor("#666666"));
			v.setBackgroundDrawable(draw);
		}
	}

	public static boolean isExistsNetwork() {
		try {
			InetAddress address = InetAddress.getByName("www.google.com");
			return !address.equals("");
		} catch (Exception e) {}
		return false;
	}

	public static SharedPreferences getPrefs(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	public static String getValueFromPrefs(Context context, String key) {
		return getPrefs(context).getString(key, "");
	}

	public static void writeToPrefs(Context context, String key, String value) {
		SharedPreferences.Editor editor = getPrefs(context).edit();
		editor.putString(key, value);
		editor.apply();
	}

	public static void loadTableMain(TableViewer table) {
		table.newCollapse("Первая лига");
		table.newCollapse("Вторая лига");
		table.newCollapse("Третья лига");


		for (int i = 0; i < 13; i++) table.createNewRow(0, i+1, new String[][] {{"Дружина Александра Невского"}, {"30", "1"}, {"28", "2"}, {"628s", "5"}});
		for (int i = 0; i < 8; i++) table.createNewRow(1, i+1, new String[][] {{"Дружина Александра Невского"}, {"30", "1"}, {"28", "2"}, {"628s", "5"}});
		for (int i = 0; i < 4; i++) table.createNewRow(2, i+1, new String[][] {{"Дружина Александра Невского"}, {"30", "1"}, {"28", "2"}, {"628s", "5"}});
	}

	public static void loadTableMainLocal(TableViewer table) {
		table.newCollapse("Первая лига");
		table.newCollapse("Вторая лига");
		table.newCollapse("Третья лига");


		for (int i = 0; i < 13; i++) table.createNewRow(0, i+1, new String[][] {{"Дружина Александра Невского"}, {"30", "1"}, {"28", "2"}, {"628s", "5"}});
		for (int i = 0; i < 8; i++) table.createNewRow(1, i+1, new String[][] {{"Дружина Александра Невского"}, {"30", "1"}, {"28", "2"}, {"628s", "5"}});
		for (int i = 0; i < 4; i++) table.createNewRow(2, i+1, new String[][] {{"Дружина Александра Невского"}, {"30", "1"}, {"28", "2"}, {"628s", "5"}});
	}

	public static void setTextCell(TextView text) {
		text.setTextSize(11f);
		text.setTextColor(Color.BLACK);
		text.setGravity(Gravity.CENTER);
		
	}
	
	public static void initRequest() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}
	
	public static void SetAssets(Context ctx, ImageView img, String path) {
		try {
			InputStream ims = ctx.getAssets().open(path);
			Drawable d = Drawable.createFromStream(ims, null);
			img.setImageDrawable(d);
			ims .close();
		}
		catch(IOException ex) {return;}
	}
	
	public static Typeface font(Context ctx) {
		return Typeface.createFromAsset(ctx.getAssets(), "Font.ttf");
	}
	
	public static int dp(Context context, float dp)
	{
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	public static void anim(View v, int time) {
		ObjectAnimator animation = ObjectAnimator.ofFloat(v, "alpha", 0, 1.0f);
		animation.setDuration(time);
		animation.start();
	}
	
	public static void disanim(View v, int time) {
		ObjectAnimator animation = ObjectAnimator.ofFloat(v, "alpha", 1.0f, 0);
		animation.setDuration(time);
		animation.start();
	}
}

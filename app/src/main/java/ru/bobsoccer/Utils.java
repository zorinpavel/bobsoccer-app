package ru.bobsoccer;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static Map getWindowSize(Context context) {
        Map<String, Integer> size = new HashMap<>();

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();

        display.getMetrics(displayMetrics);

        // since SDK_INT = 1;
        size.put("width", displayMetrics.widthPixels);
        size.put("height", displayMetrics.heightPixels);
        try {
            // used when 17 > SDK_INT >= 14; includes window decorations (statusbar bar/menu bar)
            size.put("width", (Integer) Display.class.getMethod("getRawWidth").invoke(display));
            size.put("height", (Integer) Display.class.getMethod("getRawHeight").invoke(display));
        } catch (Exception ignored) { }

        try {
            // used when SDK_INT >= 17; includes window decorations (statusbar bar/menu bar)
            Point realSize = new Point();
            Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
            size.put("width", realSize.x);
            size.put("height", realSize.y);
        } catch (Exception ignored) {}

        return size;
    }


    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }


    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

}

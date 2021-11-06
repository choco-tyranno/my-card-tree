package com.choco_tyranno.team_tree.presentation;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public class DisplayUtil {
    public static int dpToPixel(Resources resources, int dp) {
        return dp * (getDpi(resources) / 160);
    }

    public static int pixelToDp(Resources resources, int pixel) {
        float divideResult = (float) getDpi(resources) / 160;
        return (int) ((float) pixel / divideResult);
    }

    public static int getDpi(Resources resources) {
        float scale = resources.getDisplayMetrics().density;
        return (int)(scale * 160);
    }

    public static int getScreenWidthAsPixel(Context context) {
        return ((Activity) context).getWindowManager().getCurrentWindowMetrics().getBounds().right;
    }

    public static int getScreenWidthAsDp(Context context) {
        return pixelToDp(context.getResources(), getScreenWidthAsPixel(context));
    }
}

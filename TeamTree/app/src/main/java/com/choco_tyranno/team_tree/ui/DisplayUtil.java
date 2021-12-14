package com.choco_tyranno.team_tree.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;

public class DisplayUtil {
    public static int dpToPixel(Resources resources, int dp) {
        return dp * (getDpi(resources) / 160);
    }

    public static int pixelToDp(Resources resources, int pixel) {
        float divideResult = (float) getDpi(resources) / 160;
        return (int) ((float) pixel / divideResult);
    }

    public static float getDensity(Resources resources){
        return resources.getDisplayMetrics().density;
    }

    public static int getDpi(Resources resources) {
        float scale = getDensity(resources);
        return (int)(scale * 160);
    }

    public static int getScreenWidthAsPixel(Context context) {
        return ((Activity) context).getWindowManager().getCurrentWindowMetrics().getBounds().right;
    }

    public static int getScreenWidthAsDp(Context context) {
        return pixelToDp(context.getResources(), getScreenWidthAsPixel(context));
    }
}

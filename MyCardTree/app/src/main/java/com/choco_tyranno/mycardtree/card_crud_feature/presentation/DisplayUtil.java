package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;

public class DisplayUtil {
    public static int dpToPixel(Resources resources, int dp){
        return dp*(int)resources.getDisplayMetrics().density;
    }
    public static int pixelToDp(Resources resources, int pixel){
        return pixel/(int)resources.getDisplayMetrics().density;
    }

    public static int getScreenWidth(Context context){
        return ((Activity) context).getWindowManager().getCurrentWindowMetrics().getBounds().right;
    }
}

package com.choco_tyranno.team_tree.presentation;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;

public class DisplayUtil {
    public static int dpToPixel(Resources resources, int dp){
        return dp*(getDpi(resources)/160);
    }

    public static int pixelToDp(Resources resources, int pixel){
        return pixel/(getDpi(resources)/160);
    }

    public static int getDpi(Resources resources){
        float scale = resources.getDisplayMetrics().density;
        int dpi = 0;
        switch (String.valueOf(scale)){
            case "1.0":
                //mdpi
                dpi = 160;
                break;
            case "1.5":
                //hdpi
                dpi = 240;
                break;
            case "2.0":
                //xdpi
                dpi = 320;
                break;
            case "3.0":
                //xxdpi
                dpi = 480;
                break;
            case "4.0":
                //xxxdpi
                dpi = 640;
                break;
            case "0.75":
                //ldpi
                dpi = 120;
                break;
            default:
                throw new RuntimeException("DisplayUtil#dpToPixel : Proper scale not found");
        }
        return dpi;
    }

    public static int getScreenWidth(Context context){
        //px
        return ((Activity) context).getWindowManager().getCurrentWindowMetrics().getBounds().right;
    }
}

package com.choco_tyranno.team_tree.ui;

import android.content.Context;
import android.widget.Toast;

public class SingleToaster {
    private SingleToaster() {

    }

    public static Toast makeTextShort(Context context, String msg) {
        return Toast.makeText(context, msg, Toast.LENGTH_SHORT);
    }

    public static Toast makeTextLong(Context context, String msg) {
        return Toast.makeText(context, msg, Toast.LENGTH_LONG);
    }

}

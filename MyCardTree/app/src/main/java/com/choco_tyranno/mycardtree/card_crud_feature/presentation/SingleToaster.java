package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import android.content.Context;
import android.widget.Toast;

import java.util.Optional;

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
